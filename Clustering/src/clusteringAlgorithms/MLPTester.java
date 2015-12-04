package clusteringAlgorithms;

import java.awt.*;
import java.util.Random;



public class MLPTester{

	static Random gen=new Random();

	static String[] filelist={"banknote","mammograph","pima-indians-diabetes","wine_cultivar","userKnowledgeModeling",
		"iris","cmc","fertility","heart","glass"};
	static String[] algNames={"k_Means","DB_Scan","CompetLearning","ACO", "PSO"};

	static enum Algs{k_Means, DB_Scan, CompetLearning, ACO, PSO};


	// main()
	public static void main(String[] args) {

		long startTime=System.currentTimeMillis();


		ParameterLoader pl=new ParameterLoader("params.txt");
		//pl.initfile(filelist);
		double[][][] parameters=pl.loadParameters();

		for(int file=0;file<10;file++){

			// to skip all but one dataset, for testing
			if(file != 8)
				continue;

			ArffReader dataFile = new ArffReader(filelist[file]+".arff");

			int nin=dataFile.nAttributes; // number of attributes from file
			int nout=dataFile.nClasses;		// number of classes=number of output nodes=nodes in last layer


			////////////////////////////////////////////////////
			// main setting of parameters
			//



			int	examples=dataFile.data.length; // actual number of data records in the file

                        
                        


			System.out.println("File: "+filelist[file]);
			System.out.println("Number examples: "+examples);
			System.out.print("Number of attributes: "+nin);
			if(nin==4)System.out.print(" (Oops, it looked like 5 on UCI but they count the class)");
			//System.out.println("\nNumber of classifications: "+nout);

			for(Algs alg: Algs.values()){ // loop through all the algorithms

				// to skip an algorithm for testing
				if(alg==Algs.k_Means)
					continue;
		//		if(alg==Algs.DB_Scan)
		//			continue;
                                if(alg==Algs.CompetLearning)
                                continue;
				if(alg==Algs.ACO)
					continue;
                		if(alg==Algs.PSO)
					continue;

				System.out.println("\nAlg: "+algNames[alg.ordinal()]);
                                System.out.println("Length of dataFile " + dataFile.data[0].length);

                                


                                Algorithm net=null;
					switch(alg){
					//	case k_Means:
					//		net=new k_Means();
					//		break;
						case DB_Scan:
							net=new DB_Scan();
							break;
					//	case CompetLearning:
					//		net=new CompetLearning();
					//		break;
					//	case ACO:
					//		net=new ACO();
					//		break;
					//	case PSO:
					//		net=new PSO();
					//		break;                                                        
                                                        
					}

					net.generateClusters(dataFile.data,parameters[file][0]);
                                
                                
                                
                                
                                
                                
                                
			}//alg
                        
                        
                                //Prints out all the data
                            //    for (int z = 0; z<examples; z++)
                            //    {                                
                            //    for (int i = 0; i<nin; i++)
                            //    {
                            //        System.out.print(dataFile.data[z][i]  + " ");
                            //    }
                            //        System.out.println();
                            //    }
                        

		}//file

		System.out.println("\nFinished in "+(double)(System.currentTimeMillis()-startTime)/1000+" sec");
	}//main

}
