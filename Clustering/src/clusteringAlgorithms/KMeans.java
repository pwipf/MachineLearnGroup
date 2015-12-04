/**
 * @author Rob Lewis
 * @date 12.4.2015
 */
package clusteringAlgorithms;

import java.util.Random;

public class KMeans {

	FileOut fileWriter;                          // Used to output to CSV
	private DataSet d;                           // A data set containing a set of arbitrary dimensionality points
	private int k;                               // Number of clusters for the learning process
	private double convergenceThreshold;         // A small number in the range [0,1) used for stopping the algorithm
	private int[] belongsToCluster;			     // Denotes which cluster a point belongs to (range 1...k)
	private double[][] clusterCenters;           // Points that contain the cluster centers
	private double[][] previousClusterCenters;   // Used in tandem with the convergence threshold for determining the stopping point
	private Random randomizer = new Random();   // 8 is my favorite number
	/**
	 * 
	 * @param d A data set containing a set of arbitrary dimensionality points
	 * @param k Number of clusters for the learning process
	 * @param convergenceThreshold A small number in the range [0,1) used for stopping the algorithm.
	 */
	public KMeans(DataSet d, int k, double convergenceThreshold) {
		// Initialize file writer
		fileWriter = new FileOut(d.getDataSetName() + "k-means");
		fileWriter.writer("int clusterKey, int class, double[] points");
		// Error check
		if (k >= d.getNumberOfPoints()) {
			System.err.println("Error: Must be fewer clusters than points");
			System.exit(1);
		}
		if (convergenceThreshold < 0 || convergenceThreshold >= 1) {
			System.err.println("Bad Convergence Threshold value. Must be in the range [0,1).");
			System.exit(1);
		}

		this.d = d;
		this.k = k;
		clusterCenters = new double[k][d.getPointDimensionality()];
		belongsToCluster = new int[d.getNumberOfPoints()];
		// Initialize the cluster centers to random points within the space, ensuring that each center
		// is unique, and will indeed have an initial point that it "owns"
		for (int i = 0; i < k; i++) {
			// Assign the cluster to a random point within the set
			clusterCenters[i] = d.getData()[Math.abs(randomizer.nextInt()) % d.getNumberOfPoints()].clone();
			// Ensure each center is unique
			for (int j = 0; j < i; j++) {
				if (clusterCenters[i].equals(clusterCenters[j])) {
					i--; // Cluster center needs to be unique, so it gets recalculated if it has identical value.
					break;
				}
			}
		}
		mainAlg();
	}

	// Where the magic happens
	private void mainAlg() {
		boolean done = false;
		while (!done) {
			
			// IMPORTANT STEP!!
			// MAKE SURE THAT OLD CLUSTER VALUES ARE SAVED FOR TERMINATION PURPOSES
			previousClusterCenters = clusterCenters.clone();

			for (int i = 0; i < d.getNumberOfPoints(); i++) {
				// Assign each point to the center closest to it
				double minDistance = Double.MAX_VALUE;

				// iterate through the points for each point updating the minDistance as closer clusters are found
				for (int j = 0; j < clusterCenters.length; j++) {
					double curDistance = squareDistance(d.getData()[i], clusterCenters[j]); // Calculate distance
					if (curDistance < minDistance) {
						minDistance = curDistance;
						belongsToCluster[i] = j; // Denotes that the i'th point is closest to the j'th cluster
					}
				}
			}
			// Now the cluster centers need to be updated based on the points
			// The cluster gets updated to the center of the points that belong to them
			for (int i = 0; i < clusterCenters.length; i++) {
				int numElementsBelongingToCluster = 0; // Guaranteed to be at least 1 after updating based on how clusters were picked
				// Initializer a new point with all 0's to be the new cluster center
				double[] newClusterCenter = new double[d.getPointDimensionality()];
				for (int zeroInitializer = 0; zeroInitializer < newClusterCenter.length; zeroInitializer++) {
					newClusterCenter[zeroInitializer] = 0;
				}
				// Find points belonging to the cluster, and sum them into the new cluster center
				for (int j = 0; j < d.getNumberOfPoints(); j++) {
					if (belongsToCluster[j] == i) {       // Point belongs to cluster
						numElementsBelongingToCluster++;  // iterate for the purpose of calculating average later
						for (int dimension = 0; dimension < d.getPointDimensionality(); dimension++) {
							newClusterCenter[dimension] += d.getData()[i][dimension]; // rolling sum of points in cluster
						}
					}
				}
				// Now we average over that rolling sum mentioned before
				for (int dimension = 0; dimension < newClusterCenter.length; dimension++) {
					try {
					newClusterCenter[dimension] *= (1/numElementsBelongingToCluster);
					} catch(ArithmeticException e) {
						System.err.printf("Hmm...For some reason a cluster exists without any children.%nThis problem seems to correct itself in subsequent updates.%n%n");
					}
				}
				// Update the array with all cluster centers
				clusterCenters[i] = newClusterCenter;
			}
			
			// Output relevant data to CSV
			for (int i = 0; i < d.getNumberOfPoints(); i++) {
				writeToCSV(belongsToCluster[i], d.getData()[i], i);
			}
			
			// Check to see if algorithm has reached optimal solution.
			done = true; // Assume task complete, and then check to see if it's not.
			for (int i = 0; i < clusterCenters.length; i++) {
				// If the distance between the new cluster center and the old is greater than
				// the convergence threshold, repeat the algorithm.
				if (Math.sqrt(squareDistance(clusterCenters[i], previousClusterCenters[i])) > convergenceThreshold) {
					done = false;
				}
			}
			fileWriter.writer("");
		}
		sumSquaredErr();
	}

	/**
	 * 
	 * @param p1 Some point of arbitrary dimensionality
	 * @param p2 Some point of arbitrary, but equal dimensionality
	 * @return the Square distance between the points
	 */
	private static double squareDistance(double[] p1, double[] p2) {

		// If the points do not have the same dimensionality, exit with error
		if (p1.length != p2.length) {
			System.err.println("Points do not have the same dimensionality");
			System.exit(1);
		}

		// Keep a rolling sum for the distance between the points
		double distance = 0;

		// Calculate square distance between points
		for (int i = 0; i < p1.length; i++) {
			distance += Math.pow(p1[i] - p2[i], 2);
		}

		return distance;
	}
	
	private void writeToCSV(int clusterKey, double[] point, int index) {
		StringBuilder sb = new StringBuilder();
		sb.append(clusterKey);
		sb.append(",");
		sb.append(d.getClasses()[index]);
		
		for (int i = 0; i < point.length; i++) {
			sb.append(",");
			sb.append(point[i]);
		}
		fileWriter.writer(sb.toString());
	}
	
	private void sumSquaredErr() {
		double sse = 0; // rolling sum
		for (int i = 0; i < clusterCenters.length; i++) {
			for (int j = 0; j < d.getNumberOfPoints(); j++) {
				if (belongsToCluster[j] == i) {
					// Find the square distance between the points
					
					for (int dimensions = 0; dimensions < d.getPointDimensionality(); dimensions++) {
						sse += Math.pow(d.getData()[j][dimensions] - clusterCenters[i][dimensions], 2);
					}
				}
			}
		}
		sse /= clusterCenters.length;
		fileWriter.writer(String.format("%n,SSE = ,%f", sse));
	}
}
