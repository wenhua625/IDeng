package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.novarise.webase.BConstants;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.framework.WebControl;
import com.novarise.webase.util.BuildHTML;
import com.novarise.webase.xml.XmlUtil;

public class Work extends HttpServlet {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html; charset=GBK";
	/**
	 * Constructor of the object.
	 */
	public Work() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}



	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType(CONTENT_TYPE);
		
		response.setHeader("Cache-Control", "no-cache");
		
		PrintWriter out = response.getWriter();
		WebControl control = new WebControl();
		String loginFlag = (String)(request.getSession().getAttribute("LS.YHDM"));
		
		String htmlfile=" ";
		String proname = request.getParameter("proname");
		if (proname == null || proname.trim().length() == 0) {
			String error = SystemFunction.showError(101, "����!û��Ҫ������¼�", "����!û��Ҫ������¼�",request);
			out.println(error);
			return;
		}
		
		if(proname.equals("QUIT")){
			try{
				control.parseDisplayQUIT( request, response);
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ�˳�ʱ����", "ϵͳ�˳�ʱ����",request);
				out.println(error);
				return;
			}
			
		}
		
		if(proname.equals("LOGON")){
			//System.out.println("Login..");
			try{
				htmlfile = control.parseDisplayQX(request, response);
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ��½ʱ����", "ϵͳ��½ʱ����["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		 //�ϴ�ͼƬ
        if(proname.substring(0, 2).equals("UE")){
        	try{
        		
				htmlfile=control.parseUpLoadFile(htmlfile,proname,request, response);
				
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        		e.printStackTrace();
        	} 
        	out.print(htmlfile);
    		out.flush();
    		out.close();
    		return;
        }
		
		if(proname.substring(0,4).equals("TREE")){
			try{
				if(loginFlag == null){
				    return ;
				}
				response.setContentType("text/xml;charset=GB2312");
				
				htmlfile = control.parseDisplayTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		if(proname.substring(0,4).equals("JREE")){
			try{
				if(loginFlag == null){
				    return ;
				}
				htmlfile = control.parseDisplayJSONTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		
//		Extjs TRee
		if(proname.substring(0,4).equals("EREE")){
			try{
				///response.setContentType("text/xml;charset=GB2312");
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayTREE(proname, request, response));
				out.flush();
				out.close();
				return;
				//htmlfile = control.parseDisplayTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		
		
		
		if(proname.substring(0,2).equals("IN")){
			try{
				
				/*if(loginFlag == null){
				    return ;
				}*/
				htmlfile = WebControl.parseModifyIN(proname, request, response);
				//�����Ŀ�꣬��ֱ��forward��Ŀ��ҳ
				String targetUrl = request.getParameter("target");
				if(targetUrl == null) targetUrl ="";
				if(targetUrl.length() !=0){
					request.getRequestDispatcher(targetUrl).forward(request, response);
					return;
				}
				
			}catch(Exception e){
				htmlfile = "����["+proname+"]ʱ���� "+e.toString();
				
			}
			
		}
		
		if(proname.substring(0,2).equals("FW")){
			try{
				control.parseForward(proname, request, response);
			}catch(Exception e){
				htmlfile = "����["+proname+"]ʱ���� "+e.toString();
				
			}
			
		}
        
		
		
		//�����ִ�г���
        if(proname.substring(0,3).equals("EXE")){
            try{
                String path = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\PT850\\Pt_ud.exe";
                Runtime.getRuntime().exec(path);
                
                htmlfile = "���ڴ����ϴ�����";
            }catch(Exception e){
                htmlfile = "����[EXE]ʱ���� "+e.toString();
                
            }
            
        }
        //��ͨPOS��Lincese
        if(proname.substring(0,3).equals("POS")){
            try{
            	WebControl.parseLicense(proname);
            	htmlfile ="POS�ն��ѿ�ͨ";
            }catch(Exception e){
                htmlfile = "����[POS]ʱ���� "+e.toString();
                
            }
            
        }
        //��������JSON����
        if(proname.substring(0,2).equals("XL")){
			try{
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayXLJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        //��������JSON����
        if(proname.substring(0,2).equals("AL")){
			try{
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayXLJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //�����б�JSON����
        if(proname.substring(0,2).equals("MJ")){
			try{
				/**if(loginFlag == null){
				    return ;
				}**/
				out.println(control.parseDisplayMJJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //����ExelJSON����
        if(proname.substring(0,2).equals("EJ")){
			try{
				if(loginFlag == null){
				    return ;
				}
				
				out.println(control.parseExcelJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //����΢��֧��
        if(proname.substring(0,2).equals("WX")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayWXPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
//      �ϴ��ļ�
        if(proname.substring(0, 2).equals("UE")){
        	try{
				htmlfile=control.parseUpLoadFile(htmlfile,proname,request, response);
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
//      ����ͼƬ�ļ�
        if(proname.substring(0, 2).equals("DE")){
        	try{
				htmlfile=control.parseDownloadFile(htmlfile,proname,request, response);
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
       
        
		out.print(htmlfile);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
