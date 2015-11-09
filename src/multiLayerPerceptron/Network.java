

package multiLayerPerceptron;

import java.util.Random;

import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

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

	double scale;

	double[][][] w;
	double[][][] v;
	double[][]   b;

  static Random gen=new Random();

	// constructor
	// set size, activation functions. True is linear
	Network(int[] sizes, double scale){
		this.sizes=sizes;
		layers=sizes.length;
		nInputs=sizes[0];
		nOutputs=sizes[sizes.length-1];

		this.scale=scale;

		w=new double[layers][][];
		v=new double[layers][][];
		b=new double[layers][];
		for(int l=1;l<layers;l++){
			w[l]=new double[sizes[l]][sizes[l-1]];
			v[l]=new double[sizes[l]][sizes[l-1]];
			b[l]=new double[sizes[l]];
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					w[l][i][j]=gen.nextGaussian();
					v[l][i][j]=0;
				}
				b[l][i]=gen.nextGaussian();
			}
		}
	}

	// feedForward()
	// takes a vector of inputs, runs it through the network,
	// returns a vector of outputs.
	// (Had better be the same as the feedForward step in the backprop function below!)
	double[] feedForward(double[] input){
		double[] a = input;
		for(int l=1;l<layers;l++){
			a=sigmoid(vecAdd(matMult(w[l],a), b[l]));
		}
		return a;
	}


	// Backpropogation Trainer
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// trainDiffEv()
	// call with training data and parameters
	public void trainBackprop(double[][] input, double[][] output, int epochs, double eta, double mu){
		int size=input.length;
		if(size==0 || size != output.length)
			throw new RuntimeException("Input number != Output number (or zero)");
		if(input[0].length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output[0].length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");


		for(int e=0;e<epochs;e++){
			shuffle(input,output,size);
			double error=0;
			for(int i=0;i<size;i++){
				backProp(input[i],output[i],eta,mu);
				error+=ssCost(feedForward(input[i]),output[i]);
			}

			if(scale==0)//for graph
				scale=error/size;
			//System.out.println(error/trains);
			point(map(e,0,epochs,.05,1),map(error/size,0,scale,.05,1));//graph
		}
	}

	// backProp()
	// learning algorithm.
	// Runs one "mini-batch", accumulates weight and bias adjustments,  then makes updates.
	void backProp(double[] input, double[] y, double eta, double mu){

		// store the activations, delta errors, and inputs (z) to each neruon
		// for use in the backpropogation step.
		double[][] a = new double[layers][];
		double[][] delta = new double[layers][];
		double[][] z = new double[layers][]; // z is the vector to store "what went into the activation function"

		a[0]=input; // activation of first layer is simply the input
		int last=layers-1;

		//feedForward, saving z's and a's
		for(int l=1;l<layers;l++){
			z[l]=vecAdd(matMult(w[l],a[l-1]), b[l]);
			a[l]=sigmoid(z[l]);
		}

		// get cost vector at output, to start the backpropogation
		double[] cost=costDeriv(a[last], y);

		delta[last] = vElemMult(cost, sigmoidDeriv(z[last]));


		// backprop!!!
		for(int l=layers-2;l>=1;l--){
			delta[l] = vElemMult( matMult( transpose(w[l+1]), delta[l+1]), sigmoidDeriv(z[l]));
		}

		// update weights, biases (gradient descent)
		for(int l=1;l<layers;l++){
			v[l] = matSub(matMult(mu, v[l]), matMult(eta,matMult(delta[l],a[l-1])));
			w[l] = matAdd(w[l], v[l]);
			//w[l] = matSub(w[l], matMult(eta,matMult(delta[l],a[l-1])));
			b[l] = vecSub(b[l], vecMult(eta, delta[l]));
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


	// Differential Evolution Trainer
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// trainDiffEv()
	// call with training data and parameters
	public void trainDiffEv(double[][] input, double[][] output, int maxGen, int npop, double beta, double rho){
		int size=input.length;
		if(size==0 || size != output.length)
			throw new RuntimeException("Input number != Output number (or zero)");
		if(input[0].length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output[0].length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");


		//initialize population. Each member is a bunch of weights and biases.
		Member[] population=new Member[npop];
		for(int i=0;i<npop;i++){
			population[i]=new Member();
			population[i].initRandom();
		}

		//////////////////////
		// run DE algorithm
		int generation=0;
		while(generation<maxGen){
			generation++;
			for(int i=0;i<npop;i++){
				Member m=population[i];
				Member m2,m3;
				do{
					m2= population[gen.nextInt(npop)];
				}while(m2==m);
				do{
					m3=population[gen.nextInt(npop)];
				}while(m3==m2 || m3==m);

				Member trial=mutate(m,m2,m3,beta);

				Member offspring=crossover(m,trial,rho);

				double fo=fitness(offspring,input,output);
				double fm=fitness(m,input,output);
				if(fo < fm){ //minimize fitness as it is just the error
					population[i]=offspring;
					fm=fo; //fm always has the lesser value, for the graph below
				}

				point(map(generation,0,maxGen,.05,1),map(fm,0,1,.05,1));
				//System.out.println(fm);
			}
		}

		// had better set the network weights
		int best=0;
		double fit=fitness(population[best],input,output);
		for(int i=1;i<npop;i++){
			double fit2=fitness(population[i],input,output);
			if(fit2<fit){
				best=i;
				fit=fit2;
			}
		}
		//set network weights to population[best]
		for(int l=1;l<layers;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					w[l][i][j]=population[best].w[l][i][j];
				}
				b[l][i]=population[best].b[l][i];
			}
		}
	}

	class Member{
		double[][][] w;
		double[][]   b;


		Member(){
			w=new double[sizes.length][][];
			b=new double[sizes.length][];
			for(int l=1;l<sizes.length;l++){
				w[l]=new double[sizes[l]][sizes[l-1]];
				b[l]=new double[sizes[l]];
			}
		}

		void initRandom(){
			for(int l=1;l<sizes.length;l++){
				for(int i=0;i<sizes[l];i++){
					for(int j=0;j<sizes[l-1];j++){
						w[l][i][j]=gen.nextGaussian();
					}
					b[l][i]=gen.nextGaussian();
				}
			}
		}
	}

	double fitness(Member m, double[][] input, double[][] output){
		int size=input.length;
		double error=0;
		for(int i=0;i<size;i++){
			double[] netOutput=feedForward(m,input[i]);
			error += ssCost(netOutput,output[i]);
		}
		error/=size;
		return error;
	}

	double[] feedForward(Member m, double[] input){
		double[] a = input;
		for(int l=1;l<layers;l++){
			a=sigmoid(vecAdd(matMult(m.w[l],a), m.b[l]));
		}
		return a;
	}

	Member mutate(Member m1, Member m2, Member m3, double beta){
		Member temp= new Member();
		for(int l=1;l<sizes.length;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					temp.w[l][i][j]=m1.w[l][i][j] + beta*(m2.w[l][i][j]-m3.w[l][i][j]);
				}
				temp.b[l][i]=m1.b[l][i] + beta*(m2.b[l][i]-m3.b[l][i]);
			}
		}
		return temp;
	}

	Member crossover(Member parent, Member milkman, double rho){
		Member temp=new Member();
		for(int l=1;l<sizes.length;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					temp.w[l][i][j]=(gen.nextDouble()<rho)? parent.w[l][i][j] : milkman.w[l][i][j];
				}
				temp.b[l][i]=(gen.nextDouble()<rho)? parent.b[l][i] : milkman.b[l][i];
			}
		}
		return temp;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////

	// vectorized helper functions
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

	double getScale(){return scale;}
}
