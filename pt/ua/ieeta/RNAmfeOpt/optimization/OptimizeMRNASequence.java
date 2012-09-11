
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.EnumMap;
import java.util.List;
import java.util.Vector;
import pt.ua.ieeta.RNAmfeOpt.main.OptimiserParameter;
import pt.ua.ieeta.RNAmfeOpt.sa.*;

/**
 *
 * @author Paulo Gaspar
 */
public class OptimizeMRNASequence
{
    public SimulatedAnnealing sa;
    
    /* Parameter list. */
    private EnumMap<OptimiserParameter, Object> params;
    
    public OptimizeMRNASequence(String sequence, IFitnessAssessor fitnessCalculator, EnumMap<OptimiserParameter, Object> params)
    {
        assert sequence != null;
        assert fitnessCalculator != null;
        assert params != null;
        
        int startNuc = (Integer) params.get(OptimiserParameter.BEGIN);
        int endNuc = (Integer) params.get(OptimiserParameter.END);
        
        assert (endNuc > startNuc) && ((endNuc  - startNuc + 1)%3 == 0) && (startNuc > 0) && (endNuc > 0);
        
        int geneticCodeTable = (Integer) params.get(OptimiserParameter.GCT);
        
        /* Create seed. */
        List<IOptimizationTarget> optimisationTargetList = new Vector<IOptimizationTarget>();
        CodonSequenceOptimizationTarget target = new CodonSequenceOptimizationTarget(sequence, geneticCodeTable, startNuc-1, endNuc-1);
        optimisationTargetList.add(target);
        EvolvingSolution seed = new EvolvingSolution(optimisationTargetList);
        
        /* Create a new simulated annealing task. */
        sa = new SimulatedAnnealing(fitnessCalculator, seed, 1, 0.85, 0.2, 0.1, 0.3);
        
        /* Set all stoping criteria. */
        int maxIterations = (Integer) params.get(OptimiserParameter.MAXITER);
        int maxTime = (Integer) params.get(OptimiserParameter.MAXTIME);
        double targetEnergy = (Double) params.get(OptimiserParameter.TARGETMFE);
        boolean optimisationType = ((Integer) params.get(OptimiserParameter.TYPE)) == 0;
        sa.setStopCriteria(maxIterations, maxTime, targetEnergy, optimisationType);
    }
    
    public void run()
    {
        sa.start();
        try
        {
            sa.join();
        } catch (InterruptedException ex)
        {
            System.out.println("An unknown error occured while running the optimization: " + ex.getMessage());
        }
    }
    
    public EvolvingSolution getSolution()
    {
        return sa.getSolution();
    }
    
    public static void main(String[] args)
    {
        System.out.println("* Starting codon sequence optimization experiment *");

        EnumMap<OptimiserParameter, Object> list = new EnumMap<OptimiserParameter, Object>(OptimiserParameter.class);
        
        String rnaSequence = "AUGGAGGUGGCUGGCUGUUUCUGCAACAUGGAGCUGGGGUGGGGCAUCCCAGUGUCAAAGACUGCAGAGGGGAUUGCUGCACUGCACAGCUUGCAAGCCUUUCCUGAUGACCAGGAGAGUUCCAUAACCAGGUCUGUAGUUCCCACCUUGGCAGACACAGCCAAGCCCUCAGCCCCAGUCACUUCCCACUCCCUGCUCUCCAGGUACCACCCGGGUCAGUGA";
        
        list.put(OptimiserParameter.SEQUENCE,  rnaSequence);
        list.put(OptimiserParameter.OUTPUT,    OptimiserParameter.OUTPUT.getDefaultValue());
        list.put(OptimiserParameter.BEGIN,     OptimiserParameter.BEGIN.getDefaultValue());
        list.put(OptimiserParameter.END,       rnaSequence.length());
        list.put(OptimiserParameter.TYPE,      OptimiserParameter.TYPE.getDefaultValue());
        list.put(OptimiserParameter.TARGETMFE, OptimiserParameter.TARGETMFE.getDefaultValue());
        list.put(OptimiserParameter.MAXTIME,   OptimiserParameter.MAXTIME.getDefaultValue());
        list.put(OptimiserParameter.MAXITER,   OptimiserParameter.MAXITER.getDefaultValue());
        list.put(OptimiserParameter.GCT,       OptimiserParameter.GCT.getDefaultValue());
        list.put(OptimiserParameter.QUIET,     OptimiserParameter.QUIET.getDefaultValue());
        list.put(OptimiserParameter.KEEPGC,    OptimiserParameter.KEEPGC.getDefaultValue());
        
        OptimizeMRNASequence codonSequenceOptimizer = new OptimizeMRNASequence(rnaSequence, new PseudoEnergyFitnessAssessor(), list);
        codonSequenceOptimizer.run();
        
        System.out.println("* Terminated codon sequence optimization experiment *");
        
        System.out.println("Solution: ");
        codonSequenceOptimizer.getSolution().print();
        
        System.out.println("Score: " + codonSequenceOptimizer.sa.getScore());
    }
}
