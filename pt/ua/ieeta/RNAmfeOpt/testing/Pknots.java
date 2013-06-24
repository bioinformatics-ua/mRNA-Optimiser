
package pt.ua.ieeta.RNAmfeOpt.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Paulo Gaspar
 */
public class Pknots extends ExternalPredictor
{
    private String inputSequence;
    private double outputEnergy = 0.0;
    
    private static final Pattern pattern = Pattern.compile("[.\\(\\)\\[\\]\\{\\}]+  \\([ ]?(.+)\\)");

    public Pknots()
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
        return "Pknots";
    }
    
    @Override
    public void run()
    {
        /* Arguments to create and run a new MUSCLE process. */
        String cmdArgs[] = new String[2];
        
        String runningDir = ".\\Predictors\\pknotsRG\\";
        cmdArgs[0] = runningDir+"pknotsRG.exe";
        cmdArgs[1] = inputSequence;
        
        Process p = null;
        String line = null;
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
            br.readLine();
            line = br.readLine();
            Matcher m = pattern.matcher(line);
            m.find();
            outputEnergy = Double.valueOf(m.group(1).trim());
            
//            System.out.println(line);
//            System.out.println(outputEnergy);
            
            /* Wait for the application to terminate. */
//            int returnValue = p.waitFor();
//            System.out.println(returnValue);
        }
        catch (Exception ex)
        { //TODO: excepçoes.
            System.out.println("An exception occured while trying to run PknotsRG: " + ex.getLocalizedMessage() + ": " + line);
            if (p != null) p.destroy();
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        Pknots teste = new  Pknots();
        teste.setSequence("AUGUGUGGCUACUACGGAAACUACUAUGGCGGCAGAGGCUAUGGCUGCUGUGGCUAUGGAGGCCUGGGCUAUGGCUAUGGAGGCCUGGGCUGUGGCUAUGGCUCCUACUAUGGCUGUGGCUACCGUGGACUGGGCUGUGGCUAUGGCUAUGGCUGUGGCUAUGGCUCACGCUCUCUCUAUGGCUGUGGCUAUGGAUGUGGCUCUGGCUAUGGCUCUGGAUUUGGCUACUACUACUGA");
        teste.start();
        
        teste.join();
        
        System.out.println(teste.getEnergy());
    }
    
}
