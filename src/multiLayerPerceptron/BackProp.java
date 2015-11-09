package multiLayerPerceptron;

import java.awt.*;
import java.util.Random;
import static multiLayerPerceptron.StdDraw.*;


public class BackProp{

	static Random gen=new Random();
	static Color[] colorlist={BLACK,BOOK_BLUE,RED,BLUE,CYAN,GRAY,GREEN,MAGENTA,ORANGE,PINK};


	public static void main(String[] args) {

		String filename="iris.arff";
		ArffReader dataFile=new ArffReader(filename);

		setupGraphic(); // simple graph to show realtime squared error for each epoch
		                // different color for each fold of the 10 folds


		int nin=dataFile.nAttributes; // number of attributes from file
		int nout=dataFile.nClasses;		// number of classes=number of output nodes=nodes in last layer


		////////////////////////////////////////////////////
		// main setting of parameters
		//
		int[]	size=new int[]{nin,20,nout}; // the number of nodes in each layer. {nin,10,nout} is 1 hidden layer with 10 nodes

		// Backprop parameters
		int	epochs =500;  // number of times to run the training example list through the backprop.
		double	eta=.1; // learning rate
		double	mu =.1;   // momentum coefficient

		// Differential Evolution parameters
		int maxEpochs=800;
		int npop   =5;
		double beta=.5;
		double rho =.7;


		int	examples=dataFile.data.length; // actual number of data records in the file

		boolean[] act=new boolean[size.length]; //this array is the activation type for each layer.
																						//true is linear, false is sigmoid
		for(int i=0;i<size.length;i++)          // I think for classification sigmoid all the way through is best.
			act[i]=false;


		// need to setup an output vector all zeros with a 1 at exactly one position, the proper class.
		double[][] outputVector= new double[examples][nout];

		for(int i=0;i<examples;i++)
			for(int j=0;j<nout;j++)
				if((int)dataFile.dataClass[i]==j)
					outputVector[i][j]=1;
				else
					outputVector[i][j]=0;


		System.out.println("File: "+filename);
		System.out.println("Number examples: "+examples);
		System.out.println("Number of attributes: "+nin);
		System.out.println("Number of classifications: "+nout);

		//////////////////////////////////////////////////////////////////////
		// 10 fold X-Validation

		int totalRight=0,totalWrong=0,color=0;

		for(int fold=0;fold<10;fold++){
			System.out.println("\nFold "+(fold+1));

			//create a new MLP
			Network net=new Network(size);

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
			System.out.println("training on examples "+firstTrainIndex+"-"+lastTrainIndex);
//pick a training alg
			//net.trainBackprop(trainx, trainy, epochs, eta, mu);
			net.trainDiffEv(trainx, trainy, maxEpochs, npop, beta, rho);

			/////////////////////////////////////////////////////////////
			// test using simply the net.feedForward function, then choose the output node
			// with the highest number (they are all between 0 and 1 if sigmoid activation)
			System.out.println("testing on examples "+firstTestIndex+"-"+lastTestIndex);
			int numRight=0;
			int numWrong=0;
			for(int i=0;i<ntests;i++){
				double[] output=net.feedForward(testx[i]);

				//find highest number in outputvector, that is what we choose for the output "answer"
				int answer=0;
				for(int j=1;j<nout;j++)
					if(output[j]>output[answer])
						answer=j;

				if((int)testy[i][answer]==1)
					numRight++;
				else
					numWrong++;
			}

			totalRight+=numRight;
			totalWrong+=numWrong;

			System.out.println("Number correct: "+numRight+"\n  Number wrong: "+numWrong);
			System.out.println((double)numRight/(numRight+numWrong)*100+"% correct");
		}

		System.out.println("\nTotal: "+(double)totalRight/(totalRight+totalWrong)*100+"% correct");
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
