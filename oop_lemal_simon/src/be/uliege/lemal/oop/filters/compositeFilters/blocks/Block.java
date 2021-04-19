package be.uliege.lemal.oop.filters.compositeFilters.blocks;

import be.uliege.lemal.oop.filters.compositeFilters.CompositeFilter;
import be.uliege.lemal.oop.filters.elementaryFilters.DelayFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Vector;

/**
 * The Block class. It contains a filter, a list of inputs and a list of outputs.
 */
public class Block {
	private final Input[] inputs;
	private final Output[] outputs;
	private final Filter filter;
	private boolean checked = false;
	
	/**
	 * @param	filter			The filter this block must contain.
	 * @throws	FilterException	Thrown if filter is null.
	 */
	public Block(Filter filter) throws FilterException {
		if (filter == null) {
			throw new FilterException("Tried to create new block from null filter.");
		}
		
		this.filter = filter;
		
		inputs = new Input[filter.nbInputs()];
		for (int i = 0; i < filter.nbInputs(); i++) {
			inputs[i] = new Input();
		}
		outputs = new Output[filter.nbOutputs()];
		for (int i = 0; i < filter.nbOutputs(); i++) {
			outputs[i] = new Output(this);
		} // Initialises inputs and outputs.
	}
	
	/**
	 * Resets the filter and all inputs.
	 */
	public void reset() {
		filter.reset();
		for (Input input : inputs) {
			input.reset();
		}
	}
	
	/**
	 * Returns filter.
	 *
	 * @return	Filter	The filter this block contains.
	 */
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * Connects output number outputNb of this block to input.
	 *
	 * @param	outputNb		The number of the output,
	 * 							between 0 and the number of outputs of filter.
	 * @param	input			The input the block must connect to.
	 * @throws	FilterException	Thrown if output number is out of range or if input is null.
	 */
	public void connectBlockToInput(int outputNb, Input input) throws FilterException {
		if (input == null) {
			throw new FilterException("In composite filter, tried to connect filter to null input" +
					".");
		}
		if (outputNb < 0 || outputNb >= filter.nbOutputs()) {
			throw new FilterException(String.format("In composite filter, output number %d is out" +
							" of range for filter. Must be between 0 and %d (excluded).",
					outputNb, filter.nbOutputs()));
		}
		
		outputs[outputNb].connect(input);
	}
	
	/**
	 * Connects output to input number inputNb of this block.
	 *
	 * @param	output			The output that asks to connect.
	 * @param	inputNb			The number of the input,
	 * 							between 0 and the number of inputs of filter.
	 * @throws	FilterException	Thrown if input number is out of range or if output is null.
	 */
	public void connectOutputToBlock(Output output, int inputNb) throws FilterException {
		if (output == null) {
			throw new FilterException("In composite filter, tried to " +
					"connect null output to filter.");
		}
		if (inputNb < 0 || inputNb >= filter.nbInputs()) {
			throw new FilterException(String.format("In composite filter, input number %d is out " +
							"of range for filter. Must be between 0 and %d (excluded).", inputNb,
					filter.nbInputs()));
		}
		
		output.connect(inputs[inputNb]);
	}
	
	/**
	 * Connects this block output number outputNb to another block's input number inputNb.
	 *
	 * @param	outputNb		The number of the output,
	 * 							between 0 and the number of outputs of filter.
	 * @param	connectTo		The other block, whose input this block must connect to.
	 * @param	inputNb			The number of the input, between 0 and
	 * 							the number of inputs of the other block's filter.
	 * @throws	FilterException	Thrown if connectTo is null
	 * 							or if output number is out of range for this block
	 * 							or if input number is out of range for the next one.
	 */
	public void connectBlockToNextBlock(int outputNb, Block connectTo, int inputNb)
			throws FilterException {
		if (connectTo == null) {
			throw new FilterException("In composite filter, tried to connect filter to null " +
					"filter.");
		}
		if (outputNb < 0 || outputNb >= filter.nbOutputs()) {
			throw new FilterException(String.format("In composite filter, output number %d is out" +
							" of range for filter. Must be between 0 and %d (excluded).",
					outputNb, filter.nbOutputs()));
		}
		
		connectTo.connectOutputToBlock(outputs[outputNb], inputNb);
	}
	
