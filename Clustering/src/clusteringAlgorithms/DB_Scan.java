package clusteringAlgorithms;


import java.util.ArrayList;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Brendan Burns
 */
public class DB_Scan extends Algorithm {

    static double inputData[][];
//inputData[numInputs][tupleSize]
//For each tuple [][0......tupleSize] Values dependant on the dataset, and the number of attributes.    
    static int numInputs;
    static int tupleSize;
    static int dataVisited[][];
//dataVisited[numInputs][2]
//For each tuple, [][0] = If the Node is visited. 0 = False, 1 = True
//                [][1] = is the cluster it belongs to -2 = "Not in a Cluster" -1 = "Noise"
    static int numberClusters = 0;

    //The number of clusters will increase as the algorithm runs. 
    //The 0 is just the starting position
    static boolean hasNoise = false; //Simple check to remove a redundant println in cases where no noise exists.
//    static int eps;
//    static int minPts;
    //static ArrayList<Integer> neighborPts = new ArrayList<Integer>();


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    //Tunable Variables        
    //EPS value aka euclidean distance between datapoints
        double eps = 20;  
    //Minimum number of points needed to form a Cluster
        int minPts = 3;
        
        
        
        // TODO code application logic here
        numInputs = 20;//30;
        tupleSize = 2;//7;
        inputData = new double[numInputs][]; // 30-item subset of Iris data set (10 each class)
        dataVisited = new int[numInputs][2];
//        inputData[0] = new double[]{5.1, 3.5, 1.4, 0.2, 0, 0, 1}; // sepal length, sepal width, petal length, petal width -> 
//        inputData[1] = new double[]{4.9, 3.0, 1.4, 0.2, 0, 0, 1}; // Iris setosa = 0 0 1, Iris versicolor = 0 1 0, Iris virginica = 1 0 0
//        inputData[2] = new double[]{4.7, 3.2, 1.3, 0.2, 0, 0, 1};
//        inputData[3] = new double[]{4.6, 3.1, 1.5, 0.2, 0, 0, 1};
//        inputData[4] = new double[]{5.0, 3.6, 1.4, 0.2, 0, 0, 1};
//        inputData[5] = new double[]{5.4, 3.9, 1.7, 0.4, 0, 0, 1};
//        inputData[6] = new double[]{4.6, 3.4, 1.4, 0.3, 0, 0, 1};
//        inputData[7] = new double[]{5.0, 3.4, 1.5, 0.2, 0, 0, 1};
//        inputData[8] = new double[]{4.4, 2.9, 1.4, 0.2, 0, 0, 1};
//        inputData[9] = new double[]{4.9, 3.1, 1.5, 0.1, 0, 0, 1};
//
//        inputData[10] = new double[]{7.0, 3.2, 4.7, 1.4, 0, 1, 0};
//        inputData[11] = new double[]{6.4, 3.2, 4.5, 1.5, 0, 1, 0};
//        inputData[12] = new double[]{6.9, 3.1, 4.9, 1.5, 0, 1, 0};
//        inputData[13] = new double[]{5.5, 2.3, 4.0, 1.3, 0, 1, 0};
//        inputData[14] = new double[]{6.5, 2.8, 4.6, 1.5, 0, 1, 0};
//        inputData[15] = new double[]{5.7, 2.8, 4.5, 1.3, 0, 1, 0};
//        inputData[16] = new double[]{6.3, 3.3, 4.7, 1.6, 0, 1, 0};
//        inputData[17] = new double[]{4.9, 2.4, 3.3, 1.0, 0, 1, 0};
//        inputData[18] = new double[]{6.6, 2.9, 4.6, 1.3, 0, 1, 0};
//        inputData[19] = new double[]{5.2, 2.7, 3.9, 1.4, 0, 1, 0};
//
//        inputData[20] = new double[]{6.3, 3.3, 6.0, 2.5, 1, 0, 0};
//        inputData[21] = new double[]{5.8, 2.7, 5.1, 1.9, 1, 0, 0};
//        inputData[22] = new double[]{7.1, 3.0, 5.9, 2.1, 1, 0, 0};
//        inputData[23] = new double[]{6.3, 2.9, 5.6, 1.8, 1, 0, 0};
//        inputData[24] = new double[]{6.5, 3.0, 5.8, 2.2, 1, 0, 0};
//        inputData[25] = new double[]{7.6, 3.0, 6.6, 2.1, 1, 0, 0};
//        inputData[26] = new double[]{4.9, 2.5, 4.5, 1.7, 1, 0, 0};
//        inputData[27] = new double[]{7.3, 2.9, 6.3, 1.8, 1, 0, 0};
//        inputData[28] = new double[]{6.7, 2.5, 5.8, 1.8, 1, 0, 0};
//        inputData[29] = new double[]{7.2, 3.6, 6.1, 2.5, 1, 0, 0};


