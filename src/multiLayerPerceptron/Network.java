

package multiLayerPerceptron;

import java.util.Random;

import static multiLayerPerceptron.MatMath.*;

// Network class.
//
// The index scheme is: Layer object 0 is the input layer, so it is mostly ignored.
// The rest of the layers are the hidden layers with the last one being the output.
// The weights, etc. are indexed i'th node, j'th input.
//
class Network{
	int[] sizes;	// sizes[0]=number of input nodes, sizes[len-1]=number of ouput nodes
	int layers;		// sizes.length
	int nInputs, nOutputs;

	double[][][] w;
	double[][]   b;

  static Random gen=new Random();

	public void train(double[][] input, double[][] output, double[] parameters){

	}

	// constructor
	// set size, activation functions. True is linear
	Network(int[] sizes){
		this.sizes=sizes;
		layers=sizes.length;
		nInputs=sizes[0];
		nOutputs=sizes[sizes.length-1];

		w=new double[layers][][];
		b=new double[layers][];
		for(int l=1;l<layers;l++){
			w[l]=new double[sizes[l]][sizes[l-1]];
			b[l]=new double[sizes[l]];
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					w[l][i][j]=gen.nextGaussian();
				}
				b[l][i]=gen.nextGaussian();
			}
		}
	}

	// feedForward()
	// takes a vector of inputs, runs it through the network,
	// returns a vector of outputs.
	double[] feedForward(double[] input){
		double[] a = input;
		for(int l=1;l<layers;l++){
			a=sigmoid(vecAdd(matMult(w[l],a), b[l]));
		}
		return a;
	}


	// helper functions
	static double ssCost(double[] a, double[] y){
		double c=0;

		for(int i=0;i<a.length;i++)
			c += Math.pow(a[i] - y[i],2)/2;
		return c;
	}

	static double[] costDeriv(double[] a, double[] y){
		double[] c=new double[a.length];
		for(int i=0;i<a.length;i++)
			c[i]= a[i] - y[i]; //quadratic cost Derivative
		return c;
	}

	static double[] sigmoid(double[] x){
		double[] y=new double[x.length];
		for(int i=0;i<x.length;i++)
			y[i]=1/(1+Math.exp(-x[i]));
		return y;
	}

	static double[] sigmoidDeriv(double[] x){
		double[] y=new double[x.length];
		for(int i=0;i<x.length;i++){
			y[i]= 1/(1+Math.exp(-x[i]));
		  y[i]= y[i]*(1-y[i]);
		}
		return y;
	}

	// shuffle()
	// simple inefficient array shuffler,
	// shuffles the first n records of two arrays, a and b, preserving the association between a and b
	static void shuffle(double[][] a, double[][] b, int n){
		for(int i=0;i<n;i++){
			int j=gen.nextInt(n);
			double[] temp=a[i];
			a[i]=a[j];
			a[j]=temp;

			temp=b[i];
			b[i]=b[j];
			b[j]=temp;
		}
	}
}
