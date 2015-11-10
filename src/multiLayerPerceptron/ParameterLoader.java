package multiLayerPerceptron;

import java.io.*;
import java.util.*;

public class ParameterLoader {

    static final int NDATAFILES = 10;
    static final int NALGS = 4;
    static final String[] ALGNAMES = {"Backprop", "MuLambda", "DiffEvolution", "GeneticAlg"};
    String filename;

    public ParameterLoader(String filename) {
        this.filename = filename;
    }

    public double[][][] loadParameters() {
        Scanner scan = null;
        File file = new File(filename);
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }

        String s;
        Double[][][] params = new Double[NDATAFILES][NALGS][];
        for (int i = 0; i < NDATAFILES; i++) {
            s = scan.nextLine();//read blank line
            s = scan.nextLine();//read dataset name
            for (int j = 0; j < NALGS; j++) {
                s = scan.nextLine();//read blank line
                s = scan.nextLine();//read algorithm name
                ArrayList<Double> paramlist = new ArrayList<>();
                while (true) {
                    s = scan.nextLine();//read parameter name
                    if (s.length() == 0) {
                        break;
                    }
                    double d = scan.nextDouble();
                    paramlist.add(d);
                    s = scan.nextLine();//finish line with double
                }
                params[i][j] = paramlist.toArray(new Double[paramlist.size()]);
            }
        }

        // sadly can't use toArray() above with primitives
        double[][][] p = new double[NDATAFILES][NALGS][];
        for (int i = 0; i < NDATAFILES; i++) {
            for (int j = 0; j < NALGS; j++) {
                p[i][j] = new double[params[i][j].length];
                for (int k = 0; k < params[i][j].length; k++) {
                    p[i][j][k] = params[i][j][k];
                }
            }
        }
        return p;
    }

    public void initfile(String[] datafiles) {
        String[][] paramNames = {{"Epochs", "eta", "mu"},
        {"initialPopulationSize", "generations", "mu (parental generation size)", "lambda (child population size)", "max Number of Mutations"},
        {"MaxGen", "Pop", "beta", "pi"},
        {"MaxGen", "Pop", "crossoverRate", "mutationPercentChange", "mutationChance"}};

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(filename));

            if (datafiles.length != NDATAFILES) {
                System.err.println("NDATAFILES size mismatch inifile()");
                System.exit(1);
            }
            for (int i = 0; i < NDATAFILES; i++) {
                br.write(System.lineSeparator());
                br.write("%%% DataFile: " + datafiles[i]);
                br.write(System.lineSeparator());
                for (int j = 0; j < NALGS; j++) {
                    br.write(System.lineSeparator());
                    br.write(ALGNAMES[j]);
                    br.write(System.lineSeparator());
                    for (int k = 0; k < paramNames[j].length; k++) {
                        br.write(paramNames[j][k]);
                        br.write(System.lineSeparator());
                        switch (j) {
                            case 0:
                                switch (k) {
                                    case 0:
                                        br.write("1000");
                                        break;
                                    case 1:
                                        br.write("0.005");
                                        break;
                                    case 2:
                                        br.write("0.2");
                                        break;
                                }
                                break;
                            case 1:
                                switch (k) {
                                    case 0:
                                        br.write("100");
                                        break;
                                    case 1:
                                        br.write("50");
                                        break;
                                    case 2:
                                        br.write("80");
                                        break;
                                    case 3:
                                        br.write("20");
                                        break;
                                    case 4:
                                        br.write("5");
                                        break;
                                }
                                break;
                               
                            case 2://diffev
                                switch (k) {
                                    case 0:
                                        br.write("500");
                                        break;
                                    case 1:
                                        br.write("10");
                                        break;
                                    case 2:
                                        br.write("0.8");
                                        break;
                                    case 3:
                                        br.write("0.6");
                                        break;
                                }
                                break;
                            case 3:
                                switch (k) {
                                    case 0:
                                        br.write("100");
                                        break;
                                    case 1:
                                        br.write("10");
                                        break;
                                    case 2:
                                        br.write("0.25");
                                        break;
                                    case 3:
                                        br.write("0.2");
                                        break;
                                    case 4:
                                        br.write("0.015");
                                        break;
                                }
                                break;
                            }
                        br.write(System.lineSeparator());
                    }
                    br.write(System.lineSeparator());
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Can't write file " + filename);
        }

    }
}
