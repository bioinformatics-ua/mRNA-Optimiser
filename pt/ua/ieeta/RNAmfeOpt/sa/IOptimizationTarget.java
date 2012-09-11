package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public abstract class IOptimizationTarget implements Cloneable
{

    protected String name = "Abstract optimization target";

    /** Cause this target to mutate (make a change) taking into consideration
     * the current iteration and dispersion factor. */
    public abstract void performMutation(int k, int kmax, double dispersionFactor);

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        assert name != null;

        this.name = name;
    }

    @Override
    public IOptimizationTarget clone()
    {
        try
        {
            return (IOptimizationTarget) super.clone();
        } catch (CloneNotSupportedException ex)
        {
            System.out.println("An exception occured when trying to clone a Feature: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IOptimizationTarget other = (IOptimizationTarget) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    public abstract void print();
}
