package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.elementaryFilters.AdditionFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The EchoFilter class. Its structure is that of a feedback comb filter.
 * It can be applied to an audio sequence to create an echo.
 */
public class EchoFilter extends CompositeFilter {
	/**
	 * @param	delay			The number of samples between a sample and its echo.
	 * @param	decay			The loss of intensity of the echo.
	 * @throws	FilterException	Thrown if delay is nonpositive or if decay is outside (0, 1).
	 */
	public EchoFilter(int delay, double decay) throws FilterException {
		if (delay <= 0) {
			throw new FilterException("Tried to create echo filter with nonpositive delay.");
		}
		if (decay <= 0 || decay >= 1) {
			throw new FilterException("Tried to create echo filter with decay outside interval " +
					"(0, 1)");
		}
		
		try {
			Filter wait = new DelayFilter(delay);
			Filter gain = new GainFilter(1 - decay);
			Filter add = new AdditionFilter();
			
			addBlock(wait);
			addBlock(gain);
			addBlock(add);
			connectInputToBlock(0, add, 0);
			connectBlockToBlock(add, 0, wait, 0);
			connectBlockToBlock(wait, 0, gain, 0);
			connectBlockToBlock(gain, 0, add, 1);
			connectBlockToOutput(add, 0, 0);
		}
		catch (FilterException e) {
			throw new FilterException("Unexpected error: An exception was thrown while " +
					"initialising echo filter. Message reads:\n" + e.getMessage());
			// In practice, no exception should be thrown here.
		}
	}
}
