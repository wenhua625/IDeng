package com.novarise.webase.util;

import java.util.ArrayList;
import java.util.List;



public class PicVideoUtils
{
 
	
	public static void handler(String ffmpegPath,String upFilePath,String mediaPicPath){
		  List<String> cutpic = new ArrayList<String>();
			cutpic.add(ffmpegPath);
			cutpic.add("-i");
			cutpic.add(upFilePath); // 同上（指定的文件即可以是转换为flv格式之前的文件，也可以是转换的flv文件）
			cutpic.add("-y");
			cutpic.add("-f");
			cutpic.add("image2");
			cutpic.add("-ss"); // 添加参数＂-ss＂，该参数指定截取的起始时间
			cutpic.add("0"); // 添加起始时间为第17秒
			cutpic.add("-t"); // 添加参数＂-t＂，该参数指定持续时间
			cutpic.add("0.001"); // 添加持续时间为1毫秒
			cutpic.add("-s"); // 添加参数＂-s＂，该参数指定截取的图片大小
			cutpic.add("1024*768"); // 添加截取的图片大小为350*240
			cutpic.add(mediaPicPath); // 添加截取的图片的保存路径

			boolean mark = true;
			ProcessBuilder builder = new ProcessBuilder();
			try {
			   
			    builder.command(cutpic);
			    builder.redirectErrorStream(true);
			    // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
			    //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
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
