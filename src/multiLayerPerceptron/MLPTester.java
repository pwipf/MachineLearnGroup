package multiLayerPerceptron;

import java.awt.*;
import java.util.Random;
import static multiLayerPerceptron.StdDraw.*;


public class MLPTester{

	static Random gen=new Random();
	static Color[] colorlist={BLACK,BOOK_BLUE,RED,BLUE,CYAN,GRAY,GREEN,MAGENTA,ORANGE,PINK};
	static String[] filelist={"banknote","mammograph","breastcancer","wine_cultivar","wine_quality",
		"pima-indians-diabetes","cmc","fertility","heart","glass"};


	public static void main(String[] args) {


		for(int file=0;file<10;file++){

			ArffReader dataFile = new ArffReader(filelist[file]+".arff");

			int nin=dataFile.nAttributes; // number of attributes from file
			int nout=dataFile.nClasses;		// number of classes=number of output nodes=nodes in last layer


			////////////////////////////////////////////////////
			// main setting of parameters
			//
			int[]	size=new int[]{nin,20,nout}; // the number of nodes in each layer. {nin,10,nout} is 1 hidden layer with 10 nodes

			// Backprop parameters
			int	epochs =1000;  // number of times to run the training example list through the backprop.
			double	eta=.0005; // learning rate
			double	mu =.1;   // momentum coefficient

			// Differential Evolution parameters
			int maxGenerations=500;
			int npop   =8;
			double beta=1;
			double rho =.6;


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


			for(int alg=0;alg<2;alg++){//0=backprop, 1=diffev
				String algname=(alg==0? "Backpropogation": (alg==1? "Differential Evolution":
								(alg==2? "Something else": "The Last One")));
				System.out.println("\nAlg: "+algname);

				setupGraphic(); // simple graph to show realtime squared error for each epoch
										// different color for each fold of the 10 folds

				//////////////////////////////////////////////////////////////////////
				// 10 fold X-Validation

				int[] right=new int[10];
				int[] wrong=new int[10];
				int color=0;

				double scale=0;

				for(int fold=0;fold<10;fold++){
					//System.out.println("\nFold "+(fold+1));

					//create a new MLP
					Network net=new Network(size,scale);

					setPenColor(colorlist[color++%10]);

					int ntests=examples/10;
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

					if(alg==0){
						net.trainBackprop(trainx, trainy, epochs, eta, mu);
					}
					else if(alg==1){
						net.trainDiffEv(trainx, trainy, maxGenerations, npop, beta, rho);
					}

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
					}

					//System.out.println("fold: "+fold+" right: "+right[fold]+" wrong: "+wrong[fold]);

					scale=net.getScale();
				}

				double mean=0;
				double sd=0;
				for(int i=0;i<10;i++)
					mean+=(double)right[i]/(right[i]+wrong[i])*100;
				mean/=10;
				for(int i=0;i<10;i++)
					sd+=Math.pow(((double)right[i]/(right[i]+wrong[i])*100)-mean, 2);
				sd/=10;
				sd=Math.sqrt(sd);

				System.out.println("Accuracy:\n\tmean: "+mean+"\n\tstandard dev: "+sd+"\n");

				String gfilename=filelist[file]+"_"+algname+".jpg";
				save(gfilename);
			}//alg
		}//file
	}//main


  // stuff for graphical output
	static void setupGraphic(){
		setCanvasSize(800,600);
		setPenRadius(.001);
		line(0,.05,1,.05);
		line(.05,0,.05,1);
		text(0,.5,"error");
		text(.5,0,"Time");
		setPenColor(BOOK_RED);
		setPenRadius(.005);
	}
}
