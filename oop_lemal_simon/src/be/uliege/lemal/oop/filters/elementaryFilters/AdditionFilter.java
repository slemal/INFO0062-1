package be.uliege.lemal.oop.filters.elementaryFilters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The AdditionFilter class. It computes the sum of several samples.
 */
public class AdditionFilter implements Filter {
	protected final int nbInputs;
	
	/**
	 * Initialises a new addition filter that expects two inputs.
	 */
	public AdditionFilter() {
		nbInputs = 2;
	}
	
	/**
	 * Initialises a new addition filter that expects a custom number of inputs.
	 *
	 * @param	nbInputs		The number of inputs the addition filter expects.
	 * @throws	FilterException	Thrown if nbInputs is negative.
	 */
	public AdditionFilter(int nbInputs) throws FilterException {
		if (nbInputs < 0) {
			throw new FilterException("Tried to create addition filter with negative number of " +
					"inputs.");
		}
		
		this.nbInputs = nbInputs;
	}
	
	/**
	 * Returns the number of inputs/outputs expected by an addition filter.
	 *
	 * @return	int	The expected number of inputs/outputs.
	 */
	public int nbInputs() {
		return nbInputs;
	}
	
	public int nbOutputs() {
		return 1;
	}
	
	/**
	 * Resets the addition filter (does nothing in this case).
	 */
	public void reset() {
	} // There is nothing to do.
	
	/**
	 * Returns the sum of the provided samples
	 *
	 * @param	input			An array containing several samples to sum.
	 * @return	int[]			An array containing the sum of the several samples.
	 * @throws	FilterException	Thrown if input is null or if the length of input
	 * 							does not match the expected number of inputs.
	 */
	public double[] computeOneStep(double[] input) throws FilterException {
		if (input == null) {
			throw new FilterException("Provided null input to addition filter.");
		}
		if (input.length != nbInputs()) {
			throw new FilterException(String.format("Provided %d input(s) to addition filter. It " +
					"expects exactly %d inputs.", input.length, nbInputs));
		}
		
		double[] output = new double[]{0};
		for (int i = 0; i < nbInputs; i++) {
			output[0] += input[i];
		}
		return output;
	}
}
