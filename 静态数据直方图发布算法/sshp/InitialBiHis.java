package com.sshp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitialBiHis {
	public List<double[]> arrList;
	public InitialBiHis(double[] orgH, double e3){
		System.out.println("<=================InitialBiHis=================>");
		int n = orgH.length;
		List<double[]> arrList0 = new ArrayList<double[]>();
		arrList0.add(orgH);
		double err0 = err(arrList0, e3);
		System.out.println("the length of arrList:"+n+", err:"+err0);
		List<Integer> BisectIndex = new ArrayList<Integer>();
		List<Double> BisectErr = new ArrayList<Double>();
		for(int i=0; i<n-1; i++) {
			double[] arr1 = new double[i+1];
			double[] arr2 = new double[n-i-1];
			System.arraycopy(orgH, 0, arr1, 0, i+1);
			System.arraycopy(orgH, i+1, arr2, 0, n-i-1);
			List<double[]> arrList = new ArrayList<double[]>();
			arrList.add(arr1);
			arrList.add(arr2);
			double err = err(arrList, e3);
			if(err<err0) {
				BisectIndex.add(i);
				BisectErr.add(err);
			}
		}
		//filterErr(BisectIndex, BisectErr, 20);
		System.out.println("BisectErr："+BisectErr.size());
		int minIndex = BisectIndex.get(selectMinErr(BisectErr));
		
		this.arrList = new ArrayList<double[]>();
		double[] arr1 = new double[minIndex+1];
		double[] arr2 = new double[n-minIndex-1];
		System.arraycopy(orgH, 0, arr1, 0, minIndex+1);
		System.arraycopy(orgH, minIndex+1, arr2, 0, n-minIndex-1);
		this.arrList.add(arr1);
		this.arrList.add(arr2);
		System.out.println("Initial finished! result:");
		System.out.println("the length of arr1:"+arr1.length+" "+Arrays.toString(arr1));
		System.out.println("the length of arr2:"+arr2.length+" "+Arrays.toString(arr2));
	}
	
	/**
	 * 获取最小误差值对应的下标
	 * @param errList
	 * @return
	 */
	public int selectMinErr(List<Double> errList) {
		int minIndex = 0;
		double minErr = Double.MAX_VALUE;
		int n = errList.size();
		for(int i=0; i<n; i++) {
			if(errList.get(i)<minErr) {
				minIndex = i;
				minErr = errList.get(i);
			}
		}
		return minIndex;
	}
	
	/**
	 * 保留一定范围的误差
	 * @param indexList
	 * @param errList
	 * @param y
	 */
	public void filterErr(List<Integer> indexList, List<Double> errList, int y) {
		int n = errList.size();
		if(n<y) {
			return;
		}
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if(errList.get(i)>errList.get(j)) {
					double temp = errList.get(i);
					errList.set(i, errList.get(j));
					errList.set(j, temp);
					int tempIndex = indexList.get(i);
					indexList.set(i, indexList.get(j));
					indexList.set(j, tempIndex);
				}
			}
		}
		errList.removeAll(errList.subList(y, n));
		indexList.removeAll(indexList.subList(y, n));
	}
	/**
	 * 误差计算
	 * @param arrList
	 * @param e3
	 * @return
	 */
	public double err(List<double[]> arrList, double e3) {
		int k = arrList.size();
		double re = 0;
		for(int i=0; i<k; i++) {
			double[] arr = arrList.get(i);
			double mean = 0;                   //分组arr的均值
			for(int j=0; j<arr.length; j++) {
				mean += arr[j];
			}
			mean /= arr.length;
			for(int j=0; j<arr.length; j++) {
				re += Math.abs(arr[j]-mean);
			}
		}
		return re + (double)k/e3;
	}
	
	/**
	 * 利用轮盘赌抽样出最小误差的方案
	 * @param errList
	 * @param e1
	 * @return
	 */
	public int selectMinErr(List<Double> errList, double e1) {
		int n = errList.size();
		int minI = 0;
		double[] f = new double[n];
		for(int i=0; i<n; i++) {
			f[i] = Math.pow(Math.E, -e1*errList.get(i)/4);     //效用函数
		}
		double[] p = new double[n];
		double sum = 0;
		for(int i=0; i<n; i++) {
			sum += f[i];
		}
		for(int i=0; i<n; i++) {
			p[i] = f[i]/sum;
		}
		double r = Math.random();
		double cumP = 0;
		for(int i=0; i<n; i++) {
			cumP += p[i];
			if(cumP > r) {
				minI = i;
				break;
			}
		}
		System.out.println("selected err："+errList.get(minI));
		return 	minI;
	}
	
	public static void main(String[] args) {
		double[] h = new double[] {0,0,0,0,0,1,1,0,0,1,1,10,10,100,100,100};
		List<double[]> arrList = new InitialBiHis(h, 1).arrList;
		for(int i=0; i<arrList.size(); i++) {
			System.out.println(Arrays.toString(arrList.get(i)));
		}
	}
}
