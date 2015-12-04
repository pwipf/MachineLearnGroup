package clusteringAlgorithms;

public class DataSet {

	private String dataSetName;       // Name of the file in this data set
	private double[] pointClasses;    // Indexes the classes of each point
	private double data[][];          // Some data set
	private int iterator = 0;         // Used as an iterator when adding new points to the data set
	private int numberOfPoints;		  // Number of points in the set
	private int pointDimensionality;  // Number of dimensions within each point
	
	
	public DataSet(String dataSetName, int numberOfPoints, int pointDimensionality) {
		this.dataSetName = dataSetName;
		data = new double[numberOfPoints][pointDimensionality];
		this.numberOfPoints = numberOfPoints;
		this.pointDimensionality = pointDimensionality;
		pointClasses = new double[numberOfPoints];
	}
	
	// Add a complete data set at once
	void addDataSet(double[][] data) {
		this.data = data;
	}
	
	void addClassSet(double[] pointClasses) {
		this.pointClasses = pointClasses;
	}
	
	void addPoint(double[] point) {
		data[iterator] = point;
		iterator++;
	}
	
	double[][] getData() {
		return data;
	}
	
	double[] getClasses() {
		return pointClasses;
	}
	
	
	int getNumberOfPoints() {
		return numberOfPoints;
	}
	
	int getPointDimensionality() {
		return pointDimensionality;
	}
	
	String getDataSetName() {
		return dataSetName;
	}
	
}
