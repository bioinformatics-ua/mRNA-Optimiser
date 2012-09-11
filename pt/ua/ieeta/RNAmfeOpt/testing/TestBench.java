package pt.ua.ieeta.RNAmfeOpt.testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.main.PseudoEnergyCalculator;
import pt.ua.ieeta.RNAmfeOpt.optimization.GeneSets;

/**
 * Test bench to try different energy bond weights in several genes
 * @author Paulo Gaspar
 */
public class TestBench extends Thread
{
    private static final boolean DEBUG = true;
    private static final int NUM_GENES_TO_TEST = 36;
    
    private double[] accurateEnergyArray, pseudoEnergyArray;
    
    private double AU = 2, CG = 3, GU = 2;

    public TestBench(double AU, double CG, double GU)
    {
        this.AU = AU;
        this.CG = CG;
        this.GU = GU;
    }
    
    @Override
    public void run()
    {
        accurateEnergyArray = new double[NUM_GENES_TO_TEST];
        pseudoEnergyArray = new double[NUM_GENES_TO_TEST];
        int index = 0;
        
        String setOfGenes[] = GeneSets.genesRandomSet2;
        
        /* Use pre-calculated accurate-mfe results instead of calling RNAfold. */
        System.arraycopy(GeneSets.preCalcRandomSet2, 0, accurateEnergyArray, 0, NUM_GENES_TO_TEST);
        
        long time = 0;
        for (int i = 0; i < NUM_GENES_TO_TEST; i++)
        {
            /* Get a gene to test. */
            String sequence = setOfGenes[i];
            
            try
            {
                if (DEBUG) 
                    time = System.currentTimeMillis();
                
                /* Calculate pseudo energy. */
                PseudoEnergyCalculator.setBondEnergy(AU, CG, GU); //set energy bonds!
                double pseudoEnergy = PseudoEnergyCalculator.calculateEnergyEstimate(sequence);
                pseudoEnergyArray[index++] = pseudoEnergy;
                
                /* Calculate accurate energy. */
//                ViennaRNAFold rnaFold = new ViennaRNAFold(sequence);
//                rnaFold.start();
//                rnaFold.join();
//                accurateEnergyArray[index-1] = rnaFold.getEnergy();
                
                if (DEBUG)
                {
                    time = System.currentTimeMillis() - time;

                    String message = "AccurateEnergy: " + accurateEnergyArray[index-1] + "\tPseudoEnergy: " + pseudoEnergy + "\tTime: " + time;
                    System.out.println(message.replace(".", ","));
                }
            } 
            catch (Exception ex)
            {
                Logger.getLogger(TestBench.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("Test bench for mRNA mfe pseudo energy calculation");
        System.out.println("---------------------------------------------------------");
        
//        TestBench tb = new TestBench(2, 3 ,2); //AU CG GU
        TestBench tb = new TestBench(1.011575, 3.117125, 1.008516); //AU CG GU
//        TestBench tb = new TestBench(1.319552, 3.607064, 1.176796); //AU CG GU
        tb.start();
        tb.join();
        
        System.out.println("---------------------------------------------------------");
        System.out.println("Correlation: " + tb.calculateCorrelation());
    }
}
