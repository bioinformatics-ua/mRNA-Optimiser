
package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public class BondsNeighbourGenerator implements INeighbourGenerator
{

    /* Only one bond energy is mutated. "mutationAffectedPercent" is ignored. */
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
        
        /* Calculate a random position, to select which parameter wil be changed. */
        int randomPos = (int) Math.round(Math.random() * (solution.getFeatureList().size()-1));
        
        /* Get parameters to optimize. */
        IOptimizationTarget targetToMutate = neighbour.getFeatureList().get(randomPos);
        
        /* Mutate selected parameter, considering the current iteration and dispersion factor. */
        targetToMutate.performMutation(k, kmax, dispersionFactor);
        
        return neighbour;
    }
    
}
