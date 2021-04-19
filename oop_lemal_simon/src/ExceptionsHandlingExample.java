/*
 * INFO0062 - Object-Oriented Programming
 * Example of filter created with a CompositeFilter class
 *
 * This short class demonstrates an example of filter created with the CompositeFilter as
 * described by the statement.
 *
 * The filter here consists of two GainFilter (with real value 0.1) connected to the input which
 * both feeds an AdditionFilter. When applied to a WAV file, this construction produces the same
 * sound as the original WAV file but with a lower volume.
 *
 * Regardless of how you designed your project, your final library MUST compile while using this
 * file as a main program. Compilation errors would mean that:
 * -classes described in the statement (i.e., AdditionFilter, GainFilter, DelayFilter and
 *  CompositeFilter) aren't correctly named,
 * -the interface of the CompositeFilter class isn't correctly named or does not use the same
 *  parameters as described in the statement.
 * However, the inner workings of your CompositeFilter class (with or without auxiliary classes)
 * and the way you handle exceptions remain entirely free.
 *
 * @author: J.-F. Grailet (ULiege)
 */

import be.uliege.lemal.oop.filters.compositeFilters.CompositeFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.AdditionFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DummyFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

public class ExceptionsHandlingExample {
	public static void main(String[] args) throws Exception {
		try {
			// Creates the CompositeFilter object, with one input and one output
			CompositeFilter audioFilter = new CompositeFilter();
			
			// Creates a loop with no delay
			CompositeFilter badLoop = new CompositeFilter(0, 1);
			Filter f = new DummyFilter(0);
			badLoop.addBlock(f);
			badLoop.connectBlockToBlock(f, 0, f, 0);
			badLoop.connectBlockToOutput(f, 0, 0);
			
			// Creates the basic blocks
			Filter mult1 = new GainFilter(0.1);
			Filter mult2 = new GainFilter(0.1);
			Filter add = new AdditionFilter();
			Filter delay = new DelayFilter(132600);
			Filter dummy = new DummyFilter(88400);
			
			// CompositeFilter badComposite = new CompositeFilter(0, -42);
			
			// Filter badDelay = new DelayFilter(0);
			
			// Adds them to the CompositeFilter
			audioFilter.addBlock(mult1);
			audioFilter.addBlock(mult2);
			audioFilter.addBlock(add);
			audioFilter.addBlock(dummy);  // Causes a warning. Comment this out.
			audioFilter.addBlock(delay);  // Causes a warning.
			audioFilter.addBlock(badLoop);  // Causes a warning.
			
			// audioFilter.addBlock(null);
			
			// audioFilter.addBlock(dummy);
			
			// Connects the blocks together
			audioFilter.connectInputToBlock(0, mult1, 0);
			audioFilter.connectInputToBlock(0, mult2, 0);
			audioFilter.connectBlockToBlock(delay, 0, delay, 0);
			audioFilter.connectBlockToBlock(dummy, 0, dummy, 0);
			audioFilter.connectBlockToBlock(mult1, 0, add, 0);
			audioFilter.connectBlockToBlock(mult2, 0, add, 1);  // Comment this out.
			audioFilter.connectBlockToOutput(add, 0, 0);  // Try commenting this out
			
			// audioFilter.connectBlockToBlock(add, 0, mult2, 0);
			
			// audioFilter.connectBlockToOutput(add, 1, 0);
			// audioFilter.connectBlockToOutput(add, 0, 1);
			
			// audioFilter.connectInputToBlock(0, null, 0);
			
			// audioFilter.connectBlockToOutput(dummy, 0, 0);
			
			// Applies the filter
			TestAudioFilter.applyFilter(audioFilter, "Debussy_Pagodes_1m.wav", "Filtered.wav");
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
