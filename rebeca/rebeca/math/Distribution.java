package rebeca.math;

import java.util.Random;

/**
 * Defines a set of methods a distribution (of values) must provide.
 */
public interface Distribution 
{

	/**
	 * Returns the next value.
	 * @return the next value.
	 */
	public double getValue();

	/**
	 * Returns the distribution's expected value.
	 * @return the distribution's expected value.
	 */
	public double getExpectedValue();

	/**
	 * Returns the distribution's variance.
	 * @return the distribution's variance.
	 */
    public double getVariance();
    
    /**
     * Returns the random generator that is used to calculate the 
     * distribution's next value.
     * @return the distribution's (pseudo) random generator. 
     */
    public Random getRandom();
    
    /**
     * Sets the random generator that is used to calculate the distribution's
     * next value.
     * @param rand a (pseudo) random generator. 
     */
    public void setRandom(Random rand);
    
    /**
     * Sets the seed of the distributions random number generator using a 
     * single {@code long} seed.
     * @param seed the initial seed
     */
    public void setSeed(long seed);
}
