package pt.ua.ieeta.RNAmfeOpt.testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.main.PseudoEnergyCalculator;
import pt.ua.ieeta.RNAmfeOpt.optimization.GeneSets;
import pt.ua.ieeta.RNAmfeOpt.optimization.OptimizeCodonSequence;
import pt.ua.ieeta.RNAmfeOpt.optimization.PseudoEnergyGCContentFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;

/**
 * Test bench to optimize several genes using pseudo energy and also consider the
 * GC content of the Wild and Optimized genes
 * @author Paulo Gaspar
 */
public class TestBench4 extends Thread
{
    private static final boolean DEBUG = true;
    private static final int NUM_GENES_TO_TEST = 1; //MAX = 36
    private static final int START_AT_GENE = 35;
    private static final String setOfGenes[] = GeneSets.genesRandomSet2;
    
    private double[] accurateEnergyArray;
    
    private int numberOfIterations;
    
    public TestBench4(int numIters)
    {
        numberOfIterations = numIters;
    }
    
    @Override
    public void run()
    {
        accurateEnergyArray = new double[setOfGenes.length];
        
        /* Use pre-calculated accurate-mfe results instead of calling RNAfold. */
        System.arraycopy(GeneSets.preCalcRandomSet2, 0, accurateEnergyArray, 0, setOfGenes.length);
        
        for (int i = START_AT_GENE; i < START_AT_GENE+NUM_GENES_TO_TEST; i++)
        {
            /* Get a gene to optimize. */
            final String originalSequence = setOfGenes[i];
            final int index = i;
            
            /* Launch a new thread to optimize the sequence and calculate its scores. */
            new Thread( new Runnable() {

                @Override
                public void run()
                {
                    System.out.println("Starting thread " + index);
               
                    try
                    {
                        /* Optimize gene using pseudo energy. */
                        OptimizeCodonSequence optimizer = new OptimizeCodonSequence(originalSequence, new PseudoEnergyGCContentFitnessAssessor(originalSequence), numberOfIterations);
                        optimizer.start();
                        optimizer.join();
                        String optimizedSequence = ((CodonSequenceOptimizationTarget)optimizer.getSolution().getFeatureList().get(0)).getCodingSequence().toString();
                        double optimizedPseudoEnergy = optimizer.sa.getScore();

                        /* Calculate accurate energy of the optimized sequence using RNAfold. */
                        ViennaRNAFold rnaFold = new ViennaRNAFold(optimizedSequence);
                        rnaFold.start();
                        rnaFold.join();

                        if (DEBUG)
                        {
                            double originalPseudoEnergy = PseudoEnergyCalculator.calculateEnergyEstimate(originalSequence);
                            double optPseudoEnergy = PseudoEnergyCalculator.calculateEnergyEstimate(optimizedSequence);

                            double originalRNAfoldEnergy = accurateEnergyArray[index];
                            double optimizedRNAfoldEnergy = rnaFold.getEnergy();

                            double gcWild = PseudoEnergyGCContentFitnessAssessor.getGCContent(originalSequence);
                            double gcOptimized = PseudoEnergyGCContentFitnessAssessor.getGCContent(optimizedSequence);

                            double pseudoEnergyGain = ((optPseudoEnergy-originalPseudoEnergy)/Math.abs(originalPseudoEnergy)) * 100;
                            double gcContentGain = ((gcOptimized - gcWild) / gcWild) * 100;

                            double rnafoldEnergyGain = ((optimizedRNAfoldEnergy-originalRNAfoldEnergy)/Math.abs(originalRNAfoldEnergy)) * 100;

                            String message = index + " PseudoEnergyGain: " + pseudoEnergyGain + "\t\tGCGain: " + gcContentGain + "\t\tRNAFoldEnergy: " + optimizedRNAfoldEnergy;
                            System.out.println(message.replace(".", ","));
                        }
                    } 
                    catch (Exception ex)
                    {
                        Logger.getLogger(TestBench4.class.getName()).log(Level.SEVERE, null, ex);
                    }

//                    System.out.println("Ended thread " + index);
                }
            }).start();
            
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
        
        System.out.println("Test bench for mRNA mfe optimization with GC content");
        System.out.println("Num SA iterations for each gene: " + numIterations);
        System.out.println("---------------------------------------------------------");
        
        TestBench4 tb = new TestBench4(numIterations); //AU CG GU
        tb.start();
        tb.join();
        
        System.out.println("---------------------------------------------------------");
    }
    
   
    
}
