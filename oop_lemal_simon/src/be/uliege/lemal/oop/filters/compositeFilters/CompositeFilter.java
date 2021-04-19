package be.uliege.lemal.oop.filters.compositeFilters;

import be.uliege.lemal.oop.filters.compositeFilters.blocks.Block;
import be.uliege.lemal.oop.filters.compositeFilters.blocks.Input;
import be.uliege.lemal.oop.filters.compositeFilters.blocks.Output;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Vector;

/**
 * The CompositeFilter class.
 * It allows one to define complex filters composed of several elementary filters.
 */
public class CompositeFilter implements Filter {
	protected final int nbInputs, nbOutputs;
	protected final Vector<Block> blocks;
	// This contains all filters that were added as blocks to the composite filter.
	protected final Output[] inputs;
	protected final Input[] outputs;
	// This is done one purpose. When we call the method computeOneStep, we want the composite
	// filter to directly write the provided inputs in the corresponding block's inputs.
	// In the same way, when a block produce an output, we want it to write it in the output queue.
	protected boolean checked = false;  // This variable is used to check the filter is valid.
	
	/**
	 * @param	nbInputs		The number of inputs the composite filter expects.
	 * @param	nbOutputs		The number of outputs the composite filter provides.
	 * @throws	FilterException	Thrown if nbInputs or nbOutputs is negative.
	 */
	public CompositeFilter(int nbInputs, int nbOutputs) throws FilterException {
		if (nbInputs < 0) {
			throw new FilterException("Tried to create composite filter with negative number of " +
					"inputs.");
		}
		if (nbOutputs < 0) {
			throw new FilterException("Tried to create composite filter with negative number of " +
					"outputs.");
		}
		
		this.nbInputs = nbInputs;
		this.nbOutputs = nbOutputs;
		blocks = new Vector<Block>();
		
		inputs = new Output[nbInputs];
		for (int i = 0; i < nbInputs; i++) {
			inputs[i] = new Output(null);
		}
		outputs = new Input[nbOutputs];
		for (int i = 0; i < nbOutputs; i++) {
			outputs[i] = new Input();
		} // Initialise all that has to be initialised.
	}
	
	/**
	 * Creates a new composite filter with one input and one output.
	 */
	public CompositeFilter() {
		nbInputs = 1;
		nbOutputs = 1;
		blocks = new Vector<Block>();
		inputs = new Output[nbInputs];
		for (int i = 0; i < nbInputs; i++) {
			inputs[i] = new Output(null);
		}
		outputs = new Input[nbOutputs];
		for (int i = 0; i < nbOutputs; i++) {
			outputs[i] = new Input();
		} // Initialise all that has to be initialised.
	}
	
	/**
	 * Returns the number of inputs/outputs expected by this filter.
	 *
	 * @return	int	The expected number of inputs/outputs.
	 */
	public int nbInputs() {
		return nbInputs;
	}
	
	public int nbOutputs() {
		return nbOutputs;
	}
	
	/**
	 * Resets all blocks composing this filter.
	 */
	public void reset() {
		for (Block block : blocks) {
			block.reset();
		}
	}
	
	/**
	 * Adds filter f, as a block, to the composite filter.
	 *
	 * @param	f				The filter to add.
	 * @throws	FilterException	Thrown if f was already added or if f is null.
	 */
	public void addBlock(Filter f) throws FilterException {
		checked = false;  // If the user adds new blocks, we might have to show a warning.
		
		if (f == null) {
			throw new FilterException("Tried to add null filter to composite filter.");
		}
		
		for (Block block : blocks) {
			if (block.getFilter() == f) {
				throw new FilterException("Filter was already added to composite filter.");
			}
		}
		
		blocks.add(new Block(f));
	}
	
	/**
	 * Connects a block's output to another block's input.
	 *
	 * @param	f1				The filter whose output is asked to connect.
	 * @param	o1				The number of the output that is asked to connect.
	 * @param	f2				The filter whose input is asked to connect.
	 * @param	i2				The number of the output that is asked to connect.
	 * @throws	FilterException	Thrown if any of f1 or f2 is null or was not previously added as
	 * 							a block or if o1 or i2 is out of range.
	 */
	public void connectBlockToBlock(Filter f1, int o1, Filter f2, int i2) throws FilterException {
		checked = false;  // If the user makes new connections, they have to be checked.
		
		if (f1 == null || f2 == null) {
			throw new FilterException("In composite filter, tried to connect null filter.");
		}
		
		Block b1 = null, b2 = null;
		for (Block block : blocks) {
			if (block.getFilter() == f1) {
				b1 = block;
			}
			if (block.getFilter() == f2) {
				b2 = block;
			}
		} // Searches for filters f1 and f2 in the blocks that compose the filter.
		
		if (b1 == null || b2 == null) {
			throw new FilterException("Filter was not previously added to composite filter.");
		} // If one of these was not found, the user forgot to add the filter first.
		
		b1.connectBlockToNextBlock(o1, b2, i2);
	}
	
