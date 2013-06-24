
package pt.ua.ieeta.RNAmfeOpt.optimization;

import pt.ua.ieeta.RNAmfeOpt.main.PseudoEnergyCalculator;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;

/**
 *
 * @author Paulo Gaspar
 */
public class PseudoEnergyFitnessAssessor implements IFitnessAssessor
{
    private double AU = 1.011575, CG = 3.117125, GU = 1.008516;

    public PseudoEnergyFitnessAssessor()
    {
        PseudoEnergyCalculator.setBondEnergy(AU, CG, GU); //set energy bonds!
    }
    
    @Override
    public double getFitness(EvolvingSolution solution)
    {
        CodonSequenceOptimizationTarget optimizedCodonSequence = (CodonSequenceOptimizationTarget) solution.getFeatureList().get(0);
        
        /* Calculate pseudo energy. */
        return PseudoEnergyCalculator.calculateMFE(optimizedCodonSequence.getSequence(), true); //calculateEnergyEstimate(optimizedCodonSequence.getSequence());
    }
}
