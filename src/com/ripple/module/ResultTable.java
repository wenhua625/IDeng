/**
 * <p>Title: ResultTable.java</p>
 * <p>Description: 模拟ResultSet的类，为分页查询所用</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <P>All Rights Reserved.</p>
 * <p>Company: Advanced Digital Technology Co.Ltd.</p>
 * @author 陈立宇
 * @version 2.0
 */
package com.ripple.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



/**
 *
 * 2005-04-18:添加分页参数设置（陈立宇）
 * <p>封装了分页查询的结果集操作，提供类似ResultSet的操作接口</p>
 * @author
 * @version 1.1  2006/08/17 
 * @see DefaultSqlOperator
 */
public class ResultTable {

  private ArrayList resultArray = null;
  private ArrayList fldArray = null;
  private int nRow = 0;
  private int nCol = 0;
  private int nCurtRow = 0;
  private int nMaxRow = 0;

  private int iRecordCount = 0; // 记录数
  private int iPageCount = 0; //分页数

  private int iPageth = 1; //当前页数
  private int iPageSize = 20; //每页最大记录数

  private HashMap map = new HashMap(); //条件map

  private String actionUrl = null;

  private String style = "class='w01_11px_grey'";

  private String pagethParamName = "pageth";

  public ResultTable() {
  }

  /**
   * 陈立宇
   * @param iPageth int
   * @param iPageSize int
   * @param url String
   */
  public ResultTable(int iPageth, int iPageSize, String url) {
    this.setPageth(iPageth);
    this.setPageSize(iPageSize);
    this.setUrl(url);
  }

  /**
   * 陈立宇
   * @param iPageth int
   */
  public void setPageth(int iPageth) {
    this.iPageth = iPageth;
  }

  /**
   * 陈立宇
   * @param iPageSize int
   */
  public void setPageSize(int iPageSize) {
    this.iPageSize = iPageSize;
  }

  /**
   * 陈立宇
   * @param url String
   */
  public void setUrl(String url) {
    this.actionUrl = url;
  }

  /**
   * 陈立宇
   * @return int
   */
  public int getPageth() {
    return this.iPageth;
  }

  /**
   * 陈立宇
   * @return int
   */
  public int getPageSize() {
    return this.iPageSize;
  }

  /**
   * 陈立宇
   * @return String
   */
  public String getUrl() {
    return this.actionUrl;
  }

  /**
   * 陈立宇
   * 条件参数
   * @param name String
   * @param value String
   */
  public void putParameter(String name, String value) {
    map.put(name, value);
  }

  /**
   *
   * @return int
   * 陈立宇
   */
  public int getNextPage() {
    return this.iPageth + 1;
  }

  /**
   *
   * @return int
   * 陈立宇
   */
  public int getPreviousPage() {
    return this.iPageth - 1;
  }

  /**
   *
   * @return int
   * 陈立宇
   */
  public int getFirstPage() {
    return 1;
  }

  /**
   *
   * @return int
   * 陈立宇
   */
  public int getLastPage() {
    return this.iPageCount;
  }

  public void setPagethParamName(String pagethParamName) {
    this.pagethParamName = pagethParamName;
  }

  public String getPagethParamName() {
    return this.pagethParamName;
  }

  /**
   * 当前页的记录数
   * @return int
   */
  public int size() {
    return this.nRow;
  }

