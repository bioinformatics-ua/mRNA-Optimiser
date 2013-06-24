
package pt.ua.ieeta.RNAmfeOpt.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Paulo Gaspar
 */
public class ViennaRNAFold extends ExternalPredictor
{
    private String inputSequence;
    private double outputEnergy = 0.0;
    
    private static final Pattern deletionPattern = Pattern.compile("[.\\(\\)]+ \\([ ]?(.+)\\)");

    public ViennaRNAFold()
    {
    }

    @Override
    public void setSequence(String inputSequence)
    {
        assert inputSequence != null;
        assert !inputSequence.isEmpty();
        
        this.inputSequence = inputSequence+"\n";
    }
    
    @Override
    public double getEnergy()
    {
        return outputEnergy;
    }
    
    @Override
    public String getPredictorName()
    {
        return "Vienna RNAFold";
    }
    
    @Override
    public void run()
    {
        /* Arguments to create and run a new MUSCLE process. */
        String cmdArgs[] = new String[2];
        
        String runningDir = ".\\Predictors\\RNAFold\\";
        cmdArgs[0] = runningDir+"RNAfold.exe";
        cmdArgs[1] = "-noPS";
        
        Process p = null;
        try
        {
            /* Run the application with the specified arguments, in the specified path. */
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(cmdArgs, null, new File(runningDir));
            
//            new StreamConsumer(p.getErrorStream()).start();
//            new StreamConsumer(p.getInputStream()).start();
            
            OutputStream stdin = p.getOutputStream ();
            stdin.write(inputSequence.getBytes() );
            stdin.flush();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            br.readLine();
            String line = br.readLine();
            Matcher m = deletionPattern.matcher(line);
            m.find();
            outputEnergy = Double.valueOf(m.group(1).trim());
            
            System.out.println(outputEnergy);
            
            /* Wait for the application to terminate. */
//            int returnValue = p.waitFor();
//            System.out.println(returnValue);
        }
        catch (Exception ex)
        { //TODO: excepçoes.
            System.out.println("An exception occured while trying to run Vienna RNAfold: " + ex.getLocalizedMessage());
            if (p != null) p.destroy();
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
//        new  ViennaRNAFold("CUGUGCAUGCAUGACUGCUGAUGCUGACUGCAUG").start();
        
        ViennaRNAFold teste = new  ViennaRNAFold();
        teste.setSequence("AUGUGUGGCUACUACGGAAACUACUAUGGCGGCAGAGGCUAUGGCUGCUGUGGCUAUGGAGGCCUGGGCUAUGGCUAUGGAGGCCUGGGCUGUGGCUAUGGCUCCUACUAUGGCUGUGGCUACCGUGGACUGGGCUGUGGCUAUGGCUAUGGCUGUGGCUAUGGCUCACGCUCUCUCUAUGGCUGUGGCUAUGGAUGUGGCUCUGGCUAUGGCUCUGGAUUUGGCUACUACUACUGA");
        teste.start();
        
        teste.join();
        
        System.out.println(teste.getEnergy());
    }
    
}
