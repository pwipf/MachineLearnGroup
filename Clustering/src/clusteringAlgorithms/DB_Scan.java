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

     double inputData[][];
//inputData[numInputs][tupleSize]
//For each tuple [][0......tupleSize] Values dependant on the dataset, and the number of attributes.    
     int numInputs;
     int tupleSize;
     int dataVisited[][];
//dataVisited[numInputs][2]
//For each tuple, [][0] = If the Node is visited. 0 = False, 1 = True
//                [][1] = is the cluster it belongs to -2 = "Not in a Cluster" -1 = "Noise"
     int numberClusters = 0;

    //The number of clusters will increase as the algorithm runs. 
    //The 0 is just the starting position
     boolean hasNoise = false; //Simple check to remove a redundant println in cases where no noise exists.
//    static int eps;
//    static int minPts;
    //static ArrayList<Integer> neighborPts = new ArrayList<Integer>();


    /**
     * @param args the command line arguments
     */
    
    public void generateClusters(double[][] data, double[] parameters){
        //Tunable Variables        
    //EPS value aka euclidean distance between datapoints
        double eps = parameters[0];  
    //Minimum number of points needed to form a Cluster
        int minPts = (int)parameters[1];
        System.out.println("Minimum number of points " + minPts);
  
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
        int clusterPosition[] = new int[inputData.length];
        
        for (int i = 0; i<numInputs; i++)
        {
        clusterPosition[i] = dataVisited[i][1];    
        }
        
        
        double error =calculateSumofSquaresError(inputData, clusterPosition, numberClusters);
        System.out.println("Total Error is " + error);
	}  

    public void DBScan(double eps, int minPts) {
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

    public ArrayList<Integer> getNeighborPts(double eps, double[] point) {
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

    public void expandClusters(double[] point, ArrayList<Integer> neighborPoints, int pLocation, double eps, int minPts) {
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
