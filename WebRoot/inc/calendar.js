// JavaScript Document
<!--Start-->
var YearStr,MonthStr,DayStr,NowDayStr;
var WeekStr,week,week_one;
var WeekArr = new Array("星期日", "星期一", "星期二", "星期三","星期四", "星期五", "星期六");
var dt = new Date();
var showtype;
var caleX = 0;
var caleY = 0;

YearStr  = dt.getYear();
MonthStr = dt.getMonth() + 1;
NowDayStr = DayStr   = dt.getDate();
week     = dt.getDay();
WeekStr  = WeekArr[week];
document.write("<DIV id=idCalendar style='position:absolute;top:100px;left:100px;width:260px;border: #000000 1px solid;background-color: #ffffff;display:none'></DIV>");

function initCalendar(){
 var CaleHTML="";
 CaleHTML += "<TABLE border=0 cellspacing=0 cellpadding=1 width='100%'>";
 CaleHTML += "<TR bgcolor=#33246C class='WHS'><TD><BUTTON onclick='prevMonth()' class=btn><span lang=EN-US style='font-family:Arial'>&#9668;</span></BUTTON></TD>";
 CaleHTML += "<TD align=center><SPAN style='color:white'>"+ YearStr +"年"+ MonthStr +"月</SPAN></TD>"
 CaleHTML += "<TD align=right><BUTTON onclick='nextMonth()' class=btn><span lang=EN-US style='font-family:Arial'>&#9658;</span></BUTTON></TD></TR>";
 CaleHTML += "<TR><TD colspan=3>";
 CaleHTML += "<TABLE border=0 cellspacing=0 cellpadding=0 width='100%'><TR  class='WHS'>";
 for(var i=0; i<WeekArr.length; i++){
  CaleHTML += "<TD>" + WeekArr[i] + "</TD>";
 }
 CaleHTML += "</TR><TR  class='WHS'><TD colspan=7><HR size=1 color=blank width='90%'></TD></TR>";
 CaleHTML += bodyCalendar();
 CaleHTML += "</TABLE>";
 CaleHTML += "</TD></TR>";
 CaleHTML += "<TR class='WHS'><TD colspan=3>今天是：<Font color=red>"+ dt.getYear() +"-"+ (dt.getMonth() + 1) +"-"+ dt.getDate() +"</font></TD></TR>";
 CaleHTML += "</TABLE>";
 return CaleHTML;
}

function bodyCalendar(){
 var ResultStr = "<TR class='WHS'>";
 var tempStr = "" + MonthStr + "/1/" + YearStr;
 var d = new Date(tempStr);
 week_one = d.getDay();
 for(var i=0;i<week_one;i++){
  ResultStr += "<TD></TD>";
 }
 var bYear = YearStr/4;
 var maxDay;
 switch(MonthStr){
  case 1:
  case 3:
  case 5:
  case 7:
  case 8:
  case 10:
  case 12:
   maxDay = 31;
   break;
  case 2:
   if(bYear==0)
    maxDay = 28;
   else
    maxDay = 29;
   break;
  default:
   maxDay = 30;
   break;
 }
 var k;
 var bday;
 for(var i=0;i<maxDay;i++){

  if((i+1) == DayStr)
   bday = "border: #006432 1px solid;";
  //file://else if((i+1) == NowDayStr)
   //file://bday = "border: #ff0000 1px solid;";
  else
   bday = "border: #ffffff 1px solid;";

  BRStr = i+week_one+1;
  if(BRStr>=7 && BRStr%7==0){

   ResultStr += "<TD align=right style='cursor:hand;"+ bday +"' onmouseover='TDMove()' onmouseout='TDOut()' onclick='TDClick()'>"+ eval(i+1) +"</TD></TR><TR class='WHS'>";
   k=0;
  }
  else{
   ResultStr += "<TD align=right style='cursor:hand;"+ bday +"' onmouseover='TDMove()' onmouseout='TDOut()' onclick='TDClick()'>"+ eval(i+1) +"</TD>";
   k+=1;
  }
 }
 for(var i=0;i<7-k;i++){
  ResultStr += "<TD></TD>";
 }
 ResultStr += "</TR>";
 return ResultStr;
}

function prevMonth(){
 MonthStr -= 1;
 if(MonthStr == 0){
  MonthStr = 12;
  YearStr -= 1;
 }

 idCalendar.innerHTML = initCalendar();
}

function nextMonth(){
 MonthStr += 1;
 if(MonthStr > 12){
  MonthStr = 1;
  YearStr += 1;
 }
 idCalendar.innerHTML = initCalendar();
}


