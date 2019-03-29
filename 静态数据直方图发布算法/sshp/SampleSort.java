package com.sshp;

import java.util.ArrayList;
import java.util.List;

import com.FileUtil;

public class SampleSort {
	
	public int[] sortLag;     //排序下标	
	public double[] sortH;    //排序结果
	
	public SampleSort(double[] orgH, double e1){
		List<Double> SortH = new ArrayList<Double>();        //排序队列集合
		List<Integer> SortLag = new ArrayList<Integer>();    //排序下表集合
		List<Double> reH = new ArrayList<Double>();          //种群剩余个体
		List<Integer> reLag = new ArrayList<Integer>();      //种群剩余个体下标		
		int n = orgH.length;	
		long beginTime = System.currentTimeMillis();
		System.out.println("开始抽样排序...");
		for(int i=0; i<n; i++){
			reH.add(orgH[i]);
			reLag.add(i);
		}
		/* 由于轮盘赌抽样最终结果与顺序有关，因此在抽样排序前首先对原始数据按大到小进行排序
		 * 首先从小到大进行排序*/
		for(int i=0; i<n-1; i++){
			for(int j=i+1; j<n; j++){
				if(reH.get(i)>reH.get(j)){
					double tpd = reH.get(i);
					reH.set(i, reH.get(j));
					reH.set(j, tpd);
					
					int tpI = reLag.get(i);
					reLag.set(i, reLag.get(j));
					reLag.set(j, tpI);
				}
			}		
		}
		/*然后进行抽样排序*/
		SortH.add(reH.get(0));
		SortLag.add(reLag.get(0));
		reH.remove(0);
		reLag.remove(0);
		double preh = reH.get(0);
		while(reH.size()>0){
			double[] fit = GetFit(preh,reH,e1);
			int optI = Select(filter(fit, 10));
			preh = reH.get(optI);
			SortH.add(preh);
			SortLag.add(reLag.get(optI));
			reH.remove(optI);
			reLag.remove(optI);
		}
		sortH = new double[n];
		sortLag = new int[n];
		for(int i=0; i<n; i++){
			sortH[i] = SortH.get(i);
			sortLag[i] = SortLag.get(i);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("排序结果：");
		for(int i=0; i<n; i++) {
			System.out.println(sortH[i]+" "+sortLag[i]);
		}
		System.out.println("抽样排序完成！耗时："+(endTime-beginTime)+"ms.");	
	}
	
	/**
	 * 过滤函数
	 * @param fit
	 * @param y
	 * @return
	 */
	public double[] filter(double[] fit, int y) {
		int n = fit.length;
		if(n<y) {
			return fit;
		}
		double[] arr = new double[y];
		System.arraycopy(fit, 0, arr, 0, y);
		return arr;
	}
	/**
	 * 计算剩余桶的适应度值
	 * @param h     条件桶计数
	 * @param reH   抽样桶计数集合
	 * @param e     抽样排序算法隐私预算
	 * @return
	 */
	public double[] GetFit(double h, List<Double> reH, double e){
		int n = reH.size();
		double[] fit = new double[n];
		for(int i=0; i<n; i++){
			double x = h-reH.get(i);			
			double u = Math.abs(x); //效用函数			
			fit[i] = Math.pow(Math.E, -e*u/2); //指数机制公式
		}
		return fit;
	}
	
	/**
	 * 轮盘赌选择出最接近的桶
	 * @param fit
	 * @return  返回抽样桶计数的下标值
	 */
	public int Select(double[] fit){
		int optI = 0;
		int n = fit.length;
		double[] refit = new double[n];
		double sum = 0;
		for(int i=0; i<n; i++)
			sum += fit[i];
		for(int i=0; i<n; i++)
			refit[i] = fit[i]/sum;
		double r = Math.random();
		double sum_r = 0;
		for(int i=0; i<n; i++){
			sum_r += refit[i];
			if(sum_r>r){
				optI = i;
				break;
			}				
		}
		return optI;
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		double[] orgH = FileUtil.read("G://科研科研//静态直方图发布论文//实验数据//waitakere.txt", null);
		double[] sortH = new SampleSort(orgH, 0.0001).sortH;
		FileUtil.write("G://科研科研//静态直方图发布论文//实验数据//waitakere//sort_waitakere.txt", sortH, false);
	}
}