      inputData[0] = new double[] { 65.0, 220.0 };
      inputData[1] = new double[] { 73.0, 160.0 };
      inputData[2] = new double[] { 59.0, 110.0 };
      inputData[3] = new double[] { 61.0, 120.0 };
      inputData[4] = new double[] { 75.0, 150.0 };
      inputData[5] = new double[] { 67.0, 240.0 };
      inputData[6] = new double[] { 68.0, 230.0 };
      inputData[7] = new double[] { 70.0, 220.0 };
      inputData[8] = new double[] { 62.0, 130.0 };
      inputData[9] = new double[] { 66.0, 210.0 };
      inputData[10] = new double[] { 77.0, 190.0 };
      inputData[11] = new double[] { 75.0, 180.0 };
      inputData[12] = new double[] { 74.0, 170.0 };
      inputData[13] = new double[] { 70.0, 210.0 };
      inputData[14] = new double[] { 61.0, 110.0 };
      inputData[15] = new double[] { 58.0, 100.0 };
      inputData[16] = new double[] { 66.0, 230.0 };
      inputData[17] = new double[] { 59.0, 120.0 };
      inputData[18] = new double[] { 68.0, 210.0 };
      inputData[19] = new double[] { 61.0, 130.0 };



        //Shuffle the data
        for (int i = 0; i < inputData.length; ++i) // shuffle indices
        {
            int r = new Random().nextInt(inputData.length - i) + i;
            double tmp[] = inputData[r];
            inputData[r] = inputData[i];
            inputData[i] = tmp;
        }

        //Initalize Visited Array    
        for (int i = 0; i < dataVisited.length; i++) {
            dataVisited[i][0] = 0;        //Not Visited
            dataVisited[i][1] = -2;       //Not in a cluster

        }

        DBScan(eps, minPts);

        //System.out.println("Total Number of Clusters:" + numberClusters);
        //Prints out all Cluster tuples
        for (int i = 0; i < (numberClusters); i++) {
            System.out.println("Cluster # " + (i + 1) + " ");
            for (int z = 0; z < inputData.length; z++) {

                if (dataVisited[z][1] == i) {

                    for (int k = 0; k < inputData[0].length; ++k) {
                        System.out.print(inputData[z][k] + " ");
                    }
                    System.out.println();

                }

            }
        }

