package nnBuilder;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ReadWrite{   
   PrintStream fout; 
   BufferedReader fin;

 
   ReadWrite()
   {
       String filename = "savedNets/nn_0.csv";
     try{
         fout= new PrintStream ( new FileOutputStream(filename));
     }catch(IOException fo){
         System.out.println(fo); 
      }
    }
   
   // Reads a file in
   ReadWrite(String filename, String in)
   {
       try{
       fin = new BufferedReader(new FileReader(filename));

     }catch(IOException fo){
         System.out.println(fo); 
      }
    }
   public void writer(String out)
   {
     
         fout.println(out);
      
   }
   public void writer(int out)
   {
     
         fout.println(out);
      
   }
   public void writer(char out)
   {
     
         fout.println(out);
      
   }
   public void writer(double out)
   {
     
         fout.println(out);
      
   }
   public void writer(float out)
   {
     
         fout.println(out);
      
   }
   public String reader() {
      try{
       return fin.readLine();     
       }catch(IOException e){
         System.out.println("error reading from file " + e);
         return "error\t";
      }
   }
   
   // NN specific functions
   // Write the neural net out to a csv
   public void writeNeuralNet(ArrayList<NNLayer> neuralNet) {
       StringBuilder sb = new StringBuilder();
       for (int i = 0; i < neuralNet.size(); i++) {
           NNLayer curLayer = neuralNet.get(i);
           for (int j = 0; j < curLayer.numNodes(); j++) {
               sb.append(String.format("%d,%d", i, j)); // First element in a line will be the node layer,
                                                         // The second indicates which ndoe in that layer
               for (int w = 0; w < curLayer.getNode(j).getWeights().length; w++) {
                   sb.append(String.format(",%f", curLayer.getNode(j).getWeights()[w]));
               }
               // Sends the line to the file
               writer(sb.toString());
               // Clear the string buffer for the next line
               sb.delete(0, sb.length());
           }
       }
   }
}