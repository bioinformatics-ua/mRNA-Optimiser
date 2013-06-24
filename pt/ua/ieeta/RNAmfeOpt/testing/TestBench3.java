package pt.ua.ieeta.RNAmfeOpt.testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.optimization.ExternalFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.optimization.GeneSets;
import pt.ua.ieeta.RNAmfeOpt.optimization.OptimizeCodonSequence;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;

/**
 * Test bench to optimize several genes using an external 
 * evaluator (such as RNAfold).
 * 
 * @author Paulo Gaspar
 */
public class TestBench3 extends Thread
{
    private static final boolean DEBUG = true;
    private static final int NUM_GENES_TO_TEST = 36; //MAX random set 2 = 36
    
    private static final ExternalPredictor predictor = new UNAFold(); //RNAStructure(); //Pknots(); //UNAFold(); //ViennaRNAFold();
    
    private int numberOfIterations;
    
    public TestBench3(int numIters)
    {
        numberOfIterations = numIters;
    }
    
    @Override
    public void run()
    {
        String setOfGenes[] = GeneSets.genesRandomSet2;
        
        long time = 0;
        for (int i = 0; i < NUM_GENES_TO_TEST; i++)
        {
            /* Get a gene to optimize. */
            String originalSequence = setOfGenes[i];
            
            try
            {
                if (DEBUG) 
                    time = System.currentTimeMillis();
                
                /* Optimize gene. */
                OptimizeCodonSequence optimizer = new OptimizeCodonSequence(originalSequence, new ExternalFitnessAssessor(predictor), numberOfIterations);
                optimizer.start();
                optimizer.join();
                                
                if (DEBUG)
                {
                    time = System.currentTimeMillis() - time;

                    String optimizedSequence = ((CodonSequenceOptimizationTarget)optimizer.getSolution().getFeatureList().get(0)).getCodingSequence().toString();
                    String message = "Energy [" + predictor.getPredictorName() + "]: " + optimizer.sa.getScore() + "\tTime: " + time + "\tSequence: " + optimizedSequence;
                    System.out.println(message.replace(".", ","));  
                }
            } 
            catch (Exception ex)
            {
                Logger.getLogger(TestBench3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        int numIterations = 4000;
        if (args.length > 0)
        {
            String iter = args[0];
            if (iter.matches("[1-9][0-9]*"))
                numIterations = Integer.parseInt(iter);
        }
        
        System.out.println("Test bench for mRNA mfe optimization (using "+predictor.getPredictorName()+")");
        System.out.println("Num SA iterations for each gene: " + numIterations);
        System.out.println("---------------------------------------------------------");

        TestBench3 tb = new TestBench3(numIterations);
        tb.start();
        tb.join();
        
        System.out.println("---------------------------------------------------------");
    }
    
   
    
}
