package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class FileUtil
{
    /** 
     * 读取文件并按行输出
     * @param filePath
     * @param spec 允许解析的最大行数， spec==null时，解析所有行
     * @return
     * @author l00428364
     * @since 2017-12-8
     */
    public static double[] read(final String filePath, final Integer spec)
    {
        File file = new File(filePath);
        // 当文件不存在或者不可读时
        if ((!isFileExists(file)) || (!file.canRead()))
        {
            System.out.println("file [" + filePath + "] is not exist or cannot read!!!");
            return null;
        }
        
        List<String> lines = new LinkedList<String>();
        BufferedReader br = null;
        FileReader fb = null;
        try
        {
            fb = new FileReader(file);
            br = new BufferedReader(fb);

            String str = null;
            int index = 0;
            while (((spec == null) || index++ < spec) && (str = br.readLine()) != null)
            {
            	//一行数据赋值到链表的一个元素
                lines.add(str);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(br);
            closeQuietly(fb);
        }

        //返回链表转化的字符串数组
        int n = lines.size();
        String[] contents = new String[n];
        contents = lines.toArray(new String[n]);
        //将字符串数组转换成为double
        double[] dataArray = new double[n];
        for(int i=0; i<n; i++){
        	dataArray[i] = Double.parseDouble(contents[i]);
        }
        return dataArray;
    }
    /** 
     * 写文件
     * @param filePath 输出文件路径
     * @param content 要写入的内容
     * @param append 是否追加
     * @return
     * @author l00428364
     * @since 2017-12-8
     */
    public static int write(final String filePath, final double[] dataArray, final boolean append)
    {
    	//将数据转换成字符串
    	int n = dataArray.length;
    	String[] contents = new String[n];
    	for(int i=0; i<n; i++){
    		contents[i] = Double.toString(dataArray[i]);
    	}
    	
        File file = new File(filePath);
        

        // 当文件存在但不可写时
        if (isFileExists(file) && (!file.canRead()))
        {
            return 0;
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        try
        {
            if (!isFileExists(file))
            {
                file.createNewFile();
            }

            fw = new FileWriter(file, append);
            bw = new BufferedWriter(fw);
            for (String content : contents)
            {
                if (content == null)
                {
                    continue;
                }
                //按行将内容写入新文件中
                bw.write(content);
                bw.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 0;
        }
        finally
        {
            closeQuietly(bw);
            closeQuietly(fw);
        }

        return 1;
    }

    public static int write(final String filePath, final String[] contents, final boolean append) {
    	
    	
        File file = new File(filePath);
        

        // 当文件存在但不可写时
        if (isFileExists(file) && (!file.canRead()))
        {
            return 0;
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        try
        {
            if (!isFileExists(file))
            {
                file.createNewFile();
            }

            fw = new FileWriter(file, append);
            bw = new BufferedWriter(fw);
            for (String content : contents)
            {
                if (content == null)
                {
                    continue;
                }
                //按行将内容写入新文件中
                bw.write(content);
                bw.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 0;
        }
        finally
        {
            closeQuietly(bw);
            closeQuietly(fw);
        }

        return 1;
    }
    
    private static void closeQuietly(Closeable closeable)
    {
        try
        {
            if (closeable != null)
            {
                closeable.close();
            }
        }
        catch (IOException e)
        {
        }
    }

    private static boolean isFileExists(final File file)
    {
        if (file.exists() && file.isFile())
        {
            return true;
        }

        return false;
    }
    
}
