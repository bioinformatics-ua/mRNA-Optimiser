
package pt.ua.ieeta.RNAmfeOpt.optimization;

import pt.ua.ieeta.RNAmfeOpt.main.PseudoEnergyCalculator;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;

/**
 *
 * @author Paulo Gaspar
 */
public class PseudoEnergyGCContentFitnessAssessor implements IFitnessAssessor
{
    private double AU = 1.011575, CG = 3.117125, GU = 1.008516;
    private String originalSequence;
    private double originalEnergy;
    private double originalGCContent;
    
    public PseudoEnergyGCContentFitnessAssessor(String originalSequence)
    {
        assert originalSequence != null;
        assert !originalSequence.isEmpty();
        
        /* set energy bonds */
        PseudoEnergyCalculator.setBondEnergy(AU, CG, GU);
        
        this.originalSequence = originalSequence;
        originalEnergy = PseudoEnergyCalculator.calculateMFE(originalSequence, true); //calculateEnergyEstimate(originalSequence);        
        originalGCContent = getGCContent(originalSequence);
    }
    
    @Override
    public double getFitness(EvolvingSolution solution)
    {
        CodonSequenceOptimizationTarget optimizedCodonSequence = (CodonSequenceOptimizationTarget) solution.getFeatureList().get(0);
        
        /* Calculate pseudo energy. */
        double newEnergy = PseudoEnergyCalculator.calculateMFE(optimizedCodonSequence.getSequence(), true); //calculateEnergyEstimate(optimizedCodonSequence.getSequence());
        double newGCContent = getGCContent(optimizedCodonSequence.getSequence());
        
        double energyGain = ((newEnergy-originalEnergy)/Math.abs(originalEnergy)) * 100;
        double gcContentGain = ((newGCContent - originalGCContent) / originalGCContent) * 100;
        
        return energyGain - Math.abs(gcContentGain);
    }
    
    public static double getGCContent(String sequence)
    {
        assert sequence != null;
        assert !sequence.isEmpty();
        
        double count = 0;
        for (int i = 0; i<sequence.length(); i++)
        {
            if ((sequence.charAt(i) == 'G') || (sequence.charAt(i) == 'C'))
                count++;
        }
        
        return count / sequence.length();
    }
}
