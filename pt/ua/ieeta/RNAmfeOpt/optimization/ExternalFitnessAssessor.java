
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.testing.ExternalPredictor;

/**
 *
 * @author Paulo Gaspar
 */
public class ExternalFitnessAssessor implements IFitnessAssessor
{
    private Class externalPredictorClass;
    
    public ExternalFitnessAssessor(ExternalPredictor predictor)
    {
//        assert predictor != null;
        
        this.externalPredictorClass = predictor.getClass();
//        this.predictor = predictor;
    }
    
    @Override
    public double getFitness(EvolvingSolution solution)
    {
        assert solution != null;
        
        CodonSequenceOptimizationTarget optimizedCodonSequence = (CodonSequenceOptimizationTarget) solution.getFeatureList().get(0);
        assert optimizedCodonSequence != null;
        
        /* Calculate pseudo energy. */
        return calculateFitness(optimizedCodonSequence.getSequence());
//        ViennaRNAFold rnaFold = new ViennaRNAFold(optimizedCodonSequence.getSequence());
//        rnaFold.start();
//
//        try
//        {
//            rnaFold.join();
//        } catch (InterruptedException ex)
//        {
//            Logger.getLogger(ExternalFitnessAssessor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return rnaFold.getEnergy();
    }
    
    public double calculateFitness(String sequence)
    {
        ExternalPredictor externalPredictor = null;
        try
        {
            externalPredictor = (ExternalPredictor) externalPredictorClass.newInstance();
            externalPredictor.setSequence(sequence);
            externalPredictor.start();
            externalPredictor.join();
        } 
        catch (Exception ex)
        {
            Logger.getLogger(ExternalFitnessAssessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return externalPredictor.getEnergy();
    }
}
