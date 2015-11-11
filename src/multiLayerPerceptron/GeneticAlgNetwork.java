/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiLayerPerceptron;

import java.util.ArrayList;
import java.util.Random;
import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

/**
 *
 * @author Magpie
 */
public class GeneticAlgNetwork extends Network {

    ArrayList<Member> population = new ArrayList<>();
    //static double population[][] = new double [numberIndividuals][numberChromosomes];
    ArrayList<Double> populationFitness = new ArrayList<>();
    int nWeightsBiases; // stores the total number of weights and biases

    GeneticAlgNetwork(int[] sizes) {
        super(sizes);

        nWeightsBiases = 0;
        for (int l = 1; l < layers; l++) {
            nWeightsBiases += sizes[l] * sizes[l - 1] + sizes[l];
        }
    }

    @Override
    public void train(double[][] input, double[][] output, double[] parameters) {
        int maxGen = (int) parameters[0];
        int npop = (int) parameters[1];
        double beta = parameters[2];
        double rho = parameters[3];
        double mutationRate = parameters[4];
        int size = input.length;
        if (size == 0 || size != output.length) {
            throw new RuntimeException("Input number != Output number (or zero)");
        }
        if (input[0].length != nInputs) {
            throw new RuntimeException("Input size doesn't match network");
        }
        if (output[0].length != nOutputs) {
            throw new RuntimeException("Output size doesn't match network");
        }




        //Need to first create our random population.
        createPopulation(npop);
        int generation = 0;

        double averageInverseFitness = calculateTotalFitness(input, output) / population.size();

        for (int i = 0; i < maxGen; i++) {
            generation++;
            //Calculate the average fitness between all individuals, the calculate method also stores their individual fitness scores

            //Selects individuals based on their fitness ratio (individual/overall population)
            //selection picks bigger ratio, so the selection uses the total of all fitnesses inversed
            selectPopulation(averageInverseFitness);

            //Randomly picks a individual, pairs them with another individual. Swaps positions of a part of the chromosome, and forms two unique individuals as a result
            populationCrossover(rho, input, output);
            //Randomly changes a gene within the chromosome with a small chance, and the difference is determined by the mutation offset
            mutatePopulation(beta, mutationRate);






            averageInverseFitness = calculateTotalFitness(input, output) / population.size();

            //Plot entire population
            //for (int p = 0; p<population.size(); p++)
            //{
            // point(map(generation, 0, maxGen, .05, 1), map(populationFitness.get(p), 0, 1, .05, 1));
            //
            //}

            if (populationFitness.isEmpty())
            {
            point(map(generation, 0, maxGen, .05, 1), map(0, 0, .5, .05, 1));    
            continue;
            }

            //Plot only fittest
            point(map(generation, 0, maxGen, .05, 1), map(populationFitness.get(getFittest()), 0, .5, .05, 1));
        }
        //Recalculate Fitness based on the Last Generation (So the individual's fitness correctly matches up with the current population)


        //Returns the chromosome of the fittest individual
        //double[] fittest = getFittest();

        // had better set the network weights
        int best = 0;
        best = getFittest();
        //set network weights to population[best]
        for (int l = 1; l < layers; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    w[l][i][j] = population.get(best).w[l][i][j];
                }
                b[l][i] = population.get(best).b[l][i];
            }
        }


    population.clear();
    populationFitness.clear();

    }

    public void createPopulation(int numberIndividuals) {


        for (int i = 0; i < numberIndividuals; i++) {
            Member m = new Member();
            m.initRandom();
            population.add(m);
        }




    }

    //Average fitness is calculated by 1/sqrt(1+distance)
    //Identical values return a 1, the highest fitness, otherwise a decimal number is returned based off the distance.
    double calculateTotalFitness(double input[][], double output[][]) {
        //System.out.println("Calculating Fitness");
        //System.out.println(population.size());
        //System.out.println(numTraining);
        //System.out.println(numberChromosomes);
        populationFitness.clear();
        double totalInverseFitness = 0;
        double individualFitness = 0;

        for (int i = 0; i < population.size(); i++) {


            individualFitness = fitness(population.get(i), input, output);
            totalInverseFitness += 1 / individualFitness;

            populationFitness.add(individualFitness);
            individualFitness = 0;
        }




        return totalInverseFitness;


    }

