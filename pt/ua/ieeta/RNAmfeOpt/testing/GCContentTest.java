package pt.ua.ieeta.RNAmfeOpt.testing;

import pt.ua.ieeta.RNAmfeOpt.optimization.GeneSets;

/**
 * Check the number of Guanin and Citosine in each gene
 * @author Paulo Gaspar
 */
public class GCContentTest extends Thread
{
    private static final boolean DEBUG = true;
    private static final int NUM_GENES_TO_TEST = 36;
    
    private double[] accurateEnergyArray, pseudoEnergyArray;
    
    public GCContentTest()
    {
    }
    
    @Override
    public void run()
    {
        String setOfGenes[] = GeneSets.genesRandomSet2;
        
        for (int i = 0; i < NUM_GENES_TO_TEST; i++)
        {
            /* Get a gene to test. */
            String sequence = setOfGenes[i];
            
            System.out.println("GC content: " + getGCContent(sequence));
        }
    }
    
    private float getGCContent(String sequence)
    {
        float GCContent = 0;
        for (int i = 0; i < sequence.length(); i++)
        {
            char nucleotide = sequence.charAt(i);
            if ((nucleotide == 'G') || (nucleotide == 'C'))
                GCContent++;
        }
        
        return GCContent/sequence.length();
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Testing GC content of genes ");
        System.out.println("---------------------------------------------------------");
        
        GCContentTest tb = new GCContentTest();
        tb.start();
        tb.join();
        
        System.out.println("---------------------------------------------------------");
    }

    
}
