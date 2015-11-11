
package multiLayerPerceptron;

import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

public class BackpropNetwork extends Network{

	static double scale=0;

	double[][][] v; //velocity for using momentum

	BackpropNetwork(int[] sizes){
		super(sizes);

		v=new double[layers][][]; // just need to add the velocity initalization
		for(int l=1;l<layers;l++){
			v[l]=new double[sizes[l]][sizes[l-1]];
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					v[l][i][j]=0;
				}
			}
		}
	}

	@Override
	public void train(double[][] input, double[][] output, double[] parameters){
		int epochs=(int)parameters[0];
		double eta=parameters[1];
		double mu =parameters[2];

		double[][] inputCopy=new double[input.length][input[0].length];
		double[][] outputCopy=new double[output.length][output[0].length];


		for(int i=0;i<input.length;i++){
			//System.arraycopy(input[i], 0, inputCopy[i], 0, input[0].length);
			for(int j=0;j<input[0].length;j++)
				inputCopy[i][j]=input[i][j];
			//System.arraycopy(output[i], 0, outputCopy[i], 0, output[0].length);
			for(int j=0;j<output[0].length;j++)
				outputCopy[i][j]=output[i][j];
		}

		int size=input.length;
		if(size==0 || size != output.length)
			throw new RuntimeException("Input number != Output number (or zero)");
		if(input[0].length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output[0].length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");



		for(int e=0;e<epochs;e++){
			double error=0;
			shuffle(inputCopy,outputCopy,size);
			for(int i=0;i<size;i++){
				backProp(inputCopy[i],outputCopy[i],eta,mu);
				error+=ssCost(feedForward(inputCopy[i]),outputCopy[i]);
			}

			if(scale==0){//for graph
				scale=error/size;
				//System.out.println("scale: "+scale);
			}
			//System.out.println(error);
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
			//w[l] = matSub(w[l], matMult(eta,matMult(delta[l],a[l-1]))); //to not use momentum
			b[l] = vecSub(b[l], vecMult(eta, delta[l]));
		}
	}
}
