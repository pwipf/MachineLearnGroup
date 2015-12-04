package mainAlgs;


import java.io.*;
import java.util.*;

/**
 *
 * @author Phil
 */
public class ArffReader {
	
	double data[][];
	double dataClass[];
	int nClasses;
	String strMap[][]; // first dimension is the attribute number, second is the names of the strings
	public enum Datatype{
    REAL, NOMINAL, STRING, DATE, UNKNOWN
  }
	ArrayList<Datatype> attType=new ArrayList<>();
	ArrayList<String>   attName=new ArrayList<>();
	ArrayList<String[]> stringMap=new ArrayList<>();
	int nAttributes=0;

	public double[][] getData(){
		return data;
	}


	public ArffReader(String filename){
		Scanner scan=null;
		File file=new File(filename);
		try{
			scan=new Scanner(file);
		}catch(FileNotFoundException e){
			System.out.println("File not found");
			System.exit(1);
		}


		while(scan.hasNextLine()){
			String line=scan.nextLine();
			if(line==null || line.length()==0 || line.charAt(0) !='@')
				continue;

			Scanner lineScan=new Scanner(line);
			String token =lineScan.next();
			if(token.equalsIgnoreCase("@DATA"))
				break;
			if(token.equalsIgnoreCase("@RELATION")){
				System.out.println("Relation: "+lineScan.next());
			}
			if(token.equalsIgnoreCase("@ATTRIBUTE")){
				nAttributes++;
				if(lineScan.findInLine("'")!=null){
					lineScan.useDelimiter("'");
					attName.add(lineScan.next());
					lineScan.reset();
					lineScan.next();
				}
				else{
					attName.add(lineScan.next());
				}
				String type=lineScan.next();
				if(type.equalsIgnoreCase("REAL") || type.equalsIgnoreCase("NUMERIC")
								|| type.equalsIgnoreCase("INTEGER")){
					attType.add(Datatype.REAL);
					stringMap.add(null);
				}
				else if(type.charAt(0)=='{'){
					attType.add(Datatype.NOMINAL);
					Scanner nomScan=new Scanner(type.substring(1));
					nomScan.useDelimiter(",");
					ArrayList<String> nomList=new ArrayList<>();
					while(nomScan.hasNext()){
						nomList.add(nomScan.next());
					}
					String[] sl=nomList.toArray(new String[nomList.size()]);
					// get rid of trailing '}'
						sl[sl.length-1]=sl[sl.length-1].substring(0,sl[sl.length-1].length()-1);
					stringMap.add(sl);
				}
			}
		}

		// scan is now on first line of data
		ArrayList<double[]> dataList =new ArrayList<>();
		ArrayList<Integer>  classList=new ArrayList<>();
		while(scan.hasNext()){
			String current=scan.nextLine();
			if(current==null || current.length()==0 || current.charAt(0)=='%')
				continue;
			Scanner scanLine=new Scanner(current);
			scanLine.useDelimiter(",");
			double[] dList=new double[nAttributes-1];
			int theClass=0;
			for(int a=0;a<nAttributes;a++){
				switch(attType.get(a)){
					case REAL:
						dList[a]=scanLine.nextDouble();
						break;
					case NOMINAL:
						String str=scanLine.next();
						for(int s=0;s<stringMap.get(a).length;s++){
							if(stringMap.get(a)[s].equalsIgnoreCase(str)){
								if(a==nAttributes-1){//last attribute is class
									theClass=s;
								}
								else{
									dList[a]=s;
								}
								break;
							}
						}
						break;
				}
			}
			dataList.add(dList);
			classList.add(theClass);
		}
		nAttributes--;//the last attribute is assumed to be the class
		nClasses=stringMap.get(nAttributes).length;
		data=dataList.toArray(new double[dataList.size()][]);
		dataClass=new double[classList.size()];
		for(int i=0;i<classList.size();i++){
			dataClass[i]=classList.get(i);
		}
		strMap=stringMap.toArray(new String[stringMap.size()][]);
		// printData(data);
		// printData(strMap);
		// printDataClass(dataClass);
	}
	
	private static void printData(double[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.printf("%d -- ", i);
			for (int j = 0; j < arr[i].length; j++) {
				System.out.printf("%f, ", arr[i][j]);
			}
			System.out.println();
		}
	}
	
	private static void printData(String[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.printf("%d -- ", i);
			for (int j = 0; j < arr[i].length; j++) {
				System.out.printf("%s, ", arr[i][j]);
			}
			System.out.println();
		}
	}
	
	private void printDataClass(double[] classes) {
		int iter = 0;
		for (double d : classes) {
			System.out.printf("%d -- %f%n", iter, dataClass[iter]);
			iter++;
		}	
	}
	
	public double[] getClassSet() {
		return dataClass;
	}
	
	public double getDataClass(int index) {
		return dataClass[index];
	}
}