
package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public class BondEnergyOptimizationTarget extends IOptimizationTarget
{
    private static boolean DEBUG = true;
    
    /* GPV Weight value. */
    private double bondEnergy;
    private String bondName = null;

    /* Constraints */
    private static double MIN = 0;
    private static double MAX = 5;

    public BondEnergyOptimizationTarget(double bondEnergy, String name)
    {
        assert (bondEnergy >= MIN && bondEnergy <= MAX);
        assert name != null;

        this.bondEnergy = bondEnergy;
        this.bondName = name;
    }

    public double getBondEnergy()
    {
        return bondEnergy;
    }

    private void setBondEnergy(double bondEnergy)
    {
        assert (bondEnergy >= MIN && bondEnergy <= MAX);

        this.bondEnergy = bondEnergy;
    }

    @Override
    public void performMutation(int k, int kmax, double dispersionFactor)
    {
        assert k >= 0;
        assert kmax > 0;
        assert kmax >= k;
        assert dispersionFactor > 0;

        /* Control the dispersion factor. */
        dispersionFactor = dispersionFactor > 1 ? 1 : dispersionFactor;

        /* Calculate the mutation factor. */
        double unityUniformDistribution = Math.random() - 0.5;
        double mutationFactor = unityUniformDistribution * 2 * ((kmax - k) / (double) kmax) * dispersionFactor;

        /* Apply mutation. */
        double value = getBondEnergy();
        value = Math.min(MAX, Math.max(MIN, value + mutationFactor));

        if (DEBUG)
            System.out.println("Bond Energy " + bondName + " was " + getBondEnergy() + " and now is " + value + " (mutationFactor=" + mutationFactor + ")");

        /* Save value. */
        setBondEnergy(value);
    }
    
    @Override
    public void print()
    {
        System.out.println(bondName + " = " + bondEnergy);
    }
}
