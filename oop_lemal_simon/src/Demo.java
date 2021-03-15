/*
 * INFO0062 - Object-Oriented Programming
 * Project basis
 *
 * Example code to filter a WAV file using audio.jar. The filter to apply has to be implemented by
 * the students first.
 */

import be.uliege.lemal.oop.filters.compositeFilters.EchoFilter;
import be.uliege.lemal.oop.filters.compositeFilters.JCRev;
import be.uliege.lemal.oop.filters.compositeFilters.Reverb;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

import java.io.IOException;
import java.util.Arrays;

public class Demo {
	public static void main(String[] args) {
		try {
			Filter myFilter;
			if (args.length > 0 && "reverb".equalsIgnoreCase(args[0])) {
				args = Arrays.copyOfRange(args, 1, args.length);
				myFilter = new Reverb();
			}
			else if (args.length > 0 && "jcrev".equalsIgnoreCase(args[0])) {
				args = Arrays.copyOfRange(args, 1, args.length);
				myFilter = new JCRev();
			}
			else myFilter = new EchoFilter(22050, 0.6);
			// Chooses between echo, reverb and jcrev mode.
			
			if (args.length == 0) {
				throw new IOException("Please provide a source file and a destination (optional) as command line arguments.");
			}
			String source = args[0], filtered = "Filtered";
			if (args.length == 2) {
				filtered = args[1];
			}
			
			if (args.length > 2) {
				throw new IOException("You provided too many arguments.");
			}
			
			if (!source.endsWith(".wav") && !source.endsWith(".WAV")) {
				source += ".wav";
			}
			
			TestAudioFilter.applyFilter(myFilter, source, filtered);
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
