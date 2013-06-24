
package pt.ua.ieeta.RNAmfeOpt.sa;

/**
 *
 * @author Paulo Gaspar
 */
public class SimulatedAnnealing extends Thread
{
    /* Final results: solution and its score. */
    private EvolvingSolution resultingSolution;
    private double score;
    
    /* Simmulated annealing parameters */
    private double coolingSchedule;
    private double convergenceFraction;
    private double initialDispersionFactor;
    private double mutationAffectedPercent;
    private int kmax; //maximum number of iterations
    private double emax; //maximum energy
    private int timemax; //maximum time (in minutes)
    private IFitnessAssessor fitnessCalculator;
    private EvolvingSolution seed;
    private boolean isMaximizeMode; //is the goal maximizing or minimizing?
    
    /* Neighbour generator. */
    INeighbourGenerator neighbourGenerator = new CodonSequenceNeighbourGenerator(); //new BondsNeighbourGenerator(); 

    /* DEBUG flag. */
    private boolean DEBUG = false;
    
    /* Starting time */
    private long startTime;
    
    /** Class constructor. Receives some parameters for the simmulated annealing algorithm.
     ** @param fitnessCalculator The class that implements the IFitnessAssessor interface. Responsible for calculating the fitness. 
     ** @param seed The initial object to start from. */
    public SimulatedAnnealing(IFitnessAssessor fitnessCalculator, EvolvingSolution seed)
    {
        assert fitnessCalculator != null;
        assert seed != null;
        assert !seed.getFeatureList().isEmpty();
        
        this.kmax = 1000; // a maximum of one thousand generations
        this.coolingSchedule = 0.9; // each generation the temperature decreases 10%
        this.convergenceFraction = 0.1; // the algorithm stops if there isn't evolution for (10% of kmax) consecutive generations
        this.initialDispersionFactor = 0.25; // In the first mutation, the variation of a feature is from this interval: ]-0.25; +0.25[ (although this is controlled by the IOptimizationTarget implementation)
        this.mutationAffectedPercent = 0.3; //30% of all features are affected by mutation in each generation
        this.fitnessCalculator = fitnessCalculator; // the class (function) that will calculate the fitness of an individual
        this.seed = seed; // initial individual
        this.emax = Double.MAX_VALUE;
        this.isMaximizeMode = true;
        this.timemax = Integer.MAX_VALUE;
    }
    
    /** Class constructor. Receives a few parameters for the simmulated annealing algorithm.
     * @param fitnessCalculator The class that implements the IFitnessAssessor interface. Responsible for calculating the fitness. 
     * @param seed The initial object to start from. 
     * @param kmax The maximum number of iterations. Each iteration corresponds to a single call to the fitnessCalculator object. 
     * @param coolingSchedule Controls the simmulated annealing temperature decrease: 
     *                        smaller values make the temperature decrease faster, and the algorithm terminate faster as well.
     *                        Larger values (closer to 1) generally return better solutions. 
     * @param convergenceFraction The percentage of kmax that is considered to be the maximum number of consecutive iterations 
     *                            without evolution. 
     * @param initialDispersionFactor Parameter used when creating mutations. Larger values create larger mutations. 
     * @param mutationAffectedPercent The maximum percentage of features that is affected by mutation in each iteration. */
    public SimulatedAnnealing(IFitnessAssessor fitnessCalculator, EvolvingSolution seed, int kmax, double coolingSchedule, double convergenceFraction, double initialDispersionFactor, double mutationAffectedPercent)
    {
        assert fitnessCalculator != null;
        assert seed != null;
        assert !seed.getFeatureList().isEmpty();
        assert kmax > 0;
        assert ((coolingSchedule > 0) && (coolingSchedule < 1));
        assert convergenceFraction > 0;
        assert ((initialDispersionFactor > 0) && (initialDispersionFactor <= 1));
        
        this.kmax = kmax;
        this.coolingSchedule = coolingSchedule;
        this.fitnessCalculator = fitnessCalculator;
        this.seed = seed;
        this.convergenceFraction = convergenceFraction;
        this.initialDispersionFactor = initialDispersionFactor;
        this.mutationAffectedPercent = mutationAffectedPercent;
        this.emax = Double.MAX_VALUE;
        this.isMaximizeMode = true;
        this.timemax = Integer.MAX_VALUE;
    }
    
    public void setStopCriteria(int maxiter, int maxtime, double targetenergy, boolean isMaximizeMode)
    {
        assert maxiter > 0;
        assert maxtime > 0;
                
        this.kmax = maxiter;
        this.timemax = maxtime;
        this.emax = targetenergy;
        this.isMaximizeMode = isMaximizeMode;
    }
    