//Probabilistically selecting new members of the population through fitness scoring.
//individual fitness/overall fitness
    public void selectPopulation(double averageInverseFitness) {



//
        Random random = new Random();
        ArrayList<Member> newPopulation = new ArrayList<Member>();
        for (int i = 0; i < population.size(); i++) {
            double fitnessRatio = 1 / populationFitness.get(i);
            fitnessRatio = fitnessRatio / averageInverseFitness;

            //Smaller fitness is better, so we invert the ratio to make comparisons

//            double totalFitness = 0;
//            for (int z = 0; z<population.size(); z++)
//            {
//            totalFitness += populationFitness.get(z);
//            }
//            totalFitness /= population.size();
//            System.out.println("Average Total Fitness is " + totalFitness);
//            System.out.println("Individual Fitness " + populationFitness.get(i));
//            System.out.println("Average inverse fitness is " + averageInverseFitness);
//            System.out.println("Fitness Ratio is " + fitnessRatio );
//            System.out.println();

            //Ratios at 100% or higher are guaranteed to be added to the next generation
            while (fitnessRatio >= 1) {
                //System.out.println(population.get(i)[0] + "added to newPopulation");
                newPopulation.add(population.get(i));
                fitnessRatio -= 1;
                //System.out.println(newPopulation.get(i)[0]);
            }

            double chanceSelected = random.nextDouble();

            //Individual was successful in being selected to join the new population
            if (chanceSelected <= fitnessRatio) {
                newPopulation.add(population.get(i));
            }

        }

        //System.out.println(newPopulation.get(0)[1]);
        //Replace population with the new population
        population.clear();

        for (int i = 0; i < newPopulation.size(); i++) {
            population.add(newPopulation.get(i));
        }

        newPopulation.clear();
    }

//For every individual in the population, splits and swaps parts of the chromosome between two individuals. The chance of this occuring is the crossoverRate
    public void populationCrossover(double crossoverRate, double[][] input, double[][] output) {
        for (int p = 0; p < population.size(); p++) {
//Crossover is successful
            if (Math.random() <= crossoverRate) {

                int secondParent = (int) (Math.random() * population.size());

                int chromosomeSplit = (int) (Math.random() * sizes.length);
//System.out.println("Split at" + chromosomeSplit);
//System.out.println("sizes.length is " + sizes.);
//System.out.println("Sizes length is" + sizes.length);

                Member child1 = new Member();
                Member child2 = new Member();



                for (int l = 1; l < sizes.length; l++) {
                    for (int i = 0; i < sizes[l]; i++) {
                        for (int j = 0; j < sizes[l - 1]; j++) {

                            if (l <= chromosomeSplit) {
                                child1.w[l][i][j] = population.get(p).w[l][i][j];
                            } else {
                                child1.w[l][i][j] = population.get(secondParent).w[l][i][j];
                            }

                            if (l <= chromosomeSplit) {
                                child2.w[l][i][j] = population.get(secondParent).w[l][i][j];
                            } else {
                                child2.w[l][i][j] = population.get(p).w[l][i][j];
                            }



                        }



                        if (l <= chromosomeSplit) {
                            child1.b[l][i] = population.get(p).b[l][i];
                        } else {
                            child1.b[l][i] = population.get(secondParent).b[l][i];
                        }

                        if (l <= chromosomeSplit) {
                            child2.b[l][i] = population.get(secondParent).b[l][i];
                        } else {
                            child2.b[l][i] = population.get(p).b[l][i];
                        }
                    }
                }


                //If the child is more fit, we replace the parent
                if (fitness(child1, input, output) < fitness(population.get(p), input, output)) {
                    population.set(p, child1);
                }

                //If the seceond child is more fit than the second parent, we replace the secondParent
                if (fitness(child2, input, output) < fitness(population.get(secondParent), input, output)) {
                    population.set(secondParent, child2);
                }



            }



        }
    }

    public void mutatePopulation(double beta, double mutationRate) {
        for (int p = 0; p < population.size(); p++) {
            Member temp = new Member();
            for (int l = 1; l < sizes.length; l++) {
                for (int i = 0; i < sizes[l]; i++) {
                    for (int j = 0; j < sizes[l - 1]; j++) {
                        if (Math.random() <= mutationRate) {
                            temp.w[l][i][j] = population.get(p).w[l][i][j] + ((beta * ((Math.random() * 2) - 1)) * population.get(p).w[l][i][j]);
                            //w[l][i][j] = gen.nextGaussian();
                        } else {
                            temp.w[l][i][j] = population.get(p).w[l][i][j];
                        }

                    }
                    if (Math.random() <= mutationRate) {
                        temp.b[l][i] = population.get(p).b[l][i] + ((beta * ((Math.random() * 2) - 1)) * population.get(p).b[l][i]);
                        //w[l][i][j] = gen.nextGaussian();
                    } else {
                        temp.b[l][i] = population.get(p).b[l][i];
                    }
                }
            }

            population.set(p, temp);

        }
    }
