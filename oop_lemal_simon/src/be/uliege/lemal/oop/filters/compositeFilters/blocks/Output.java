package be.uliege.lemal.oop.filters.compositeFilters.blocks;

import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Vector;

/**
 * The Output class. It is simply a vector of input that the output writes in.
 * In addition, it has a pointer to the block it is part of.
 * This will be useful for checking all loops in a composite filter are delayed.
 */
public class Output {
	private final Vector<Input> writeTo;
	private final Block partOf;
	
	/**
	 * @param	partOf	The block this output is part of.
	 */
	public Output(Block partOf) {
		writeTo = new Vector<Input>();
		this.partOf = partOf;
	}
	
	/**
	 * Puts value in every input in the list of inputs this output writes to.
	 *
	 * @param	value	The value that is to be put.
	 */
	public void put(double value) {
		for (Input input : writeTo) {
			input.put(value);
		}
	}
	
	/**
	 * Returns partOf.
	 *
	 * @return	Block	The block this output is part of.
	 */
	protected Block isPartOf() {
		return partOf;
	}
	
	/**
	 * Adds input connectTo to the list this output writes to and
	 * sends a message to it so it disconnects from its previous output.
	 *
	 * @param connectTo The input it must connect to.
	 * @throws FilterException Thrown if input is null.
	 */
	protected void connect(Input connectTo) throws FilterException {
		if (connectTo == null) {
			throw new FilterException("Tried to connect output to null input.");
		}
		
		connectTo.connect(this);
		writeTo.add(connectTo);
	}
	
	/**
	 * Removes input disconnectFrom from the list of inputs this output writes to.
	 * This method is intended to be called by the input that wants to disconnect.
	 *
	 * @param	disconnectFrom	The input it must disconnect from.
	 */
	protected void disconnect(Input disconnectFrom) {
		writeTo.removeElement(disconnectFrom);
	}
}
