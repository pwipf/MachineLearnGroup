package multiLayerPerceptron;

import java.awt.*;
import java.util.Random;
import static multiLayerPerceptron.StdDraw.*;


public class MLPTester{

	static Random gen=new Random();
	static Color[] colorlist={BLACK,BOOK_BLUE,RED,BLUE,CYAN,GRAY,GREEN,MAGENTA,ORANGE,PINK};
	static String[] filelist={"banknote","mammograph","breastcancer","wine_cultivar","wine_quality",
		"pima-indians-diabetes","cmc","fertility","heart","glass"};
	static String[] algNames={"Backprop","EvStrat","DiffEv","GenAlg"};

	static enum Algs{Backprop, EvolutionaryStrategy, DiffEv, GeneticAlg};

	private static int numFolds = 10;

	// main()
	public static void main(String[] args) {

		long startTime=System.currentTimeMillis();


		ParameterLoader pl=new ParameterLoader("params.txt");
		//pl.initfile(filelist);
		double[][][] parameters=pl.loadParameters();

		for(int file=0;file<10;file++){

			// to skip all but one dataset, for testing
			if(file != 9)
				continue;

			ArffReader dataFile = new ArffReader(filelist[file]+".arff");

			int nin=dataFile.nAttributes; // number of attributes from file
			int nout=dataFile.nClasses;		// number of classes=number of output nodes=nodes in last layer


			////////////////////////////////////////////////////
			// main setting of parameters
			//
			int[]	sizes=new int[]{nin,15,15,nout}; // the number of nodes in each layer. {nin,10,nout} is 1 hidden layer with 10 nodes


			int	examples=dataFile.data.length; // actual number of data records in the file

			// need to setup an output vector all zeros with a 1 at exactly one position, the proper class.
			double[][] outputVector= new double[examples][nout];

			for(int i=0;i<examples;i++)
				for(int j=0;j<nout;j++)
					if((int)dataFile.dataClass[i]==j)
						outputVector[i][j]=1;
					else
						outputVector[i][j]=0;


			System.out.println("File: "+filelist[file]);
			System.out.println("Number examples: "+examples);
			System.out.print("Number of attributes: "+nin);
			if(nin==4)System.out.print(" (Oops, it looked like 5 on UCI but they count the class)");
			System.out.println("\nNumber of classifications: "+nout);

			for(Algs alg: Algs.values()){ // loop through all the algorithms

				// to skip an algorithm for testing
				if(alg==Algs.Backprop)
					continue;
				if(alg==Algs.EvolutionaryStrategy)
					continue;
        //if(alg==Algs.DiffEv)
          //continue;
				if(alg==Algs.GeneticAlg)
					continue;

				System.out.println("\nAlg: "+algNames[alg.ordinal()]);

				setupGraphic(alg.ordinal(),file,parameters[file][alg.ordinal()]); // simple graph to show realtime squared error for each epoch
										// different color for each fold of the 10 folds

				//////////////////////////////////////////////////////////////////////
				// numFolds fold X-Validation

				int[] right=new int[numFolds];
				int[] wrong=new int[numFolds];

				double scale=0;


				for(int fold=0;fold<numFolds;fold++){
					//System.out.println("\nFold "+(fold+1));

					setPenColor(colorlist[fold%colorlist.length]);

					int ntests=examples/numFolds;
					int ntrains=examples-ntests;

					int firstTrainIndex=(fold*ntrains)%examples;
					int lastTrainIndex=((firstTrainIndex+ntrains)-1)%examples;
					int firstTestIndex=(firstTrainIndex+ntrains)%examples;
					int lastTestIndex=((firstTestIndex+ntests)-1)%examples;

					// create arrays for the training and testing sets, then fill them from the "master" (dataFile.data)
					// X IS THE INPUT VECTOR AND Y IS THE OUTPUT VECTOR
					double[][] trainx=new double[ntrains][];
					double[][] trainy=new double[ntrains][];
					double[][] testx=new double[ntests][];
					double[][] testy=new double[ntests][];

					for(int i=0;i<ntrains;i++){
						trainx[i]=dataFile.data[(i+firstTrainIndex)%examples];
						trainy[i]=outputVector[(i+firstTrainIndex)%examples];
					}
					for(int i=0;i<ntests;i++){
						testx[i]=dataFile.data[(i+firstTestIndex)%examples];
						testy[i]=outputVector[(i+firstTestIndex)%examples];
					}



					///////////////////////////////////////////////////////////////
					// send the network the training data and train it.

					//System.out.println("training on examples "+firstTrainIndex+"-"+lastTrainIndex);

					Network net=null;
					switch(alg){
						case Backprop:
							net=new BackpropNetwork(sizes,scale);
							break;
						case EvolutionaryStrategy:
							net=new EvolutionaryStrategy(sizes);
							break;
						case DiffEv:
							net=new DiffEvNetwork(sizes);
							break;
						case GeneticAlg:
							net=new GeneticAlgNetwork(sizes);
							break;
					}

					net.train(trainx,trainy,parameters[file][alg.ordinal()]);

					/////////////////////////////////////////////////////////////
					// test using simply the net.feedForward function, then choose the output node
					// with the highest number (they are all between 0 and 1 if sigmoid activation)

					//System.out.println("testing on examples "+firstTestIndex+"-"+lastTestIndex);
					right[fold]=0;
					wrong[fold]=0;
					for(int i=0;i<ntests;i++){
						double[] output=net.feedForward(testx[i]);

						//find highest number in outputvector, that is what we choose for the output "answer"
						int answer=0;
						for(int j=1;j<nout;j++)
							if(output[j]>output[answer])
								answer=j;

						if((int)testy[i][answer]==1)
							right[fold]++;
						else
							wrong[fold]++;

						scale=BackpropNetwork.scale;
					}

					//System.out.println("fold: "+(fold+1)+", right: "+right[fold]+", wrong: "+wrong[fold]+
					//				", "+(double)right[fold]/(right[fold]+wrong[fold])*100+"%");

				}

				double mean=0;
				double sd=0;
				for(int i=0;i<numFolds;i++)
					mean+=(double)right[i]/(right[i]+wrong[i])*100;
				mean/=numFolds;
				for(int i=0;i<numFolds;i++)
					sd+=Math.pow(((double)right[i]/(right[i]+wrong[i])*100)-mean, 2);
				sd/=numFolds;
				sd=Math.sqrt(sd);

				System.out.println("Accuracy:\n\tmean: "+mean+"%\n\tstandard dev: "+sd+"%\n");

				String gfilename=filelist[file]+"_"+algNames[alg.ordinal()]+".jpg";
				save(gfilename);
			}//alg

		}//file

		System.out.println("\nFinished in "+(double)(System.currentTimeMillis()-startTime)/1000+" sec");
	}//main


  // stuff for graphical output
	static void setupGraphic(int alg, int file, double[] params){
		setCanvasSize(800,600);
		setPenRadius(.001);
		line(0,.05,1,.05);
		line(.05,0,.05,1);
		text(0,.5,"error");
		if(alg==0)
			text(.5,0,"Epoch");
		else
			text(.5,0,"Generation");
		String ps="Parameters: "+params[0];
		for(int i=1;i<params.length;i++)
			ps=String.format("%s, %s", ps, params[i]);
		text(.5,1.02,String.format("Data: %s.arff    Alg: %s    %s", filelist[file],algNames[alg],ps));
		setPenColor(BOOK_RED);
		setPenRadius(.005);
	}
}
