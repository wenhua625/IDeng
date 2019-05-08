package com.novarise.webase.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class DataImport {
	public void importData(String filePath,String extName) throws Exception{
		
		File directory=new File(filePath);
		
		if(!directory.isDirectory()){
			throw new Exception("文件目录设置不正确，请配置!");
		}
		File[] files=directory.listFiles();
		String filename="";
		for(int i=0;i<files.length;i++){
			
			/*如果是目录跳过*/
			if(files[i].isDirectory())
				continue;
			filename=files[i].getName();
			
			/*如果文件类型不是所要求的类型跳过*/
			if(!filename.substring(filename.indexOf(".")+1).equals(extName))
				continue;

			
			importSingleFile(files[i]);
			cutFile(files[i],filePath+"processed\\");
			
		

		}
		
	}
	
	public void importSingleFile(File fileName)throws Exception {
		SQLUpdater updater = DSManager.getSQLUpdater();	
		//File file=new File(fileName);
		BufferedReader fi = new BufferedReader(new FileReader(fileName));
		String line=null;
		String tablename=null;
		String fieldValue[]=null;
		boolean columnFlag=false;
		String sql="";
		
		
		while((line=fi.readLine())!=null){
			
			if(line.indexOf("[")!=-1 && line.indexOf("]")!=-1){
				
				tablename=line.substring(1,line.length()-1);
				
				columnFlag=true;
				
				//语句初始化
				sql="";

			}else{
				
				fieldValue=line.split("\\|");
				
				if(columnFlag){
					
					//组装字段
					sql="insert into "+tablename+"(";
					
					sql=makeSql(sql,fieldValue,false);
					//标识位复位
					columnFlag=false;
					
				}else{
					
					
					
					String sqlvalue=sql+" values(";
					
					sqlvalue=makeSql(sqlvalue,fieldValue,true);
					
					
					updater.executeUpdate(sqlvalue);
					
					
					System.out.println(sqlvalue);
					


				}
				
			}

		}
		fi.close();
		
	}
	
	public void cutFile(File file,String targetPath) throws Exception{
		
		String filename="";

		filename=targetPath+file.getName();
		
		File filenew=new File(filename);
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenew)));
			
			String lineStr="";
			
			StringBuffer sb=new StringBuffer();
			
			while((lineStr=br.readLine())!=null){
				
				sb.append(lineStr);
				sb.append("\r\n");
			}
			
			bw.write(sb.toString());
			
			br.close();
		
			file.delete();
		
			bw.close();
			
		}  catch (Exception e) {

			throw new Exception("文件剪切时出错!"+e.toString());
		}
	}
	
   public  String makeSql(String sql,String [] fieldValue,boolean flag){
		
		
		for(int i=0;i<fieldValue.length;i++){
			
			if(fieldValue[i].equals(""))
				fieldValue[i]=null;
			
			if(i==fieldValue.length-1){
				
				if(flag)
					if(fieldValue[i] == null)
						sql+="null"+"')";
					else
						sql+="'"+fieldValue[i]+"')";
				else
					sql+=fieldValue[i]+")";
				
			}else{
				
				if(flag)
					if(fieldValue[i] == null)
						sql+="null"+",";
					else
						sql+="'"+fieldValue[i]+"',";
				else
					sql+=fieldValue[i]+",";
				
			}
			
		}
		
		return sql;
	}
   
    public static void main(String args[]){
    	/*DataImport di = new DataImport();
    	String fileName="D:\\SocketServer\\sale18812009-4-30.pos";
    	try {
			di.importData(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
}
