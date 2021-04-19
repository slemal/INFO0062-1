package be.uliege.lemal.oop.filters.elementaryFilters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The DelayFilter class. It delays a sequence by queuing the received sample.
 */
public class DelayFilter implements Filter {
	protected final int delay;
	protected int read = 0, write = 0;
	// read corresponds to the first readable index. write to the first writable index.
	protected final double[] queue;
	
	/**
	 * @param	delay			The number of samples to delay.
	 * @throws	FilterException	Thrown if delay is nonpositive.
	 */
	public DelayFilter(int delay) throws FilterException {
		if (delay <= 0) {
			throw new FilterException("Tried to create delay filter with nonpositive delay.");
		}
		
		this.delay = delay;
		queue = new double[delay + 1];
		// The "+1" allows us to write the input before reading
		// the output without overwritting some value.
		reset();  // Initialises the queue.
	}
	
	/**
	 * Returns the number of inputs/outputs expected by a delay filter.
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
	 * Resets the delay filter by clearing the queue.
	 */
	public void reset() {
		for (int i = 0; i <= delay; i++) queue[i] = 0;
		// The queue is filled with 0s.
		read = 0;  // read is set to zero and write is set to delay (the greatest index).
		write = delay;
	}
	
	/**
	 * Returns the sample provided delay steps before,
	 * or 0 if less than delay steps have been conducted.
	 *
	 * @param	input			An array containing one sample to delay.
	 * @return	int[]			An array containing the delayed sample.
	 * @throws	FilterException	Thrown if input is null or if the length of input
	 * 							does not match the expected number of inputs.
	 */
	public double[] computeOneStep(double[] input) throws FilterException {
		if (input == null) {
			throw new FilterException("Provided null input to delay filter.");
		}
		if (input.length != nbInputs()) {
			throw new FilterException(String.format("Provided %d inputs to delay filter. It " +
					"expects one input.", input.length));
		}
		
		// If an input is given, we put it in the queue, read the first element and return it.
		double[] output = new double[1];
		queue[write++] = input[0];
		output[0] = queue[read++];
		
		read %= delay + 1;
		write %= delay + 1;
		
		return output;
	}
	
	/**
	 * Returns the sample provided delay steps before,
	 * or 0 if less than delay steps have been conducted.
	 *
	 * @return	int[]			An array containing the delayed sample.
	 * @throws	FilterException	Thrown if queue is empty.
	 */
	public double[] computeOneStep() throws FilterException {
		if (read == write) {
			throw new FilterException("Tried to read empty queue from delay filter.");
		} // If the queue is empty, throws an exception.
		
		double[] output = new double[1];
		output[0] = queue[read++];
		
		read %= delay + 1;
		
		return output;
	}
}
