package com.xcd0.simplecalculator;


public class StringStack {
	private int stackSize;
	private int stackPointer;
	private String[] stack;
	
	public StringStack( int size ) {
		this.stackPointer = 0;
		this.stackSize = size;
		
		stack = new String[ stackSize ];
	}
	
	public void push( String tmp ) {
		if( stackPointer >= stackSize ) {
			return;
		}
		stack[ stackPointer++ ] = tmp;
	}
	
	public String pop() {
		if( stackPointer <= 0 ) {
			return "";
		}
		return stack[ --stackPointer ];
	}
	
	public int size() {
		return this.stackPointer;
	}
}