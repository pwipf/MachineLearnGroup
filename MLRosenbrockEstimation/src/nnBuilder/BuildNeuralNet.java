package nnBuilder;

import java.util.ArrayList;
import java.util.Scanner;

public class BuildNeuralNet {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Input or Weight vectors
		ArrayList<NNLayer> vectors = new ArrayList<NNLayer>();
		Scanner in = new Scanner(System.in);
		System.out.printf("How many inputs values will you be using (positive integer)?  >");
		int numInputs = in.nextInt();
		System.out.printf("%n");
		// Add the input layer, but use a very large number to distinguish it from the other weights
		vectors.add(new NNLayer(numInputs));

		// Generate the weight vectors
		boolean done = false;
		int iterations = 1;
		while (!done) {
			// Generate some number of weight vectors for the nodes in that layer
			System.out.printf("How many nodes in layer %d? >", iterations);
			iterations++;
			int numNodes = in.nextInt();
			vectors.add(new NNLayer(numNodes));
			System.out.printf("Is this the final layer (y/n)? >");
			String c = in.next().toLowerCase();
			if (c.equals("y")) {
				done = true;
				// close input stream
				in.close();
			}
			else {
				done = false;
			}
		}
		// Populate node weights
                // The output vector is not yet populated, because this loop makes reference to the next layer, for which the 
		// output layer has none.
		for (int i = 0; i < vectors.size() - 1; i++) {
			vectors.get(i).populateNodeWeights(vectors.get(i + 1).numNodes());

		}
		// Handle output layer
		vectors.get(vectors.size() - 1).populateNodeWeights(1);
		printNN(vectors);
	}

	private static void printNN(ArrayList<NNLayer> vectors) {

		int maxRows = Integer.MIN_VALUE;
		// Find the nnlayer with the most nodes
		for (int i = 0; i < vectors.size(); i++) {
			if (vectors.get(i).numNodes() > maxRows) {
				maxRows = vectors.get(i).numNodes();
			}
		}
		// Outer loop cycles through rows
		// maxRowSize is the variable used to control this.
		for (int i = 0; i < vectors.size(); i++) {
                    NNLayer curLayer = vectors.get(i);
			for (int j = 0; j < curLayer.numNodes(); j++) {
				System.out.printf("%nLayer %d node %d%n", i, j);
				for (int w = 0; w < curLayer.getNode(j).getWeights().length; w++) {
					System.out.printf("%f, ", vectors.get(i).getNode(j).getWeights()[w]);
				}
			}
		}
	}


}
