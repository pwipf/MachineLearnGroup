package multiLayerPerceptron;

import java.awt.*;
import java.util.Random;
import static multiLayerPerceptron.StdDraw.*;


public class BackProp{

	static Random gen=new Random();
	static Color[] colorlist={BLACK,YELLOW,BLUE,DARK_GRAY,CYAN,GRAY,GREEN,MAGENTA,ORANGE,PINK};


	public static void main(String[] args) {

		String filename="bankNote.arff";
		ArffReader dataFile=new ArffReader(filename);

		setupGraphic(); // simple graph to show realtime squared error for each epoch
		                // different color for each fold of the 10 folds


		int nin=dataFile.nAttributes; // number of attributes from file
		int nout=dataFile.nClasses;		// number of classes=number of output nodes=nodes in last layer


		int[]	size=new int[]{nin,10,nout}; // the number of nodes in each layer.
		int	epochs=100;  // number of times to run the training example list through the backprop.
		double	eta=.05; // learning rate
		double	mu=.1;   // momentum coefficient

		int	examples=dataFile.data.length;

		boolean[] act=new boolean[size.length]; //this array is the activation type for each layer.
																						//true is linear, false is sigmoid
		for(int i=0;i<size.length;i++)
			act[i]=false;
		//act[size.length-1]=true; // last node is linear activation

		double[][] outputVector= new double[examples][nout];

		// set the output vectors y. Each output node
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

		////////////////////////////////////
		// 10 fold X-Validation

		int totalRight=0,totalWrong=0,color=0;

		for(int fold=0;fold<10;fold++){
			System.out.println("\nFold "+fold);
			BackpropNetwork net=new BackpropNetwork(size,act);

			setPenColor(colorlist[color++%10]);

			int ntests=examples/10;
			int ntrains=examples-ntests;

			int firstTrainIndex=(fold*ntrains)%examples;
			int lastTrainIndex=((firstTrainIndex+ntrains)-1)%examples;
			int firstTestIndex=(firstTrainIndex+ntrains)%examples;
			int lastTestIndex=((firstTestIndex+ntests)-1)%examples;

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


			System.out.println("training on examples "+firstTrainIndex+"-"+lastTrainIndex);

			net.train(trainx, trainy, epochs, eta, mu);

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

		System.out.println("\nTotal: "+totalRight/(totalRight+totalWrong)*100+"% correct");
	}//main


	static void setupGraphic(){
		// stuff for graphical output
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
