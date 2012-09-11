
package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public interface INeighbourGenerator
{
    /** Creates a new evolving solution that is neighbour to a given one. */
    public EvolvingSolution getNeighbour(EvolvingSolution solution, int k, int kmax, double dispersionFactor, double mutationAffectedPercent);
}