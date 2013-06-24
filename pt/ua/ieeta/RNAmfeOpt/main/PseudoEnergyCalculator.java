
package pt.ua.ieeta.RNAmfeOpt.main;

/**
 *
 * @author Paulo Gaspar
 */
public class PseudoEnergyCalculator 
{
    /*********************/
    /*     A  C  G  U    */
    /*   A 0  0  0  2
     *   C 0  0  3  0
     *   G 0  3  0  2
     *   U 2  0  2  0
     */
    private static final double [][] energyBondMatrix = {{0,0,0,2}, {0,0,3,0}, {0,3,0,2}, {2,0,2,0}};
    
    private static double AA = 0;
    private static double UU = 0;
    private static double CC = 0;
    private static double GG = 0;
    private static double AC = 0;
    private static double AG = 0;
    private static double CU = 0;
    private static double AU = 2;
    private static double CG = 3;
    private static double GU = 2;
    
    private static final int INITIAL_BLOCK_SIZE = 2; //initial number of nucleotides to test the folding
    private static final int MIN_FOLDING_GAP = 3; //mininum number of nucleotides in the folding zone
    
    public static void setBondEnergy(double AU, double CG, double GU)
    {
        PseudoEnergyCalculator.AU = AU;
        PseudoEnergyCalculator.CG = CG;
        PseudoEnergyCalculator.GU = GU;
    }
    
    public static synchronized double calculateEnergyEstimate(String sequence)
    {
        int FINAL_BLOCK_SIZE = (int) Math.floor(sequence.length()/2);
        double energyEstimate = 0;
        
        /* Fold on one side of the sequence... */
        int blockSize;
        for (blockSize = INITIAL_BLOCK_SIZE; blockSize < FINAL_BLOCK_SIZE; blockSize++)
        {
            if (sequence.length() < MIN_FOLDING_GAP+2*blockSize) break;
            StringBuilder sb = new StringBuilder(sequence.substring(MIN_FOLDING_GAP+blockSize, MIN_FOLDING_GAP+2*blockSize)).reverse();
            energyEstimate += getPseudoEnergy(sequence.substring(0, blockSize), sb.toString());
        }
        
        /* One nucleotide was out in the last block test? */
        if (((sequence.length() + MIN_FOLDING_GAP) % 2) != 0)
        {
            blockSize--;
            StringBuilder sb = new StringBuilder(sequence.substring(MIN_FOLDING_GAP+blockSize+1, MIN_FOLDING_GAP+2*blockSize+1)).reverse();
            energyEstimate += getPseudoEnergy(sequence.substring(0, blockSize), sb.toString());
        }
        
        sequence = new StringBuilder(sequence).reverse().toString();
        
        /* ...and then fold on the other side. */
        for (blockSize = INITIAL_BLOCK_SIZE; blockSize < FINAL_BLOCK_SIZE; blockSize++)
        {
            if (sequence.length() < MIN_FOLDING_GAP+2*blockSize) break;
            StringBuilder sb = new StringBuilder(sequence.substring(MIN_FOLDING_GAP+blockSize, MIN_FOLDING_GAP+2*blockSize)).reverse();
            energyEstimate += getPseudoEnergy(sequence.substring(0, blockSize), sb.toString());
        }
        
        return -energyEstimate;
    }
    
    /** Calculates an MFE estimate using the pseudo energy and a linear regression equation. 
     ** @param afterOptimization Indicates if the input sequence is an optimized sequence or not. */
    public static double calculateMFE(String sequence, boolean afterOptimization)
    {
        double pseudoEnergy = calculateEnergyEstimate(sequence);
        
        /* This first divides the pseudoEnergy by the sequence size to average the energy of
         * all calculated folds (it's not really a necessary step). Then it uses the linear 
         * regression formula to convert the value to an MFE range. */
        if (afterOptimization)
            return ((pseudoEnergy/sequence.length())+15.212)/0.5287;
        else
            return ((pseudoEnergy/sequence.length())+8.21526067512)/0.456482883313;
    }
    
    /* Given two nucleotide sequences, return the energy of their bond when they pair. */
    private static double getPseudoEnergy(String seq1, String seq2)
    {
        double bondEnergy = 0;
        
        for (int i = 0; i < seq1.length(); i++)
        {
            bondEnergy += getBondBetweenNucleotides(seq1.charAt(i), seq2.charAt(i));
        }
        
        return bondEnergy;
    }
    
    /* Get the energy bond between two paired nucleotides. */
    private static double getBondBetweenNucleotides(char n1, char n2)
    {
        if ((n1 == 'G' && n2 == 'C') || (n1 == 'C' && n2 == 'G'))
            return CG;
        else if ((n1 == 'A' && n2 == 'U') || (n1 == 'U' && n2 == 'A'))
            return AU;
        else if ((n1 == 'G' && n2 == 'U') || (n1 == 'U' && n2 == 'G'))
            return GU;
        
        return 0;
    }
}
