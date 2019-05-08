package com.novarise.webase.util;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.ghtPay.core.util.SHA256Util;

public class MD5Util {

	 /*** 
     * MD5加码 生成32位md5码 
     */  
    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
  
    }  
	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	
	
	
	private static String Key = "adSoftwareadtec4245adhaglkccyhh8"; 
	
	//������Կ
	//private static String ght_Key = "857e6g8y51b5k365f7v954s50u24h14w"; 
	//��ʽ��Կ
	private static String ght_Key = "07f310acc88959fe45485395cded12d8"; 
	
	
	
	
	
	 /**
		 * ΢��֧��ǩ���㷨sign
		 * @param characterEncoding
		 * @param parameters
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters){
			StringBuffer sb = new StringBuffer();
			Set es = parameters.entrySet();//���в��봫�εĲ�����accsii��������
			Iterator it = es.iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String k = (String)entry.getKey();
				Object v = entry.getValue();
				if(null != v && !"".equals(v) 
						&& !"sign".equals(k) && !"key".equals(k)) {
					sb.append(k + "=" + v + "&");
				}
			}
			sb.append("key=" + Key);
			String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
			return sign;
		}
		
		public static String wxSign(SortedMap parameters)
		{
			
			 String characterEncoding = "UTF-8";  
		        String weixinApiSign = "9A0A8659F005D6984697E2CA0A9CF3B7";  
		        System.out.println("΢�ŵ�ǩ���ǣ�" + weixinApiSign);  
		        String mySign = createSign(characterEncoding,parameters);  
		        System.out.println("��     ��ǩ���ǣ�"+mySign);  
		          
		        if(weixinApiSign.equals(mySign)){  
		            System.out.println("��ϲ��ɹ���~");  
		        }else{  
		            System.out.println("ע�������Ǹ�ʧ����~");  
		        }  
		          
		        String userAgent = "Mozilla/5.0(iphone;CPU iphone OS 5_1_1 like Mac OS X) AppleWebKit/534.46(KHTML,like Geocko) Mobile/9B206 MicroMessenger/5.0";  
		          
		        char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger")+15);  
		          
		        System.out.println("΢�ŵİ汾�ţ�"+new String(new char[]{agent}));
			
			return mySign;
		}
		
		
		 /**
		 * ΢��֧��ǩ���㷨sign
		 * @param characterEncoding
		 * @param parameters
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public static String createSHA256Sign(String characterEncoding,SortedMap<Object,Object> parameters){
			StringBuffer sb = new StringBuffer();
			Set es = parameters.entrySet();//���в��봫�εĲ�����accsii��������
			Iterator it = es.iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String k = (String)entry.getKey();
				Object v = entry.getValue();
				if(null != v && !"".equals(v) 
						&& !"sign".equals(k) && !"key".equals(k)) {
					sb.append(k + "=" + v + "&");
				}
			}
			sb.append("key=" + ght_Key);
			//System.out.println("��ǩ����ַ�"+sb.toString());
			String sign =  SHA256Util.SHA256Encode(sb.toString(), characterEncoding); //MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
			return sign;
		}
	
}
