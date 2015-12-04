package clusteringAlgorithms;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileOut{   
   PrintStream fout; 
   BufferedReader fin;

 
   FileOut(String filename)
   {
	 Date d = new Date();
	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
     try{
         fout= new PrintStream (new FileOutputStream(dateFormat.format(d) + " " + filename + ".csv"));
     }catch(IOException fo){
         System.out.println(fo); 
      }
    }
   
   // Reads a file in
   FileOut(String filename, String in)
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
   public String reader(){
      try{
       return fin.readLine();     
       }catch(IOException e){
         System.out.println("error reading from file " + e);
         return "error\t";
      }
   }
}