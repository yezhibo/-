package com;

import java.util.Arrays;

/**
 * laplace噪声向量类
 * @ProjectName DPHistogram
 * @author Yezhibo
 * @CreatTime 2018年5月9日下午8:26:39
 */
public class LaplaceVector {
	
	private double mv;      //位置参数
	private double lamda;   //尺度参数   方差为2*lamda^2
	public double[] lap;    //独立噪声向量
	
	/**
	 * 构造函数
	 * @param n
	 * @param s
	 * @param e
	 */
	public  LaplaceVector(int n, double s, double e){
		this.lap = new double[n];
		this.mv = 0;               //均值默认为0
		this.lamda = s/e;
		for(int i=0; i<n; i++){
			double u = Math.random() - 0.5; //产生一个-0.5到0.5之间的随机数
			double absu = Math.abs(u);
			if(u>0){
				lap[i] = mv-lamda*Math.log(1-2*absu);
			}else{
				lap[i] = mv+lamda*Math.log(1-2*absu);
			}
		}
	}
	public double[] Getlap() {
		return lap;
	}
	
	public static void main(String[] args) {
		LaplaceVector lapv = new LaplaceVector(100, 1, Math.log(2));
		System.out.println(Arrays.toString(lapv.lap));
	}
}
