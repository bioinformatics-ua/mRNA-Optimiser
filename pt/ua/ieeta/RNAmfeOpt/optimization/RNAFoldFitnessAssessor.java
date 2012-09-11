
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.testing.ViennaRNAFold;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;

/**
 *
 * @author Paulo Gaspar
 */
public class RNAFoldFitnessAssessor implements IFitnessAssessor
{
    @Override
    public double getFitness(EvolvingSolution solution)
    {
        CodonSequenceOptimizationTarget optimizedCodonSequence = (CodonSequenceOptimizationTarget) solution.getFeatureList().get(0);
        
        /* Calculate pseudo energy. */
        ViennaRNAFold rnaFold = new ViennaRNAFold(optimizedCodonSequence.getSequence());
        rnaFold.start();

        try
        {
            rnaFold.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(RNAFoldFitnessAssessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rnaFold.getEnergy();
    }
}
