
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
public class ViennaRNAFold extends Thread
{
    private String inputSequence;
    private double outputEnergy = 0.0;
    
    private static final Pattern deletionPattern = Pattern.compile("[.\\(\\)]+ \\((.+)\\)");

    public ViennaRNAFold(String inputSequence)
    {
        assert inputSequence != null;
        assert !inputSequence.isEmpty();
        
        this.inputSequence = inputSequence+"\n";
    }
    
    public double getEnergy()
    {
        return outputEnergy;
    }
    
    @Override
    public void run()
    {
        /* Arguments to create and run a new MUSCLE process. */
        String cmdArgs[] = new String[2];
        
        cmdArgs[0] = "RNAfold.exe";
        cmdArgs[1] = "-noPS";
        
        Process p = null;
        try
        {
            /* Run the application with the specified arguments, in the specified path. */
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(cmdArgs, null, new File("."));
            
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
            
            /* Wait for the application to terminate. */
//            int returnValue = p.waitFor();
        }
        catch (Exception ex)
        { //TODO: excepçoes.
            System.out.println("An exception occured while trying to run Vienna RUNfold: " + ex.getLocalizedMessage());
            if (p != null) p.destroy();
        }
    }
    
    public static void main(String[] args)
    {
        new  ViennaRNAFold("CUGUGCAUGCAUGACUGCUGAUGCUGACUGCAUG").start();
    }
    
}
