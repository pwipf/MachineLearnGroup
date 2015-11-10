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

// Network class.
// Has an array of inner class Layer objects, which each store the network state for that layer,
// including weight vector, velocity vector, bias value, and activation type for each node.
// Actually the activation type is for the layer. Could set it for each node but this would be
// tedious and seems very unhelpful to implement.
//
// For approximating a real valued function we find it necessary to use a linear activation on the
// output layer, to allow values outside the range (0,1), and sigmoid activations on all other
// layers to avoid the redundancy of linear nodes.
//
// The index scheme is: Layer object 0 is the input layer, so it is mostly ignored.
// The rest of the layers are the hidden layers with the last one being the output.
// The weights, etc. are indexed i'th node, j'th input.
//
class Network {

    static ArrayList<Member> population = new ArrayList<Member>();
    //static double population[][] = new double [numberIndividuals][numberChromosomes];
    static ArrayList<Double> populationFitness = new ArrayList<Double>();
    int[] sizes;	// sizes[0]=number of input nodes, sizes[len-1]=number of ouput nodes
    int layers;		// sizes.length
    int nInputs, nOutputs;
    int counter = 0;
    static Random gen = new Random();

    class Layer {

        double[][] w;				// weight
        double[][] v;				// velocity (for momentum)
        double[] b;				// bias
        boolean linAct;	// activation function (for layer)

        Layer(int n, int nFrom, boolean linAct) {
            w = new double[n][nFrom];
            v = new double[n][nFrom];
            b = new double[n];
            this.linAct = linAct;
        }
    }
    // the Network, an array of Layers
    Layer[] net;

    // constructor
    // set size, activation functions. True is linear
    Network(int[] sizes, boolean[] LinearAct) {
        this.sizes = sizes;
        layers = sizes.length;
        nInputs = sizes[0];
        nOutputs = sizes[sizes.length - 1];

        net = new Layer[layers];
        for (int i = 1; i < layers; i++) {
            net[i] = new Layer(sizes[i], sizes[i - 1], LinearAct[i]);
        }

        // set initial random weights/biases. Also velocities set to 0.
        initializeWeightsBiases();


    }

