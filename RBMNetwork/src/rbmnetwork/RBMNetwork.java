/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbmnetwork;

import java.util.Scanner;

/**
 *
 * @author Brendan Burns
 */
public class RBMNetwork {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      System.out.println("\nBegin Radial Basis Function (RBF) network input-output demo\n");
      
      
    Scanner in = new Scanner(System.in);
    System.out.printf("How many inputs values will you be using?  >");
    int numInput = in.nextInt();
    System.out.printf("%n");
    System.out.printf("How many Gaussian basis functions will you be using?  >");
    int numGaussian = in.nextInt();
    System.out.printf("%n");  
    System.out.printf("How many Outputs will you have?  >");
    int numOutput = in.nextInt();
    System.out.printf("%n");  
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      

      //int numInput = 3;
      //int numGaussian = 4;
      //int numOutput = 2;
    
      //4 Parameters for a RBM Network
      //1-Centriods (means) for every node with the same number of inputs
      //2-Standard Deviation (RBF widths). 1 SD for every Gaussian function
      //3- Weights, number dependant on numGaussian functions * number of outputs
      //4- Bias values, number is equal to the number of outputs

    
    
      //Initializing RBM
      System.out.println("\nCreating a " + numInput + "-" + numGaussian + "-" + numOutput + " radial net");
      RadialNet rn = new RadialNet(numInput, numGaussian, numOutput);

      
      
      //Creating Centriods
      double[][] centroids = new double[numGaussian][numInput];             //mean for each Gaussian basis function
      //centroids[0] = new double[] { -3.0, -3.5, -3.8 };
      //centroids[1] = new double[] { -1.0, -1.5, -1.8 };
      //centroids[2] = new double[] { 2.0, 2.5, 2.8 };
      //centroids[3] = new double[] { 4.0, 4.5, 4.8 };
      
      
      
      for (int i = 0; i < numGaussian; ++i)
      {
        for (int j = 0; j < numInput; ++j)    
        {   
        centroids[i][j] = ((Math.random()*20)-10);            //filling it out with random numbers for the time being -10 to 10   
        }
          
      }
      
      
      System.out.println("\nSetting centroids (means) to:");
      Helpers.ShowMatrix(centroids, -1);
      System.out.println("Loading centroids into radial net");
      rn.SetCentroids(centroids);

      
      //Creating Standard Deviations
      //double[] stdDevs = new double[] { 2.22, 3.33, 4.44, 5.55 };
      double[] stdDevs = new double[numGaussian];
            for (int i = 0; i < stdDevs.length; ++i)
      {
            stdDevs[i] = (Math.random()*6)+2;            //filling with random numbers for time being.
          
      }
      
      
      
      
      
      System.out.println("\nSetting standard deviations (widths) to:");
      Helpers.ShowVector(stdDevs, 2, 4, true);
      System.out.println("Loading standard deviations into radial net");
      rn.SetStdDevs(stdDevs);

      
      
      //Creating Weights
      double[][] hoWeights = new double[numGaussian][numOutput];
      
        for (int i = 0; i < numGaussian; ++i)
      {
        for (int j = 0; j < numOutput; ++j)    
        {
        hoWeights[i][j] = ((Math.random()*20)-10);            //filling it out with random numbers for the time being -10 to 10   
        }
          
     }
      
      
      
      
      
      //hoWeights[0] = new double[] { 5.0, -5.1 };
      //hoWeights[1] = new double[] { -5.2, 5.3 };
      //hoWeights[2] = new double[] { -5.4, 5.5 };
      //hoWeights[3] = new double[] { 5.6, -5.7 };
      
      //hoWeights[0][0] = -5.22;
      //hoWeights[0][1] = -5.354;
      
      System.out.println("\nSetting hidden-output weights to:");
      Helpers.ShowMatrix(hoWeights, -1);
      System.out.println("Loading hidden-output weights into radial net");
      rn.SetWeights(hoWeights);

      
      
      
      //Creating Biases
      //double[] oBiases = new double[] { 7.0, 7.1 };
      double[] oBiases = new double[numOutput];
            for (int i = 0; i < numOutput; ++i)
      {
            oBiases[i] = (Math.random()+7);            //filling with random numbers for time being.
          
      }      
      
      
      
      
      System.out.println("\nSetting output biases to:");
      Helpers.ShowVector(oBiases, 1, 4, true);
      System.out.println("Loading output biases into radial net");
      rn.SetBiases(oBiases);
      
      
      
      
      //Input Values

      //double[] xValues = new double[] { 1.0, -2.0, 3.0 };
      
      double[] xValues = new double[numInput];
            for (int i = 0; i < numInput; ++i)
      {
            xValues[i] = (Math.random()*10)-5;            //filling with random numbers for time being.
          
      }   
      
      
      
      System.out.println("\nSetting x-input to:");
      Helpers.ShowVector(xValues, 1, 4, true);

      System.out.println("\nComputing the output of the radial net\n");
      double[] yValues = rn.ComputeOutputs(xValues);

      System.out.println("\nThe output of the RBF network is:");
      Helpers.ShowVector(yValues, 4, 4, true);

      System.out.println("\nEnd RBF network demo\n");
      //Console.ReadLine();
    } // Main

  } // Program





 // ns