//Returns the fittest individual of the population.

    public int getFittest() {
        int fittestPosition = 0;
        double lowestFitness = Double.MAX_VALUE;
        for (int i = 0; i < population.size(); i++) {
            if (lowestFitness > populationFitness.get(i)) {
                lowestFitness = populationFitness.get(i);
                fittestPosition = i;
            }



        }

        return fittestPosition;
    }

    // helper functions in case is helpful to have the weights and biases all in a nice vector
    //
    // serializeGenes()
    // Creates a vector (1D array) of weights and biases from all the weights/biases in the network.
    double[] serializeGenes(Member m) {
        double[] temp = new double[nWeightsBiases];
        int k = 0;

        for (int l = 1; l < sizes.length; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    temp[k++] = m.w[l][i][j];
                }
                temp[k++] = m.b[l][i];
            }
        }
        return temp;
    }

    // of course a vector form of the weights and biases is not good for running feedforward to
    // get output from the network.
    //
    // unSerializeGenes()
    // Creates a NEW Member from the serialized weight/biases.
    Member unSerializeGenes(double[] ser) {
        Member m = new Member();
        int k = 0;
        for (int l = 1; l < sizes.length; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    m.w[l][i][j] = ser[k++];
                }
                m.b[l][i] = ser[k++];
            }
        }
        return m;
    }

    double fitness(Member m, double[][] input, double[][] output) {
        int size = input.length;
        double error = 0;
        for (int i = 0; i < size; i++) {
            double[] netOutput = feedForward(m, input[i]);
            error += ssCost(netOutput, output[i]);
        }
        error /= size;
        return error;
    }

    double[] feedForward(Member m, double[] input) {
        double[] a = input;
        for (int l = 1; l < layers; l++) {
            a = sigmoid(vecAdd(matMult(m.w[l], a), m.b[l]));
        }
        return a;
    }

    // inner class Member
    // a simplified and faster version of the network class, since it only stores the weights
    // and biases, and does not initialize them unless initRandom is explicitly called.
    class Member {

        double[][][] w;
        double[][] b;

        Member() {
            w = new double[sizes.length][][];
            b = new double[sizes.length][];
            for (int l = 1; l < sizes.length; l++) {
                w[l] = new double[sizes[l]][sizes[l - 1]];
                b[l] = new double[sizes[l]];
            }
        }

        void initRandom() {
            for (int l = 1; l < sizes.length; l++) {
                for (int i = 0; i < sizes[l]; i++) {
                    for (int j = 0; j < sizes[l - 1]; j++) {
                        w[l][i][j] = gen.nextGaussian() * 10;
                    }
                    b[l][i] = gen.nextGaussian() * 10;
                }
            }
        }
    }
}