function TDMove(){
 var obj = window.event.srcElement;
 obj.style.setAttribute("border", "#005AB5 1px solid");
}

function TDOut(){
 var obj = event.srcElement;
 if(parseInt(obj.innerText) == DayStr)
  obj.style.setAttribute("border","#006432 1px solid");
 //file://else if(parseInt(obj.innerText) == NowDayStr)
  //file://obj.style.setAttribute("border","#ff0000 1px solid");
 else
  obj.style.setAttribute("border","#ffffff 1px solid");
}
var tobj;
function showCalendar(stype){
 showtype = stype;
 var obj  = event.srcElement;
 var pobj = obj.parentElement;
 tobj = pobj.childNodes(0);
 v = tobj.value;
 if(v != ""){
  pos = v.indexOf("-");
  if(pos != -1 && pos == 4){
   YearStr = parseInt(v.substring(0,pos));
   v = v.substring(pos+1,v.length);
  }
  pos = v.indexOf("-");
  if(pos != -1){
   MonthStr = parseInt(v.substring(0,pos));
   v = v.substring(pos+1,v.length);
  }
  if(v.length>0)
   DayStr = parseInt(v);
 }
 var objParent = obj.offsetParent;
 var left      = obj.offsetLeft;
 var top    = obj.offsetTop;
 while(objParent.tagName.toUpperCase() != "BODY"){
  left += objParent.offsetLeft;
  top  += objParent.offsetTop;
  objParent = objParent.offsetParent;
 }
 left += obj.offsetWidth;
 top  += obj.offsetHeight;

 idCalendar.style.top = top+1;
 idCalendar.style.left = left-260;
 caleX = left - 260;
 caleY = top + 1;
 idCalendar.innerHTML = initCalendar();
 idCalendar.style.display="";
 hideElement("SELECT");
 /*var left = event.clientX;
 var top  = event.clientY;
 idCalendar.style.top = top+5;
 idCalendar.style.left = left-250;
 idCalendar.innerHTML = initCalendar();
 idCalendar.style.display="";*/
}

function TDClick(){
 DayStr = event.srcElement.innerText;
 if(showtype=="day"){
  tobj.value = YearStr +"-" + MonthStr + "-" + DayStr;
  //tobj.value = YearStr + MonthStr +  DayStr;
 }
 else{
  tobj.value = YearStr + "-" + MonthStr;
 }
 hideCalendar();
}

function getCaleToSec(){
 var dt;
 if(showtype=="day")
  dt = new Date(MonthStr+"/"+DayStr+"/"+YearStr);
 else
  dt = new Date(MonthStr+"/1/"+YearStr);
 var s  = dt.getTime();
 return s/1000;
}

function hideCalendar(){
 idCalendar.style.display = "none";
 showElement("SELECT");
}

function C_MouseUp(){
 if(idCalendar == null)
  return;
 var top  = parseInt(idCalendar.style.top);
 var left = parseInt(idCalendar.style.left);
 var wi   = idCalendar.offsetWidth;
 var he   = idCalendar.offsetHeight;
 var x    = event.clientX;
 var y    = event.clientY;
 var x1 = left + wi;
 var y1 = top + he;

 if(x>x1||x<left||y>y1||y<top){
  hideCalendar();
  showElement("SELECT");
 }
}

function hideElement(elmID){
 for (i = 0; i < document.all.tags(elmID).length; i++){
  obj = document.all.tags(elmID)[i];
  if (! obj || ! obj.offsetParent)
   continue;

  objLeft   = obj.offsetLeft;
  objTop    = obj.offsetTop;
  objParent = obj.offsetParent;
  while (objParent.tagName.toUpperCase() != "BODY")
  {
   objLeft  += objParent.offsetLeft;
   objTop   += objParent.offsetTop;
   objParent = objParent.offsetParent;
  }

  if(caleX > (objLeft + obj.offsetWidth) || objLeft > (caleX + idCalendar.offsetWidth))
   ;
  else if(objTop > (caleY + idCalendar.offsetHeight))
   ;
  else if(caleY > (objTop + obj.offsetHeight))
   ;
  else
   obj.style.visibility = "hidden";
 }
}

function showElement(elmID){
 for (i = 0; i < document.all.tags(elmID).length; i++){
  obj = document.all.tags(elmID)[i];
  if (! obj || ! obj.offsetParent)
   continue;
  obj.style.visibility = "";
 }
}

document.onmouseup = C_MouseUp;
<!--End-->

