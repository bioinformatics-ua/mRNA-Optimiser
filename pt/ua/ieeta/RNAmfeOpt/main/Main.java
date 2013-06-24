
package pt.ua.ieeta.RNAmfeOpt.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Scanner;
import pt.ua.ieeta.RNAmfeOpt.main.Options.Multiplicity;
import pt.ua.ieeta.RNAmfeOpt.main.Options.Separator;

/**
 *
 * @author Paulo Gaspar
 * 
 * This is the main entry class for the optimization application.
 */
public class Main 
{
    private static String usageHints =  "\nmRNA Secondary Structure optimizer (v1.1)\n"
                                +"Paulo Gaspar, University of Aveiro, 2012\n"
                                +"paulogaspar@ua.pt\n"
                                +"\n"
                                +"Usage: mRNAoptimizer [options] nucleotideSequence\n"
                                +"\n"
                                +"Example usages: \n"
                                +"	mRNAoptimizer GUCACGUACUGACGUACUGCAGUCA\n"
                                +"	mRNAoptimizer -f sequence_file.txt\n"
                                +"	mRNAoptimizer -f sequence_file.txt -b 100 -e 300 -o output.txt\n"
                                +"\n"
                                +"Options:\n"
                                +"\n"
                                +"-f inputFile      Give input sequence as a file.\n\n"
                                +"-o outputFile     Output results to file (Default = standard output).\n\n"
                                +"-b index          Index of the first nucleotide of the start codon (default=1)\n\n"
                                +"-e index          Index of the last nucleotide of the stop codon \n"
                                +"                  (default = sequence size)\n\n"
                                +"-d type           Optimization type: 0 to maximize MFE (default)\n"
                                +"                                     1 to minimize MFE\n\n"
                                +"-m max/min MFE    Set maximum or minimum MFE, depending on the selected type\n"
                                +"                  The algorithm stops if it reaches this value.\n"
                                +"                  NOTE: please don't use the '-' symbol. Use only a positive\n"
                                +"                  number and the program will convert it to negative!\n"
                                +"                  (default = no limit)\n\n"
                                +"-t maxTime        Maximum optimization time, in minutes (default = no limit).\n\n"
                                +"-i iterations     Number of iterations the algorithm runs. The more iterations\n"
                                +"                  the longer it will take, but results will usually be better\n"
                                +"                  (default = 4000)\n\n"
                                +"-c codingTable    Genetic Code Table to use(default=1) according to this list:\n"
                                +"                  http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi\n\n"
                                +"-q                Don't output anything else than the resulting sequence.\n\n"
                                +"-g                Try keeping the same GC content as the original sequence.\n"
                                +"                  With this option, the MFE optimization won't be as great.\n"
                                +"";

