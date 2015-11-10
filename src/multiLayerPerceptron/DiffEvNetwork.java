/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiLayerPerceptron;

import static multiLayerPerceptron.MatMath.*;
import static multiLayerPerceptron.StdDraw.*;

/**
 *
 * @author Magpie
 */
public class DiffEvNetwork extends Network{

	DiffEvNetwork(int[] sizes, double scale){
		super(sizes);
	}

	@Override
	public void train(double[][] input, double[][] output, double[] parameters){
		int maxGen=(int)parameters[0];
		int npop  =(int)parameters[1];
		double beta=parameters[2];
		double pi  =parameters[3];
		int size=input.length;
		if(size==0 || size != output.length)
			throw new RuntimeException("Input number != Output number (or zero)");
		if(input[0].length != nInputs)
			throw new RuntimeException("Input size doesn't match network");
		if(output[0].length != nOutputs)
			throw new RuntimeException("Output size doesn't match network");


		//initialize population. Each member is a bunch of weights and biases.
		Member[] population=new Member[npop];
		for(int i=0;i<npop;i++){
			population[i]=new Member();
			population[i].initRandom();
		}

		//////////////////////
		// run DE algorithm
		int generation=0;
		while(generation<maxGen){
			generation++;
			for(int i=0;i<npop;i++){
				Member m=population[i];
				Member m2,m3;
				do{
					m2= population[gen.nextInt(npop)];
				}while(m2==m);
				do{
					m3=population[gen.nextInt(npop)];
				}while(m3==m2 || m3==m);

				Member trial=mutate(m,m2,m3,beta);

				Member offspring=crossover(m,trial,pi);

				double fo=fitness(offspring,input,output);
				double fm=fitness(m,input,output);
				if(fo < fm){ //minimize fitness as it is just the error
					population[i]=offspring;
					fm=fo; //fm always has the lesser value, for the graph below
				}

				point(map(generation,0,maxGen,.05,1),map(fm,0,1,.05,1));
				//System.out.println(fm);
			}
		}

		// had better set the network weights
		int best=0;
		double fit=fitness(population[best],input,output);
		for(int i=1;i<npop;i++){
			double fit2=fitness(population[i],input,output);
			if(fit2<fit){
				best=i;
				fit=fit2;
			}
		}
		//set network weights to population[best]
		for(int l=1;l<layers;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					w[l][i][j]=population[best].w[l][i][j];
				}
				b[l][i]=population[best].b[l][i];
			}
		}
	}

	class Member{
		double[][][] w;
		double[][]   b;


		Member(){
			w=new double[sizes.length][][];
			b=new double[sizes.length][];
			for(int l=1;l<sizes.length;l++){
				w[l]=new double[sizes[l]][sizes[l-1]];
				b[l]=new double[sizes[l]];
			}
		}

		void initRandom(){
			for(int l=1;l<sizes.length;l++){
				for(int i=0;i<sizes[l];i++){
					for(int j=0;j<sizes[l-1];j++){
						w[l][i][j]=gen.nextGaussian();
					}
					b[l][i]=gen.nextGaussian();
				}
			}
		}
	}

	double fitness(Member m, double[][] input, double[][] output){
		int size=input.length;
		double error=0;
		for(int i=0;i<size;i++){
			double[] netOutput=feedForward(m,input[i]);
			error += ssCost(netOutput,output[i]);
		}
		error/=size;
		return error;
	}

	double[] feedForward(Member m, double[] input){
		double[] a = input;
		for(int l=1;l<layers;l++){
			a=sigmoid(vecAdd(matMult(m.w[l],a), m.b[l]));
		}
		return a;
	}

	Member mutate(Member m1, Member m2, Member m3, double beta){
		Member temp= new Member();
		for(int l=1;l<sizes.length;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					temp.w[l][i][j]=m1.w[l][i][j] + beta*(m2.w[l][i][j]-m3.w[l][i][j]);
				}
				temp.b[l][i]=m1.b[l][i] + beta*(m2.b[l][i]-m3.b[l][i]);
			}
		}
		return temp;
	}

	Member crossover(Member parent, Member milkman, double rho){
		Member temp=new Member();
		for(int l=1;l<sizes.length;l++){
			for(int i=0;i<sizes[l];i++){
				for(int j=0;j<sizes[l-1];j++){
					temp.w[l][i][j]=(gen.nextDouble()<rho)? parent.w[l][i][j] : milkman.w[l][i][j];
				}
				temp.b[l][i]=(gen.nextDouble()<rho)? parent.b[l][i] : milkman.b[l][i];
			}
		}
		return temp;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////


}
