package com.novarise.webase.xml;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

/*
 * Created on 2004-8-30
 *
 * 数据转换
 */


import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author YinHuiHua
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataFormator {

        /**
        * 根据传入的日期返回太阳日
        *
        * @param cd 日期 YYYYMMDD YYYY-MM-DD
        *
        * @return int Julian Day
        */
        public static int getJulianDay(String cd) {
                if (cd == null ) throw new NullPointerException("the import date is null") ;
                String date = cd.trim().replaceAll("-","").replaceAll(" ","");
                Calendar c = Calendar.getInstance();
                c.set(
                        (Integer.valueOf(date.substring(0, 4))).intValue(),
                        (Integer.valueOf(date.substring(4, 6))).intValue()-1,
                        (Integer.valueOf(date.substring(6, 8))).intValue());
                return c.get(6);
        }

        /**
        * 获得由指定长度的某个字符串组成的字符串
        *
        * @param str 源字符串
        * @param len 长度
        *
        * @return String
        */
        public static String getString(String str, int len) {
                if (str == null)
                        return null;
                String s = str;
                while (s.length() < len) {
                        s = str + s;
                }
                return s.substring(0,len);
        }

        /**
        * 将金额转化成为一个字符串，并在数字前边添加定长的指定的字符
        *
        * @param amount 金额
        * @param len 长度
        * @param digit 保留的小数的位数
        * @param fillString 填充字符串
        * @param point 是否需要小数点
        *
        * @return String
        */
        public static String getAmount(double amount, int len, int digit, String fillString ,boolean point){
                String am = String.valueOf(Math.round(amount * (Math.pow(10,digit))));
                if (am.equals("0")){
                        if(point) return  stringAddBOF("0."+getString("0", digit), len, fillString);
                        else return stringAddBOF("0"+getString("0", digit), len, fillString);
                }
                if (am.length()<=digit) am = stringAddBOF(am, digit+1, fillString);
                if (point) am = am.substring(0,am.length()-digit)+"."+am.substring(am.length()-digit,am.length());
                String result = getString(fillString,len-(am.length())) + am ;
                return result;
        }

        /**
        * 在一个字符串首加上一定数量的字符串，并从返回字符串尾部起的n个字符
        * 若源字符串长度大于返回长度，则返回原字符串尾部起的n个字符
        *
        * @param str 源字符串
        * @param len 长度
        * @param fillString 填充的字符穿
        *
        * @return String 返回的字符串
        */
        public static String stringAddBOF(String str, int len, String fillString) {
                if (str == null) return str;
                if (str.length()>len) return str.substring(str.length()-len, str.length());
                String s = str;
                while (s.length()< len) {
                        s =  fillString + s;
                }
                return s.substring(s.length()-len, s.length());
        }

        /**
        * 在一个字符串首加上一定数量的字符串，并从返回字符串尾部起的n个字符
        * 若源字符串长度大于返回长度，则返回原字符串尾部起的n个字符
        *
        * @param str 源字符串
        * @param len 长度
        * @param fillString 填充的字符穿
        *
        * @return String 返回的字符串
        */
        public static String stringAddEOF(String str, int len, String fillString) {
                if (str == null) return str;
                if (str.length()>len) return str.substring(0, len);
                String s = str;
                while (s.length()< len) {
                        s = s + fillString;
                }
                return s.substring(0, len);
        }

        /**
        * 在某个字符串的首或尾加入定长的字符
        *
        * @param str 源字符串
        * @param len 长度
        *
        * @return String
        */
        public static String getDates(String str, int len) {
                if (str == null)
                        return null;
                String s = str;
                while (s.length() < len) {
                        s = str + s;
                }
                return s.substring(0,len);
        }

        /**
        * 将数字精确到某位 四舍五入
        *
        * @param str 源字符串
        * @param len 长度
        *
        * @return String
        */
        public static double number(double num, int dig) {
                BigDecimal b = new BigDecimal(num);
                return b.setScale(dig, BigDecimal.ROUND_HALF_UP).doubleValue();
        }


        public static  String parseNormalString(String numstr) {
          if(numstr.length() ==1)
           {
               numstr="0"+numstr;
          }
            return numstr;
         }

         public static String parseNotNullString(String str)
         {
             if(str==null||str.length() ==0)
             {
               return " ";
             }else
             {
               return str;
             }
         }

//date to String eg:2004-06-16---->20040616

     public static String dateToString(String date,String market)
     {
        StringBuffer r_date=new StringBuffer();
       if (date.length()==0 ) return "";

       int i=date.indexOf(market);


      while(i!=-1)
      {
         r_date.append(parseNormalString(date.substring(0,i))) ;
         date=date.substring(i+1);
         i=date.indexOf(market);
      }
      r_date.append(parseNormalString(date.substring(i+1))) ;
      return r_date.toString() ;
  }
}

