//	initial commit of neural net.
//	set up for arbitrary number of layers, inputs, outputs.
//	right now can take and input, feed it through network and return an output,
//		only after manually setting weights and biases.


public class NNProj {



	public static void main(String[] args) {
		int[] size={2,1};

		Network net=new Network(size);
		double[][] w={{-2,-2}};
		double[] b={3};

		net.setWeights(1, w);
		net.setBiases(1, b);

		double[] input={0,0};
		double[] out;

		out = net.feedForward(input);

		showArray(out);


	}

	static void showArray(double[] a){
		for(double d:a)
			System.out.print("["+d+"] ");
		System.out.println();
	}

}



// Network class.
// currently has a Weight class and Bias class but I think should have a Layer class instead,
// which would mean an array of layers, each layer containing a size, a matrix of weights and
// an array of biases.
//
// The reason element 0 is skipped or ignored is to keep the indexes lined up with the sizes array.
//		(Could easily change this)
class Network{
	int[] sizes;	// sizes[0]=number of input nodes, sizes[len-1]=number of ouput nodes
	int layers;		// sizes.length

	class W{ // weights for layer (one w matrix for each layer, starting with second layer
		double[][] w;	// w[i][j], i=node in current layer, j=node in prev layer
		int nLayer;  // size of current layer, i
		int nFrom;		// size of prev or from layer, j
		W(int i, int j){
			nLayer = i;
			nFrom = j;
			w = new double[i][j];
		}
	}

	class B{		// biases for layer (one bias for each node in layer, starts with second layer (skip input layer)
		double[] b;	// biases
		int n;		// size of layer
		B(int i){
			n=i;
			b=new double[i];
		}
	}

	W[] w;
	B[] b;

	Network(int[] sizes){
		this.sizes=sizes;
		layers=sizes.length;
		w=new W[layers]; // will start with layer 1, ignore layer 0 which will be the inputs
		b=new B[layers];
		for (int i=1;i<layers;i++){	// don't initialize 0th elements, exception if try to access
			w[i]=new W(sizes[i],sizes[i-1]);
			b[i]=new B(sizes[i]);
		}

		///////set weights, biases.  randomly and then train, explicitly, whatever

	}

	void setWeights(int layer, double[][] weights){
		for(int i=0;i<sizes[layer];i++)
			for(int j=0;j<sizes[layer-1];j++)
				w[layer].w[i][j]=weights[i][j];
	}
	void setBiases(int layer, double[] biases){
		for(int i=0;i<sizes[layer];i++)
			b[layer].b[i]=biases[i];
	}

	double[] feedForward(double[] input){
		if(input.length != sizes[0])
			System.err.println("[feedforward] size mismatch");

		double[] in=new double[input.length]; // inputs
		double[] out=null;

		System.arraycopy(input, 0, in, 0, input.length);

		for(int l=1;l<layers;l++){
			out = new double[sizes[l]];
			for(int i=0;i<sizes[l];i++){
				out[i]=sigmoid(dot(in,w[l].w[i]) + b[l].b[i]);
			}
			//if(l<layers-1)
			in = out;
		}
		return out;
	}


	static double dot(double[] v1, double[] v2){
		double p=0.0;
		int n=v1.length;
		if(v1.length!=v2.length)
			System.err.println("[dot] size mismatch");
		for(int i=0;i<n;i++){
			p+=v1[i]*v2[i];
		}
		return p;
	}

	static double[] vadd(double[] v1, double[] v2){
		int n=v1.length;
		double[] sum = new double[n];
		if(v1.length!=v2.length)
			System.err.println("[dot] size mismatch");
		for(int i=0;i<n;i++){
			sum[i]=v1[i]+v2[i];
		}
		return sum;
	}

	static double sigmoid(double x){
		return 1/(1+Math.exp(-x));
	}

	static double[] sigmoid(double[] x){
		double[] y=new double[x.length];
		for(int i=0;i<x.length;i++)
			y[i]=sigmoid(x[i]);
		return y;
	}
}

