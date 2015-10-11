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
    
        public double[][] DoCentroids(double[][] trainData)
    {
      // centroids are representative inputs that are relatively different
      // compute centroids using the x-vaue of training data
      // store into this.centroids
      int numAttempts = trainData.length;
      int[] goodIndices = new int[numHidden];  // need one centroid for each hidden node
      double maxAvgDistance = Double.MIN_VALUE; // largest average distance for a set of candidate indices
      for (int i = 0; i < numAttempts; ++i)
      {
        int[] randomIndices = DistinctIndices(numHidden, trainData.length); // candidate indices
        double sumDists = 0.0; // sum of distances between adjacent candidates (not all candiates)
        for (int j = 0; j < randomIndices.length - 1; ++j) // adjacent pairs only
        {
          int firstIndex = randomIndices[j];
          int secondIndex = randomIndices[j + 1];
          sumDists += AvgAbsDist(trainData[firstIndex], trainData[secondIndex], numInput); // just the input terms
        }

        double estAvgDist = sumDists / numInput; // estimated average distance for curr candidates
        if (estAvgDist > maxAvgDistance) // curr candidates are far apart
        {
          maxAvgDistance = estAvgDist;
          goodIndices = randomIndices;
        }
      } // now try a new set of candidates

      System.out.println("The indices (into training data) of the centroids are:");
      Helpers.ShowVector(goodIndices, goodIndices.length, true);

      // store copies of x-vales of data pointed to by good indices into this.centroids
      for (int i = 0; i < numHidden; ++i)
      {
        int idx = goodIndices[i]; // idx points to trainData
        for (int j = 0; j < numInput; ++j)
        {
          this.centroids[i][j] = trainData[idx][j]; // make a copy of values
        }
      }
      
      SetCentroids(this.centroids);
      return(this.centroids);
    } // DoCentroids
        
        
        private static double AvgAbsDist(double[] v1, double[] v2, int numTerms)
    {
      // average absolute difference distance between two vectors, first numTerms only
      // helper for computing centroids
      if (v1.length != v2.length)
        System.out.println("Vector lengths not equal in AvgAbsDist()");
      double sum = 0.0;
      for (int i = 0; i < numTerms; ++i)
      {
        double delta = Math.abs(v1[i] - v2[i]);
        sum += delta;
      }
      return sum / numTerms;
    }

    private int[] DistinctIndices(int n, int range)
    {
      // helper for ComputeCentroids()
      // generate n distinct numbers in [0, range-1] using reservoir sampling
      // assumes rnd exists
      int[] result = new int[n];
      for (int i = 0; i < n; ++i)
        result[i] = i;

      for (int t = n; t < range; ++t)
      {
        int m = (int) Math.random() * (t + 1);
        if (m < n) result[m] = t;
      }
      return result;
    }
    
    
    
        public double[] DoWidths(double[][] centroids)
    {
      // compute widths based on centroids, store into this.widths
      // note the centroids parameter could be omitted - the intent is to make relationship clear
      // this version uses a common width which is the average dist between all centroids
      double sumOfDists = 0.0;
      int ct = 0; // could calculate number pairs instead
      for (int i = 0; i < centroids.length - 1; ++i)
      {
        for (int j = i + 1; j < centroids.length; ++j)
        {
          double dist = EuclideanDist(centroids[i], centroids[j], centroids[i].length);
          sumOfDists += dist;
          ++ct;
        }
      }
      double avgDist = sumOfDists / ct;
      double width = avgDist;

      System.out.println("The common width is: " + width);

      for (int i = 0; i < this.stdDevs.length; ++i) // all widths the same
        this.stdDevs[i] = width;
      
      return this.stdDevs;
    }
        
        private static double EuclideanDist(double[] v1, double[] v2, int numTerms)
    {
      // Euclidean distance between two vectors, first numTerms only
      // helper for computing RBF outputs and computing hidden node widths
      if (v1.length != v2.length)
        System.out.println("Vector lengths not equal in EuclideanDist()");
      double sum = 0.0;
      for (int i = 0; i < numTerms; ++i)
      {
        double delta = (v1[i] - v2[i]) * (v1[i] - v2[i]);
        sum += delta;
      }
      return Math.sqrt(sum);
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
