package pt.ua.ieeta.RNAmfeOpt.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;
import pt.ua.ieeta.RNAmfeOpt.geneticCodeTable.GeneticCodeTableParser;

/**
 *
 * @author Paulo Gaspar
 */
public class ErrorHandler 
{
    
    /* Patterns to identify errors in rna sequences. */
    private static Pattern pattern1 = Pattern.compile("^[ACGUacgu]+$"); //Correct pattern
    private static Pattern pattern2 = Pattern.compile(".*\\d+.*"); //has numbers
    private static Pattern pattern3 = Pattern.compile("[A-Z]+"); //has letters other than ACUG
    private static Pattern pattern4 = Pattern.compile(".*[^A-Z0-9]+.*"); //has symbols
    public static int checkSequenceIsValid(String rnaSequence)
    {
        assert rnaSequence != null;
        
        /* Empty sequence is not valid. */
        if (rnaSequence.isEmpty()) return 1;
        
        /* Correctly matches pattern. */
        if (pattern1.matcher(rnaSequence).matches()) 
            return 0;
        
        if (pattern2.matcher(rnaSequence).matches()) 
            return 2;
        
        if (pattern3.matcher(rnaSequence).matches()) 
            return 3;
        
        if (pattern4.matcher(rnaSequence).matches()) 
            return 4;
        
        return 99;
    }
    
    public static String getErrorMessage(int errorCode)
    {
        assert errorCode >= 0;
        
        switch (errorCode)
        {
            case 0: return "No error"; //should not reach this
            case 98: return "Genetic Code Table file was not found, or could not read it.";
            case 99: return "Unknown error";
            default: return "Unknown error";
            
            /* Sequence errors. */
            case 1: return "Empty sequence";
            case 2: return "The sequence contains numbers";
            case 3: return "The sequence contains invalid letters.";
            case 4: return "Invalid characters inside the sequence";
            
            /* Begining index errors. */
            case 50: return "The begining index you provided is not valid.\nIt must be an integer number in the range [1 , sequence size-2].";
            case 51: return "The begining index you provided is not in the correct format.\nIt must be an integer number in the range [1 , sequence size-2].";
            case 52: return "The begining index you provided is out of bounds.\nIt must be an integer number in the range [1 , sequence size-2].";
            
            /* Ending index errors. */
            case 60: return "The ending index you provided is not valid.\nIt must be an integer number in the range [3 , sequence size].";
            case 61: return "The ending index you provided is not in the correct format.\nIt must be an integer number in the range [3 , sequence size].";
            case 62: return "The ending index you provided is out of bounds.\nIt must be an integer number in the range [3 , sequence size].";
            
            /* General index errors. */
            case 67: return "Your sequence length is not multiple of three. The sequence to be optimized must be a codon sequence, hence a multiple of three nucleotides.";
            case 68: return "The range you supplied is not a multiple of three. The sub-sequence to be optimized must be a codon sequence, hence a multiple of three nucleotides.";
            case 69: return "The end index is larger than the begining index, or is not wide enought to acommodate a single codon.\nEnding Index must be greater than Begining index + 1.";
            
            /* Other parameter errors. */
            case 70: return "The type of optimization you specified is invalid.\nIt must be 0 (for MFE maximization) or 1 (for MFE minimization).";
            case 71: return "The minimum (or maximum) free energy you supplied is invalid.\nPlease use a POSITIVE floating point number without any units.\nWe will convert it to a negative number internally.\ne.g.:  213.5  (which will become -213.5)";
            case 72: return "The provided maximum time is invalid.\nIt must be a positive integer number, without units.\ne.g.:   5";
            case 73: return "The genetic code table index you provided is not valid.\nPlease use a positive integer number, relating to the code table to use, according to:\nhttp://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi";
            case 74: return "Could not create the output file you requested. Perhaps you don't have enought permissions.";
            case 75: return "The number of iterations you provided is invalid.\nPlease use a positive integer number. For better results use numbers larger than 1000.";
        }
    }
    
