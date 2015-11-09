/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nn;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Brendan Burns
 */

public class GeneticAlg {
static int numberIndividuals = 1000;
//Based off number of attributes
static int numberChromosomes = 7;

//Range of values each gene in the chromosome can have
static int chromosomeRange = 5;
//Mutations add or subtract by the given offset
static int mutationOffset = 2;
//Chance of a crossover event with two parents
static double crossoverRate = .25;
//Chance of a mutation occuring per Chromosome gene in every individual
static double mutationRate = 0.015;
//When randomly constructing chromosomes, determines whenever the chromosome values will contain negatives.
static boolean randomIncludesNegatives = false;
    
//
//Population size isn't entirely static due to the random nature of the selection process.
//They're stored in a ArrayList so that array size will not matter
//Each arraylist element (individuals) all contain a chromosome array

static ArrayList<double[]> population = new ArrayList<double[]>();
//static double population[][] = new double [numberIndividuals][numberChromosomes];
static ArrayList<Double> populationFitness = new ArrayList<Double>();
//static double populationFitness[] = new double [numberIndividuals];
static double inputData[][];
static int numInputs;


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
      
     //Sample data that the current fitness function is based off of.   
        
      numInputs = 30;  
      inputData = new double[numInputs][]; // 30-item subset of Iris data set (10 each class)
      inputData[0] = new double[] { 5.1, 3.5, 1.4, 0.2, 0, 0, 1 }; // sepal length, sepal width, petal length, petal width -> 
      inputData[1] = new double[] { 4.9, 3.0, 1.4, 0.2, 0, 0, 1 }; // Iris setosa = 0 0 1, Iris versicolor = 0 1 0, Iris virginica = 1 0 0
      inputData[2] = new double[] { 4.7, 3.2, 1.3, 0.2, 0, 0, 1 };
      inputData[3] = new double[] { 4.6, 3.1, 1.5, 0.2, 0, 0, 1 };
      inputData[4] = new double[] { 5.0, 3.6, 1.4, 0.2, 0, 0, 1 };
      inputData[5] = new double[] { 5.4, 3.9, 1.7, 0.4, 0, 0, 1 };
      inputData[6] = new double[] { 4.6, 3.4, 1.4, 0.3, 0, 0, 1 };
      inputData[7] = new double[] { 5.0, 3.4, 1.5, 0.2, 0, 0, 1 };
      inputData[8] = new double[] { 4.4, 2.9, 1.4, 0.2, 0, 0, 1 };
      inputData[9] = new double[] { 4.9, 3.1, 1.5, 0.1, 0, 0, 1 };

      inputData[10] = new double[] { 7.0, 3.2, 4.7, 1.4, 0, 1, 0 };
      inputData[11] = new double[] { 6.4, 3.2, 4.5, 1.5, 0, 1, 0 };
      inputData[12] = new double[] { 6.9, 3.1, 4.9, 1.5, 0, 1, 0 };
      inputData[13] = new double[] { 5.5, 2.3, 4.0, 1.3, 0, 1, 0 };
      inputData[14] = new double[] { 6.5, 2.8, 4.6, 1.5, 0, 1, 0 };
      inputData[15] = new double[] { 5.7, 2.8, 4.5, 1.3, 0, 1, 0 };
      inputData[16] = new double[] { 6.3, 3.3, 4.7, 1.6, 0, 1, 0 };
      inputData[17] = new double[] { 4.9, 2.4, 3.3, 1.0, 0, 1, 0 };
      inputData[18] = new double[] { 6.6, 2.9, 4.6, 1.3, 0, 1, 0 };
      inputData[19] = new double[] { 5.2, 2.7, 3.9, 1.4, 0, 1, 0 };

