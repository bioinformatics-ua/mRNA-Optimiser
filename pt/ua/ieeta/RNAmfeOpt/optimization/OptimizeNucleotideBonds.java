
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.sa.BondEnergyOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.SimulatedAnnealing;

/**
 *
 * @author Paulo Gaspar
 */
public class OptimizeNucleotideBonds extends Thread
{
    public SimulatedAnnealing sa;
    
    public OptimizeNucleotideBonds()
    {
        /* Create seed. */
        List<IOptimizationTarget> featureList = new Vector<IOptimizationTarget>();
        featureList.add(new BondEnergyOptimizationTarget(3, "CG")); //3
        featureList.add(new BondEnergyOptimizationTarget(2, "AU")); //2
        featureList.add(new BondEnergyOptimizationTarget(2, "GU")); //2
        EvolvingSolution seed = new EvolvingSolution(featureList);
        
        sa = new SimulatedAnnealing(new RunBondTestBench(), seed);
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
            Logger.getLogger(OptimizeNucleotideBonds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public EvolvingSolution getSolution()
    {
        return sa.getSolution();
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("* Starting bond energy optimization experiment *");
        
        OptimizeNucleotideBonds bondEnergyOptimizer = new OptimizeNucleotideBonds();
        bondEnergyOptimizer.start();
        bondEnergyOptimizer.join();
        
        System.out.println("* Terminated bond energy optimization experiment *");
        
        System.out.println("Solution: ");
        bondEnergyOptimizer.getSolution().print();
        
        System.out.println("Score: " + bondEnergyOptimizer.sa.getScore());
        
    }
}
