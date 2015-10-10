/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbmnetwork;

/**
 *
 * @author Brendan Burns
 */
  public class Helpers
  {
    public static void ShowVector(double[] vector, int decimals, int valsPerLine, boolean blankLine)
    {
      for (int i = 0; i < vector.length; ++i)
      {
        if (i > 0 && i % valsPerLine == 0) // max of 12 values per row 
          System.out.println("");
        if (vector[i] >= 0.0) System.out.print(" ");
        System.out.print(vector[i] + " "); // n decimals
      }
      if (blankLine) System.out.println("\n");
    }

    public static void ShowVector(int[] vector, int valsPerLine, boolean blankLine)
    {
      for (int i = 0; i < vector.length; ++i)
      {
        if (i > 0 && i % valsPerLine == 0) // max of 12 values per row 
          System.out.println("");
        if (vector[i] >= 0.0) System.out.print(" ");
        System.out.print(vector[i] + " ");
      }
      if (blankLine) System.out.println("\n");
    }

    public static void ShowMatrix(double[][] matrix, int numRows)
    {
      int ct = 0;
      if (numRows == -1) 
          numRows = Integer.MAX_VALUE; // if numRows == -1, show all rows
      for (int i = 0; i < matrix.length && ct < numRows; ++i)
      {
        for (int j = 0; j < matrix[0].length; ++j)
        {
          if (matrix[i][j] >= 0.0) System.out.print(" "); // blank space instead of '+' sign
          System.out.print(matrix[i][j] + " ");
        }
        System.out.println("");
        ++ct;
      }
      System.out.println("");
    }
  } // class Helpers
