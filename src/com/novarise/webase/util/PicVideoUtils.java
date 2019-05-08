package com.novarise.webase.util;

import java.util.ArrayList;
import java.util.List;



public class PicVideoUtils
{
 
	
	public static void handler(String ffmpegPath,String upFilePath,String mediaPicPath){
		  List<String> cutpic = new ArrayList<String>();
			cutpic.add(ffmpegPath);
			cutpic.add("-i");
			cutpic.add(upFilePath); // ͬ�ϣ�ָ�����ļ���������ת��Ϊflv��ʽ֮ǰ���ļ���Ҳ������ת����flv�ļ���
			cutpic.add("-y");
			cutpic.add("-f");
			cutpic.add("image2");
			cutpic.add("-ss"); // ��Ӳ�����-ss�����ò���ָ����ȡ����ʼʱ��
			cutpic.add("0"); // �����ʼʱ��Ϊ��17��
			cutpic.add("-t"); // ��Ӳ�����-t�����ò���ָ������ʱ��
			cutpic.add("0.001"); // ��ӳ���ʱ��Ϊ1����
			cutpic.add("-s"); // ��Ӳ�����-s�����ò���ָ����ȡ��ͼƬ��С
			cutpic.add("1024*768"); // ��ӽ�ȡ��ͼƬ��СΪ350*240
			cutpic.add(mediaPicPath); // ��ӽ�ȡ��ͼƬ�ı���·��

			boolean mark = true;
			ProcessBuilder builder = new ProcessBuilder();
			try {
			   
			    builder.command(cutpic);
			    builder.redirectErrorStream(true);
			    // ���������Ϊ true�����κ���ͨ���˶���� start() ���������ĺ����ӽ������ɵĴ�������������׼����ϲ���
			    //������߾���ʹ�� Process.getInputStream() ������ȡ����ʹ�ù���������Ϣ����Ӧ�������ø�����
			    builder.start();
			} catch (Exception e) {
			    mark = false;
			    System.out.println(e);
			    e.printStackTrace();
			}
	  }
	
		  
	   public static void main(String[] args){
		   handler("D:\\ffmpeg\\bin\\ffmpeg.exe","D:\\ffmpeg\\aa1ef98f28334b5cafd955a39575dbd7.mp4","d:\\eeeeeeee.jpg");
	   }
	
	    
	    
 
	
 
}
