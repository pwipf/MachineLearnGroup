//	Rosenbrock function approximation with MLP.
//  Phil Wipf
//  Brenden Burns
//  Rob Lewis
//
//  CS 4something
//  12 Oct 2015
//
//	This program sets up a multilayer neural net to approximate the Rosenbrock function with
//  and arbitrary number of input dimensions.  It uses an arbitrary number and size of hidden layers,
//  learning rate (eta) can be set, as well as momentum (mu, 0 for none up to 1 for no damping).
//  Also the type of activation, linear or sigmoid, can be set for each layer.

package nnBuilder;

import java.util.Random;
import static nnBuilder.MatMath.*;

public class RosenMLP{

	static Random gen=new Random();


	public static void main(String[] args) {
		int epochs;
		int n;
		int h;
		double eta,mu;
		int[]size;
		boolean[] act;
		try{
			n=Integer.parseInt(args[1]);
			h=Integer.parseInt(args[2]);
			size=new int[h+2];
			size[0]=n;
			size[h+1]=1;
			for(int i=0;i<h;i++)
				size[i+1]=Integer.parseInt(args[2+i]);
			epochs=Integer.parseInt(args[h+3]);
			eta=Double.parseDouble(args[h+4]);
			mu=Double.parseDouble(args[h+5]);

		}
		catch(Exception e){
			System.out.println("Usage: Arguments:\n(number of inputs), (number of hidden layers),"
							+ " [nodes in h1], [nodes in h2], ... (training epochs), (eta),"
							+ " (mu)\nUsing defaults");
			n=2;
			epochs=1000000;
			eta=.7;
			mu=.3;
			size=new int[]{n,10,1};
			h=size.length-2;
		}
		act=new boolean[h+2];
		for(int i=0;i<h+2;i++)
			act[i]=false;
		act[h+1]=true;

		Network net=new Network(size,act);

		int tests = epochs/10;
		System.out.println("training on Rosenbrock function of "+n+" variables...");
		System.out.println("expect approx 100 status (current error) updates...");

		double[][] x=new double[epochs][n];
		double[] y=new double[epochs];
		for(int i=0;i<epochs;i++){
			for(int j=0;j<n;j++)
				x[i][j]=rran();
			y[i]=Rosen(x[i]);
		}

		y=normalize(y);

		for(int i=0;i<epochs-tests;i++){
			//slowdown learn rate as epochs increase
			double slowedEta = eta * ((epochs-tests)-i/1.2)/((epochs-tests));
			net.train(x[i], vectorize(y[i]), slowedEta, mu, 1, (epochs-tests)/100);
		}


		System.out.println("testing...");
		double deNormPercError=0;
		double SSE=0;
		double yPrime;
		for(int i=epochs-tests;i<epochs;i++){
			yPrime=net.feedForward(x[i])[0];
			SSE += Network.cost(vectorize(yPrime), vectorize(y[i]))[0];
			//error=Network.cost(vectorize(yPrime), vectorize(y[i]))[0];
			deNormPercError+=Math.abs(unNormalize(yPrime)-unNormalize(y[i]))/Math.abs(unNormalize(y[i]));
			//System.out.println("\ny= "+y[i][0]+"\ny'="+yPrime[0]);
		}
		SSE/=tests;
		deNormPercError/=tests;
		System.out.println("Mean SSE (Sum Squared Error) for "+tests+" random tests (y normalized "
						+ "to (-1,+1) ): "+SSE);
		System.out.println("Mean percent error after de-normalizing y to original scale: "+
						Math.round(deNormPercError*10000)/100.0+"%");
	}//main

	// normalize y values to fall within range -1 to 1
	// if output layer is linear activation then the network can handle larger
	// range but works better normalized.
	static double max,min,offset;
	static double[] normalize(double[] y){
		max=Double.MIN_VALUE;
		min=Double.MAX_VALUE;
		for(int i=0;i<y.length;i++){
			max=Math.max(max,y[i]);
			min=Math.min(min,y[i]);
		}
		offset=(max-min)/2;
		for(int i=0;i<y.length;i++){
			y[i]-=max-offset;
			y[i]/=offset;
		}
		return y;
	}

	static double[] unNormalize(double[] y){
		for(int i=0;i<y.length;i++){
			y[i]*=offset;
			y[i]+=max-offset;
		}
		return y;
	}
	static double unNormalize(double y){
		y*=offset;
		y+=max-offset;
		return y;
	}

