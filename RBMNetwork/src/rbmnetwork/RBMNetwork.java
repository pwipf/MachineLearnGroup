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
      
      //30 data entries, 7 inputs
      //
      int numInputTuples = 30;
      double[][] inputData = new double[numInputTuples][7];
      
      
      
      
      inputData[0] = new double[] { -0.784, 1.255, -1.332, -1.306, 0, 0, 1 };
      inputData[1] = new double[] { -0.995, -0.109, -1.332, -1.306, 0, 0, 1 };
      inputData[2] = new double[] { -1.206, 0.436, -1.386, -1.306, 0, 0, 1 };
      inputData[3] = new double[] { -1.312, 0.164, -1.278, -1.306, 0, 0, 1 };
      inputData[4] = new double[] { -0.890, 1.528, -1.332, -1.306, 0, 0, 1 };
      inputData[5] = new double[] { -0.468, 2.346, -1.170, -1.048, 0, 0, 1 };
      inputData[6] = new double[] { -1.312, 0.982, -1.332, -1.177, 0, 0, 1 };
      inputData[7] = new double[] { -0.890, 0.982, -1.278, -1.306, 0, 0, 1 };
      inputData[8] = new double[] { -1.523, -0.382, -1.332, -1.306, 0, 0, 1 };
      inputData[9] = new double[] { -0.995, 0.164, -1.278, -1.435, 0, 0, 1 };

      inputData[10] = new double[] { 1.220, 0.436, 0.452, 0.241, 0, 1, 0 };
      inputData[11] = new double[] { 0.587, 0.436, 0.344, 0.370, 0, 1, 0 };
      inputData[12] = new double[] { 1.115, 0.164, 0.560, 0.370, 0, 1, 0 };
      inputData[13] = new double[] { -0.362, -2.019, 0.074, 0.112, 0, 1, 0 };
      inputData[14] = new double[] { 0.693, -0.655, 0.398, 0.370, 0, 1, 0 };
      inputData[15] = new double[] { -0.151, -0.655, 0.344, 0.112, 0, 1, 0 };
      inputData[16] = new double[] { 0.482, 0.709, 0.452, 0.498, 0, 1, 0 };
      inputData[17] = new double[] { -0.995, -1.746, -0.305, -0.275, 0, 1, 0 };
      inputData[18] = new double[] { 0.798, -0.382, 0.398, 0.112, 0, 1, 0 };
      inputData[19] = new double[] { -0.679, -0.927, 0.020, 0.241, 0, 1, 0 };

      inputData[20] = new double[] { 0.482, 0.709, 1.155, 1.659, 1, 0, 0 };
      inputData[21] = new double[] { -0.046, -0.927, 0.669, 0.885, 1, 0, 0 };
      inputData[22] = new double[] { 1.326, -0.109, 1.101, 1.143, 1, 0, 0 };
      inputData[23] = new double[] { 0.482, -0.382, 0.939, 0.756, 1, 0, 0 };
      inputData[24] = new double[] { 0.693, -0.109, 1.047, 1.272, 1, 0, 0 };
      inputData[25] = new double[] { 1.853, -0.109, 1.479, 1.143, 1, 0, 0 };
      inputData[26] = new double[] { -0.995, -1.473, 0.344, 0.627, 1, 0, 0 };
      inputData[27] = new double[] { 1.537, -0.382, 1.317, 0.756, 1, 0, 0 };
      inputData[28] = new double[] { 0.904, -1.473, 1.047, 0.756, 1, 0, 0 };
      inputData[29] = new double[] { 1.431, 1.528, 1.209, 1.659, 1, 0, 0 };
    

      
      
      //Splitting the arrays to 80-20
      //80% will be training data
      //20% will be testing data
      
      //number of tuples used for training the RBM network
      //Assumes inputs are randomized
      //Otherwise there may be issues.
      
      int numTraining = (int) (numInputTuples * .8);
      double[][] trainingData = new double[numTraining][numInput];
      //Remaining tuples for testing the RBM network
      int numTesting = numInputTuples - numTraining;
      double[][] testingData = new double[numTesting][numInput];
      
      System.arraycopy(inputData, 0, trainingData, 0, numTraining);
      System.arraycopy(inputData, numTraining, testingData, 0, numTesting);
      
      System.out.println("Printing training data");
      Helpers.ShowMatrix(trainingData, -1);
      
      System.out.println("Printing testing data");
      Helpers.ShowMatrix(testingData, -1);      
      
      
      
      
      
      

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
      //Given input to be tested.
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