    // initializeWeightsBiases()
    // compresses initial weights deviation from mean 0
    // to avoid early saturation.
    private void initializeWeightsBiases() {
        for (int l = 1; l < layers; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                net[l].b[i] = random(0, 1);
                for (int j = 0; j < sizes[l - 1]; j++) {
                    net[l].w[i][j] = random(0, 1);// /Math.sqrt(sizes[l-1]));
                    net[l].v[i][j] = 0; //velocity starts at zero.
                }
            }
        }
    }

    // random()
    // returns gaussian distr. with mean and sd
    double random(double mean, double sd) {
        return gen.nextGaussian() * sd + mean;
    }

    // feedForward()
    // takes a vector of inputs, runs it through the network,
    // returns a vector of outputs.
    // (Had better be the same as the feedForward step in the backprop function below!)
    double[] feedForward(double[] input) {
        double[] a = input;
        double[] z;
        for (int l = 1; l < layers; l++) {
            z = vecAdd(matMult(net[l].w, a), net[l].b);
            if (net[l].linAct) {
                a = z;
            } else {
                a = sigmoid(z);
            }
        }
        return a;
    }

    // Backpropogation Trainer
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // trainDiffEv()
    // call with training data and parameters
    public void trainBackprop(double[][] input, double[][] output, int epochs, double eta, double mu) {
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


        double maxe = 0;// for graph

        for (int e = 0; e < epochs; e++) {
            //System.out.print(e+" ");
            shuffle(input, output, size);
            double error = 0;
            for (int i = 0; i < size; i++) {
                backProp(input, output, i, i + 1, eta, mu);
                error += ssCost(feedForward(input[i]), output[i]);
            }

            if (e == 0)//for graph
            {
                maxe = error / size;
            }
            //System.out.println(error/trains);
            point(map(e, 0, epochs, .05, 1), map(error / size, 0, maxe, .05, 1));//graph
        }
    }

    // backProp()
    // learning algorithm.
    // Runs one "mini-batch", accumulates weight and bias adjustments,  then makes updates.
    void backProp(double[][] input, double[][] y, int batchStart, int batchEnd, double eta, double mu) {

        // store the activations, delta errors, and inputs (z) to each neruon
        // for use in the backpropogation step.
        double[][] a = new double[layers][];
        double[][] delta = new double[layers][];
        double[][] z = new double[layers][]; // z is the vector to store "what went into the activation function"

        double[][][] deltaW = new double[layers][][]; // array to store accumulated weight updates
        double[][] deltaB = new double[layers][];			// array to store accumulated bias updates
        for (int i = 1; i < layers; i++) {//initialize
            deltaW[i] = new double[net[i].w.length][net[i].w[0].length];
            deltaB[i] = new double[net[i].b.length];
        }

        //backprop a mini-batch and accumulate the update for weights and biases
        //System.out.println("start mini-batch");
        for (int i = batchStart; i < batchEnd; i++) {
            a[0] = input[i]; // activation of first layer is simply the input
            int last = layers - 1;

            //feedForward, saving z's and a's
            for (int l = 1; l < layers; l++) {
                z[l] = vecAdd(matMult(net[l].w, a[l - 1]), net[l].b);
                if (net[l].linAct) {
                    a[l] = z[l]; //linear final layer activation
                } else {
                    a[l] = sigmoid(z[l]);
                }
            }


            // remove, this was to make sure we are using the same feedforward for training as for testing.
            if (a[last][0] != feedForward(input[i])[0]) {
                throw new RuntimeException("something fishy " + a[last][0] + ", " + feedForward(input[i])[0]);
            }


            // get cost vector at output, to start the backpropogation
            double[] cost = costDeriv(a[last], y[i]);
            //System.out.println("a: "+a[last][0]+" should be "+y[i][0]+" SSE: "+ssCost(a[last],y[i])[0]);

            if (net[last].linAct) {
                delta[last] = cost; // deriv of linear is 1
            } else {
                delta[last] = vElemMult(cost, sigmoidDeriv(z[last]));
            }


            //delta[last] = vecMult(delta[last],y[0]);

            // backprop!!!
            for (int l = layers - 2; l >= 1; l--) {
                if (net[l].linAct) {
                    delta[l] = matMult(transpose(net[l + 1].w), delta[l + 1]); // deriv of linear is 1
                } else {
                    delta[l] = vElemMult(matMult(transpose(net[l + 1].w), delta[l + 1]), sigmoidDeriv(z[l]));
                }
            }

            for (int l = 1; l < layers; l++) {
                deltaW[l] = matAdd(deltaW[l], matMult(delta[l], a[l - 1])); // add up the adjustment to each weight/bias
                deltaB[l] = vecAdd(deltaB[l], delta[l]);
            }
        } // end mini-batch

        // update weights, biases (gradient descent)
        double avg = eta / (batchEnd - batchStart);
        for (int l = 1; l < layers; l++) {

            net[l].v = matSub(matMult(mu, net[l].v), matMult(avg, deltaW[l]));
            net[l].w = matAdd(net[l].w, net[l].v);
            //net[l].w = matSub(net[l].w, matMult(avg,deltaW[l]));
            net[l].b = vecSub(net[l].b, vecMult(avg, delta[l]));
        }
    }

    // shuffle()
    // simple inefficient array shuffler,
    // shuffles the first n records of two arrays, a and b, preserving the association between a and b
    static void shuffle(double[][] a, double[][] b, int n) {
        for (int i = 0; i < n; i++) {
            int j = gen.nextInt(n);
            double[] temp = a[i];
            a[i] = a[j];
            a[j] = temp;

            temp = b[i];
            b[i] = b[j];
            b[j] = temp;
        }
    }

    // Differential Evolution Trainer
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // trainDiffEv()
    // call with training data and parameters
    public void trainDiffEv(double[][] input, double[][] output, int maxGen, int npop, double beta, double rho) {
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


        //initialize population. Each member is a weight matrix.
        Member[] population = new Member[npop];
        for (int i = 0; i < npop; i++) {
            population[i] = new Member();
            population[i].initRandom();
        }

        System.out.println("Created population (size " + npop + ")");

        //////////////////////
        // run DE algorithm
        int generation = 0;
        while (generation < maxGen) {
            generation++;
            for (int i = 0; i < npop; i++) {
                Member m = population[i];
                Member m2, m3;
                do {
                    m2 = population[gen.nextInt(npop)];
                } while (m2 == m);
                do {
                    m3 = population[gen.nextInt(npop)];
                } while (m3 == m2 || m3 == m);

                Member trial = mutate(m, m2, m3, beta);

                Member offspring = crossover(m, trial, rho);

                double fo = fitness(offspring, input, output);
                double fm = fitness(m, input, output);
                if (fo < fm) { //minimize fitness as it is just the error
                    population[i] = offspring;
                    fm = fo; //fm always has the lesser value, for the graph below
                }

                point(map(generation, 0, maxGen, .05, 1), map(fm, 0, 1, .05, 1));
                //System.out.println(fm);
            }
        }

        // had better set the network weights
        int best = 0;
        double fit = fitness(population[best], input, output);
        for (int i = 1; i < npop; i++) {
            double fit2 = fitness(population[i], input, output);
            if (fit2 < fit) {
                best = i;
                fit = fit2;
            }
        }
        //set network weights to population[best]
        for (int l = 1; l < layers; l++) {
            for (int i = 0; i < sizes[l]; i++) {
                for (int j = 0; j < sizes[l - 1]; j++) {
                    net[l].w[i][j] = population[best].w[l][i][j];
                }
                net[l].b[i] = population[best].b[l][i];
            }
        }
    }

    //maxGen max generations
    //npop is the population
    //beta is the mutation rate
    //rho is the crossover rate
    public void trainGeneticAlg(double[][] input, double[][] output, int maxGen, int npop, double beta, double rho, double mutationRate) {

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
             
            
            
            
            //Plot only fittest
            point(map(generation, 0, maxGen, .05, 1), map(populationFitness.get(getFittest()), 0, 1, .05, 1));
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
                    net[l].w[i][j] = population.get(best).w[l][i][j];
                }
                net[l].b[i] = population.get(best).b[l][i];
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
    public static void selectPopulation(double averageInverseFitness) {



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
                    population.set(secondParent, child1);
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
                        if (Math.random() <= mutationRate)
                        {
                        temp.w[l][i][j] = population.get(p).w[l][i][j] + ((beta * ((Math.random()*2)-1))*population.get(p).w[l][i][j]);    
                        //w[l][i][j] = gen.nextGaussian();
                        }
                        else
                        {
                        temp.w[l][i][j] = population.get(p).w[l][i][j];    
                        }
                        
                    }
                        if (Math.random() <= mutationRate)
                        {
                        temp.b[l][i] = population.get(p).b[l][i] + ((beta * ((Math.random()*2)-1))*population.get(p).b[l][i]);    
                        //w[l][i][j] = gen.nextGaussian();
                        }
                        else
                        {
                        temp.b[l][i] = population.get(p).b[l][i];    
                        }
                }
            }

        population.set(p, temp);

        }
    }
//Returns the fittest individual of the population.

    public static int getFittest() {
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
}
