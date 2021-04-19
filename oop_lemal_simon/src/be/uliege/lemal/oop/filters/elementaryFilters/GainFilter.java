package be.uliege.lemal.oop.filters.elementaryFilters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The GainFilter class. It multiplies a whole sequence of samples by a given factor.
 */
public class GainFilter implements Filter {
	protected final double factor;
	
	/**
	 * @param	factor	The factor to multiply samples by.
	 */
	public GainFilter(double factor) {
		this.factor = factor;
	}
	
	/**
	 * Returns the number of inputs/outputs expected by a gain filter.
	 *
	 * @return	int	The expected number of inputs/outputs.
	 */
	public int nbInputs() {
		return 1;
	}
	
	public int nbOutputs() {
		return 1;
	}
	
	/**
	 * This method resets the gain filter (does nothing in this case).
	 */
	public void reset() {
	} // There is nothing to do.
	
	/**
	 * Returns the result of the multiplication of the provided sample by factor.
	 *
	 * @param	input			An array containing the sample to multiply.
	 * @return	int[]			An array containing the multiplied sample.
	 * @throws	FilterException	Thrown if input is null or if the length of input
	 * 							does not match the expected number of inputs.
	 */
	public double[] computeOneStep(double[] input) throws FilterException {
		if (input == null) {
			throw new FilterException("Provided null input to gain filter.");
		}
		if (input.length != nbInputs()) {
			throw new FilterException(String.format("Provided %d inputs to gain filter. It " +
					"exactly expects one input.", input.length));
		}
		
		double[] output = new double[1];
		output[0] = factor * input[0];
		
		return output;
	}
}
