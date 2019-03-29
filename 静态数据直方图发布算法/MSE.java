package com;

/**
 * @ProjectName DPHistogram
 * @author Yezhibo
 * @CreatTime 2018年5月9日下午10:38:59
 */
public class MSE {

	double[] orgH;			//原始直方图
	double[] proH;			//隐私保护算法处理后的直方图
	public double[] mse;			//均方误差
	
	/**
	 * 构造函数
	 * @param orgH
	 * @param proH
	 * @param qRange
	 */
	public MSE(double[] orgH, double[] proH, int[] qRange){
		int hsize = orgH.length;
		int qsize = qRange.length;
		this.orgH = new double[hsize];
		this.proH = new double[hsize];
		this.mse = new double[qsize];
		long beginTime = System.currentTimeMillis();
		System.out.println("start compute query Err.");
		for(int i=0; i<hsize; i++){
			this.orgH[i] = orgH[i];
			this.proH[i] = proH[i];
		}
		
		for(int i=0; i<qsize; i++){
			int qrange = qRange[i];
			mse[i] = GetMse(qrange);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("compute finished used "+(endTime-beginTime)+"ms.");
	}
	
	/**
	 * mse计算函数
	 * @param qrange
	 * @return
	 */
	public double GetMse(int qrange){
		double qmse = 0;
		int hsize = orgH.length;
		
		//计算所有可能的范围查询误差
		int n = hsize-qrange+1;
		double[] err = new double[n];
		double mean = 0;
		for(int i=0; i<n; i++){
			double qorgh = 0;
			double qproh = 0;
			for(int j=0; j<qrange; j++){
				qorgh += orgH[i+j];
				qproh += proH[i+j];
			}
			err[i] = (qproh-qorgh)*(qproh-qorgh);
			mean += err[i];
		}
		//计算误差均值
		mean /= n;
		qmse = mean;		
		return qmse;
	}
	
}
