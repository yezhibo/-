package com.sshp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.FileUtil;
import com.LaplaceVector;
import com.MSE;

public class SSHP {
	
	private double[] sortH;
	public double[] proH;
	
	public SSHP(double[] orgH, double e) {
		long beginTime = System.currentTimeMillis();
		double e0 = e/1000;
		//double e1 = e/2000;
		double e2 = e/2000;
		double e3 = e-e2-e0;
		proH = new double[orgH.length];
		SampleSort sampleSort = new SampleSort(orgH, e0);
		int[] sortLag = sampleSort.sortLag;
		sortH = sampleSort.sortH;
		List<double[]> initArrList = new InitialBiHis(sortH, e3).arrList;
		List<double[]> finalArrList = new ArrayList<double[]>();
		System.out.println("<==============ClusterSplit part1==============>");
		List<double[]> arrList1 = new ClusterSplit(initArrList.get(0), e2, e3).arrFinalList;
		System.out.println("<==============ClusterSplit part2==============>");
		List<double[]> arrList2 = new ClusterSplit(initArrList.get(1), e2, e3).arrFinalList;
		finalArrList.addAll(arrList1);
		finalArrList.addAll(arrList2);
		addLapNoise(finalArrList,e3);
		reSort(sortLag,proH);
		long endTime = System.currentTimeMillis();
		System.out.println("SSHP finished used "+(endTime-beginTime)+"ms.");
	}
	
	/**
	 * 恢复原始顺序
	 * @param sortLag
	 * @param proH
	 */
	public void reSort(int[] sortLag, double[] proH) {
		int n = sortLag.length;
		for(int i=0; i<n-1; i++){
			for(int j=i+1; j<n; j++){
				if(sortLag[i]>sortLag[j]){
					int tempLag = sortLag[i];
					sortLag[i] = sortLag[j];
					sortLag[j] = tempLag;
					
					double tempdata = proH[i];
					proH[i] = proH[j];
					proH[j] = tempdata;
				}
			}
		}
	}
	
	/**
	 * 直接排序方法，算法中未使用
	 * @param orgH
	 * @return
	 */
	public int[] sort(double[] orgH) {
		int n = orgH.length;
		int[] sortLag = new int[n];
		sortH = new double[n];
		for(int i=0; i<n; i++) {
			sortLag[i] = i;
			sortH[i] = orgH[i];
		}
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if(sortH[i]>sortH[j]) {
					double temp = sortH[i];
					sortH[i] = sortH[j];
					sortH[j] = temp;
					int tempI = sortLag[i];
					sortLag[i] = sortLag[j];
					sortLag[j] = tempI;
				}
			}
		}
		return sortLag;
	}
	
	/**
	 * 对分组后的直方图添加拉普拉斯噪声
	 * @param arrList
	 * @param e
	 */
	public void addLapNoise(List<double[]> arrList,double e) {
		int k = arrList.size();
		double[] lap = new LaplaceVector(k, 1, e).lap;
		int p = 0;
		for(int i=0; i<k; i++) {
			double mean = getMean(arrList.get(i));
			int l = arrList.get(i).length;
			for(int j=0; j<l; j++) {
				proH[p++] = mean + lap[i]/l;
			}			
		}
	}
	
	/**
	 * 获取均值
	 * @param arr
	 * @return
	 */
	public double getMean(double[] arr) {
		double mean = 0;
		int n = arr.length;
		for(int i=0; i<n; i++) {
			mean += arr[i];
		}
		mean /= n;
		return mean;
	}
	
	/**
	 * 主算法 测试
	 * @param args
	 */
	public static void main(String[] args) {
		int[] qrange = new int[] {1,10,100,500,1000,2000,3000,4000,5000,6000,7000};	
		String waitakere_path = "G://科研科研//静态直方图发布论文//实验数据//log//search_logs.txt";
		String path = "G://科研科研//静态直方图发布论文//实验数据//log";
		double[] orgH = FileUtil.read(waitakere_path, null);
		double[] mse = new double[11];
		long beginTime = System.currentTimeMillis();
		for(int i=1; i<=1; i++) {
			double e = 0.1;
			if(i==1) {
				e = Math.log(2);
			}else if(i==2) {
				e = 0.1;
			}else if(i==3) {
				e = 0.01;
			}
			for(int j=0; j<1; j++) {                     //测30次，最后取平均值
				double[] proH = new SSHP(orgH, e).proH;
				double[] mse_j = new MSE(orgH, proH, qrange).mse;				
				System.out.println("MSE:"+Arrays.toString(mse_j));
				for(int k=0; k<mse.length; k++) {
					mse[k] += mse_j[k]/30;
				}
			}
			//FileUtil.write(path+"//SSHP"+i+".txt", mse, false);
		}	
		long endTime = System.currentTimeMillis();
		System.out.println("sshp算法执行时间："+(endTime-beginTime));
	}
}
