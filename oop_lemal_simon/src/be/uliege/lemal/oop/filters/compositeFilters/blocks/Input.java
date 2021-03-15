/*
 * This class and the Node class are inspired by those we designed in class during the fourth
 * exercises session on the fifth of March 2020.
 */

package be.uliege.lemal.oop.filters.compositeFilters.blocks;

import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Vector;

/**
 * The Input class. It consists of a queue (a simply linked list) and a pointer to an output,
 * to prevent multiple outputs to connect to the same input.
 */
public class Input {
	private Node first = null, last = null;
	private int size = 0;
	private Output readsFrom = null;
	
	/**
	 * Adds a new value to the queue.
	 *
	 * @param	value	The value to be added.
	 */
	protected void put(double value) {
		Node n = new Node(value);
		if (size == 0) {
			last = first = n;
		} else {
			last.setNext(n);
			last = n;
		}
		size++;
	}
	
	/**
	 * Gets a value from the queue.
	 *
	 * @return	double			The gotten value.
	 * @throws	FilterException	Thrown if computing outputs from previous block throws an exception.
	 * 							In practice, this should not happen unless the user designs and
	 * 							uses incorrect filters.
	 */
	public double get() throws FilterException {
		// When get is called, if the queue is empty, we ask the previous block to compute.
		if (size == 0) {
			if (readsFrom.isPartOf() == null) {
				throw new FilterException("Unexpected error: A composite filter's input is empty.");
				// Should not happen in practice.
			}
			readsFrom.isPartOf().computeOutput();
		}
		
		double value = first.getValue();
		first = first.getNext();
		if (--size == 0) {
			last = null;
		}
		return value;
	}
	
	/**
	 * Returns size.
	 *
	 * @return	int	The size of the queue.
	 */
	protected int size() {
		return size;
	}
	
	/**
	 * Clears the queue.
	 */
	protected void reset() {
		first = last = null;
		size = 0;
	}
	
	/**
	 * Disconnects from current output and connects to a new one.
	 * This method is intended to be called by the output that wants to connect.
	 *
	 * @param	connectTo		The new output this input must connect to.
	 * @throws	FilterException	Thrown if output is null.
	 */
	protected void connect(Output connectTo) throws FilterException {
		if (connectTo == null) {
			throw new FilterException("Tried to connect null output to input.");
		}
		
		disconnect();
		readsFrom = connectTo;
	}
	
	/**
	 * Sends a message to the output that is currently connected to this input (if any)
	 * and asks it to disconnect.
	 * This method is called by the connect method if a new output wants to connect to this input.
	 */
	private void disconnect() {
		if (readsFrom != null) {
			readsFrom.disconnect(this);
			readsFrom = null;
			// If this is connected to an output, we ask the output to disconnect.
		}
	}
	
	
	/**
	 * Checks this input is connected to an output which itself is part of a valid block.
	 *
	 * @param path				The blocks that were checked since the last delay we encountered.
	 * @throws FilterException	Thrown if this input is not connected to anything
	 * 							or if checking the previous block throws an exception.
	 */
	public void check(Vector<Block> path) throws FilterException {
		if (readsFrom == null) {
			throw new FilterException("In composite filter, input is not connected to anything.");
		} // We check input is connected to something.
		
		if (readsFrom.isPartOf() != null) {
			// Null only if input reads directly from an input of the composite filter.
			readsFrom.isPartOf().check(path);  // Otherwise we check the previous block is valid.
		}
	}
}
