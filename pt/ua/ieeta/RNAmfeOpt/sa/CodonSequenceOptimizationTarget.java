
package pt.ua.ieeta.RNAmfeOpt.sa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import pt.ua.ieeta.RNAmfeOpt.geneticCodeTable.GeneticCodeTable;
import pt.ua.ieeta.RNAmfeOpt.geneticCodeTable.GeneticCodeTableParser;

/**
 *
 * @author Paulo Gaspar
 */
public class CodonSequenceOptimizationTarget extends IOptimizationTarget
{
    private static boolean DEBUG = false;
    
    /* Gene codon sequence. */
    private StringBuilder codingSequence;
    private int codingStartIndex, codingEndIndex;
    
    /* Non-coding zones. */
    private String beginingSequence;
    private String endingSequence;
    
    
    /* Genetic code table to use. */
    GeneticCodeTable gct;
    int gctID;

    public CodonSequenceOptimizationTarget(String rnaSequence, int geneticCodeTable, int codingStartIndex, int codingEndIndex)
    {
        assert rnaSequence != null;
        assert !rnaSequence.isEmpty();
        assert geneticCodeTable > 0;
        assert codingEndIndex > codingStartIndex;
        assert (codingEndIndex  - codingStartIndex + 1)%3 == 0; // number of coding nucleotides is multiple of 3

        this.codingStartIndex = codingStartIndex;
        this.codingEndIndex = codingEndIndex;
        
        /* Divide mRNA sequence into parts: the coding part, and the remaining anterior and posterior parts. */
        this.codingSequence = new StringBuilder(rnaSequence.subSequence(codingStartIndex, codingEndIndex+1));
        this.beginingSequence = rnaSequence.substring(0, codingStartIndex);
        this.endingSequence = rnaSequence.substring(codingEndIndex+1, rnaSequence.length());
                
        try
        {
            this.gct = GeneticCodeTableParser.getInstance().getCodeTableByID(geneticCodeTable);
            this.gctID = geneticCodeTable;
        } 
        catch (FileNotFoundException ex)
        {
            System.out.println("Error: Could not find the genetic code table file: " + ex.getLocalizedMessage());
        } 
        catch (IOException ex)
        {
            System.out.println("Error: Could not read the genetic code table file: " + ex.getLocalizedMessage());
        }
    }
    
    @Override
    public CodonSequenceOptimizationTarget clone()
    {
        return new CodonSequenceOptimizationTarget(getSequence(), gctID, codingStartIndex, codingEndIndex);
    }

    public StringBuilder getCodingSequence()
    {
        return codingSequence;
    }
    
    public String getSequence()
    {
        String sequence = beginingSequence + getCodingSequence().toString() + endingSequence;
//        assert sequence.length() == 
        return sequence;
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

        /* Calculate the percentage of mutated codons. */
        double mutatedPercent = ((kmax - k) / (double) kmax) * dispersionFactor;

        /* Apply mutation. */
        int numCodons = getCodingSequence().length() / 3;
        int numAffectedCodons = (int) Math.max(mutatedPercent * numCodons, 1);
        assert numAffectedCodons > 0;
        StringBuilder sequence = getCodingSequence();
        while (numAffectedCodons > 0)
        {
            int randomIndex = (int) (Math.random() * numCodons);
            String codon = sequence.substring(randomIndex*3, randomIndex*3+3);
            String newCodon = getSynonymousCodon(codon);
            
            sequence.replace(randomIndex*3, randomIndex*3+3, newCodon);
            
            if (DEBUG)
                System.out.println("Changed codon " + codon + " to " + newCodon);

            numAffectedCodons--;
        }
    }
    
    private String getSynonymousCodon(String codon)
    {
        Vector<String> codonList = gct.getSynonymousFromCodon(codon);
        int randomIndex = (int) (Math.random() * codonList.size());
        
        return codonList.get(randomIndex);
    }

    @Override
    public void print()
    {
        System.out.println("Sequence: " + codingSequence);
    }
}