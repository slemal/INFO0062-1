package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.elementaryFilters.AdditionFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The Reverb class. It implements a reverberator designed by William G. Gardner
 * to simulate the reverberation of a large room.
 */
public class Reverb extends CompositeFilter {
	/**
	 * @throws	FilterException	In practice, this should never be thrown.
	 * 							This constructor takes no input from the user.
	 */
	public Reverb() throws FilterException {
		try {
			Filter all1, all2, wait;
			
			// First sequence
			CompositeFilter seq1 = new CompositeFilter();
			all1 = new AllPassFilter(353, 0.3);
			all2 = new AllPassFilter(529, 0.3);
			wait = new DelayFilter(176);
			
			seq1.addBlock(all1);
			seq1.addBlock(all2);
			seq1.addBlock(wait);
			seq1.connectInputToBlock(0, all1, 0);
			seq1.connectBlockToBlock(all1, 0, all2, 0);
			seq1.connectBlockToBlock(all2, 0, wait, 0);
			seq1.connectBlockToOutput(wait, 0, 0);
			
			// Second sequence
			CompositeFilter seq2 = new CompositeFilter();
			wait = new DelayFilter(750);
			all1 = new AllPassFilter(3837, 0.5, new AllPassFilter(2734, 0.25));
			
			seq2.addBlock(wait);
			seq2.addBlock(all1);
			seq2.connectInputToBlock(0, wait, 0);
			seq2.connectBlockToBlock(wait, 0, all1, 0);
			seq2.connectBlockToOutput(all1, 0, 0);
			
			// Third sequence
			// Nested Filter
			CompositeFilter nested = new CompositeFilter();
			all1 = new AllPassFilter(3352, 0.25);
			all2 = new AllPassFilter(1323, 0.25);
			
			nested.addBlock(all1);
			nested.addBlock(all2);
			nested.connectInputToBlock(0, all1, 0);
			nested.connectBlockToBlock(all1, 0, all2, 0);
			nested.connectBlockToOutput(all2, 0, 0);
			
			CompositeFilter seq3 = new CompositeFilter();
			wait = new DelayFilter(132);
			all1 = new AllPassFilter(5292, 0.5, nested);
			
			seq3.addBlock(wait);
			seq3.addBlock(all1);
			seq3.connectInputToBlock(0, wait, 0);
			seq3.connectBlockToBlock(wait, 0, all1, 0);
			seq3.connectBlockToOutput(all1, 0, 0);
			
			
			// Complete filter
			Filter add1 = new AdditionFilter();
			Filter add2 = new AdditionFilter(3);
			Filter delay = new DelayFilter(1367);
			Filter low = new LowPassFilter(88, 0.7133);
			
			addBlock(seq1);
			addBlock(seq2);
			addBlock(seq3);
			addBlock(add1);
			addBlock(add2);
			addBlock(delay);
			addBlock(low);
			
			double[] factor = new double[]{0.34, 0.14, 0.14, 0.1};
			Filter[] gain = new Filter[4];
			for (int i = 0; i < 4; i++) {
				gain[i] = new GainFilter(factor[i]);
				addBlock(gain[i]);
			}
			
			connectInputToBlock(0, add1, 0);
			connectBlockToBlock(add1, 0, seq1, 0);
			connectBlockToBlock(seq1, 0, seq2, 0);
			connectBlockToBlock(seq1, 0, gain[0], 0);
			connectBlockToBlock(gain[0], 0, add2, 0);
			connectBlockToBlock(seq2, 0, delay, 0);
			connectBlockToBlock(delay, 0, seq3, 0);
			connectBlockToBlock(delay, 0, gain[1], 0);
			connectBlockToBlock(gain[1], 0, add2, 1);
			connectBlockToBlock(seq3, 0, gain[2], 0);
			connectBlockToBlock(seq3, 0, low, 0);
			connectBlockToBlock(gain[2], 0, add2, 2);
			connectBlockToBlock(low, 0, gain[3], 0);
			connectBlockToBlock(gain[3], 0, add1, 1);
			connectBlockToOutput(add2, 0, 0);
		}
		catch (FilterException e) {
			throw new FilterException("Unexpected error: An exception was thrown while " +
					"initialising reverberator. Message reads:\n" + e.getMessage());
			// In practice, no exception should be thrown here.
		}
	}
}
