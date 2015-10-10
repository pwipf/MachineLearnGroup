package nnBuilder;

import java.util.Random;

public class Node {

	// initializes nextLayerSize weights to a random value between -1 and 1
	private double weights[];
	private int output = 0;
	public Node(int nextLayerSize) {
		weights = new double[nextLayerSize];
		Random r = new Random();
		for (int i = 0; i < nextLayerSize; i++) {
			// add the random weights to the array list
			weights[i] = r.nextDouble() * 2 - 1;
		}
	}
	
	// For building a layer with a specific input (primarily used for the input layer as to
	// distinguish it from the other layers that fall between -1 and 1
	public Node(int size, double val) {
		for (int i = 0; i < size; i++) {
			weights[i] = val;
		}
	}
	
	// Get the weights for this node
	public double[] getWeights() {
		return weights;
	}
	
	// Update weights
	public void updateWeights(double[] weights) {
		this.weights = weights;
	}
        
        // Get node output
        public double getOutput() {
            return output;
        }
    }