	/**
	 * Connects a block's output to an output of the composite filter.
	 *
	 * @param	f1				The filter whose output is asked to connect.
	 * @param	o1				The number of the filter's output that is asked to connect.
	 * @param	o2				The number of the composite filter's output
	 * 							that is asked to connect.
	 * @throws	FilterException	Thrown if f1 is null or was not previously added as a block
	 * 							or if o1 or o2 is out of range.
	 */
	public void connectBlockToOutput(Filter f1, int o1, int o2) throws FilterException {
		checked = false;  // If a block is connected to an output, it has to be checked.
		
		if (f1 == null) {
			throw new FilterException("In composite filter, tried to connect null filter to " +
					"output.");
		}
		if (o2 < 0 || o2 >= nbOutputs) {
			throw new FilterException(String.format("Output number %d is out of range for " +
					"composite filter. Must be between 0 and %d (excluded).", o2, nbOutputs));
		}
		
		Block b1 = null;
		for (Block block : blocks) {
			if (block.getFilter() == f1) {
				b1 = block;
			}
		}
		
		if (b1 == null) {
			throw new FilterException("Filter was not previously added to composite filter.");
		}
		
		b1.connectBlockToInput(o1, outputs[o2]);
	}
	
	/**
	 * Connects an input of the composite filter to a block's input.
	 *
	 * @param	i1				The number of the composite filter's input that is asked to connect.
	 * @param	f2				The filter whose input is asked to connect.
	 * @param	i2				The number of the filter's input that is asked to connect.
	 * @throws	FilterException	Thrown if f2 is null or was not previously added as a block
	 * 							or if i1 or i2 is out of range.
	 */
	public void connectInputToBlock(int i1, Filter f2, int i2) throws FilterException {
		// Here we do not reset checked.
		// It is not useful, connecting an input to a block cannot cause a problem.
		if (f2 == null) {
			throw new FilterException("In composite filter, tried to connect input to null filter" +
					".");
		}
		if (i1 < 0 || i1 >= nbInputs) {
			throw new FilterException(String.format("Input number %d is out of range for " +
					"composite filter. Must be between 0 and %d (excluded).", i1, nbInputs));
		}
		
		Block b2 = null;
		for (Block block : blocks) {
			if (block.getFilter() == f2) {
				b2 = block;
			}
		}
		
		if (b2 == null) {
			throw new FilterException("Filter was not previously added to composite filter.");
		}
		
		b2.connectOutputToBlock(inputs[i1], i2);
	}
	
	/**
	 * Checks the composite filter is valid, that is
	 * all its outputs are connected to something,
	 * all blocks' inputs are connected to something,
	 * there is no loop without delay,
	 * all nested composite filters are valid.
	 *
	 * @throws	FilterException	Thrown if one of the above conditions is not satisfied.
	 */
	public void check() throws FilterException {
		if (!checked) {
			// If the filter was not already checked,
			for (Block block : blocks) {
				block.resetChecked();
			} // Resets all checked variables to false.
			
			Vector<Block> path = new Vector<Block>();  // New empty path.
			for (Input output : outputs) {
				output.check(path);
			} // Checks each output.
			
			checked = true;
			// If we reach this line with no exception being thrown, this filter is valid.
			
			for (Block block : blocks) {
				if (!block.checked()) {
					System.err.println("Warning: In composite filter, a filter is not connected " +
							"to any outputs. You might want to remove it or connect it to " +
							"something.");
				}
			} // Prints a warning for each filter that was not checked.
		}
	}
	
	/**
	 * Applies the composite filter to input and returns the output.
	 *
	 * @param	input			The input the filter must be applied to.
	 * @return	double[]		The output the filter returned.
	 * @throws	FilterException	Thrown if the filter is not valid or if input is null or if the
	 * 							length of input does not match the expected number of inputs.
	 */
	public double[] computeOneStep(double[] input) throws FilterException {
		check();
		
		if (input == null) {
			throw new FilterException("Provided null input to composite filter.");
		}
		if (input.length != nbInputs()) {
			throw new FilterException(String.format("Provided %d input(s) to composite filter. It" +
					" expects exactly %d input(s).", input.length, nbInputs));
		}
		
		for (int i = 0; i < nbInputs; i++) {
			inputs[i].put(input[i]);
		} // Start by putting provided values in the corresponding inputs.
		
		double[] output = new double[nbOutputs];
		for (int i = 0; i < nbOutputs; i++) {
			output[i] = outputs[i].get();
		} // Then gets a value from  each output and returns them.
		return output;
	}
}
