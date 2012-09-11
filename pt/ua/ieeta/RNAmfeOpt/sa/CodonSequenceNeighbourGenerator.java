
package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public class CodonSequenceNeighbourGenerator implements INeighbourGenerator
{

    /* There is only one feature, which is the codon sequence. */
    @Override
    public EvolvingSolution getNeighbour(EvolvingSolution solution, int k, int kmax, double dispersionFactor, double mutationAffectedPercent)
    {
        assert solution != null;
        assert solution.getFeatureList() != null;
        assert k >= 0;
        assert kmax > 0;
        assert k <= kmax;        
        
        /* Create a neighbour solution object. */
        EvolvingSolution neighbour = new EvolvingSolution(solution);
        
        /* Get parameters to optimize. */
        IOptimizationTarget targetToMutate = neighbour.getFeatureList().get(0);
        
        /* Mutate selected parameter, considering the current iteration and dispersion factor. */
        targetToMutate.performMutation(k, kmax, dispersionFactor);
        
        return neighbour;
    }
    
}