    /** Runs the simulated annealing algorithm. Evolves the seed to find the maximum 
     ** fitness according to the fitness assessor.
     ** @return The solution that was found. */
    public EvolvingSolution runSimulatedAnnealing()
    {        
        assert fitnessCalculator != null;
        assert seed != null;
        
        /* Control maximum number of iterations. */
        if (kmax <= 0)
        {
            System.out.println("Invalid maximum number of interations! Must be a positive integer number. The input was: " + kmax);
            
            score = 0;
            resultingSolution = null;
            
            return null;
        }
        
        /* Record starting time. */
        startTime = System.currentTimeMillis();
        
        /* The evolving object. Initially takes the seed value. */
        EvolvingSolution s = seed;
        
        /* sbest is the best evolution so far. 
         * snew is the object to support a new evolution.*/
        EvolvingSolution sbest, snew;
        
        /* Fitness values: current, new and best. */
        double e, enew = 0, ebest;
        
        /* Counter to deal with convergency: if no evolution occurs through 10% 
         * of the maximum cicles, give up! */
        int convergenceCounter = 0;

        /* Calculate fitness for the initial seed. */
        e = calculateEnergy(fitnessCalculator, s);

        /* Initial values. */
        sbest  = s; ebest = e;
        int k = 0; //k is the iteration counter 
        double pacceptance;

        /* Main loop. 
         * Don't stop while:
         *      - the counter doesn't reach the max, 
         *      - AND while the max energy isn't achieved 
         *      - AND while there isn't putative convergence. */
        //while ((k < kmax) && (e < emax) && (convergenceCounter < convergenceFraction*kmax))
        while (!isTerminateCriteriaMet(k, e, convergenceCounter, System.currentTimeMillis()))
        {
              if (DEBUG)
                System.out.println("Simulated Annealing, Iteration " + k + ". Best: " + ebest + "   Last: " + enew);
            
              /* Obtain random neighbour. */
              snew = getNeighbour(s, k);
             
              /* Calculate energy. */
              enew = calculateEnergy(fitnessCalculator, snew);

              /* Calculate acceptance probability. */
              pacceptance = calculateAcceptanceProbability(e, enew, k);
                            
              /* Accept current solution? If it's better, always accept (enew > e) ! */
              if (isBetterThan(enew, e) || (pacceptance > Math.random()))
              {
                  s = snew;
                  e = enew;
              }
              
              //TODO: make a restart strategy! If (ebest-e) is very large, it means the 
              //current solution it too far apart from the best solution found so far

              /* Count number of repeated consecutive best scores to find convergence. */
              if (isBetterOrEqualTo(ebest, enew))  
                  convergenceCounter++;
              else
              {
                  convergenceCounter = 0;
                  
                  /* Found a better solution? Record it. */
                  sbest = snew;
                  ebest = enew;
              }

              /* Increment iteration counter. */
              k = k + 1;
        }
        
        if (DEBUG)
        {
            String reason;
            if (k >= kmax)
                reason = "the end of iterations was reached.";
            else if (e >= emax) 
                reason = "maximum fitness reached.";
            else if(convergenceCounter >= 0.1*kmax){
                reason = "convergence reached.";
            }
            else
                reason = "UNKNOWN.";
            
            System.out.println("Terminated because " + reason);
        }

        score = ebest;
        resultingSolution = sbest;

        return sbest;
    }
    
    private boolean isBetterThan(double energy1, double energy2)
    {
        if (isMaximizeMode)
            return energy1 > energy2;
        else
            return energy1 < energy2;
    }
    
    private boolean isBetterOrEqualTo(double energy1, double energy2)
    {
        if (isMaximizeMode)
            return energy1 >= energy2;
        else
            return energy1 <= energy2;
    }
    
    private boolean isTerminateCriteriaMet(int iteration, double energy, double convergenceCounter, long currentTime)
    {
        int timeDuration = (int) (currentTime-startTime)/60000; //calculate duration in minutes
        return (iteration >= kmax) || isBetterOrEqualTo(energy, emax) || (convergenceCounter >= convergenceFraction*kmax) || (timeDuration >= timemax);
    }
    
    /** Calculates the probability of acceptance of a solution. Takes into consideration
     ** the current solution's fitness, the new solution's fitness, and the iteration number. */
    private double calculateAcceptanceProbability(double e, double enew, int k)
    {
        assert k >= 0;
        
        return Math.exp(-(e - enew)/(kmax*Math.pow(coolingSchedule, k)));
    }
            

    /** Calculate fitness score for an object using a Fitness Assessor. 
     ** @param fitnesCalculator The class that implements the IFitnessAssessor interface. Responsible for calculating the fitness. 
     ** @param solution The input object to be evaluated by the fitness evaluator. 
     ** @return Fitness score for the object. */
    private double calculateEnergy(IFitnessAssessor fitnessCalculator, EvolvingSolution solution)
    {
        assert fitnessCalculator != null;
        assert solution != null;
        assert !solution.getFeatureList().isEmpty(); //perhaps an "if" instead?
        
        try 
        {
            double fitness = fitnessCalculator.getFitness(solution);
            //assert ((fitness >= 0) && (fitness <= 1)); //fitness constrains ?
            
            return fitness;
        }
        catch(Exception ex)
        {
            System.out.println("An exception occured while calculating the fitness score: " + ex.getMessage());
            return 0;
        }
    }
    
    /** Gets a neighbour object from an input object. */
    private EvolvingSolution getNeighbour(EvolvingSolution solution, int k)
    {
        assert solution != null;
        assert !solution.getFeatureList().isEmpty();
                        
        EvolvingSolution neighbour = neighbourGenerator.getNeighbour(solution, k, kmax, initialDispersionFactor, mutationAffectedPercent);
        
        if (neighbour == null)
        {
            System.out.println("Simulated Anealing: There was a problem generating a new neighbour from the current solution.");
            return solution;
        }
        
        return neighbour;
    }

    /** Returns the score of the found solution. */
    public double getScore()
    {
        return score;
    }
    
    /** Returns the found solution. */
    public EvolvingSolution getSolution()
    {
        return resultingSolution;
    }

    /* The runnable interface. Allows to run this class as a thread. */
    @Override
    public void run()
    {
        if (DEBUG) System.out.println("Started the Simulated Annealing algorithm.");
        runSimulatedAnnealing();
        if (DEBUG) System.out.println("Ended the Simulated Annealing algorithm.");
    }
}