      inputData[20] = new double[] { 6.3, 3.3, 6.0, 2.5, 1, 0, 0 };
      inputData[21] = new double[] { 5.8, 2.7, 5.1, 1.9, 1, 0, 0 };
      inputData[22] = new double[] { 7.1, 3.0, 5.9, 2.1, 1, 0, 0 };
      inputData[23] = new double[] { 6.3, 2.9, 5.6, 1.8, 1, 0, 0 };
      inputData[24] = new double[] { 6.5, 3.0, 5.8, 2.2, 1, 0, 0 };
      inputData[25] = new double[] { 7.6, 3.0, 6.6, 2.1, 1, 0, 0 };
      inputData[26] = new double[] { 4.9, 2.5, 4.5, 1.7, 1, 0, 0 };
      inputData[27] = new double[] { 7.3, 2.9, 6.3, 1.8, 1, 0, 0 };
      inputData[28] = new double[] { 6.7, 2.5, 5.8, 1.8, 1, 0, 0 };
      inputData[29] = new double[] { 7.2, 3.6, 6.1, 2.5, 1, 0, 0 };    
        
//Steps to create the Genetic Algorithm  
 //     
 ///
 /////
 //////     
      
      
   //Need to first create our random population.   
    createPopulation();   
    
    
   //Tuneable Variable, Generations until we stop the algorithm 
   int numberGenerations = 100;
    
    for (int i = 0; i<numberGenerations; i++)
    {
    //Calculate the average fitness between all individuals, the calculate method also stores their individual fitness scores    
    double averageFitness = calculateTotalFitness()/population.size();
    
    //Selects individuals based on their fitness ratio (individual/overall population)
    //A fitness ratio of 1.36 for example would the individual would progress to the next generation, with a 36% chance of including an additional copy
    selectPopulation(averageFitness);
    
    //Randomly picks a individual, pairs them with another individual. Swaps positions of a part of the chromosome, and forms two unique individuals as a result
    populationCrossover();
    //Randomly changes a gene within the chromosome with a small chance, and the difference is determined by the mutation offset
    mutatePopulation();
    
    }
    //Recalculate Fitness based on the Last Generation (So the individual's fitness correctly matches up with the current population)
    calculateTotalFitness();
    
    //Returns the chromosome of the fittest individual
    double[] fittest = getFittest();

    System.out.println("Highest Fitness Chromosome is ");
    for (int i = 0; i<fittest.length; i++)
    {
    System.out.println(fittest[i] + " ");    
    }
    
    }
    
    
    //Generate individuals with random chromosomes.
    public static void createPopulation()
    {
    
        
        
     double[] tempArray = new double[numberChromosomes];
    for (int i = 0; i<numberIndividuals; i++){
     for (int j = 0; j<numberChromosomes; j++){
         
        double chromosome = Math.random()*chromosomeRange;
        if (!randomIncludesNegatives)
        {chromosome = java.lang.Math.abs(chromosome);}
        
        tempArray[j] = chromosome;
         
     }
     population.add(tempArray);

       }  
    }
    
    //Average fitness is calculated by 1/sqrt(1+distance)
    //Identical values return a 1, the highest fitness, otherwise a decimal number is returned based off the distance.
    static double calculateTotalFitness()
    {
    //System.out.println("Calculating Fitness");
    //System.out.println(population.size());
    //System.out.println(numTraining);
    //System.out.println(numberChromosomes);
    populationFitness.clear();
    double totalFitness = 0;
    double individualFitness = 0;
    
    for (int i = 0; i<population.size(); i++){
    
     for (int z = 0; z<numInputs; z++) {  
     for (int j = 0; j<numberChromosomes; j++){
     
     //Fitness Function-----------------------------------------------------------------------------------------------------------
     //Currently only compares the Individual's own chromosome (population.get('individual')[chromosome slot]) with each 'solution' input
     //Higher fitness means the individual is closer to the majority of the inputs.
     
     //Probably will need to be modifed in a way so given an input (the individuals own chromosome) checks the output, and fitness is calculated from that.
         
         double distance = java.lang.Math.abs(inputData[z][j] - population.get(i)[j]);
         //System.out.println("Distance is " + distance);
         totalFitness += 1/(java.lang.Math.sqrt(1+distance));
         individualFitness += 1/(java.lang.Math.sqrt(1+distance));
     }
     //-------------------------------------------------------------------------------------------------------------------------------
     
     }
     //Store individual fitness
     populationFitness.add(individualFitness);
     individualFitness = 0;
     }  
    
    
    
        
    return totalFitness;     
    
    
}
    
    
//Probabilistically selecting new members of the population through fitness scoring.
//individual fitness/overall fitness

