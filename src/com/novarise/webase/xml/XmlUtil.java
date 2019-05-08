package com.novarise.webase.xml;

//文件包 
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class XmlUtil {
	
		/** 
	    * doc2String 
	    * 将xml文档内容转为String 
	    * @return 字符串 
	    * @param document 
	    */ 
	   public static String doc2String(Document document) 
	   { 
	      String s = ""; 
	      try 
	      { 
	           //使用输出流来进行转化 
	           ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	           //使用GB2312编码 
	           OutputFormat format = new OutputFormat("  ", true, "GB2312"); 
	           XMLWriter writer = new XMLWriter(out, format); 
	           writer.write(document); 
	           s = out.toString("GB2312"); 
	      }catch(Exception ex) 
	      {             
	           ex.printStackTrace(); 
	      }       
	      return s; 
	   }
	   
	   /** 
	    * string2Document 
	    * 将字符串转为Document 
	    * @return  
	    * @param s xml格式的字符串 
	    */ 
	   public static Document string2Document(String s) 
	   { 
	      Document doc = null; 
	      try 
	      { 
	           doc = DocumentHelper.parseText(s); 
	      }catch(Exception ex) 
	      {             
	           ex.printStackTrace(); 
	      } 
	      return doc; 
	   }
	   
	   /** 
	    * doc2XmlFile 
	    * 将Document对象保存为一个xml文件到本地 
	    * @return true:保存成功  flase:失败 
	    * @param filename 保存的文件名 
	    * @param document 需要保存的document对象 
	    */ 
	   public static boolean doc2XmlFile(Document document,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	            /* 将document中的内容写入文件中 */ 
	            //默认为UTF-8格式，指定为"GB2312" 
	            OutputFormat format = OutputFormat.createPrettyPrint(); 
	            format.setEncoding("GB2312"); 
	            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format); 
	            writer.write(document); 
	            writer.close();             
	        }catch(Exception ex) 
	        { 
	            flag = false; 
	            ex.printStackTrace(); 
	        } 
	        return flag;       
	   }
       
       
       /**
         * 数据生成txtFile
         * 
         */
        public static boolean data2TxtFile( String sql,String path){

            boolean flag = true;
            File file=new File(path);
            PrintWriter out1 ;
            SQLQuery query;
            String data[][];
            StringBuffer sb = new  StringBuffer();
            try {
                out1 =new PrintWriter(new BufferedWriter(new FileWriter(file)));
                query = DSManager.getSQLQuery();
                data = query.ArrayQuery(sql);
                for(int i=0;i<data.length;i++){
                    sb.append(DataFormator.stringAddEOF(data[i][0],30," "));
                    sb.append(DataFormator.stringAddEOF(data[i][1],20," "));
                    sb.append("\n");
                    
                }
                out1.print(sb.toString());
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
                
            }
            
            
            return flag;

        }
	   
	   /** 
	    * string2XmlFile 
	    * 将xml格式的字符串保存为本地文件，如果字符串格式不符合xml规则，则返回失败 
	    * @return true:保存成功  flase:失败 
	    * @param filename 保存的文件名 
	    * @param str 需要保存的字符串 
	    */ 
	   public static boolean string2XmlFile(String str,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	         Document doc =  DocumentHelper.parseText(str);        
	         flag = doc2XmlFile(doc,filename); 
	      }catch (Exception ex) 
	      { 
	         flag = false; 
	         ex.printStackTrace(); 
	      } 
	      return flag; 
	   }
	   
	   /** 
	    * load 
	    * 载入一个xml文档 
	    * @return 成功返回Document对象，失败返回null 
	    * @param uri 文件路径 
	    */ 
	   public static Document load(String filename) throws Exception
	   { 
		  ClassLoader cl = XmlUtil.class.getClassLoader();
		  InputStreamReader fs = new InputStreamReader(cl
					.getResourceAsStream(filename));
		  Document document = null; 
	      SAXReader saxReader = new SAXReader(); 
	      document = saxReader.read(fs); 
	      return document; 
	   }
	   
	   /** 
	    * load 
	    * 载入一个xml文档 
	    * @return 成功返回Document对象，失败返回null 
	    * @param uri 文件路径 
	    */ 
	   public static Document load(InputStream is) 
	   { 
	      Document document = null; 
	      try  
	      {  
	          SAXReader saxReader = new SAXReader(); 
	          document = saxReader.read(is); 
	          
	      } 
	      catch (Exception ex){ 
	          ex.printStackTrace(); 
	      }   
	      return document; 
	   }
	   
	   /** 
	    * xmlWriteDemoByString 
	    * 演示String保存为xml文件 
	    */ 
	   public void xmlWriteDemoByString() 
	   { 
	      String s = ""; 
	      /** xml格式标题 "<?xml version='1.0' encoding='GB2312'?>" 可以不用写*/ 
	      s = "<config>\r\n" 
	         +"   <ftp name='DongDian'>\r\n" 
	         +"     <ftp-host>127.0.0.1</ftp-host>\r\n" 
	         +"     <ftp-port>21</ftp-port>\r\n" 
	         +"     <ftp-user>cxl</ftp-user>\r\n" 
	         +"     <ftp-pwd>longshine</ftp-pwd>\r\n" 
	         +"     <!-- ftp最多尝试连接次数 -->\r\n" 
	         +"     <ftp-try>50</ftp-try>\r\n" 
	         +"     <!-- ftp尝试连接延迟时间 -->\r\n" 
	         +"     <ftp-delay>10</ftp-delay>\r\n" 
	         +"  </ftp>\r\n" 
	         +"</config>\r\n"; 
	      //将文件生成到classes文件夹所在的目录里    
	      string2XmlFile(s,"xmlWriteDemoByString.xml");    
	      //将文件生成到classes文件夹里    
	      string2XmlFile(s,"classes/xmlWriteDemoByString.xml");   
	   }
	   
	   /** 
	    * 演示手动创建一个Document，并保存为XML文件 
	    */ 
	   public void xmlWriteDemoByDocument() 
	   { 
	        /** 建立document对象 */ 
	        Document document = DocumentHelper.createDocument(); 
	        /** 建立config根节点 */ 
	        Element configElement = document.addElement("config"); 
	        /** 建立ftp节点 */ 
	        configElement.addComment("东电ftp配置"); 
	        Element ftpElement = configElement.addElement("ftp"); 
	        ftpElement.addAttribute("name","DongDian"); 
	        /** ftp 属性配置 */ 
	        Element hostElement = ftpElement.addElement("ftp-host"); 
	        hostElement.setText("127.0.0.1"); 
	        (ftpElement.addElement("ftp-port")).setText("21"); 
	        (ftpElement.addElement("ftp-user")).setText("cxl"); 
	        (ftpElement.addElement("ftp-pwd")).setText("longshine"); 
	        ftpElement.addComment("ftp最多尝试连接次数"); 
	        (ftpElement.addElement("ftp-try")).setText("50"); 
	        ftpElement.addComment("ftp尝试连接延迟时间"); 
	        (ftpElement.addElement("ftp-delay")).setText("10");     
	        /** 保存Document */ 
	        doc2XmlFile(document,"classes/xmlWriteDemoByDocument.xml"); 
	   }
	   
	   public static String[][] xmlFile2Array(String xmlFile) throws Exception{
			  List ls=new ArrayList();
			  Document doc=load(xmlFile);
			  List rowList = doc.selectNodes("/data/row");
			  
			  Iterator rowIt = rowList.iterator();
			  while (rowIt.hasNext()){
				  Element row = (Element)rowIt.next();
				  
					  Iterator colIt = row.elementIterator();
					  String [] rows = new String[row.elements().size()];
					  int i=0;
					  while(colIt.hasNext()){
						  Element col = (Element)colIt.next();
						  rows[i] = col.getText();
						  i++;
					  }
					  ls.add(rows);
				  
			  }
			  String r_data[][] =new String[ls.size()][];
			  Object s[]=ls.toArray();
			  for(int i=0;i<s.length;i++){
				  r_data[i] = (String[])s[i];
			  }
			  
			  return r_data; 
		  }
	   
	  public static String[][] find(String xmlFile,String key,String value) throws Exception{
		  List ls=new ArrayList();
		  Document doc=load(xmlFile);
		  List rowList = doc.selectNodes("/data/row");
		  
		  Iterator rowIt = rowList.iterator();
		  while (rowIt.hasNext()){
			  Element row = (Element)rowIt.next();
			  if(row.element(key).getText().equals(value))
			  {
				  Iterator colIt = row.elementIterator();
				  String [] rows = new String[row.elements().size()];
				  int i=0;
				  while(colIt.hasNext()){
					  Element col = (Element)colIt.next();
					  rows[i] = col.getText();
					  i++;
				  }
				  ls.add(rows);
			  }
		  }
		  String r_data[][] =new String[ls.size()][];
		  Object s[]=ls.toArray();
		  for(int i=0;i<s.length;i++){
			  r_data[i] = (String[])s[i];
		  }
		  
		  return r_data; 
	  }
	  
	  
	  /** 
	   * 将xmlDocument转换为JSON对象 
	   * @param xmlDocument XML Document 
	   * @return JSON对象 
	   */  
	  public static String getJSONTreeXml(String xmlFile) throws Exception {
		  /*Document xmlDocument=load(xmlFile);
	      String xmlString = xmlDocument.toString(); 
	      XMLSerializer xmlSerializer = new XMLSerializer();  
	      JSON json = xmlSerializer.read(xmlString);*/
		  StringBuffer sb = new StringBuffer();
		  sb.append("[");
		  String[][] nodeArr = new String[0][0];
		  nodeArr=xmlFile2Array(xmlFile);
		  
	      return sb.toString();  
	  }  
	  
	  public static String readXml(String xmlFile,String xPath) throws Exception{
		  String text="";
		  try{
		  Document doc = load(xmlFile);
		  Node node=doc.selectSingleNode(xPath);
		  text=node.getText();
		  }catch(Exception e){
			 throw new Exception ("读取["+xmlFile+"]时出错！"+e.toString()); 
		  }
		  return text;
		  
	  }
	   
	   /** 
	    *  演示读取文件的具体某个节点的值  
	    */ 
	   public static void xmlReadDemo() throws Exception
	   { 
		   String [][] data=find("mj4tjConfig.xml","KJM","MJ0038");
		   for(int i=0;i<data.length;i++){
			   for(int j=0;j<data[i].length;j++){
				   System.out.println(data[i][j]);  
			   }
		   }
		   
	      
	   }
	   
	   public static void main(String args[])
	   {
		   
	   }
	  
	   
	   
}