  /**
   *
   *
   <script>
    function turnPage(forward) {
        if(forward == "1"){
                form1.Pageth.value="<%=iPageth+1%>";
                form1.submit();
        }
        if(forward == "0"){
                form1.Pageth.value="<%=iPageth-1%>";
                form1.submit();
        }
   }

    function toPage(forward) {
                form1.Pageth.value= forward;
                form1.submit();

   }

   </script>

   */
  /**
   *
   * @return String
   * 陈立宇
   */
  public String getTurnPageScript() {
    StringBuffer link = new StringBuffer();

    if (iPageCount > 1) {

      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>页");

      if (iPageth > 1) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:toPage('1')\"><font color='blue'>首页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:turnPage('0')\"><font color='blue'>上页</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:turnPage('1')\"><font color='blue'>下页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:toPage('" +
                    iPageCount + "')\"><font color='blue'>尾页</font></a>");
      }
    }
    else {

      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>1/1</font>页");

    }

    return link.toString();
  }

  /**
   *
   * @return String
   * 陈立宇
   */
  public String getTurnPageString() {
    StringBuffer link = new StringBuffer();
    String condStr = "";

    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>页");

      if (iPageth > 1) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>首页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getPreviousPage() + condStr +
                    "\"><font color='blue'>上页</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>下页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>尾页</font></a>");
      }
    }
    else {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>1/1</font>页");

    }

    return link.toString();

  }

  /**翻页器*/
  public String getTurnPager() {

    StringBuffer link = new StringBuffer();
    String condStr = "";
    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>页");

      if (iPageth > 1) {

        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>首页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getPreviousPage() +
                    condStr +
                    "\"><font color='blue'>上页</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>下页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>尾页</font></a>");
      }
    }
    else {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>1/1</font>页");

    }

    return link.toString();

  }

  public String getTurnPagerWithInput() {

    StringBuffer link = new StringBuffer();
    String condStr = "";
    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>页");

      if (iPageth > 1) {

        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>首页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getPreviousPage() +
                    condStr +
                    "\"><font color='blue'>上页</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>下页</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>尾页</font></a>");
      }
    }
    else {
      link.append("共<font color='red'>" + this.getCount() +
                  "</font>条记录,第<font color='red'>1/1</font>页");

    }

    link.append("&nbsp;&nbsp;");
    link.append("<input type=\"text\"   name='"+this.pagethParamName+"' ><input type='button' value='GO' name='go'>");
    link.append("&nbsp;&nbsp;");

    return link.toString();
  }

  /**
   * create result table
   * @param nCol
   */
  public ResultTable(int nCol) {

    this.nCol = nCol;
    resultArray = new ArrayList();
    fldArray = new ArrayList(nCol);
    for (int i = 0; i < nCol; i++) {
      fldArray.add(i, null);
    }
    nCurtRow = -1;
    nMaxRow = 0;

  }

  /**
   *  在需要取结果集时，首先将游标位置调整到结果集首
   */
  public void first() {
    nCurtRow = -1;
  }

  /**
   *  get Cursor Position
   * @return 返回当前游标的位置
   */
  public int getPosition() {
    return nCurtRow;
  }

  /**
   *  insert new row to Result table.
   */
  public void insertRow() {
    nCurtRow = nMaxRow;
    ArrayList rowArray = new ArrayList(nCol);
    for (int i = 0; i < nCol; i++) {
      rowArray.add(i, null);
    }
    resultArray.add(nCurtRow, rowArray);
    nMaxRow++;
  }

  /**
   * 判断当前游标的位置是否是最后端
   * @return true 不是，false 是
   */
  public boolean isEof() {
    if (nCurtRow < nMaxRow) {
      return true;
    }
    return false;
  }

  /**
   * 游标位置下移一位
   * @return 是否成功
   */
  public boolean next() {
    if (nCurtRow < (nMaxRow - 1) && nMaxRow != 0) {
      nCurtRow++;
      return true;
    }

    return false;
  }

  /**
   * 游标位置前移一位
   * @return 是否成功
   */
  public boolean before() {
    if (nCurtRow >= 0) {
      nCurtRow--;
      return true;
    }
    return false;
  }

  /**
   * 定位游标的绝对位置
   * @param 位置偏移量
   * @return 是否成功
   */
  public boolean absolute(int offset) {
    if (offset >= 0 && offset < nMaxRow) {
      nCurtRow = offset;
      return true;
    }

    return false;
  }

  /**
   * get result string by position
   * @param colnum
   * @return
   */
  public String getString(int colnum) {
    String str = null;
    if (colnum >= 0 && colnum < nCol) {
      if (nCurtRow == -1) {
        nCurtRow = 0;
      }
      ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
      str = (String) rowArray.get(colnum);
    }
    return str;
  }

  /**
   * get result string by field name
   * @param fldName
   * @return
   */
  public String getString(String fldName) {
    String str = null;
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    for (int i = 0; i < this.nCol; i++) {
      if (fldName.compareToIgnoreCase( (String) fldArray.get(i)) == 0) {
        ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
        str = (String) rowArray.get(i);
        break;
      }
    }

    return str;
  }

  /**
   * set field name
   * @param nCol
   * @param fldName
   * @return
   */
  public boolean setField(int nCol, String fldName) {
    if (nCol >= 0 && nCol < this.nCol) {
      fldArray.set(nCol, fldName);
      return true;
    }

    return false;
  }

  /**
   * set result value by position
   * @param iCol
   * @param val
   * @return
   */
  public boolean setValue(int nCol, String val) {
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    if (nCol >= 0 && nCol < this.nCol) {
      ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
      rowArray.set(nCol, val);
      return true;
    }
    return false;
  }

  /**
   * set result value by field name
   * @param fldName
   * @param val
   * @return
   */
  public boolean setValue(String fldName, String val) {
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    for (int i = 0; i < nCol; i++) {
      // 查询匹配的字段位置
      if (fldName.compareToIgnoreCase( (String) fldArray.get(i)) == 0) {
        ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
        if (i >= resultArray.size()) {

        }
        rowArray.set(i, val);
        return true;
      }
    }

    return false;
  }

  /**
   * 设置当前结果集的记录数
   * @param nCount
   */
  public void setCount(int nCount) {
    this.iRecordCount = nCount;
  }

  /**
   * 获得当前结果集的记录数
   * @return
   */
  public int getCount() {
    return this.iRecordCount;
  }

  /**
   * 设置当前分页的数目
   * @param nPageNum
   */
  public void setPageNum(int nPageNum) {
    this.iPageCount = nPageNum;
  }

  /**
   * 获得当前分页的数目
   * @return
   */
  public int getPageNum() {
    return this.iPageCount;
  }

  /**
   * 设置当前页的记录数——Fancy Added 2004－03－13
   * @param row 当前记录数
   */
  public void setRow(int row) {
    this.nRow = row;
  }

  /**
   * 返回当前页的记录数——Fancy Added 2004－03－13
   * @return 当前记录数
   */
  public int getRow() {
    return this.nRow;
  }
  
  public int getColCount() {
	  return this.nCol;
  }

}
