package nnBuilder;

/**
 * A layer of the neural network modeled as an array of doubles representing weights initialized
 * to random variables between -1 and 1
 * @author Rob
 *
 */
public class NNLayer {
	// The layers nodes
	private Node[] nodes;
	// Generate a vector of nodes that the layer represents
	public NNLayer(int numNodes) {
		nodes = new Node[numNodes];
	}

	// Populate the node weights in this layer to be random values between -1 and 1, or if it's the last layer,
	// populate the nodes such that the weight value which won't be used is equal to Integer.MAX_VALUE
	// in this way, the layer is initialized regardless
	public void populateNodeWeights(int inputsToNextLayer) {
		for (int i = 0; i < nodes.length; i++) {
			if (inputsToNextLayer == 0) {
				nodes[i] = new Node(0);
			}
			else {
				nodes[i] = new Node(inputsToNextLayer);
			}
		}
	}
	// Get a node at a specific index.
	public Node getNode(int index) {
		return nodes[index];
	}
	
	public int numNodes() {
		return nodes.length;
	}
}