    public static int checkParameters(OptionSet set, String rnaSequence)
    {
        assert set != null;
        assert rnaSequence != null;
        assert !rnaSequence.isEmpty();
        
        if (set.isSet("o"))
        {
            /* Get output value. */
            String o = set.getOption("o").getResultValue(0);
            
            try
            {
                /* Try opening a stream to assess permissions. */
                PrintStream printStream = new PrintStream(o);
                printStream.close();
            } 
            catch (FileNotFoundException e)
            {
                return 74;
            }
        }
        
        int begin = 1;
        int end = rnaSequence.length();
        if (set.isSet("b"))
        {
            /* Get b value. */
            String b = set.getOption("b").getResultValue(0);
            
            /* Check if is made of numbers */
            if (!b.matches("[0-9]+")) return 50;
            
            /* Check bounds. */
            try 
            { 
                int index = Integer.parseInt(b); 
                if ((index<1) || (index>end-2))
                    return 52;
                begin = index; //Everything looks fine. Save the number for later.
            }
            catch (NumberFormatException e)
            {
                return 51;
            }
        }
        
        if (set.isSet("e"))
        {
            /* Get e value. */
            String e = set.getOption("e").getResultValue(0);
            
            /* Check if is made of numbers */
            if (!e.matches("[0-9]+")) return 60;
            
            /* Check bounds. */
            try 
            { 
                int index = Integer.parseInt(e); 
                if ((index<3) || (index>end))
                    return 62;
                end = index; //Everything looks fine. Save the number for later.
            }
            catch (NumberFormatException ex)
            {
                return 61;
            }
        }
        
        /* Check if end is larger than begin. */
        if (end-begin<2)
            return 69;
        
        /* Check if they represent a sequence multiple of 3.*/
        if (((end-begin+1) % 3) != 0)
        {
            if (set.isSet("b") || set.isSet("e"))
                return 68;
            else
                return 67;
        }
        
        /* Check "type" parameter. */
        if (set.isSet("d"))
        {
            /* Get e value. */
            String d = set.getOption("d").getResultValue(0);
            
            /* Check if is made of numbers */
            if (!d.matches("[01]")) return 70;
        }
        
        /* Check maximum/minimum parameter. */
        if (set.isSet("m"))
        {
            /* Get m value. */
            String m = set.getOption("m").getResultValue(0);
            
            /* Check if it is a correct floating point number */
            try 
            { 
                Double.parseDouble(m); 
            }
            catch (NumberFormatException ex)
            {
                return 71;
            }
        }
        
        /* Check time parameter. */
        if (set.isSet("t"))
        {
            /* Get m value. */
            String t = set.getOption("t").getResultValue(0);
            
            /* Check if it is a correct integer number */
            try 
            { 
                int time = Integer.parseInt(t); 
                if (time <= 0)
                    return 72;
            }
            catch (NumberFormatException ex)
            {
                return 72;
            }
        }
        
        /* Check iterations parameter. */
        if (set.isSet("i"))
        {
            /* Get m value. */
            String i = set.getOption("i").getResultValue(0);
            
            /* Check if it is a correct integer number */
            try 
            { 
                int numIterations = Integer.parseInt(i); 
                if (numIterations <= 0)
                    return 75;
            }
            catch (NumberFormatException ex)
            {
                return 75;
            }
        }
        
        /* Check coding table parameter. */
        if (set.isSet("c"))
        {
            /* Get m value. */
            String c = set.getOption("c").getResultValue(0);
            
            /* Check if it is a correct integer number */
            try 
            { 
                int codingTableCode = Integer.parseInt(c);
                if (GeneticCodeTableParser.getInstance().getCodeTableByID(codingTableCode) == null)
                    return 73;
            }
            catch (NumberFormatException ex)
            {
                return 73;
            }
            catch (FileNotFoundException ex)
            {
                return 98;
            }
            catch (IOException ex)
            {
                return 98;
            }
        }
        
        return 0;
    }
}