	/**
	 * Resets checked to false, so the composite filter can be checked again.
	 */
	public void resetChecked() {
		checked = false;
	}
	
	/**
	 * Returns checked.
	 *
	 * @return	boolean	Whether or not this block was checked.
	 */
	public boolean checked() {
		return checked;
	}
	
	/**
	 * Checks that the block is part of a valid composite filter, that is
	 * all its inputs are connected to something,
	 * if it is not a delay filter, it is impossible to loop back to it,
	 * if its filter is composite, check it is a valid one.
	 *
	 * @param	path			The blocks that were checked since the last delay we encountered.
	 * 							Used to check all loops contain a delay.
	 * @throws	FilterException	Thrown if one of the above conditions is not satisfied.
	 */
	protected void check(Vector<Block> path) throws FilterException {
		if (path == null) {
			throw new FilterException("Unexpected error: In composite filter, while checking, got" +
					" null path.");
		}
		
		if (path.contains(this)) {
			throw new FilterException("Composite filter contains a loop with no delay.");
		} // If this block is already in path, we made a loop.
		// Since we reset the path when we reach a delay filter,
		// this loop cannot contain a delay. We throw an exception.
		
		if (checked) return;  // If the block was already checked, there is nothing else to do.
		
		if (filter instanceof CompositeFilter) {
			((CompositeFilter) filter).check();
		} // If filter is composite, we have to check it.
		
		checked = true;  // At this point, this filter is valid.
		// But we have to check the filters it reads from are valid as well.
		// We set checked to true now to avoid creating an infinite loop.
		
		if (filter instanceof DelayFilter) {
			path = new Vector<Block>();
		} // If this block's filter is a delay, we reset the path.
		else path.add(this);  // Otherwise we add this block to the path.
		
		// In both cases we check the blocks that write their outputs in this block.
		for (Input input : inputs) {
			input.check(path);  // We obsiously give the updated path as argument.
		}
		
		path.removeElement(this);
		// Finally we reset the path to what it was before this method was called.
		
		checked = true;
		// If no exception was thrown, this block and the previous ones are valid.
	}
	
	/**
	 * Checks all inputs are nonempty and applies filter, the puts values returned in outputs.
	 *
	 * @throws FilterException Thrown if applying the filter throws an exception.
	 *                         In practice, this should not happen
	 *                         unless the user designs and uses incorrect filters.
	 */
	protected void computeOutput() throws FilterException {
		double[] output = null;
		boolean proceed = true;
		
		if (filter instanceof DelayFilter) {
			// If this block contains a delay filter we check all its inputs.
			for (Input input : inputs) {
				if (input.size() == 0) {
					proceed = false;
				}
			} // If they are all nonempty, we proceed in the normal way.
			
			// If at least one input is empty, we ask the filter to compute one step without input.
			if (!proceed) {
				try {
					output = ((DelayFilter) filter).computeOneStep();
				} catch (FilterException e) {
					proceed = true;
					// It might not work (if empty queue). In that case, we proceed normally.
				}
			}
		}
		
		// Normal way of proceeding. Used if this block is not a delay,
		// if it is a delay and has inputs available or if it is a delay but its queue is empty.
		if (proceed) {
			double[] input = new double[filter.nbInputs()];
			for (int i = 0; i < filter.nbInputs(); i++) {
				input[i] = inputs[i].get();
			} // Gets a value from each input.
			
			try {
				output = filter.computeOneStep(input);
				// Applies the filter.
			}
			catch (FilterException e) {
				throw new FilterException("Unexpected error: An exception was thrown while " +
						"computing one step of filter. Message reads:\n" + e.getMessage());
				// In practice, no exception should be thrown here as input is well defined.
			}
		}
		
		for (int i = 0; i < filter.nbOutputs(); i++) {
			outputs[i].put(output[i]);
		} // Puts the returned values in the corresponding outputs.
	}
}
