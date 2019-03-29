package com.sshp;

public class Partition {
	
	private double[] arr;
	private boolean isLeaf;
	private double mean;
	private int depth;
	
	public Partition() {
		
	}
	
	public Partition(double[] arr, int depth) {
		this.arr = arr;
		this.depth = depth;
		this.isLeaf = false;
		mean = 0;
		for(int i=0; i<arr.length; i++) {
			mean += arr[i];
		}
		mean /= arr.length; 
	}

	public double[] getArr() {
		return arr;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public double getMean() {
		return mean;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
