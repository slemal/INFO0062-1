package be.uliege.lemal.oop.filters.elementaryFilters;

import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The MeanFilter class. It computes the mean of several samples.
 */
public class MeanFilter extends AdditionFilter {
	public MeanFilter(int nbInputs) throws FilterException {
		super(nbInputs);
	}
	
	/**
	 * Returns the mean of the provided samples
	 *
	 * @param	input			An array containing several samples.
	 * @return	int[]			An array containing the mean of the several samples.
	 * @throws	FilterException	Thrown if input is null or if the length of input
	 * 							does not match the expected number of inputs.
	 */
	@Override
	public double[] computeOneStep(double[] input) throws FilterException {
		double[] output = super.computeOneStep(input);
		output[0] /= nbInputs;
		return output;
	}
}
