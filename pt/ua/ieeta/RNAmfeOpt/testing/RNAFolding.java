
package pt.ua.ieeta.RNAmfeOpt.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Find the possible maximum number of G-C and A-U pairs in a given
 * RNA sequence. Then, display one of its optimal structure of folding.
 * @author Hyung-Joon Kim
 */
class RNAFolding {
   //---------------------------------------------------------------
   /** A string of RNA sequence */
   private String seq;
   /** The number of characters in the RNA sequence */
   private int n;   
   /** A matrix for computing max number of pairs */ 
   private int[][] B;
   /** An auxiliary matrix for traceback */
   private int[][] P;
   //----------------------------------------------------------------
    
   /**
    * Construct an instance with a given string of RNA sequence.
    */
   public RNAFolding(String s) {
      // Initialize the instance variables
      seq = s;
      n = seq.length();
      B = new int[n][n];
      P = new int[n][n];        
   }
   
   /**
    * Check G-C and A-U pairs forming in a RNA sequence.
    * @param xi, xj - two characters to be checked if they are paired.
    * @return 1 if they are paired, otherwise 0.
    */
   public int isPaired(char xi, char xj) {
      if ((xi == 'G' && xj == 'C') || (xi == 'C' && xj == 'G'))
         return 1;
      else if ((xi == 'A' && xj == 'U') || (xi == 'U' && xj == 'A'))
         return 1;
      else
         return 0;
   }
    
   /**
    * With Dynamic Programming, compute the possible max number of pairs
    * in a given RNA sequence.
    * @return max the maximum number of valid pairs.   
    */
   public int findMaxPairs() {
      int max, temp, pos;     
      for (int i=n-1; i>=0; i--) {
         for (int j=0; j<n; j++) {
            max = 0;
            //--------------------------------------------------------------
            // The algorithm looks at each recursive term to see whether it
            // gives the max num of pair. If the max. pair occurs in a 
            // particular term, the max. num will be stored in a matrix with 
            // the respective position indexed by i,j, and k. While doing that,
            // the algorithm also places a mark, such as -3, -2, -1, and k,
            // on the repective entry in a traceback matrix.
            //-------------------------------------------------------------- 
            
            // If i >= j - 4, then B[i,j] = 0. Since the matrix B is initialized
            // in the constructor, nothing needs to be done.
            if (i >= j - 4) {
               P[i][j] = -3;
            }            
            else { // If i < j - 4, then it's valid for folding.
               // Case1: i,j paired together or none of i and j is paired
               temp = B[i+1][j-1] + isPaired(seq.charAt(i),seq.charAt(j));
               if (temp > max ) {
                  max = temp;                 
                  P[i][j] = -1 - isPaired(seq.charAt(i),seq.charAt(j));                 
               }
               // Case2: i, j paired with k such that 1 <= k < j
               for (int k=i; k<j; k++) {
                  temp = B[i][k] + B[k+1][j];                   
                  if (temp > max) {
                     max = temp;                    
                     P[i][j] = k;
                  }
               }
               B[i][j] = max;               
            }      
         }
      }  
      return B[0][n-1]; // holds the maximum number of pairs
   }
    
   /**     
    * Trace back the possible maximum number of pairs, then consctruct 
    * the structure of folding using parens.
    */
   public void traceBack(int i, int j) {
      if (P[i][j] == -3) {  // Not paired
         for (int d=1; d <= j-i+1; d++) {
            System.out.print(".");
         }
      }
      else if (P[i][j] == -2) {  // i and j paired together
         System.out.print("(");         
         traceBack(i+1,j-1);
         System.out.print(")");         
      }
      else if (P[i][j] == -1) {  // i and j are not paired together
         System.out.print(".");
         traceBack(i+1,j-1);
         System.out.print(".");
      }
      else {  // i and j could be possibly paired with k such that i <= k < j
         int k = P[i][j];
         if ( i <= k && k+1 <= j ) {
            traceBack(i,k);
            traceBack(k+1,j);
         }        
      }      
   }
    
   /** 
    * Display a structure of RNA folding, having the max number of pairs.
    */
   public void showRNAFolding() {      
      System.out.println("\n     RNA sequence : " + seq);
      long time1 = System.currentTimeMillis();
      int max = findMaxPairs();  // Find the possible max num of folding
      System.out.print("Folding structure : ");
      traceBack(0,n-1);  // Trace back and construct the max num folding
      long time2 = System.currentTimeMillis();
      long elapse = time2 - time1;  // Determine its run-time 
      System.out.print("\n Folding analysis : ");
      System.out.println(max+" Pairs. The algorithm took "+(long)elapse+" ms.");      
      if (n <= 40) {
         System.out.println("\nComputed Nussinov-Jacobsen Matrix :\n");
         printMatrix();
      }
      else {
         System.out.println("\nNussinov-Jacobsen Matrix ommited due to its" +
         		" large size.");
      }
   }
    
   /** 
    * Print the entire matrix which shows how the computation of
    * the max number of pairs is processed.
    */
   public void printMatrix() {
      for (int i=0; i < B.length; i++) {
         for (int j=0; j < B.length; j++) {
            if (B[i][j] > 9) {  System.out.print(" "+B[i][j]); }
            else { System.out.print("  "+B[i][j]); }               
         }
         System.out.println();
      }
   }
   
   /**
    * Top-level function which creates an instance of 'RNAFolding' class and
    * invokes its methods.
    * @param args one or more strings of RNA sequence
    * @throws IOException
    */    
   public static void main(String[] args) throws IOException {
      // Allowable max length of a RNA sequence
      int MAX_SEQUENCE = 200000;      
      String seq ="";
      System.out.println("[ Enter a valid RNA sequence which consists of G, C, A," +
      		"and U.\n  The program takes a sequence of length up to 2000.\n  If the " +
      		"length of sequence exceeds 40, the computed matrix\n  will be ommited " +
      		"due to its large size. ]");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("\nEnter a string of RNA sequence, or 'exit' to exit" +
       		" the program.\n" + "=> ");        
      seq = br.readLine();
      seq = seq.toUpperCase();
      while (seq.toLowerCase().compareTo("exit") !=0) {         
         if (seq.length() <= MAX_SEQUENCE) {            
            // ---------------------------------------------------------
            //   Generate a random RNA sequence of length n.
            /*
            int n = 1800;
            String s = "";
            char c =' ';
            for (int i=0; i<n; i++) {
               double r = Math.random();
               if (r < .25) { c = 'G'; }
               else if (r < .5) { c = 'C'; }
               else if (r < .75) { c = 'A'; }
               else c = 'U';
               s = s + c;
            }
            RNAFolding t = new RNAFolding(s);
            */
            //-----------------------------------------------------------
            RNAFolding t = new RNAFolding(seq);
            t.showRNAFolding();   // display the result of RNA folding
            
            System.out.print("\nEnter a string of RNA sequence, or 'exit' to exit" +
             		" the program.\n" + "=> ");        
            seq = br.readLine();
            seq = seq.toUpperCase();
         }
         else {
            System.out.println("The RNA sequence is too long.");
            System.out.print("\nEnter a string of RNA sequence, or 'exit' to exit" +
             		" the program.\n" + "=> ");        
            seq = br.readLine();
            seq = seq.toUpperCase();
         }
      }      
   }
}