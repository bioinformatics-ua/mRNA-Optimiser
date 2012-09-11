package pt.ua.ieeta.RNAmfeOpt.sa;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paulo
 */
public class EvolvingSolution
{
    /* The content of this solution. */

    private List<IOptimizationTarget> featureList;

    /* Constructor. Receives a list of features. */
    public EvolvingSolution(List<IOptimizationTarget> featureList)
    {
        assert featureList != null;
        assert !featureList.isEmpty();

        this.featureList = featureList;
    }

    /* Copy constructor. Receives an EvolvingSolution, and copies its features. */
    public EvolvingSolution(EvolvingSolution solution)
    {
        assert solution != null;
        assert solution.getFeatureList() != null;
        assert !solution.getFeatureList().isEmpty();

        /* Create a new array list. */
        this.featureList = new ArrayList<IOptimizationTarget>();

        /* Copy all features from the input solution. */
        for (IOptimizationTarget f : solution.getFeatureList())
            this.featureList.add(f.clone());
    }

    /**
     * Get the list of features in this solution.
     * @return the solution
     */
    public List<IOptimizationTarget> getFeatureList()
    {
        return featureList;
    }

    /**
     * Set the list of features for this solution.
     * @param solution the solution to set
     */
    public void setSolution(List<IOptimizationTarget> solution)
    {
        assert solution != null;

        this.featureList = solution;
    }

    public void print()
    {
        for (IOptimizationTarget f : featureList)
            f.print();
    }
}
