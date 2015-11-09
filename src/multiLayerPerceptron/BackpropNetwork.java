/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiLayerPerceptron;

import java.util.Random;

import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

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
class BackpropNetwork{
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
	BackpropNetwork(int[] sizes, boolean[] LinearAct){
		this.sizes=sizes;
		layers=sizes.length;
		nInputs=sizes[0];
		nOutputs=sizes[sizes.length-1];

		net=new Layer[layers];
		for(int i=1;i<layers;i++)
			net[i]=new Layer(sizes[i],sizes[i-1],LinearAct[i]);

		// set initial random weights/biases. Also velocities set to 0.
		initializeWeightsBiases();


	}

	// initializeWeightsBiases()
	// compresses initial weights deviation from mean 0
	// to avoid early saturation.
	private void initializeWeightsBiases() {
		for (int l = 1; l < layers; l++) {
			for (int i = 0; i < sizes[l]; i++) {
				net[l].b[i] = random(0, 1);
				for (int j = 0; j < sizes[l - 1]; j++){
					net[l].w[i][j] = random(0, 1);// /Math.sqrt(sizes[l-1]));
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
	// Runs one "mini-batch", accumulates weight and bias adjustments,  then makes updates.
	void backProp(double[][] input, double[][] y, int batchStart, int batchEnd, double eta, double mu){

		// store the activations, delta errors, and inputs (z) to each neruon
		// for use in the backpropogation step.
		double[][] a = new double[layers][];
		double[][] delta = new double[layers][];
		double[][] z = new double[layers][]; // z is the vector to store "what went into the activation function"

		double[][][] deltaW=new double[layers][][]; // array to store accumulated weight updates
		double[][] deltaB=new double[layers][];			// array to store accumulated bias updates
		for(int i=1;i<layers;i++){//initialize
			deltaW[i]=new double[net[i].w.length][net[i].w[0].length];
			deltaB[i]=new double[net[i].b.length];
		}

		//backprop a mini-batch and accumulate the update for weights and biases
		//System.out.println("start mini-batch");
		for(int i=batchStart;i<batchEnd;i++){
			a[0]=input[i]; // activation of first layer is simply the input
			int last=layers-1;

			//feedForward, saving z's and a's
			for(int l=1;l<layers;l++){
				z[l]=vecAdd(matMult(net[l].w,a[l-1]), net[l].b);
				if(net[l].linAct)
					a[l]=z[l]; //linear final layer activation
				else
					a[l]=sigmoid(z[l]);
			}


			// remove, this was to make sure we are using the same feedforward for training as for testing.
			if(a[last][0] != feedForward(input[i])[0])
				throw new RuntimeException("something fishy "+a[last][0]+", "+feedForward(input[i])[0]);


			// get cost vector at output, to start the backpropogation
			double[] cost=costDeriv(a[last], y[i]);
			//System.out.println("a: "+a[last][0]+" should be "+y[i][0]+" SSE: "+ssCost(a[last],y[i])[0]);

			if(net[last].linAct)
				delta[last] = cost; // deriv of linear is 1
			else
				delta[last] = vElemMult(cost, sigmoidDeriv(z[last]));


			//delta[last] = vecMult(delta[last],y[0]);

			// backprop!!!
			for(int l=layers-2;l>=1;l--){
				if(net[l].linAct)
					delta[l] = matMult( transpose(net[l+1].w), delta[l+1]); // deriv of linear is 1
				else
					delta[l] = vElemMult( matMult( transpose(net[l+1].w), delta[l+1]), sigmoidDeriv(z[l]));
			}

			for(int l=1;l<layers;l++){
				deltaW[l] =matAdd(deltaW[l], matMult(delta[l],a[l-1])); // add up the adjustment to each weight/bias
				deltaB[l] =vecAdd(deltaB[l], delta[l]);
			}
		} // end mini-batch

		// update weights, biases (gradient descent)
		double avg=eta/(batchEnd-batchStart);
		for(int l=1;l<layers;l++){

			net[l].v = matSub(matMult(mu, net[l].v), matMult(avg,deltaW[l]));
			net[l].w = matAdd(net[l].w, net[l].v);
			//net[l].w = matSub(net[l].w, matMult(avg,deltaW[l]));
			net[l].b = vecSub(net[l].b, vecMult(avg, delta[l]));
		}
	}

	// train()
	// call with training data and parameters
	public void train(double[][] input, double[][] output, int epochs, double eta, double mu){
		int size=input.length;
		if(size==0 || size != output.length)
			throw new RuntimeException("Input number != Output number (or zero)");
		if(input[0].length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output[0].length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");


		for(int e=0;e<epochs;e++){
			//System.out.print(e+" ");
			shuffle(input,output,size);
			double error=0;
			for(int i=0;i<size;i++){
				backProp(input,output,i,i+1,eta,mu);
				double temp=ssCost(feedForward(input[i]),output[i])[0];
				error+=temp;
			}

			//System.out.println(error/trains);
			point(map(e,0,epochs,.05,1),map(error/size,0,.01,.05,1));
		}
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

	// helpful map function (from arduino library)
	double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	// vectorized helper functions
	static double[]  ssCost(double[] a, double[] y){
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
