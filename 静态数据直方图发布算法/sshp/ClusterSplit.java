package com.sshp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClusterSplit {
	
	public List<double[]> arrFinalList;
	
	public ClusterSplit(double[] orgH, double e2, double e3) {
		List<Partition> partList = new LinkedList<Partition>();
		Partition part = new Partition(orgH,0);
		partList.add(part);
		double lnH = Math.log(orgH.length);
		while(!isAllPartLeaf(partList)) {
			//1.首先拿到第一个非叶子节点
			int index = getFirstNotLeafPart(partList);
			Partition firstNonLeafPart = new Partition(partList.get(index).getArr(),partList.get(index).getDepth());      
			int k = partList.size();                                         //当前分组个数
			double err = getREErr(partList) + (double)k/e3;                  //当前分组的误差
			System.out.println("the size of partList:"+k+",err:"+err);
			//2.开始对第一个非叶子节点进行动态分割，计算分割后的误差
			partList.remove(index);
			double otherRE = getREErr(partList);               //除了当前非叶子节点外，其他分组的近似误差和，减少后边计算量
			double[] arr = firstNonLeafPart.getArr();
			int m = arr.length;
			System.out.println("the length of NonLeafPart:"+m+", depth:"+firstNonLeafPart.getDepth());
			List<Integer> BiIndexList = new ArrayList<Integer>();
			List<Double> pkErrList = new ArrayList<Double>();
			for(int i=0; i<m-1; i++) {
				double re = getREErr(firstNonLeafPart,i) + otherRE;           //分割后的近似误差
				double err1 = re+(double)(k+1)/e3;                            //分割后的总误差
				if(err1<err) {
					BiIndexList.add(i);
					pkErrList.add(err1);
				}
			}
			//如果找不到误差小的，则将当前节点标识为叶子节点
			if(pkErrList.isEmpty()) { 
				System.out.println("No Better err fond!");
				firstNonLeafPart.setLeaf(true);
				partList.add(index, firstNonLeafPart);
			}
			//否则，利用轮盘赌选择出误差小的，作为下一层
			else {	
				System.out.println("pkErrList:"+pkErrList.size());
				filterErr(BiIndexList, pkErrList, 20);
				int minIndex = BiIndexList.get(selectMinErr(pkErrList, e2, lnH));
				int depth = firstNonLeafPart.getDepth();
				double[] arr1 = new double[minIndex+1];
				double[] arr2 = new double[m-minIndex-1];
				System.arraycopy(arr, 0, arr1, 0, minIndex+1);
				System.arraycopy(arr, minIndex+1, arr2, 0, m-minIndex-1);
				Partition part1 = new Partition(arr1,depth+1);
				Partition part2 = new Partition(arr2,depth+1);
				if(arr1.length==1 || depth+1>(int)lnH) {
					part1.setLeaf(true);
				}
				if(arr2.length==1 || depth+1>(int)lnH) {
					part2.setLeaf(true);
				}
				partList.add(index,part1);
				partList.add(index+1,part2);
				System.out.println("Non-Leaf Split finished! result:");
				System.out.println("the length of arr1:"+arr1.length+" "+Arrays.toString(arr1));
				System.out.println("the length of arr2:"+arr2.length+" "+Arrays.toString(arr2));
			}
		}
		arrFinalList = new ArrayList<double[]>();
		System.out.println("ClusterSplit finished! result:");
		for(int i=0; i<partList.size(); i++) {
			arrFinalList.add(partList.get(i).getArr());
			System.out.println("the length of arr"+(i+1)+":"+partList.get(i).getArr().length+" "+Arrays.toString(partList.get(i).getArr()));
		}
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
	 * 利用轮盘赌抽样技术随机选择出误差最小的分组方案
	 * @param errList
	 * @param e2
	 * @param lnH
	 * @return
	 */
	public int selectMinErr(List<Double> errList, double e2, double lnH) {
		int n = errList.size();
		int minI = 0;
		double[] f = new double[n];
		for(int i=0; i<n; i++) {
			f[i] = Math.pow(Math.E, -e2*errList.get(i)/(4*lnH));
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
		System.out.println("selected err:"+errList.get(minI));
		return 	minI;
	}
	
	/**
	 * 求输入分组方案的误差
	 * @param partList
	 * @param e3
	 * @return
	 */
	public double getREErr(List<Partition> partList) {
		int k = partList.size();
		double re = 0;
		for(int i=0; i<k; i++) {
			re += getREErr(partList.get(i));
		}
		return re;
	}
	/**
	 * 获取输入part的近似误差
	 * @param part
	 * @return
	 */
	public double getREErr(Partition part) {
		double re = 0;
		double[] arr = part.getArr();
		double mean = part.getMean();
		int n = arr.length;
		for(int i=0; i<n; i++) {
			re += Math.abs(arr[i]-mean);
		}
		return re;
	}
	
	public double getREErr(Partition part, int bisectIndex) {
		double[] arr = part.getArr();
		int n = arr.length;
		double[] arr1 = new double[bisectIndex+1];
		double[] arr2 = new double[n-bisectIndex-1];
		System.arraycopy(arr, 0, arr1, 0, bisectIndex+1);
		System.arraycopy(arr, bisectIndex+1, arr2, 0, n-bisectIndex-1);		
		return getREErr(arr1)+getREErr(arr2);
	}
	
	public double getREErr(double[] arr) {
		double re = 0;
		double mean = 0;
		int n = arr.length;
		for(int i=0; i<n; i++) {
			mean += arr[i];
		}
		mean /= n;
		for(int i=0; i<n; i++) {
			re += Math.abs(arr[i]-mean);
		}
		return re;
	}
	/**
	 * 判断当前分组方案是否全部为叶子节点
	 * @param partList
	 * @return
	 */
	public boolean isAllPartLeaf(List<Partition> partList) {
		boolean isAllLeaf = true;
		int k = partList.size();
		for(int i=0; i<k; i++) {
			if(!partList.get(i).isLeaf()) {
				isAllLeaf = false;
				break;
			}
		}
		return isAllLeaf;
	}
	
	/**
	 * 得到队列中第一个非叶子节点
	 * @param partList
	 * @return
	 */
	public int getFirstNotLeafPart(List<Partition> partList) {
		int k = partList.size();
		int index = 0;
		for(int i=0; i<k; i++) {
			if(!partList.get(i).isLeaf()) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static void main(String[] args) {
		double[] h = new double[] {0,0,0,0,0,1,1,0,0,1,1,10,10,100,100,100};
		List<double[]> arrList = new ClusterSplit(h,1.0,1.0).arrFinalList;
		for(int i=0; i<arrList.size(); i++) {
			System.out.println(Arrays.toString(arrList.get(i)));
		}
	}
}
