/*
 * INFO0062 - Object-Oriented Programming
 * Dummy filter to test the audio.jar library
 *
 * Example of class implementing the Filter interface (see statement of the project), provided as
 * a tool to test the audio.jar library before starting the project.
 *
 * This filter is designed to let samples pass through for a given duration, before cutting them
 * for the same duration (by replacing all of them with 0's), then let them pass again, etc.
 *
 * In practice, if you use duration = 44100 * 3, the WAV file after filtering will contain 3
 * seconds of the initial file, then 3 seconds of nothing, then 3 seconds of the original content,
 * and so on and so forth.
 *
 * In the Demo.java from project_basis, you can instantiate a MyFilter object with this class
 * with either of the next instructions (duration of cutoff = 3 seconds):
 * -"Filter myFilter = new DummyFilter(44100 * 3);"
 * -"DummyFilter myFilter = new DummyFilter(44100 * 3);"
 *
 * @author: J.-F. Grailet (ULiege)
 */

package be.uliege.lemal.oop.filters.elementaryFilters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

public class DummyFilter implements Filter {
	private int duration, count;
	private boolean cutting; // True if we are "cutting" the sound (i.e., no sample can pass)
	
	/*
	 * Constructor. Duration is expressed in samples.
	 *
	 * N.B.: for the sake of simplicity, it is assumed the user will provide a duration > 0.
	 */
	
	public DummyFilter(int duration) {
		this.duration = duration;
		count = 0;
		cutting = false;
	}
	
	/*
	 * Implementation of the nbInputs() and nbOutputs() methods (trivial in this case).
	 */
	
	public int nbInputs() {
		return 1;
	}
	
	public int nbOutputs() {
		return 1;
	}
	
	/*
	 * Computes one step of the filter, i.e.:
	 * -increments the counter,
	 * -if equal to the duration, resets it and flips the "cutting" boolean,
	 * -creates the output array and copies the input in it as long as "cutting" is false
	 *  (otherwise, the output will just contain the "0" value).
	 *
	 * N.B.: in a complete project, the method needs to ensure the input array is properly sized.
	 */
	
	public double[] computeOneStep(double[] input) throws FilterException {
		if (input == null) {
			throw new FilterException("Provided null input to dummy filter.");
		}
		if (input.length != nbInputs()) {
			throw new FilterException(String.format("Provided %d inputs to dummy filter. It " +
					"expects exactly one input.", input.length));
		}
		
		count++;
		if (count == duration) {
			count = 0;
			cutting = !cutting;
		}
		
		double[] output = new double[1];
		output[0] = 0;
		if (!cutting)
			output[0] = input[0];
		return output;
	}
	
	/*
	 * reset() method as specified in the Filter interface. For fun, you can comment the next
	 * lines, use a duration that is not a multiple of 44100 (sampling frequency) and see what
	 * happens.
	 */
	
	public void reset() {
		count = 0;
		cutting = false;
	}
}
