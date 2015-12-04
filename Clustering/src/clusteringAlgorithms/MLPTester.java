package clusteringAlgorithms;

import java.awt.*;
import java.util.Random;



public class MLPTester{

	static Random gen=new Random();

	static String[] filelist={"banknote","mammograph","breastcancer","wine_cultivar","wine_quality",
		"pima-indians-diabetes","cmc","fertility","heart","glass"};
	static String[] algNames={"Backprop","EvStrat","DiffEv","GenAlg"};

	static enum Algs{Backprop, EvolutionaryStrategy, DiffEv, GeneticAlg};


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
                                System.out.println("Length of dataFile " + dataFile.data[0].length);

                                
                                //Prints out all the data
                                for (int z = 0; z<examples; z++)
                                {                                
                                for (int i = 0; i<nin; i++)
                                {
                                    System.out.print(dataFile.data[z][i]  + " ");
                                }
                                    System.out.println();
                                }


			}//alg

		}//file

		System.out.println("\nFinished in "+(double)(System.currentTimeMillis()-startTime)/1000+" sec");
	}//main

}
