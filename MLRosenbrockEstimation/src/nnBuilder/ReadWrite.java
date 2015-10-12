package nnBuilder;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.regex.*;

public class ReadWrite {

    static PrintStream fout;
    static BufferedReader fin;

//    // For writing to a file
//    ReadWrite() {
//        String filename = "savedNets/nn_0.csv";
//        try {
//            fout = new PrintStream(new FileOutputStream(filename));
//        } catch (IOException fo) {
//            System.out.println(fo);
//        }
//    }

    // Reads a file in
    // in means read a file in
    // out means read a file out
    ReadWrite(String filename) {
        try {
                fin = new BufferedReader(new FileReader(filename));
            
                fout = new PrintStream(new FileOutputStream(filename));

        } catch (IOException fo) {
            System.out.println(fo);
        }
    }

    public void writer(String out) {

        fout.println(out);

    }

    public void writer(int out) {

        fout.println(out);

    }
    
    private static void writerNoPrintln(String s) {
        // DELETE
        System.out.printf("%s", s);
        // END DELETE
        fout.println(s);
    }

    public void writer(char out) {

        fout.println(out);

    }

    public void writer(double out) {

        fout.println(out);

    }

    public void writer(float out) {

        fout.println(out);

    }

    public String reader() {
        try {
            return fin.readLine();
        } catch (IOException e) {
            System.out.println("error reading from file " + e);
            return "error\t";
        }
    }

    // NN specific functions
    // Write the neural net out to a csv
    public void writeNeuralNet(ArrayList<NNLayer> neuralNet) {
        StringBuilder sb = new StringBuilder();

        // This loop cycles through NNLayers
        for (int i = 0; i < neuralNet.size(); i++) {
            NNLayer curLayer = neuralNet.get(i);
            // Append the number of nodes in the layer in a format like "num_nodes,23"
            sb.append(String.format("num_nodes,%d%n", curLayer.numNodes()));
            // This loop cycles through the nodes in a layer
            for (int j = 0; j < curLayer.numNodes(); j++) {
                // Length of the array of doubles in each node corresponding
                // to connections to the next layer
                int weightArrayLength = curLayer.getNode(j).getWeights().length;

                // Weights at minus 1 because if it isn't, an extra comma gets inserted into csv
                // This loop cycles through weights in a node
                for (int w = 0; w < weightArrayLength - 1; w++) {
                    sb.append(String.format("%f,", curLayer.getNode(j).getWeights()[w]));
                }
                // Handle last weight of layer
                sb.append(curLayer.getNode(j).getWeights()[weightArrayLength - 1]);
                sb.append(String.format("%n"));
            }
        }
        // Sends the line to the file
        writer(sb.toString());
    }

//    public static ArrayList<NNLayer> readNeuralNet() {
//        ArrayList<NNLayer> nNet = new ArrayList<NNLayer>();
//        File saveDirectory = new File("savedNets");
//        String[] fileNames = saveDirectory.list();
//        System.out.println();
//        // List all of the files in the save directory
//        for (int i = 0; i < fileNames.length; i++) {
//            System.out.printf("%d. %s%n", i + 1, fileNames[i]);
//        }
//        // Get user input for which net to use
////        System.out.printf("Which neural net would you like to use? >");
////        Scanner in = new Scanner(System.in);
////        in.next();
////        int choice = in.nextInt() - 1;
////        // Create instance of this class to read the specified file
//        ReadWrite r = new ReadWrite("savedNets/nn_0.csv");
//        String curLine;
//        NNLayer curLayer = new NNLayer(0);
//        try {
//            while ((curLine = fin.readLine()) != null) {
//                // If the current line is "num_nodes," and one or more digits...
//                if (curLine.matches("num_nodes,\\d+")) {
//                    // Add the new layer to the neural net
//                    curLayer = new NNLayer(parseNumberOfInputs(curLine));
//                    // Initialize nodes for layer as they are not randomly
//                    // populated
//                    curLayer.initializeNodes();
//                    nNet.add(curLayer);
//                } // If the line matches 0 or 1 negative signs, a digit, 0 or 1
//                // periods, 0 or more digits, and 0 or one commas, multiple
//                // times...
//                else if (curLine.matches("(-?\\d.?\\d*,?)+")) {
//                    curLayer.sequentialNodeUpdate().updateWeights(parseNumberOfWeights(curLine));
//                }
//            }
//        } catch (IOException e) {
//            System.out.printf("%n%nError, could not read from file");
//            System.exit(1);
//        }
//        return nNet;
//    }

    /**
     *
     * @param s
     * @return
     */
    private static int parseNumberOfInputs(String s) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(s);
        m.find();
        m.group();
        return Integer.parseInt(m.group());
    }

    /**
     *
     * @param s
     * @return
     */
    private static double[] parseNumberOfWeights(String s) {
        String[] weightsAsStrings = s.split(",");
        double[] weightsAsDoubles = new double[weightsAsStrings.length];
        for (int i = 0; i < weightsAsStrings.length; i++) {
            weightsAsDoubles[i] = Double.parseDouble(weightsAsStrings[i]);
        }
        return weightsAsDoubles;
    }

    /**
     * Generates a CSV with Time,# of Layers, and the learning rate
     */
    public static void outputCSVInfo(String s) {
        StringBuffer data = new StringBuffer(readCSV());
        //System.out.printf("%n%s%n", data.toString());
        data.append(s);
        data.append(String.format("%n"));
        updateFileOut("netData/networkTrainingData.csv");
        writerNoPrintln(data.toString());
    }

    private static String readCSV() {
        updateFileIn("netData/networkTrainingData.csv");
        StringBuilder s = new StringBuilder();
        String curLine;
        try {
            while ((curLine = fin.readLine()) != null) {
                s.append(curLine);
                System.out.printf("%s%n", curLine);
            }
        } catch (IOException e) {
            System.out.printf("%n%nError, could not read from file %s");
            System.exit(1);
        }
        try {
        fin.close();
        } catch (IOException e) {
            System.out.println("Could not close non opened file reader");
        }
        return s.toString();
    }
    
    private static void updateFileIn(String filename) {
        try {
                fout.close();
                fin = new BufferedReader(new FileReader(filename));
        } catch (IOException fo) {
            System.out.println(fin);
        } catch (NullPointerException np) {
            // Do nothing.
        }
    }
    
    private static void updateFileOut(String filename) {
         try {
                fin.close();
                fout = new PrintStream(new FileOutputStream(filename));

        } catch (IOException fo) {
            System.out.println(fo);
        } catch (NullPointerException np) {
            // Do nothing.
        }
    }
}