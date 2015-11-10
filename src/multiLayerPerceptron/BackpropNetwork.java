
package multiLayerPerceptron;

import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

public class BackpropNetwork extends Network{


	BackpropNetwork(int[] sizes, double scale){
		super(sizes);
	}

	@Override
	public void train(double[][] input, double[][] output, double[] parameters){
		int epochs=(int)parameters[0];
		double eta=parameters[1];
		double mu =parameters[2];

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
}
