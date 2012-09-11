
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.testing.TestBench;
import pt.ua.ieeta.RNAmfeOpt.sa.BondEnergyOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.EvolvingSolution;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;

/**
 *
 * @author Paulo Gaspar
 */
public class RunBondTestBench implements IFitnessAssessor
{

    @Override
    public double getFitness(EvolvingSolution solution)
    {
        BondEnergyOptimizationTarget cg = (BondEnergyOptimizationTarget) solution.getFeatureList().get(0);
        BondEnergyOptimizationTarget au = (BondEnergyOptimizationTarget) solution.getFeatureList().get(1);
        BondEnergyOptimizationTarget gu = (BondEnergyOptimizationTarget) solution.getFeatureList().get(2);
        
        /* Run test bench in several genes. */
        TestBench newTest = new TestBench(au.getBondEnergy(), cg.getBondEnergy(), gu.getBondEnergy());
        newTest.start();
        
        /* Wait to finish. */
        try
        {
            newTest.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(RunBondTestBench.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double correlation = newTest.calculateCorrelation();
        System.out.println("Performed test with: AU= " + au.getBondEnergy() + "  CG= " + cg.getBondEnergy() + "  GU= " + gu.getBondEnergy() + "    Resulting Correlation: " + correlation); 
        
        
        /* Calculate correlation between RNAfold and pseudo-energy. */
        return correlation;
    }

}