    public static void main(String[] args) throws FileNotFoundException 
    {
        /* Construct options set. */
        Options options = new Options(args, Multiplicity.ZERO_OR_ONE);
        
        options.addSet("sequenceset", 1);
        options.addSet("fileset").addOption("f", Separator.BLANK);
                
        options.addOptionAllSets("o", Separator.BLANK); //output file
        options.addOptionAllSets("b", Separator.BLANK); //begin index
        options.addOptionAllSets("e", Separator.BLANK); //end index
        options.addOptionAllSets("d", Separator.BLANK); //type (max, min)
        options.addOptionAllSets("m", Separator.BLANK); //max/min MFE
        options.addOptionAllSets("t", Separator.BLANK); //max time (minutes)
        options.addOptionAllSets("i", Separator.BLANK); //num iterations
        options.addOptionAllSets("c", Separator.BLANK); //genetic code table
        options.addOptionAllSets("q");                  //quiet
        options.addOptionAllSets("g");                  //maintain GC content
                
        /* Check if args match the available options. */
        OptionSet set = options.getMatchingSet(false, false);
        if (set == null) 
        {
//            System.out.println(options.getCheckErrors());
            System.out.println(usageHints);
            System.exit(1);
        }
        
        /* Get RNA sequence. */
        String rnaSequence;
        if (set.getSetName().equals("sequenceset"))
        {
            assert set.getData() != null;
            assert set.getData().size() > 0;
            rnaSequence = set.getData().get(0);
        }
        else
        {
            String filename = set.getOption("f").getResultValue(0);
            File input = new File(filename);
            if (!input.exists() || !input.canRead())
            {
                System.out.println("Could not find, or could not read file \"" + filename + "\"");
                System.exit(1);
            }
            
            StringBuilder text = new StringBuilder();
            Scanner scanner = new Scanner(new FileInputStream(input));
            while (scanner.hasNextLine())
                text.append(scanner.nextLine());
            scanner.close();
            
            rnaSequence = text.toString();
        }
        
        /* Check if RNA sequence is correct. */
        rnaSequence = rnaSequence.toUpperCase().trim().replaceAll("\\s", "");
//        System.out.println("Input Sequence is: " + rnaSequence);
        int errorCode = ErrorHandler.checkSequenceIsValid(rnaSequence);
        if (errorCode != 0)
        {
            String errorMessage = ErrorHandler.getErrorMessage(errorCode);
            System.out.println("The provided sequence is invalid: " + errorMessage);
            System.out.println("Please use a sequence with only the letters A, C, G and U");
            System.exit(1);
        }
        
        /* Check parameters. */
        errorCode = ErrorHandler.checkParameters(set, rnaSequence);
        if (errorCode != 0)
        {
            String errorMessage = ErrorHandler.getErrorMessage(errorCode);
            System.out.println("There in an error in the provided parameters:\n" + errorMessage);
            System.exit(1);
        }
            
        /* All is OK! We can proceed.
         * Build parameter list. */
        EnumMap<OptimiserParameter, Object> params = buildParameterList(set, rnaSequence);
        if (params == null)
        {
            System.out.println("An error occured while trying to compile all the parameters.");
            System.exit(1);
        }
        
        /* All fine, start optimisation. */
        RNAOptimiser optimiser = new RNAOptimiser(params);
        optimiser.startOptimisation();
        
        /* Prepare to exit. Close all streams */
        prepareTermination(params);
    }

    private static EnumMap<OptimiserParameter, Object> buildParameterList(OptionSet set, String rnaSequence)
    {
        EnumMap<OptimiserParameter, Object> list = new EnumMap<OptimiserParameter, Object>(OptimiserParameter.class);
        
        try
        {
            list.put(OptimiserParameter.SEQUENCE, rnaSequence);
            list.put(OptimiserParameter.OUTPUT,   set.isSet("o")? new PrintStream(set.getOption("o").getResultValue(0)) : OptimiserParameter.OUTPUT.getDefaultValue());
            list.put(OptimiserParameter.BEGIN,    set.isSet("b")? Integer.parseInt(set.getOption("b").getResultValue(0)) : OptimiserParameter.BEGIN.getDefaultValue());
            list.put(OptimiserParameter.END,      set.isSet("e")? Integer.parseInt(set.getOption("e").getResultValue(0)) : rnaSequence.length());
            list.put(OptimiserParameter.TYPE,     set.isSet("d")? Integer.parseInt(set.getOption("d").getResultValue(0)) : OptimiserParameter.TYPE.getDefaultValue());
            list.put(OptimiserParameter.TARGETMFE,set.isSet("m")? Double.parseDouble("-" + set.getOption("m").getResultValue(0)) : OptimiserParameter.TARGETMFE.getDefaultValue());
            list.put(OptimiserParameter.MAXTIME,  set.isSet("t")? Integer.parseInt(set.getOption("t").getResultValue(0)) : OptimiserParameter.MAXTIME.getDefaultValue());
            list.put(OptimiserParameter.MAXITER,  set.isSet("i")? Integer.parseInt(set.getOption("i").getResultValue(0)) : OptimiserParameter.MAXITER.getDefaultValue());
            list.put(OptimiserParameter.GCT,      set.isSet("c")? Integer.parseInt(set.getOption("c").getResultValue(0)) : OptimiserParameter.GCT.getDefaultValue());
            list.put(OptimiserParameter.QUIET,    set.isSet("q")? true : OptimiserParameter.QUIET.getDefaultValue());
            list.put(OptimiserParameter.KEEPGC,   set.isSet("g")? true : OptimiserParameter.KEEPGC.getDefaultValue());
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
        
        assert list.size() == OptimiserParameter.values().length;
        
        return list;
    }

    private static void prepareTermination(EnumMap<OptimiserParameter, Object> params)
    {
        assert params != null;
        
        /* Close the output stream, if necessary. */
        PrintStream output = (PrintStream) params.get(OptimiserParameter.OUTPUT);
        if (output != System.out)
            output.close();
    }

    
}
