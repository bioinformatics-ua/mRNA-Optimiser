
package pt.ua.ieeta.RNAmfeOpt.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *
 * @author Paulo Gaspar
 */
public class UNAFold extends ExternalPredictor
{
    private String inputSequence;
    private double outputEnergy = 0.0;
    
    public UNAFold()
    {
    }
    
    @Override
    public void setSequence(String inputSequence)
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
    public String getPredictorName()
    {
        return "UNAFold";
    }
    
    @Override
    public void run()
    {
        /* Arguments to create and run a new MUSCLE process. */
        String cmdArgs[] = new String[3];
        
        String runningDir = ".\\Predictors\\UNAFold\\";
        cmdArgs[0] = runningDir+"hybrid-ss-min.exe";
        cmdArgs[1] = "-q";
        cmdArgs[2] = inputSequence;
        
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
//            br.readLine();
            String line = br.readLine();
            outputEnergy = Double.valueOf(line);
            
            System.out.println(outputEnergy);
            
            /* Wait for the application to terminate. */
//            int returnValue = p.waitFor();
//            System.out.println(returnValue);
        }
        catch (Exception ex)
        { //TODO: excepçoes.
            System.out.println("An exception occured while trying to run UNAFold: " + ex.getLocalizedMessage());
            if (p != null) p.destroy();
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
//        new  UNAFold("CUGUGCAUGCAUGACUGCUGAUGCUGACUGCAUG").start();
        UNAFold teste = new  UNAFold();
        teste.setSequence("AUGUGUGGCUACUACGGAAACUACUAUGGCGGCAGAGGCUAUGGCUGCUGUGGCUAUGGAGGCCUGGGCUAUGGCUAUGGAGGCCUGGGCUGUGGCUAUGGCUCCUACUAUGGCUGUGGCUACCGUGGACUGGGCUGUGGCUAUGGCUAUGGCUGUGGCUAUGGCUCACGCUCUCUCUAUGGCUGUGGCUAUGGAUGUGGCUCUGGCUAUGGCUCUGGAUUUGGCUACUACUACUGA");
        teste.start();
        
        teste.join();
        
        System.out.println(teste.getEnergy());
        
        // -114 Kj/mol do RNAfold
    }
    
}
