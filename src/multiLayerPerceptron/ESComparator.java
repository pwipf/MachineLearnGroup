/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package multiLayerPerceptron;
import java.util.Comparator;
import multiLayerPerceptron.Network;
import multiLayerPerceptron.EvolutionaryStrategy.Member;
/**
 *
 * @author Rob
 */

public class ESComparator implements Comparator<Member> {
        double[][] input, output;
        EvolutionaryStrategy e;
        public ESComparator(EvolutionaryStrategy e, double[][] input, double[][]output) {
            this.e = e;
            this.input = input;
            this.output = output;
        }
	@Override
	public int compare(Member m1, Member m2) {
            double m1Fitness = e.fitness(m1, input, output);
            double m2Fitness = e.fitness(m2, input, output);
            int result = 0;
            if (m1Fitness < m2Fitness) {
                result = -1;
            } else if (m1Fitness > m2Fitness) {
                result = 1;
            }
//            System.out.printf("m1 fitness = %f%nm2 fitness = %f%nResult = %d%n%n", m1Fitness, m2Fitness, result);
            return result;
        }

}
