package com.novarise.webase.servlet;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.BConstants;
import com.novarise.webase.framework.HtmlFunction;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.util.HttpClientUtil;
import com.novarise.webase.util.ImageCompress;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class FileLoadUpServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		uploadify(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req, resp);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void uploadify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		JSONArray ja = new JSONArray();
		
		String kj=request.getParameter("proname");
		SQLUpdater updater=null;
		try {
			updater = DSManager.getSQLUpdater();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("没有找到控件" + kj + "!");
			}

		} catch (Exception e) {
			throw new IOException("定位" + kj + "SQL出错!" + e.toString());
		}
        String cs=  y_sql[0][1];
		String path = y_sql[0][2];
		String ue_sql = y_sql[0][3];
		String filetype = y_sql[0][4];
		String Compress_w = y_sql[0][5];
		String Compress_h = y_sql[0][6];
		
		
		
		

		ServletFileUpload upload = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
		
		upload.setFileSizeMax(-1);

		// 相对路径
		String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		String filename="";
		
		if(cs == null) cs="";
		
		//插入图片前的工作
		if (cs.length() > 0) {
			if (cs.startsWith("delete") || cs.startsWith("update") || cs.startsWith("insert") || cs.startsWith("begin")){
				String sql_cs = HtmlFunction.parseVarAttr(cs, request, "");

				try {
					updater.executeUpdate(sql_cs);
					//System.out.println("sql=" + sql_cs);
				} catch (Exception ex) {
					System.out.println("sql=" + sql_cs);
					ex.printStackTrace();
				}
			}
			
		}
		
		String s_url= "http://" + request.getServerName();
		if(request.getServerPort() == 443){
			s_url = "https://" + request.getServerName() + request.getContextPath();
		}else if(request.getServerPort() == 80){
			s_url=s_url+ request.getContextPath();
		}else 
			s_url=s_url+ ":" + request.getServerPort() + request.getContextPath();
			
				
		//System.out.println("s_url:"+s_url);
		
		List items=null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		
		
		for (int i = 0; i < items.size(); i++) {
           //System.out.println(i+"--"+System.currentTimeMillis());
			FileItem fi = (FileItem) items.get(i);

			// 对于图片表单
			if (!fi.isFormField()) {
				File newFile = null;
				//System.out.print("文件名称："+fi.getName().toString());
				
				String[] arr = fi.getName().split("\\.");
				 String houz = arr[arr.length-1];
				// String houz = fi.getName().split("\\.")[1];
				String s_filecode = java.util.UUID.randomUUID().toString();
				
				s_filecode = s_filecode.replace("-", "");
				String s_filename=s_filecode+"."+houz;
				 filename = path + s_filename;

				if (fi.getName() != null && fi.getSize() != 0) {

					File fp = new File(url + path);
					// 创建目录
					if (!fp.exists()) {
						fp.mkdirs();// 目录不存在的情况下，创建目录。
					}
					newFile = new File(url + filename);

					if (!newFile.exists()) {
						newFile.createNewFile();
					}

					try {
						fi.write(newFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(kj.equals("UE0028")){
						Map params= new HashMap();
						HttpClientUtil hcu = new HttpClientUtil();
						
						
						params.put("username", "admin");
						params.put("password", "4297f44b13955235245b2497399d7a93");
						String login=hcu.doLoginPost("http://ad-kcc.dderp.cn:10080/login",params);
						
						//File file=new File("C:\\Users\\Administrator\\Desktop\\video\\3ccef36a08ee31fd178f633929dd66ca.mp4");
						
						JSONObject video_id=hcu.doUploadFilePost("http://ad-kcc.dderp.cn:10080/vod/upload", params,newFile );
						
						if(video_id==null){
							throw new IOException("无法上传到流媒体服务器！");
							
						}
						filename="http://ad-kcc.dderp.cn:10080"+video_id.getString("videoUrl");
						String fm="http://ad-kcc.dderp.cn:10080"+video_id.getString("snapUrl");
						//Video v=new Video();
						//com.alibaba.fastjson.JSONObject video=v.getVideoObject(video_id);
						//封面图
						//request.setAttribute("FileShortFullName", "http://ad-kcc.dderp.cn:10080"+video.get("snapUrl"));
						request.setAttribute("FileShortFullName", fm);
						request.setAttribute("video_id", video_id.getString("id"));
						
						JSONObject json = new JSONObject();
						json.put("name", filename);
						json.put("size", 321656);
						ja.add(json);
						
					}

				} else {
					filename = "";
				}
				request.setAttribute("FileCode", s_filecode);
				request.setAttribute("FileName", s_filename);
				request.setAttribute("ExeName", houz);
				if(!kj.equals("UE0028"))
				  request.setAttribute("FileFullName", s_url+"/"+filename);
				else
					request.setAttribute("FileFullName", filename);
				
			
				if(!houz.equals("mp4")){
				// 取像素
				BufferedImage bi = null;
				try {
					bi = ImageIO.read(newFile);
					int width = bi.getWidth(); // 像素
					int height = bi.getHeight(); // 像素
					request.setAttribute("Pix_W", String.valueOf(width));
					request.setAttribute("Pix_H", String.valueOf(height));
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				}

				if (filetype.equals("图像") && !(houz.equals("mp4"))) {
					
					

					if (Compress_w.length() > 0) {
						String smallPath = url + path + "small\\";

						File fp = new File(smallPath);

						if (!fp.exists()) {
							fp.mkdirs();
						}
						/*ImageCompress.imageCompress(newFile, smallPath,
								s_filename, 0.5f, 1.0f,
								Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));*/
						
						ImageCompress.reduceImg(newFile,smallPath+s_filename,Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));
						request.setAttribute("FileShortFullName", s_url+"/"+path +"small/"+ s_filename);
					}

				}else{
					//设置封面,采用video流媒体服务器，此处不需要
					/*String smallPath = url + path + "small\\";
					PicVideoUtils.handler("D:\\ffmpeg\\bin\\ffmpeg.exe", url+filename, smallPath+s_filecode+".png");
					request.setAttribute("FileShortFullName", s_url+"/"+path +"small/"+ s_filecode+".png");*/
				}
				
				
				if (ue_sql.length() > 0) {
					String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

					try {
						updater.executeUpdate(sql);
						//System.out.println("sql=" + sql);
					} catch (Exception ex) {
						System.out.println("sql=" + sql);
						ex.printStackTrace();
					}
				}
			} else // 对于字段表单
			{
				String name = fi.getFieldName();
				String value = fi.getString();
				request.setAttribute(name, value);
			}
			
			

		}
		
		
		
		
		JSONObject js = new JSONObject();
		js.put("files", ja);
		
		
		PrintWriter out = response.getWriter();
		out.print(js.toString());
		
	}
}