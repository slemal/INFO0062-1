package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.elementaryFilters.AdditionFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The AllPassFilter class. When applied to an audio sequence,
 * it changes the phase relationship among various frequencies.
 */
public class AllPassFilter extends CompositeFilter {
	/**
	 * @param	delay			The delay to provide to the nested delay filter.
	 * @param	gain			The gain to provide to the nested gain filters.
	 * @throws	FilterException	Thrown if delay is nonpositive
	 * 							or if gain is outside (0, 1).
	 */
	public AllPassFilter(int delay, double gain) throws FilterException {
		if (delay <= 0) {
			throw new FilterException("Tried to create all-pass filter with nonpositive delay.");
		}
		if (gain <= 0 || gain >= 1) {
			throw new FilterException("Tried to create all-pass filter with gain outside interval" +
					" (0, 1)");
		}
		
		try {
			Filter wait = new DelayFilter(delay);
			Filter pos = new GainFilter(gain);
			Filter neg = new GainFilter(-gain);
			Filter add1 = new AdditionFilter();
			Filter add2 = new AdditionFilter();
			
			addBlock(wait);
			addBlock(pos);
			addBlock(neg);
			addBlock(add1);
			addBlock(add2);
			
			connectInputToBlock(0, neg, 0);
			connectInputToBlock(0, add1, 0);
			connectBlockToBlock(pos, 0, add1, 1);
			connectBlockToBlock(add1, 0, wait, 0);
			connectBlockToBlock(neg, 0, add2, 0);
			connectBlockToBlock(wait, 0, add2, 1);
			connectBlockToBlock(add2, 0, pos, 0);
			connectBlockToOutput(add2, 0, 0);
		}
		catch (FilterException e) {
			throw new FilterException("Unexpected error: An exception was thrown while " +
					"initialising all-pass filter. Message reads:\n" + e.getMessage());
			// In practice, no exception should be thrown here.
		}
	}
	
	/**
	 * Creates a new all-pass filter with a nested filter.
	 *
	 * @param	delay			The delay to provide to the nested delay filter.
	 * @param	gain			The gain to provide to the nested gain filters.
	 * @param	nested			The nested filter.
	 * @throws	FilterException	Thrown if delay is nonpositive, if gain is outside [0, 1) or if
	 * 							nested filter does not expect one input or return one output.
	 */
	public AllPassFilter(int delay, double gain, Filter nested) throws FilterException {
		this(delay, gain);
		if (nested == null) {
			throw new FilterException("Tried to create all-pass filter with nested null filter.");
		}
		if (nested.nbInputs() != 1 || nested.nbOutputs() != 1) {
			throw new FilterException("In all-pass filter, nested filter should expect exactly " +
					"one input and return one output.");
		}
		
		Filter add = blocks.get(3).getFilter();  // Accesses the blocks add1 and wait defined above.
		Filter wait = blocks.get(0).getFilter();
		
		addBlock(nested);  // Adds nested between them.
		connectBlockToBlock(add, 0, nested, 0);
		connectBlockToBlock(nested, 0, wait, 0);
	}
}
