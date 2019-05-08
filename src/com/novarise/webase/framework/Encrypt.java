package com.novarise.webase.framework;

import java.security.MessageDigest;

class Encrypt{

   public final static String MD5(String s){
     char hexDigits[] = {
         '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
         'e', 'f'};
     try {
       byte[] strTemp = s.getBytes();
       MessageDigest mdTemp = MessageDigest.getInstance("MD5");
       mdTemp.update(strTemp);
       byte[] md = mdTemp.digest();
       int j = md.length;
       char str[] = new char[j * 2];
       int k = 0;
       for (int i = 0; i < j; i++) {
         byte byte0 = md[i];
         str[k++] = hexDigits[byte0 >>> 4 & 0xf];
         str[k++] = hexDigits[byte0 & 0xf];
         }
         return new String(str);
       }
       catch (Exception e){
         return null;
       }
}
 public static void main(String[] args){
    String ss="0,3,5";
    String stsArr[]=ss.split(",");
    String stsStr="";
    for(int i=0;i<stsArr.length;i++)
  	 {
  	 	stsStr+="'"+stsArr[i]+"',";
  	 }
     stsStr = stsStr.substring(0,stsStr.length()-1);
 	//System.out.print(Encrypt.MD5("25ed1bcb423b0b7200f485fc5ff71c8e"));
    System.out.println(stsStr);
 }
}