        //Prints out all Noise tuples
        if (hasNoise)
        {
        System.out.println("Noise Tuples " + " ");
        for (int z = 0; z < inputData.length; z++) {

            if (dataVisited[z][1] == -1) {

                for (int k = 0; k < inputData[0].length; ++k) {
                    System.out.print(inputData[z][k] + " ");
                }
                System.out.println();

            }

        }
    }

    }
    
    public void generateClusters(double[][] data, double[] parameters){
        //Tunable Variables        
    //EPS value aka euclidean distance between datapoints
        double eps = parameters[0];  
    //Minimum number of points needed to form a Cluster
        int minPts = (int)parameters[1];
        
  
        inputData = data;
        numInputs = inputData.length;
        tupleSize = inputData[0].length;
        dataVisited = new int[numInputs][2];


        //Shuffle the data
        for (int i = 0; i < inputData.length; ++i) // shuffle indices
        {
            int r = new Random().nextInt(inputData.length - i) + i;
            double tmp[] = inputData[r];
            inputData[r] = inputData[i];
            inputData[i] = tmp;
        }

        //Initalize Visited Array    
        for (int i = 0; i < dataVisited.length; i++) {
            dataVisited[i][0] = 0;        //Not Visited
            dataVisited[i][1] = -2;       //Not in a cluster

        }

        DBScan(eps, minPts);

        //System.out.println("Total Number of Clusters:" + numberClusters);
        //Prints out all Cluster tuples
        for (int i = 0; i < (numberClusters); i++) {
            System.out.println("Cluster # " + (i + 1) + " ");
            for (int z = 0; z < inputData.length; z++) {

                if (dataVisited[z][1] == i) {

                    for (int k = 0; k < inputData[0].length; ++k) {
                        System.out.print(inputData[z][k] + " ");
                    }
                    System.out.println();

                }

            }
        }

        //Prints out all Noise tuples
        if (hasNoise)
        {
        System.out.println("Noise Tuples " + " ");
        for (int z = 0; z < inputData.length; z++) {

            if (dataVisited[z][1] == -1) {

                for (int k = 0; k < inputData[0].length; ++k) {
                    System.out.print(inputData[z][k] + " ");
                }
                System.out.println();

            }

        }
    }

	}  

    public static void DBScan(double eps, int minPts) {
        for (int i = 0; i < inputData.length; i++) {

            if (dataVisited[i][0] == 0) //Point has not been visited before
            {
                dataVisited[i][0] = 1; //Mark point as being Visited
                double[] pointP = new double[tupleSize];
                pointP = inputData[i];                  //Stores the point into an array
                ArrayList<Integer> neighborPts = new ArrayList<Integer>();
                neighborPts = getNeighborPts(eps, pointP);

                if (neighborPts.size() < minPts) {
                    hasNoise = true;
                    dataVisited[i][1] = -1;        //Mark point as noise
                } else //Contains enough points to create a cluster
                {
                    expandClusters(pointP, neighborPts, i, eps, minPts);

                    numberClusters++;
                }

            }

        }
    }

    public static ArrayList<Integer> getNeighborPts(double eps, double[] point) {
        //Compare the distance of the point with all points including itself 
        ArrayList<Integer> neighborPts = new ArrayList<Integer>();

        for (int i = 0; i < inputData.length; i++) {
            double distance = calculateDistance(point, inputData[i]);

            if (distance <= eps) //Point is close enough to be added as a neighbor
            {
                neighborPts.add(i); //Add the point as a potential neighbor.
            }
        }

        return neighborPts;
    }

    public static double calculateDistance(double[] point, double[] potentialNeighbor) {
        double sumOfSquares = 0;

        for (int i = 0; i < point.length; i++) {
            double sum = potentialNeighbor[i] - point[i];
            sum = Math.pow(sum, 2);
            sumOfSquares += sum;

        }

        double distance = Math.sqrt(sumOfSquares);

        return distance;
    }

    public static void expandClusters(double[] point, ArrayList<Integer> neighborPoints, int pLocation, double eps, int minPts) {
        dataVisited[pLocation][1] = numberClusters;         //Add point P to the cluster.

        for (int i = 0; i < neighborPoints.size(); i++) {
            int neighborPosition = neighborPoints.get(i);
            if (dataVisited[neighborPosition][0] == 0) //Neighbor has not been visited before
            {
                dataVisited[neighborPosition][0] = 1;       //Mark Neighbor as being visited.
                ArrayList<Integer> neighborsOfNeighborPts = new ArrayList<Integer>();
                double[] pointP = new double[tupleSize];
                pointP = inputData[neighborPosition];
                neighborsOfNeighborPts = getNeighborPts(eps, pointP);

                if (neighborPoints.size() >= minPts) {
                    neighborPoints.addAll(neighborsOfNeighborPts);
                }

            }

            if (dataVisited[neighborPosition][1] == -2) //Add neighbor to cluster if it isn't already in one
            {
                dataVisited[neighborPosition][1] = numberClusters;
            }

        }

    }

}
