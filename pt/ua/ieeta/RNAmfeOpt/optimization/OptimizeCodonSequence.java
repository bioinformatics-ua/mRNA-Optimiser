
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.List;
import java.util.Vector;
import pt.ua.ieeta.RNAmfeOpt.sa.*;
import pt.ua.ieeta.RNAmfeOpt.testing.ViennaRNAFold;

/**
 *
 * @author Paulo Gaspar
 */
public class OptimizeCodonSequence extends Thread
{
    public SimulatedAnnealing sa;
    
    public OptimizeCodonSequence(String sequence, IFitnessAssessor fitnessCalculator, int numIterations, int startNuc, int endNuc)
    {
        assert sequence != null;
        assert fitnessCalculator != null;
        assert numIterations > 0;
        assert endNuc > startNuc;
        assert (endNuc  - startNuc + 1)%3 == 0; // number of coding nucleotides is multiple of 3
        assert startNuc > 0;
        assert endNuc > 0;
        
        /* Create seed. */
        List<IOptimizationTarget> targetList = new Vector<IOptimizationTarget>();
        targetList.add(new CodonSequenceOptimizationTarget(sequence, 1, startNuc-1, endNuc-1));
        EvolvingSolution seed = new EvolvingSolution(targetList);
        
        sa = new SimulatedAnnealing(fitnessCalculator, seed, numIterations, 0.85, 0.2, 0.1, 0.3);
    }
    
    public OptimizeCodonSequence(String codonSequence, IFitnessAssessor fitnessCalculator, int numIterations)
    {
        /* Default start and end nucleotides are the first and last ones. */
        this(codonSequence,  fitnessCalculator,  numIterations, 1, codonSequence.length());
    }
    
    @Override
    public void run()
    {
        sa.start();
        try
        {
            sa.join();
        } catch (InterruptedException ex)
        {
            System.out.println("An exception occured while running the simulated annealing: " + ex.getLocalizedMessage());
        }
    }
    
    public EvolvingSolution getSolution()
    {
        return sa.getSolution();
    }
    
    
    
    
    
    /*** TEST ***/
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("* Starting codon sequence optimization experiment *");

//        String sequence = "AUGGAGGUGGCUGGCUGUUUCUGCAACAUGGAGCUGGGGUGGGGCAUCCCAGUGUCAAAGACUGCAGAGGGGAUUGCUGCACUGCACAGCUUGCAAGCCUUUCCUGAUGACCAGGAGAGUUCCAUAACCAGGUCUGUAGUUCCCACCUUGGCAGACACAGCCAAGCCCUCAGCCCCAGUCACUUCCCACUCCCUGCUCUCCAGGUACCACCCGGGUCAGUGA";
        String sequence = "AUGGAGGUGGCUGGCUGUUUCUGCAACAUG";
//        OptimizeCodonSequence codonSequenceOptimizer = new OptimizeCodonSequence(sequence, new PseudoEnergyFitnessAssessor(), 4000);
        OptimizeCodonSequence codonSequenceOptimizer = new OptimizeCodonSequence(sequence, new ExternalFitnessAssessor(new ViennaRNAFold()), 4000);
        codonSequenceOptimizer.start();
        codonSequenceOptimizer.join();
        
        System.out.println("* Terminated codon sequence optimization experiment *");
        
        System.out.println("Solution: ");
        codonSequenceOptimizer.getSolution().print();
        
        System.out.println("Score: " + codonSequenceOptimizer.sa.getScore());
    }
}