	static double rran(){
		double range = 1;
		double center=0;
		return gen.nextDouble()*range-range/2+center;
	}

	static double Rosen(double[] x){
		double y=0;

		for(int i=0;i<x.length-1;i++){
			y += ( Math.pow(1-x[i], 2) + 100*Math.pow(x[i+1] - Math.pow(x[i],2) ,2));
		}

		return y;
	}

}


// Network class.
// Has an array of inner class Layer objects, which each store the network state for that layer,
// including weight vector, velocity vector, bias value, and activation type for each node.
// Actually the activation type is for the layer. Could set it for each node but this would be
// tedious and seems very unhelpful to implement.
//
// For approximating a real valued function we find it necessary to use a linear activation on the
// output layer, to allow values outside the range (0,1), and sigmoid activations on all other
// layers to avoid the redundancy of linear nodes.
//
// The index scheme is: Layer object 0 is the input layer, so it is mostly ignored.
// The rest of the layers are the hidden layers with the last one being the output.
// The weights, etc. are indexed i'th node, j'th input.
//
class Network{
	int[] sizes;	// sizes[0]=number of input nodes, sizes[len-1]=number of ouput nodes
	int layers;		// sizes.length
	int nInputs, nOutputs;
	int counter=0;

  static Random gen=new Random();

	class Layer{
		double[][] w;				// weight
		double[][] v;				// velocity (for momentum)
		double[]   b;				// bias
		boolean    linAct;	// activation function (for layer)
		Layer(int n, int nFrom, boolean linAct){
			w=new double[n][nFrom];
			v=new double[n][nFrom];
			b=new double[n];
			this.linAct=linAct;
		}
	}

	// the Network, an array of Layers
	Layer[] net;

	// constructor
	// set size, activation functions. True is linear
	Network(int[] sizes, boolean[] LinearAct){
		this.sizes=sizes;
		layers=sizes.length;
		nInputs=sizes[0];
		nOutputs=sizes[sizes.length-1];

		net=new Layer[layers];
		for(int i=1;i<layers;i++)
			net[i]=new Layer(sizes[i],sizes[i-1],LinearAct[i]);
		System.out.print("Created ");
		for(int i=0;i<layers;i++)
			System.out.print(sizes[i]+"-");
		System.out.println("\b MLP network");
		// set initial random weights/biases. Also velocities set to 0.
		initializeWeightsBiases();
	}

	// initializeWeightsBiases()
	// compresses initial weights deviation from mean 0
	// to avoid early saturation.
	private void initializeWeightsBiases() {
		System.out.println("Setting initial random weights and biases");
		for (int l = 1; l < layers; l++) {
			for (int i = 0; i < sizes[l]; i++) {
				net[l].b[i] = random(0, 1);
				for (int j = 0; j < sizes[l - 1]; j++){
					net[l].w[i][j] = random(0, 1/Math.sqrt(sizes[l-1]));
					net[l].v[i][j] = 0; //velocity starts at zero.
				}
			}
		}
	}

	// random()
	// returns gaussian distr. with mean and sd
	double random(double mean, double sd){
		return gen.nextGaussian()*sd+mean;
	}

	// feedForward()
	// takes a vector of inputs, runs it through the network,
	// returns a vector of outputs.
	// (Had better be the same as the feedForward step in the backprop function below!)
	double[] feedForward(double[] input){
		double[] a = input;
		double[] z;
		for(int l=1;l<layers;l++){
			z=vecAdd(matMult(net[l].w,a), net[l].b);
			if(net[l].linAct)
				a=z;
			else
				a=sigmoid(z);
		}
		return a;
	}

