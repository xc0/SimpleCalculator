package com.xcd0.simplecalculator;

public class dataKeep implements Cloneable {
	private String[] inputString;
	private String[] inputArray;
	private int inputCounter;
	private int statusCode;
	private String preInput;
	private String preAns;
	private String output;
	
	@Override
	public Object clone() {	//throwsを無くす
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
}
