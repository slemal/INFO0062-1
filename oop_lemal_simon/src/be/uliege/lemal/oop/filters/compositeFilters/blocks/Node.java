package be.uliege.lemal.oop.filters.compositeFilters.blocks;

/**
 * A Node class, that is used to implement a simply linked list in the class Input.
 */
class Node {
	private final double value;
	private Node next;
	
	protected Node(double value, Node next) {
		this.value = value;
		this.next = next;
	}
	
	protected Node(double value) {
		this.value = value;
		this.next = null;
	}
	
	protected void setNext(Node next) {
		this.next = next;
	}
	
	protected Node getNext() {
		return next;
	}
	
	protected double getValue() {
		return value;
	}
}