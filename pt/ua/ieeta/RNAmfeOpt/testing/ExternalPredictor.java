
package pt.ua.ieeta.RNAmfeOpt.testing;

/**
 *
 * @author Paulo
 */
public abstract class ExternalPredictor extends Thread
{
    public abstract void setSequence(String sequence);
    public abstract double getEnergy();
    public abstract String getPredictorName();
}
