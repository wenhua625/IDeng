package com.novarise.webase.example;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TimerTask;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
public class KccPush {

	
	
	/**
	 * ������Ϣ
	 * @param lxfs   �ֻ�����
	 * @param order  ���ݲ���
	 * @param type	���ݲ���
	 * @param title  ����
	 * @param content //����
	 * @return
	 * @throws IOException
	 */
	public String  pushPayment(String lxfs, String order,String type,String title,String content) {
		 String token ;
		 
		 String sql="select id,y.lxfs,isnull(model,'') model,phone_version,android_version,token,add_date,upd_date,device_token from   tj_sys_yh y  left join   tj_sys_type t on t.lxfs=y.LXFS where  y.lxfs= '@@lxfs,'";
	     
		 
			try {
				SQLQuery query = DSManager.getSQLQuery();
		    	sql = sql.replaceAll("@@lxfs,", lxfs);
				String[][] s_qxcs = query.ArrayQuery(sql);
				if(s_qxcs.length > 0){
					token = s_qxcs[0][5].trim();
					String model = (s_qxcs[0][2].trim()).toLowerCase();
					if(model.equals("huawei") || model.equals("honor")){					
					    
					   String msg = HuaWeiPushNcMsg.sendPushMessage(order,token,type,title,content);
					   return msg;
					} else	if(model.equals("xiaomi") || model.equals("redmi")  ){
						String msg =  XiaoMiPushtest.sendMessage(order, lxfs,type,title,content);
						return msg;
					}else if(model.equals("vivo")){
						VivoPush.refreshToken();
						try {
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String  msg = VivoPush.sendMessage(order, lxfs,type,title,content);
						return msg;		
					}else if(model.equals("oppo")){
						String msg = OppoPush.sendNcPushMessage(lxfs,order,type,title,content);
						return msg;				
					}else{
						String alias=s_qxcs[0][8].trim();
						String msg = JiGuangPush.buildPushObject_all_alias_alert(alias,order,type,title,content);
						return msg;
					}
				}else{
					System.out.println("û���ҵ�����"+lxfs+"��¼���޷���������");
					return "û�в鵽����"+lxfs+"��¼";
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		
	}
}
