/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiLayerPerceptron;

import java.util.Arrays;
import java.util.Stack;
import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

/**
 *
 * @author Rob
 */
public class EvolutionaryStrategy extends Network {

    public EvolutionaryStrategy(int[] sizes) {
        super(sizes);
    }

    @Override
    public void train(double[][] input, double[][] output, double[] params/*int initialPopulationSize, int epochs, int mu, int lambda, int numMutations*/) {
        int initialPopulationSize = (int) params[0];
        int epochs = (int) params[1];
        int mu = (int) params[2];
        int lambda = (int) params[3];
        int numMutations = (int) params[4];
        // Initialize initial population, parent population, child population
        Member[] initialPopulation = new Member[initialPopulationSize];
        Member[] population = new Member[mu];
        Member[] childPopulation = new Member[lambda];
        // Make a bunch of random neural nets for the parent generation
        for (int i = 0; i < initialPopulationSize; i++) {
            initialPopulation[i] = new Member();
            initialPopulation[i].initRandom();
        }
        ESComparator comparator = new ESComparator(this, input, output);
        Arrays.sort(initialPopulation, comparator);

        // Initialize values for parent population
        for (int i = 0; i < mu; i++) {
            population[i] = initialPopulation[i];
        }
        for (int iterator = 0; iterator < epochs; iterator++) {
            // Generate a child population
            for (int i = 0; i < childPopulation.length; i++) {
                // Generate offspring from a random parent
                childPopulation[i] = evolutionaryStrategyMutation(population[Math.abs(gen.nextInt() % mu)], numMutations);
            }
            // Merge child population with parent population
            Member[] mergedPopulation = new Member[childPopulation.length + population.length];
            for (int i = 0; i < population.length + childPopulation.length; i++) {
                if (i < population.length) {
                    mergedPopulation[i] = population[i];
                } else {
                    mergedPopulation[i] = childPopulation[i - population.length];
                }
            }

            // Sort the merged population
            Arrays.sort(mergedPopulation, comparator);

            // Purge the final lambda of merged population to use for next generation
            for (int i = 0; i < population.length; i++) {
                population[i] = mergedPopulation[i];
            }

            // Draw population on graph
            for (int i = 0; i < population.length; i++) {
                // If you want to print to console
                // System.out.printf("Epoch: %d ---- Member: %d ---- Fitness: %f%n", iterator, i, fitness(population[i], input, output));
                double fitness = fitness(population[i], input, output);
                point(map(iterator, 0, epochs, .05, 1), map(fitness, 0, 1, .05, 1));
            }
        }

        //set network weights to population[0], which as a sorted array will be the best
        for (int l = 1; l < layers; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    w[l][i][j] = population[0].w[l][i][j];
                }
                b[l][i] = population[0].b[l][i];

            }
        }
    }

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
                        w[l][i][j] = gen.nextGaussian();
                    }
                    b[l][i] = gen.nextGaussian();
                }
            }
        }
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

    Member mutate(Member m1, Member m2, Member m3, double beta) {
        Member temp = new Member();
        for (int l = 1; l < sizes.length; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    temp.w[l][i][j] = m1.w[l][i][j] + beta * (m2.w[l][i][j] - m3.w[l][i][j]);
                }
                temp.b[l][i] = m1.b[l][i] + beta * (m2.b[l][i] - m3.b[l][i]);
            }
        }
        return temp;
    }

    Member crossover(Member parent, Member milkman, double rho) {
        Member temp = new Member();
        for (int l = 1; l < sizes.length; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    temp.w[l][i][j] = (gen.nextDouble() < rho) ? parent.w[l][i][j] : milkman.w[l][i][j];
                }
                temp.b[l][i] = (gen.nextDouble() < rho) ? parent.b[l][i] : milkman.b[l][i];
            }
        }
        return temp;
    }

    Member evolutionaryStrategyMutation(Member parent, int maxMutations) { // randomly chooses a mutation to apply to a parent and generate
        // a new child
        // Clone the parent before applying mutation to the child
        Member child = new Member();
        for (int l = 1; l < sizes.length; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    child.w[l][i][j] = parent.w[l][i][j];
                }
                child.b[l][i] = parent.b[l][i];
            }
        }
        int numMutations = Math.abs(gen.nextInt() % maxMutations) + 1;

        for (int i = 0; i < numMutations; i++) {
            // !!!!!!!!!!!!!!!!!!!!!!!!!
            // !!!!!!!!IMPORTANT!!!!!!!!
            // !!!!!!!!!!!!!!!!!!!!!!!!!
            //
            // if more mutations are added, the modulus operator needs to
            // be changed to reflect other choices, along with the switch
            // statement below.
            int randMutation = Math.abs(gen.nextInt() % 5);
            switch (randMutation) {
                case 0:
                    child = gaussianPointMutation(child);
                    break;
                case 1:
                    child = nodeSwap(child);
                    break;
                case 2:
                    child = nodeSwap(child);
                    break;
                case 3:
                    child = invert(child);
                    break;
                case 4:
                    child = shift(child);
            }
        }
        return child;
    }

    private Member gaussianPointMutation(Member child) {
        int biasOrWeight = Math.abs(gen.nextInt() % 2); // Should a bias be updated randomly, or a weight?
        int rand1 = 0;
        while (rand1 == 0 /* Don't want to modify input layer */) {
            rand1 = Math.abs(gen.nextInt() % sizes.length);
        }
        int rand2 = Math.abs(gen.nextInt() % sizes[rand1]);
        int rand3 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);
        if (biasOrWeight == 0) { // update a random weight according to the gaussian distribution.
            child.w[rand1][rand2][rand3] += gen.nextGaussian();
        } else { // update a random bias according to the gaussian distribution
            child.b[rand1][rand2] += gen.nextGaussian();
        }
        return child;
    }

    private Member nodeSwap(Member child) { // Swap the weights of two nodes
        int rand1 = 0;
        while (rand1 == 0 /* Don't want to modify input layer */) {
            rand1 = Math.abs(gen.nextInt() % sizes.length);
        }
        int rand2 = Math.abs(gen.nextInt() % sizes[rand1]);
        int rand3 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);

        int rand4 = 0;
        while (rand4 == 0 /* Don't want to modify input layer */) {
            rand4 = Math.abs(gen.nextInt() % sizes.length);
        }
        int rand5 = Math.abs(gen.nextInt() % sizes[rand4]);
        int rand6 = Math.abs(gen.nextInt() % sizes[rand4 - 1]);

        double placeholder = child.w[rand1][rand2][rand3];
        // weight swap occurs here
        child.w[rand1][rand2][rand3] = child.w[rand4][rand5][rand6];
        child.w[rand4][rand5][rand6] = placeholder;
        return child;
    }

    private Member invert(Member child) { // Reverses the order of the nodes in some layer of the network
        Stack<Double> inversionStack = new Stack<Double>();
        int rand1 = 0;
        while (rand1 == 0 /* Don't want to modify input layer */) {
            rand1 = Math.abs(gen.nextInt() % sizes.length);
        }
        int rand2 = Math.abs(gen.nextInt() % sizes[rand1]);
        int rand3 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);
        int rand4 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);

        if (rand4 > rand3) {
            int temp = rand3;
            rand3 = rand4;
            rand4 = temp;
        }

        for (int i = rand3; i <= rand4; i++) {
            inversionStack.push(child.w[rand1][rand2][i]);
        }
        // Reverse, reverse
        for (int i = rand3; i <= rand4; i++) {
            child.w[rand1][rand2][i] = inversionStack.pop();
        }

        return child;
    }

    private Member shift(Member child) { // shifts the nodes in a layer down
        int rand1 = 0;
        while (rand1 == 0 /* Don't want to modify input layer */) {
            rand1 = Math.abs(gen.nextInt() % sizes.length);
        }
        int rand2 = Math.abs(gen.nextInt() % sizes[rand1]);
        int rand3 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);
        int rand4 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);
        int rand5 = Math.abs(gen.nextInt() % sizes[rand1 - 1]);
        if (rand3 > rand4) {
            int temp = rand3;
            rand3 = rand4;
            rand4 = temp;
        }
        if (rand3 > rand5) {
            int temp = rand3;
            rand3 = rand5;
            rand5 = temp;
        }
        if (rand4 > rand5) {
            int temp = rand4;
            rand4 = rand5;
            rand5 = temp;
        }
        double[] subArr1 = new double[rand4 - rand3];
        double[] subArr2 = new double[rand5 - rand4];
        for (int i = rand3; i < rand4; i++) {
            subArr1[i - rand3] = child.w[rand1][rand2][i];
        }
        for (int i = rand4; i < rand5; i++) {
            subArr2[i - rand4] = child.w[rand1][rand2][i];
        }

        // shifting occurs here
        for (int i = rand3; i < subArr2.length + rand3; i++) {
            child.w[rand1][rand2][i] = subArr2[i - rand3];
        }
        for (int i = rand3 + subArr2.length; i < rand5; i++) {
            child.w[rand1][rand2][i] = subArr1[i - (rand3 + subArr2.length)];
        }
        return child;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////
    // vectorized helper functions
    static double ssCost(double[] a, double[] y) {
        double c = 0;

        for (int i = 0; i < a.length; i++) {
            c += Math.pow(a[i] - y[i], 2) / 2;
        }
        return c;
    }

    static double[] costDeriv(double[] a, double[] y) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] - y[i]; //quadratic cost Derivative
        }
        return c;
    }

    static double[] sigmoid(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = 1 / (1 + Math.exp(-x[i]));
        }
        return y;
    }

    static double[] sigmoidDeriv(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = 1 / (1 + Math.exp(-x[i]));
            y[i] = y[i] * (1 - y[i]);
        }
        return y;
    }

    double getScale() {
        return scale;
    }
}
