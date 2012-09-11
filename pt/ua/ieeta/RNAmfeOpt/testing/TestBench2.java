package pt.ua.ieeta.RNAmfeOpt.testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.optimization.GeneSets;
import pt.ua.ieeta.RNAmfeOpt.optimization.OptimizeCodonSequence;
import pt.ua.ieeta.RNAmfeOpt.optimization.PseudoEnergyFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;

/**
 * Test bench to optimize several genes using pseudo energy
 * @author Paulo Gaspar
 */
public class TestBench2 extends Thread
{
    private static final boolean DEBUG = true;
    private static final int NUM_GENES_TO_TEST = 36; //MAX = 36
    private static final String setOfGenes[] = GeneSets.genesRandomSet2;
    
    private double[] accurateEnergyArray, pseudoEnergyArray;
    
    private int numberOfIterations;
    
    public TestBench2(int numIters)
    {
        numberOfIterations = numIters;
    }
    
    @Override
    public void run()
    {
        accurateEnergyArray = new double[NUM_GENES_TO_TEST];
        pseudoEnergyArray = new double[NUM_GENES_TO_TEST];
        int index = 0;
        
        /* Use pre-calculated accurate-mfe results instead of calling RNAfold. */
//        System.arraycopy(GeneSets.preCalcRandomSet1, 0, accurateEnergyArray, 0, NUM_GENES_TO_TEST);
        
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
                OptimizeCodonSequence optimizer = new OptimizeCodonSequence(originalSequence, new PseudoEnergyFitnessAssessor(), numberOfIterations);
                optimizer.start();
                optimizer.join();
                
                String optimizedSequence = ((CodonSequenceOptimizationTarget)optimizer.getSolution().getFeatureList().get(0)).getCodingSequence().toString();
                double optimizedPseudoEnergy = optimizer.sa.getScore();
                
//                pseudoEnergyArray[index++] = pseudoEnergy;
                
                /* Calculate accurate energy of the optimized sequence. */
//                ViennaRNAFold rnaFold = new ViennaRNAFold(optimizedSequence);
//                rnaFold.start();
//                rnaFold.join();
//                accurateEnergyArray[index-1] = rnaFold.getEnergy();
                
                if (DEBUG)
                {
                    time = System.currentTimeMillis() - time;

                    String message = /*"AccurateEnergy: " + rnaFold.getEnergy() +*/ "\tPseudoEnergy: " + optimizedPseudoEnergy + "\tTime: " + time;
                    System.out.println(message.replace(".", ","));
                }
            } 
            catch (Exception ex)
            {
                Logger.getLogger(TestBench2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public double calculateCorrelation()
    {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = accurateEnergyArray[0];
        double mean_y = pseudoEnergyArray[0];
        
        for (int i = 2; i < accurateEnergyArray.length + 1; i += 1)
        {
            double sweep = Double.valueOf(i - 1) / i;
            double delta_x = accurateEnergyArray[i - 1] - mean_x;
            double delta_y = pseudoEnergyArray[i - 1] - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        
        double pop_sd_x = (double) Math.sqrt(sum_sq_x / accurateEnergyArray.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y / accurateEnergyArray.length);
        double cov_x_y = sum_coproduct / accurateEnergyArray.length;
        
        result = cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
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
        
        System.out.println("Test bench for mRNA mfe optimization (using pseudo energy)");
        System.out.println("Num SA iterations for each gene: " + numIterations);
        System.out.println("---------------------------------------------------------");
        
        TestBench2 tb = new TestBench2(numIterations); //AU CG GU
        tb.start();
        tb.join();
        
        System.out.println("---------------------------------------------------------");
    }
    
   
    
}
