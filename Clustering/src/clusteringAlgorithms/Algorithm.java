/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringAlgorithms;

/**
 *
 * @author Brendan Burns
 */
public class Algorithm {
 
    
    
	public void generateClusters(double[][] inputData, double[] parameters){

	}
        
        // Say point inputData[5][] = {2, 231, 312, ....} is in cluster #2
        // assignedCluster[5] will = 2
        //
        public double calculateSumofSquaresError(double[][] inputData, int[] assignedCluster, int totalClusters)
        {
        //inefficient but should work
        double error = 0;    
        for (int i = 0; i<totalClusters; i++)                   //Iterating through the clusters
        {
        int clusterMeans[] = new int[inputData[0].length];    
        int clusterSize = 0;
        
            for (int z = 0; z<inputData.length; z++)           //Iterating through the tuples inputdata[z][]
            {
            if (assignedCluster[z] == i)
                {
                clusterSize++;
                
                //Getting the total values of the given cluster
                for (int y = 0; y<inputData[0].length; y++)     //Iterating through the points in a tuple inputData[][y]
                {
                clusterMeans[y] += inputData[z][y];
                }
                
                    
                    
                }
                
                
            }
          
            
            //clusterMeans should now contain the Total values for all tuple elements in a given cluster
            //Divide Total/Size to obtain the mean
            
            //Calculating the mean from (Total/Size)
            for (int y = 0; y<inputData[0].length; y++)
                {
                clusterMeans[y] = clusterMeans[y]/clusterSize;
                }
            
            
            
            //Now inefficently repeating the last steps to find the error, now that we have the cluster means.
            for (int z = 0; z<inputData.length; z++)           //Iterating through the tuples inputdata[z][]
            {
            if (assignedCluster[z] == i)
                {
                
                //Getting the total values of the given cluster
                for (int y = 0; y<inputData[0].length; y++)     //Iterating through the points in a tuple inputData[][y]
                {
                error = error + (inputData[z][y] - clusterMeans[y]);
                }
                
                    
                    
                }
                
                
            }
            
            
            }
            
            
            
           
            
            
        return error;    
        }
    
    
}
