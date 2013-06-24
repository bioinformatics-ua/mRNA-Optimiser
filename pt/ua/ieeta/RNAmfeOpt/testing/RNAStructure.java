
package pt.ua.ieeta.RNAmfeOpt.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 *
 * @author Paulo Gaspar
 */
public class RNAStructure extends ExternalPredictor
{
    private String inputSequence;
    private double outputEnergy = 0.0;
    
    private static final Pattern deletionPattern = Pattern.compile("[.\\(\\)]+  \\([ ]?(.+)\\)");

    public RNAStructure()
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
        return "RNAStructure";
    }
    
    @Override
    public void run()
    {
        /* Arguments to create and run a new MUSCLE process. */
        String cmdArgs[] = new String[2];
        
        String runningDir = ".\\Predictors\\RNAStructure\\";
        cmdArgs[0] = runningDir+"fold.exe";
        cmdArgs[1] = inputSequence;
        
        Process p = null;
        try
        {
            /* Run the application with the specified arguments, in the specified path. */
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(cmdArgs, null, new File(runningDir));
            
//            new StreamConsumer(p.getErrorStream()).start();
//            new StreamConsumer(p.getInputStream()).start();
            
//            OutputStream stdin = p.getOutputStream ();
//            stdin.write(inputSequence.getBytes() );
//            stdin.flush();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            outputEnergy = Double.valueOf(line);
            
//            System.out.println(outputEnergy);
            
            /* Wait for the application to terminate. */
//            int returnValue = p.waitFor();
//            System.out.println(returnValue);
        }
        catch (Exception ex)
        {
            System.out.println("An exception occured while trying to run RNAStructure: " + ex.getLocalizedMessage());
            if (p != null) p.destroy();
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        RNAStructure teste = new  RNAStructure();
        teste.setSequence("AUGUGUGGCUACUACGGAAACUACUAUGGCGGCAGAGGCUAUGGCUGCUGUGGCUAUGGAGGCCUGGGCUAUGGCUAUGGAGGCCUGGGCUGUGGCUAUGGCUCCUACUAUGGCUGUGGCUACCGUGGACUGGGCUGUGGCUAUGGCUAUGGCUGUGGCUAUGGCUCACGCUCUCUCUAUGGCUGUGGCUAUGGAUGUGGCUCUGGCUAUGGCUCUGGAUUUGGCUACUACUACUGA");
        teste.start();
        
        teste.join();
        
        /* Should be -112.0 Kj/Mol */
        System.out.println(teste.getEnergy());
    }
    
}
