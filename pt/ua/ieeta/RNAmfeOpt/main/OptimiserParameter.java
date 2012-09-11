
package pt.ua.ieeta.RNAmfeOpt.main;

/**
 *
 * @author Paulo
 */
public enum OptimiserParameter
{
    SEQUENCE(""), 
    OUTPUT(System.out), 
    BEGIN(1), 
    END(null), //warning! this parameter must be changed! The default cannot be used
    TYPE(0),  
    TARGETMFE(Double.MAX_VALUE), 
    MAXTIME(Integer.MAX_VALUE), 
    MAXITER(4000), 
    GCT(1),
    QUIET(false), 
    KEEPGC(false);
    
    private Object defaultValue;

    private OptimiserParameter(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }
}
