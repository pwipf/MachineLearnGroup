/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbmnetwork;

/**
 *
 * @author Brendan Burns
 */
  public class RadialNet
  {
    private int numInput;
    private int numHidden;
    private int numOutput;

    private double[] inputs;
    private double[][] centroids; // aka means
    private double[] stdDevs; // aka widths
    
    private double[][] hoWeights;
    private double[] oBiases;
    private double[] outputs;

    // ---------------------------------------------------------

    RadialNet(int numInput, int numHidden, int numOutput)
    {
      this.numInput = numInput;
      this.numHidden = numHidden;
      this.numOutput = numOutput;

      this.inputs = new double[numInput];
      this.centroids = MakeMatrix(numHidden, numInput);
      this.stdDevs = new double[numHidden];

      this.hoWeights = MakeMatrix(numHidden, numOutput);
      this.oBiases = new double[numOutput];
      this.outputs = new double[numOutput];
    } // ctor

    private static double[][] MakeMatrix(int rows, int cols)
    {
      double[][] result = new double[rows][];
      for (int r = 0; r < rows; ++r)
        result[r] = new double[cols];
      return result;
    }



    // ---------------------------------------------------------

    public void SetCentroids(double[][] centroids)
    {
      if (centroids.length != numHidden)
        System.out.println("Bad number of centroids");
      if (centroids[0].length != numInput)
        System.out.println("Bad centroid size");

      for (int i = 0; i < numHidden; ++i)
        for (int j = 0; j < numInput; ++j)
          this.centroids[i][j] = centroids[i][j]; 
    }

    public void SetStdDevs(double[] stdDevs)
    {
      if (stdDevs.length != numHidden)
        System.out.println("Bad number of stdDevs");
      this.stdDevs = stdDevs;
    }

    public void SetWeights(double[][] hoWeights)
    {
      if (hoWeights.length != numHidden)
        System.out.println("Bad number of weights");
      if (hoWeights[0].length != numOutput)
        System.out.println("Bad number of weights");
      for (int i = 0; i < numHidden; ++i)
        for (int j = 0; j < numOutput; ++j)
          this.hoWeights[i][j] = hoWeights[i][j]; 
    }

    public void SetBiases(double[] oBiases)
    {
      if (oBiases.length != numOutput)
        System.out.println("Bad number of hoBiases");
      this.oBiases = oBiases;
    }

    // ---------------------------------------------------------

    public double[] ComputeOutputs(double[] xValues)
    {

      inputs = xValues;

      double[] hOutputs = new double[numHidden]; // hidden node outputs
      for (int j = 0; j < numHidden; ++j) // each hidden node
      {
        double d = Distance(inputs, centroids[j]); // could use a 'distSquared' approach
        System.out.println("\nHidden[" + j + "] distance = " + d);
        double r = -1.0 * (d * d) / (2 * stdDevs[j] * stdDevs[j]);
        double g = Math.exp(r);
        System.out.println("Hidden[" + j + "] output = " + g);
        hOutputs[j] = g;
      }

      for (int k = 0; k < numOutput; ++k)
        outputs[k] = 0.0;

      for (int k = 0; k < numOutput; ++k)
        for (int j = 0; j < numHidden; ++j)
          outputs[k] += (hOutputs[j] * hoWeights[j][k]);

      for (int k = 0; k < numOutput; ++k)
        outputs[k] += oBiases[k];

      double[] result = new double[numOutput];
      result = outputs;
      return result;
    }

    public static double Distance(double[] x, double[] c)
    {
      // distance between x vector and centroid
      double sum = 0.0;
      for (int i = 0; i < x.length; ++i)
        sum += (x[i] - c[i]) * (x[i] - c[i]);
      return Math.sqrt(sum);
    }

    // ---------------------------------------------------------

  } // RadialNet

  // ===========================================================================