	// backProp()
	// learning algorithm.
	// Implements "online" learning. We did not find batch updates useful,
	// probably because of the random nature of the training data.
	void backProp(double[] input, double[] y, double eta, double mu, int outputFreq){

		// store the activations, delta errors, and inputs (z) to each neruon
		// for use in the backpropogation step.
		double[][] a = new double[layers][];
		double[][] delta = new double[layers][];
		double[][] z = new double[layers][];

		a[0]=input; // activation of first layer is simply the input
		int last=layers-1;

		//feedForward, saving z's and a's
		for(int l=1;l<layers;l++){
			z[l]=vecAdd(matMult(net[l].w,a[l-1]), net[l].b);
			if(net[l].linAct)
				a[l]=z[l]; //linear final layer activation
			else
				a[l]=sigmoid(z[l]);
		}

		counter++;
		if(outputFreq !=0 && counter%outputFreq==0)
			System.out.println("Sample "+counter+" SSE: "+cost(a[last],y)[0]+"  (current eta: "+eta);
                        // OUTPUT TO FILE
		// remove, this was to make sure we are using the same feedforward for training as for testing.
		if(a[last][0] != feedForward(input)[0])
			throw new RuntimeException("something fishy "+a[last][0]+", "+feedForward(input)[0]);


		// get delta for last layer, to start the backpropogation
		if(net[last].linAct)
			delta[last] = costDeriv(a[last], y); // deriv of linear is 1
		else
			delta[last] = vElemMult(costDeriv(a[last], y), sigmoidDeriv(z[last]));

		//delta[last] = vecMult(delta[last],y[0]);

		// backprop!!!
		for(int l=layers-2;l>=1;l--){
			if(net[l].linAct)
				delta[l] = matMult( transpose(net[l+1].w), delta[l+1]); // deriv of linear is 1
			else
				delta[l] = vElemMult( matMult( transpose(net[l+1].w), delta[l+1]), sigmoidDeriv(z[l]));
		}


		// update weights, biases (gradient descent)
		for(int l=1;l<layers;l++){
			net[l].v = matSub(matMult(mu, net[l].v), matMult(eta, matMult(delta[l], a[l-1])));
			net[l].w = matAdd(net[l].w, net[l].v);
			net[l].b = vecSub(net[l].b, vecMult(eta, delta[l]));
		}
	}

	// train()
	// call with training data and parameters
	public void train(double[] input, double[] output, double eta, double mu, int repeats, int outputFreq){
		if(input.length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output.length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");

		for(int i=0;i<repeats;i++)
			backProp(input,output,eta,mu,outputFreq);
	}

	// vectorized helper functions
	static double[] cost(double[] a, double[] y){
		double[] c=new double[a.length];
		for(int i=0;i<a.length;i++)
			c[i]= Math.pow(a[i] - y[i],2)/2; //quadratic cost Derivative
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
}

// helper class MatMath
class MatMath {
	static public double[][] matMult(double[][] a, double[][] b){
		int m=a.length;
		int n=a[0].length;
		int p=b[0].length;
		double sum;
		double[][] c=new double[m][p];
		for(int i=0;i<m;i++)
			for(int j=0;j<p;j++){
				sum=0;
				for(int k=0;k<n;k++)
					sum+=a[i][k]*b[k][j];
				c[i][j]=sum;
			}
		return c;
	}

	static public double[] matMult(double[][] a, double[] b){
		int m=a.length;
		int n=a[0].length;
		double sum;
		double[] c=new double[m];
		for(int i=0;i<m;i++){
				sum=0;
				for(int k=0;k<n;k++)
					sum+=a[i][k]*b[k];
				c[i]=sum;
			}
		return c;
	}

	static public double[][] matMult(double[] a, double[] b){
		int m=a.length;
		int n=b.length;
		double[][]c = new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i]*b[j];
		return c;
	}

	static public double[] vectorize(double s){
		double[] m=new double[1];
		m[0]=s;
		return m;
	}

	static public double[] vecAdd(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]+w[i];
		return u;
	}

	static public double[] vecSub(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]-w[i];
		return u;
	}

	static public double[] vElemMult(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]*w[i];
		return u;
	}

	static public double[][] transpose(double[][]a){
		int m=a.length;
		int n=a[0].length;
		double[][]t=new double[n][m];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				t[j][i]=a[i][j];
		return t;
	}

	static public double[][] matAdd(double[][]a,double[][]b){
		int m=a.length;
		int n=a[0].length;
		double[][]c=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i][j]+b[i][j];
		return c;
	}

	static public double[][] matSub(double[][]a,double[][]b){
		int m=a.length;
		int n=a[0].length;
		double[][]c=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i][j]-b[i][j];
		return c;
	}

	static public double[] vecMult(double s,double[] v){
		double[] w=new double[v.length];
		for(int i=0;i<v.length;i++)
			w[i]=v[i]*s;
		return w;
	}

	static public double[][] matMult(double s,double[][] a){
		int m=a.length;
		int n=a[0].length;
		double[][] b=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				b[i][j]=a[i][j]*s;
		return b;
	}
}
