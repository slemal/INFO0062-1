package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.elementaryFilters.MeanFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * The JCRev class. It implements a reverberator designed by John Chowning at CCRMA.
 * I had to modify it slightly because it was designed for a sample rate of 25 kHz.
 * https://ccrma.stanford.edu/~jos/pasp05/Schroeder_Reverberator_called_JCRev.html
 */
public class JCRev extends CompositeFilter {
	/**
	 * @throws	FilterException	In practice, this should never be thrown.
	 * 							This constructor takes no input from the user.
	 */
	public JCRev() throws FilterException {
		try {
			// Sequence of all-pass filters
			CompositeFilter seq = new CompositeFilter();
			Filter[] all = new Filter[3];
			int[] delay = new int[]{1861, 593, 199};
			for (int i = 0; i < 3; i++) {
				all[i] = new AllPassFilter(delay[i], 0.7);
				seq.addBlock(all[i]);
			}
			seq.connectInputToBlock(0, all[0], 0);
			seq.connectBlockToBlock(all[0], 0, all[1], 0);
			seq.connectBlockToBlock(all[1], 0, all[2], 0);
			seq.connectBlockToOutput(all[2], 0, 0);
			
			// Block of parallel comb filters
			CompositeFilter par = new CompositeFilter();
			Filter comb;
			Filter mean = new MeanFilter(4);
			par.addBlock(mean);
			int[] wait = new int[]{8467, 8819, 9521, 10233};
			double[] decay = new double[]{0.742, 0.733, 0.715, 0.697};
			for (int i = 0; i < 4; i++) {
				comb = new CombFilter(wait[i], decay[i]);
				par.addBlock(comb);
				par.connectInputToBlock(0, comb, 0);
				par.connectBlockToBlock(comb, 0, mean, i);
			}
			par.connectBlockToOutput(mean, 0, 0);
			
			// Connect them
			addBlock(seq);
			addBlock(par);
			connectInputToBlock(0, seq, 0);
			connectBlockToBlock(seq, 0, par, 0);
			connectBlockToOutput(par, 0, 0);
		}
		catch (FilterException e) {
			throw new FilterException("Unexpected error: An exception was thrown while " +
					"initialising JC reverberator. Message reads:\n" + e.getMessage());
			// In practice, no exception should be thrown here.
		}
	}
}
