
package pt.ua.ieeta.RNAmfeOpt.main;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EnumMap;
import pt.ua.ieeta.RNAmfeOpt.geneticCodeTable.GeneticCodeTableParser;
import pt.ua.ieeta.RNAmfeOpt.optimization.OptimizeMRNASequence;
import pt.ua.ieeta.RNAmfeOpt.optimization.PseudoEnergyFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.optimization.PseudoEnergyGCContentFitnessAssessor;
import pt.ua.ieeta.RNAmfeOpt.sa.CodonSequenceOptimizationTarget;
import pt.ua.ieeta.RNAmfeOpt.sa.IFitnessAssessor;

/**
 *
 * @author Paulo
 * 
 * This is the optimiser class, where the optimisation process starts.
 * It receives a list of parameters with all the needed information,
 * and performs an mRNA MFE optimisation using the pseudo-energy 
 * strategy.
 */
public class RNAOptimiser
{
    /* Parameter list. */
    private EnumMap<OptimiserParameter, Object> params;
    
    private boolean isVerbose;
    private PrintStream output;
    private String rnaSequence;
    private IFitnessAssessor fitnessCalculator;
    
    private String finalSequence;

    public RNAOptimiser(EnumMap<OptimiserParameter, Object> params)
    {
        assert params != null;
        assert params.size() == OptimiserParameter.values().length;
        
        this.params = params;
        
        isVerbose = !((Boolean) params.get(OptimiserParameter.QUIET));
        output = (PrintStream) params.get(OptimiserParameter.OUTPUT);
        rnaSequence = (String) params.get(OptimiserParameter.SEQUENCE);
        
        /* Select how sequences are evaluated during optimisation. */
        boolean keepGC = (Boolean) params.get(OptimiserParameter.KEEPGC);
        if (keepGC)
            fitnessCalculator = new PseudoEnergyGCContentFitnessAssessor(rnaSequence);
        else
            fitnessCalculator = new PseudoEnergyFitnessAssessor();
    }
    
    /* Make the optimisation. */
    public void startOptimisation()
    {
        NumberFormat formatter = new DecimalFormat("########.##");
        double initialGCContent = PseudoEnergyGCContentFitnessAssessor.getGCContent(rnaSequence)*100;
        double initialEnergy = PseudoEnergyCalculator.calculateEnergyEstimate(rnaSequence);
        String gctName = GeneticCodeTableParser.getInstance().getGeneticCodeTableNames().get((Integer) params.get(OptimiserParameter.GCT));
        
        printline("Sequence size: " + rnaSequence.length(), false);
        printline("GC content initial percentage: " + formatter.format(initialGCContent) + "%", false);
        printline("Initial pseudo-energy: " + formatter.format(initialEnergy), false);
        printline("Using the " + gctName + " genetic code", false);
        
        /* Optimize sequence using pseudo energy. */
        printline("Optimisation started...", false);
        OptimizeMRNASequence optimizer = new OptimizeMRNASequence(rnaSequence, fitnessCalculator, params);
        optimizer.run(); //running on this thread, instead of launching a new thread.
        printline("Done!", false);
        
        finalSequence = ((CodonSequenceOptimizationTarget) optimizer.getSolution().getFeatureList().get(0)).getSequence().toString();
        double optimizedPseudoEnergy = optimizer.sa.getScore();
        double finalGCContent = PseudoEnergyGCContentFitnessAssessor.getGCContent(finalSequence)*100;
        double finalEnergy = PseudoEnergyCalculator.calculateEnergyEstimate(finalSequence);
        printline("GC content final percentage: " + formatter.format(finalGCContent) + "%", false);
        printline("Final pseudo-energy: " + formatter.format(finalEnergy), false);
        printline("Final optimised sequence:", false);
        printline(finalSequence, true);
    }
    
    /* Prints a string to the output stream only if not in quiet mode.
     * The quiet mode can be override with the bypassVerbosity parameter. */
    private void printline(String message, boolean bypassVerbosity)
    {
        if (isVerbose || bypassVerbosity)
            output.println(message);
    }
    
    public String getResult()
    {
        return finalSequence;
    }
}