public static void selectPopulation(double averageFitness)
{
Random random = new Random();    
ArrayList<double[]> newPopulation = new ArrayList<double[]>();
    for (int i = 0; i<population.size(); i++){
    double fitnessRatio = populationFitness.get(i)/averageFitness;    
    //System.out.println("Individual Fitness " + populationFitness.get(i));
    //System.out.println("Average fitness is " + averageFitness);
    
    //Ratios at 100% or higher are guaranteed to be added to the next generation
    while (fitnessRatio >= 1)
    {
    //System.out.println(population.get(i)[0] + "added to newPopulation");      
    newPopulation.add(population.get(i));
    fitnessRatio -= 1;
    //System.out.println(newPopulation.get(i)[0]);
    }
    
    double chanceSelected = random.nextDouble();
    
    //Individual was successful in being selected to join the new population
    if (chanceSelected <= fitnessRatio)
    {
    newPopulation.add(population.get(i));    
    }
        
    }

    //System.out.println(newPopulation.get(0)[1]);
    //Replace population with the new population
    population.clear();
    
    for (int i = 0; i<newPopulation.size(); i++)
    {
    population.add(newPopulation.get(i));    
    }
    
    newPopulation.clear();
}


//For every individual in the population, splits and swaps parts of the chromosome between two individuals. The chance of this occuring is the crossoverRate
public static void populationCrossover()
{
    
    
for (int i = 0; i<population.size(); i++)    
{
//Crossover is successful    
if (Math.random() <= crossoverRate) {

int secondParent = (int)(Math.random()*population.size());
    
int chromosomeSplit = (int)(Math.random()*numberChromosomes);


double[] child1 = new double[numberChromosomes];
double[] child2 = new double[numberChromosomes];


for (int j = 0; j<numberChromosomes; j++)
{
if (j <= numberChromosomes )    
{
    child1[j] = population.get(i)[j];    
}
else
{
    child1[j] = population.get(secondParent)[j];
}

if (j <= numberChromosomes )    
{
    child2[j] = population.get(secondParent)[j];  
}
else
{
    child2[j] = population.get(i)[j];
}




}
population.set(i, child1);
population.set(secondParent, child2);

}
    
} 

}

public static void mutatePopulation()
{
for (int i = 0; i<population.size(); i++)    
{
    
    
for (int j = 0; j<numberChromosomes; j++)
{
//Mutation Event Occurs    
if (Math.random() <= mutationRate) 
{
//Even or Odd Offset
double EvenOrOdd = Math.random();
double[] tempArray = population.get(i);
if (EvenOrOdd >= .5)
{
tempArray[j] = tempArray[j]+(Math.random()*mutationOffset);    
}
else
{
tempArray[j] = tempArray[j]-(Math.random()*mutationOffset);    
}

//Add the editted array back into the list
population.set(i, tempArray);
}
    
}    
    
    
    
    
    
}




}
//Returns the fittest individual of the population.
public static double[] getFittest()
{
int fittestPosition = 0;
double highestFitness = 0;
for (int i = 0; i<population.size(); i++)
    {
    if (highestFitness < populationFitness.get(i))
        {
        highestFitness = populationFitness.get(i);
        fittestPosition = i;
        }
        
        
        
    }
    
return population.get(fittestPosition);
}

//Returns the top 'n' number of individuals
    public double[][] getTopFittest(int numberOfFittestIndividuals) {
        double[][] fittest = new double[numberOfFittestIndividuals][population.get(0).length];
        for (int z = 0; z<numberOfFittestIndividuals; z++)
        {    
        int fittestPosition = 0;
        double highestFitness = 0;
        for (int i = 0; i < population.size(); i++) {
            if (highestFitness < populationFitness.get(i)) {
                highestFitness = populationFitness.get(i);
                fittestPosition = i;
            
        }
    }
                    for (int c = 0; c < population.get(0).length; c++) 
                    {
                    fittest[z][c] = population.get(fittestPosition)[c];
                    }
                
                
                
                populationFitness.remove(fittestPosition);    
        
        
        
        
        
    }
        return fittest;
}


}