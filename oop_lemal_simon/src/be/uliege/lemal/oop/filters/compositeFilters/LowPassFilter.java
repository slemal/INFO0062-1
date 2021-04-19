package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.elementaryFilters.AdditionFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The LowPassFilter class. When applied to an audio sequence, it attenuates higher frequencies.
 */
public class LowPassFilter extends CompositeFilter {
	/**
	 * @param	delay			The delay to provide to the nested delay filter.
	 * @param	gain			The gain to provide to the nested gain filters.
	 * @throws	FilterException	Thrown if delay is nonpositive or if gain is outside (0, 1).
	 */
	public LowPassFilter(int delay, double gain) throws FilterException {
		if (delay <= 0) {
			throw new FilterException("Tried to create low-pass filter with nonpositive delay.");
		}
		if (gain <= 0 || gain >= 1) {
			throw new FilterException("Tried to create low-pass filter with gain outside interval" +
					" (0, 1)");
		}
		
		try {
			Filter invert = new GainFilter(1 - gain);
			Filter add = new AdditionFilter();
			Filter wait = new DelayFilter(delay);
			Filter mult = new GainFilter(gain);
			
			addBlock(invert);
			addBlock(add);
			addBlock(wait);
			addBlock(mult);
			
			connectInputToBlock(0, invert, 0);
			connectBlockToBlock(invert, 0, add, 0);
			connectBlockToBlock(add, 0, wait, 0);
			connectBlockToBlock(wait, 0, mult, 0);
			connectBlockToBlock(mult, 0, add, 1);
			connectBlockToOutput(add, 0, 0);
		}
		catch (FilterException e) {
			throw new FilterException("Unexpected error: An exception was thrown while " +
					"initialising low-pass filter. Message reads:\n" + e.getMessage());
			// In practice, no exception should be thrown here.
		}
	}
}
