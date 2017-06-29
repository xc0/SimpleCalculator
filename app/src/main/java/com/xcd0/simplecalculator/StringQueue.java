package com.xcd0.simplecalculator;


public class StringQueue {
	private int queueSize;
	private int str, end;
	private String[] queue;
	
	public StringQueue( int size ) {
		this.queueSize = size;
		this.str = 0;
		this.end = 0;
		this.queue = new String[ this.queueSize ];
	}
	
	public void enqueue( String in ) {
		
		if( this.str + this.queueSize == ( this.end + 1 + this.queueSize ) % this.queueSize ) {
			return;
		}
		this.end++;
		this.end = this.end % this.queueSize;
		this.queue[ this.end - 1 ] = in;
	}
	
	public String dequeue() {
		if( this.str == this.end ) {
			return "";
		}
		this.str++;
		// strがmax超えてたら減らす
		this.str = this.str % this.queueSize;
		return this.queue[ this.str - 1 ];
		
	}
	
	public int size() {
		return ( end - str + queueSize ) % queueSize;
	}
	
	public String checkFirst() {
		return this.queue[ this.str ];
	}
}