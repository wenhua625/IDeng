//-----------后退
function goBack(){
	history.back();
}
//-----------调用链接
function goURL(URL){
	window.location.href = URL;
}



//************************页面输入检测函数**************************

/*
校验输入日期合法性
formName:	form名
inputName：	input输入框名
format：	校验日期格式（目前支持YYYY-MM-DD、MM-DD-YYYY、YYYY/MM/DD、MM/DD/YYYY）
*/
function checkDate(formName, inputName, format){
	var supportFormat = new Array();
	supportFormat[0] = "YYYY-MM-DD";
	supportFormat[1] = "MM-DD-YYYY";
	supportFormat[2] = "YYYY/MM/DD";
	supportFormat[3] = "MM/DD/YYYY";
	var dateValue = eval("document."+formName+"."+inputName+".value");
	//检测是否支持格式
	var max = supportFormat.length;
	var i = 0;
	var index = 0;
	var year = "";
	var month = "";
	var day = "";
	var formatSplitSymbol = ""
	var valueSplitSymbolFir = ""
	var valueSplitSymbolSec = ""
	while(i < max){
		if(supportFormat[i] == format){
			break;
		}
		i++;
	}
	if(i == max){
		alert("校验函数不支持此格式！");
		return false;
	}
	index = i;
	dateValue = trim(dateValue);
	if(dateValue.length != format.length){
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		alert("输入日期长度不正确！");
		return false;
	}
	//分别处理各种格式
	switch(index){
		case 0:
			year = dateValue.substring(0,4);
			month = dateValue.substring(5,7);
			day = dateValue.substring(8,10);
			valueSplitSymbolFir = dateValue.substring(4,5);
			valueSplitSymbolSec = dateValue.substring(7,8);
			formatSplitSymbol = format.substring(4,5);
			break;
		case 1:
			year = dateValue.subString(6,10);
			month = dateValue.subString(0,2);
			day = dateValue.subString(3,5);
			valueSplitSymbolFir = dateValue.substring(3,4);
			valueSplitSymbolSec = dateValue.substring(5,6);
			break;
		case 2:
			year = dateValue.substring(0,4);
			month = dateValue.substring(5,7);
			day = dateValue.substring(8,10);
			valueSplitSymbolFir = dateValue.substring(4,5);
			valueSplitSymbolSec = dateValue.substring(7,8);
			formatSplitSymbol = format.substring(4,5);
			break;
		case 4:
			year = dateValue.subString(6,10);
			month = dateValue.subString(0,2);
			day = dateValue.subString(3,5);
			valueSplitSymbolFir = dateValue.substring(3,4);
			valueSplitSymbolSec = dateValue.substring(5,6);
			break;
	}
	//检验分隔符
	if(valueSplitSymbolFir.length > 0 && valueSplitSymbolFir != formatSplitSymbol){
		alert("分隔符错误！");
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else if(valueSplitSymbolSec.length > 0 && valueSplitSymbolSec != formatSplitSymbol){
		alert("分隔符错误！");
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	//检验年
	if(year.length > 0){
		if(!isYear(year)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	//检验月
	if(month.length > 0){
		if(!isMonth(month)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	//检验日
	if(day.length > 0){
		if(!isDay(day)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	return true;
}

/*
是否数字
formName:	form名
inputName：	input输入框名
*/
function isNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	firstIsZero = false;
	if(value != null && value.length > 0){
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("请输入数字！")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		if(value.charAt(0) == '0' && value.length > 2){
				alert("请输正确数字！")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
		}
	}
	return true;
}

//是否数字
function isNumber(value){
	var firstIsZero = false;
	if(value != null && value.length > 0){
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("请输入数字！")
				return false;
			}
		}
		if(value.charAt(0) == '0' && value.length > 2){
				alert("请输正确数字！")
				return false;
		}
	}
	return true;
}

//是否浮点
function isFNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	firstIsZero = false;
	if(value != null && value.length > 0){
		nF = 0;
		if(value.charAt(0) == '-' || (value.charAt(0)<='9'&&value.charAt(0)>='0')){
			for(i = 1 ; i < value.length ; i++){
				if((value.charAt(i)>'9'||value.charAt(i)<'0')&&(value.charAt(i)!='.')){
					alert("请输入数字或者小数点！")
					eval("document."+formName+"."+inputName+".focus()")
					eval("document."+formName+"."+inputName+".select()")
					return false;
				}
				if(value.charAt(i)=='.')
				{
				  nF++;
				  if (nF>1)
				  {
					alert("只能包含一个小数点！")
					eval("document."+formName+"."+inputName+".focus()")
					eval("document."+formName+"."+inputName+".select()")
					return false;
				  }
				}
			}
		}
		else{
			return false;
		}
	}
	return true;
}
//是否为空
function isNull(formName, inputName){
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length <= 0){
		alert("请输入带“ * ”的必输内容！")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return true;
	}
	return false;
}
//去除空格
function trim(srcString){
	newString = "";
	for(i = 0 ; i < srcString.length ; i++)
		if(srcString.charAt(i) != ' ')
			newString += srcString.charAt(i);
	return newString;
}
//检测年
function isYear(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 4){
		alert("请输入4位年份")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	return true;
}

//检测年
function isYear(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 4){
		alert("请输入4位年份")
		return false;
	}
	return true;
}

//检测季
function isSeason(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 1){
		alert("请输入1位季度")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) < '1' || newString.charAt(0) > '4'){
			alert("请输入1到4之间的数值")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	return true;
}
//检测月
function isMonth(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("请输入2位月份")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1'){
			alert("请输入正确月份")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
		else if(newString.charAt(0) == '1'){
			if(newString.charAt(1) > '2'){
				alert("请输入正确月份")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("请输入正确月份")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}

	}
	return true;
}

//检测月
function isMonth(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("请输入2位月份")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1'){
			alert("请输入正确月份")
			return false;
		}
		else if(newString.charAt(0) == '1'){
			if(newString.charAt(1) > '2'){
				alert("请输入正确月份")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("请输入正确月份")
				return false;
			}
		}

	}
	return true;
}

//检测日
function isDay(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("请输入2位日期")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1' && newString.charAt(0) != '2' && newString.charAt(0) != '3'){
			alert("请输入正确日期")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
		else if(newString.charAt(0) == '3'){
			if(newString.charAt(1) > '1'){
				alert("请输入正确日期")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("请输入正确日期")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}

	}
	return true;
}

//检测日
function isDay(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("请输入2位日期")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1' && newString.charAt(0) != '2' && newString.charAt(0) != '3'){
			alert("请输入正确日期")
			return false;
		}
		else if(newString.charAt(0) == '3'){
			if(newString.charAt(1) > '1'){
				alert("请输入正确日期")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("请输入正确日期")
				return false;
			}
		}

	}
	return true;
}

//是否为空
function isdateNull(formName, inputName){
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length <= 0){
		return true;
	}
	return false;
}
//大于当前日期
function validDate(select) 
{
  var newString = trim(select);
  if(newString==null||trim(newString).length!=8) {
    alert("日期格式不对");
    return false;
  }
  year = newString.substring(0,4);
  month = newString.substring(4,6);
  day = newString.substring(6);
  current = new Date();
  cur = current.getFullYear()+""+ (current.getMonth() < 9 ? '0' : '') + (current.getMonth() + 1) + ""
		+ (current.getDate() < 10 ? '0' : '') + current.getDate();
  if((newString-0)<=(cur-0)) {
    alert("输入日期不能小于当前日期");
    return false;
  }
  return isDay(day)&&isMonth(month)&&isYear(year);
}
//年，月，日必须全部为空或全部不为空，否则出错。
function checkdate(formName, syear,smonth,sday)
{
   	if (!isdateNull(formName, syear) && !isdateNull(formName, smonth) && !isdateNull(formName, sday))//年，月，日都不为空
	{
		if (isYear(formName, syear) && isMonth(formName, smonth) && isDay(formName, sday))//年，月，日都正确
        	{return true;}
        else
         	{return false;}
	}
	else if (isdateNull(formName, syear) && isdateNull(formName, smonth) && isdateNull(formName, sday))//年，月，日都为空
	{   
		return true; }
	else
	{  alert("日期输入有误。");
		return false;
	}
    
}
//起始时间不能大于结束时间
function checkBeginEnd(formName,beginDate,endDate)
{
	startdate = eval("document."+formName+"."+beginDate+".value");
	if(startdate==null) return true;
	startdate = trim(startdate);
	if(startdate=="") return true;
	enddate = eval("document."+formName+"."+endDate+".value");
	if(enddate==null) return true;
	enddate = trim(enddate);
	if(enddate=="") return true;
	if(startdate > enddate) {
          alert("起始日期不能大于截止日期，请重新输入");
		  return false;
	}
    else {
         return true;
	}
}
//起始时间不能大于结束时间
function validdate(formName, syear,smonth,sday,eyear,emonth,eday)
{
	if(!isdateNull(formName, syear) && !isdateNull(formName, smonth) && !isdateNull(formName, sday) && !isdateNull(formName, eyear) && !isdateNull(formName, emonth) && !isdateNull(formName, eday))
	{
startdate= trim(eval("document."+formName+"."+syear+".value"))+trim(eval("document."+formName+"."+smonth+".value"))+trim(eval("document."+formName+"."+sday+".value"));
enddate= trim(eval("document."+formName+"."+eyear+".value"))+trim(eval("document."+formName+"."+emonth+".value"))+trim(eval("document."+formName+"."+eday+".value"));
	   if(startdate > enddate)
		{alert("起始日期不能大于截止日期，请重新输入");
		   return false;
		}
       else
          return true;
	}
   else
	   return true;
}
//判断时间是否为有效值
function validFullDate(formName, sdate)
{
    oldString = eval("document."+formName+"."+sdate+".value");
	newString = trim(oldString);
	if (isdateNull(formName,sdate))
	{ return true;}
	
	if(newString != null && newString.length > 0){
		for(i = 0 ; i < newString.length ; i++){
			if(newString.charAt(i)>'9'||newString.charAt(i)<'0'){
				alert("请输入8位有效日期（yyyymmdd）！")
				return false;
			}
		}
		if(newString.charAt(0) == '0' && newString.length > 2){
				alert("请输入8位有效日期（yyyymmdd）！")
				return false;
		}
	}
	if(newString == null || newString.length < 8)
	{
		alert("请输入8位有效日期（yyyymmdd）");
        eval("document."+formName+"."+sdate+".focus()")
		eval("document."+formName+"."+sdate+".select()")
		return false;
	}
	year = newString.substring(0,4);
	month = newString.substring(4,6);
	day = newString.substring(6,8);
	//current = new Date();
    //cur = current.getFullYear()
     //校验月份
    if(month.charAt(0) != '0' && month.charAt(0) != '1'){
			alert("请输入正确月份")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
	}
	else if(month.charAt(0) == '1'){
		if(month.charAt(1) > '2'){
			alert("请输入正确月份")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	else if(month.charAt(0) == '0'){
		if(month.charAt(1) == '0'){
			alert("请输入正确月份")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	//校验日期
	if(day.charAt(0) != '0' && day.charAt(0) != '1' && day.charAt(0) != '2' && day.charAt(0) != '3'){
		alert("请输入正确日期")
		eval("document."+formName+"."+sdate+".focus()")
		eval("document."+formName+"."+sdate+".select()")
		return false;
	}
	else if(day.charAt(0) == '3'){
		if(day.charAt(1) > '1'){
			alert("请输入正确日期")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	else if(day.charAt(0) == '0'){
		if(day.charAt(1) == '0'){
			alert("请输入正确日期")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	return true;
}

//************************弹出层处理函数（查询结果页面使用）**************************

function Do_Layer(choice,thisForm){
	var A2 = eval(thisForm);
	if	(choice=='visible'){
		if (navigator.userAgent.indexOf('MSIE') > -1){
			A2.style.left=document.body.scrollLeft+document.body.clientWidth/2-A2.offsetWidth/2;
			A2.style.top=document.body.scrollTop+45;
			A2.style.visibility='visible';
		}
		else{
			document.A2.style.left=document.body.scrollLeft+document.body.clientWidth/2-document.A2.offsetWidth/2;
			document.A2.style.top=document.body.scrollTop+45;
			document.A2.visibility='visible';
		}
	}
	if	(choice=='hidden'){
		if (navigator.userAgent.indexOf('MSIE') > -1)
			A2.style.visibility='hidden';
		else
			document.A2.visibility='hidden';
	}
}

function Do_Layer0(choice,thisForm){
	if (thisForm=='Layer0'){
		if	(choice=='visible'){
			if (navigator.userAgent.indexOf('MSIE') > -1)
				Layer0.style.visibility='visible';
			else
				document.Layer0.visibility='visible';
		}
		if(choice=='hidden'){
			if (navigator.userAgent.indexOf('MSIE') > -1)
				Layer0.style.visibility='hidden';
			else
				document.Layer0.visibility='hidden';
		}
	}
}
function Yes_Win(URL,Name,Width,Height){
 if (!Width) { Width =360;}
 if (!Height){ Height=480;}
 if (!Name)  { Name  ="Members";}
 var win_cfg = "toolbar=no,directories=no,status=no,menubar=no,scrollbars=no,resilocation=no,scrollbars=no,resizable=no,alwaysRaised=yes,dependent=yes,align=center,valign=center,top=0,left=0,width=" + Width + ",height=" + Height;
 window.open(URL,Name,win_cfg)
}

function confirmDel(item) {
    if (confirm("确定要删除所选中的" + item + "？"))
		return true;
	return false;
}
//检查选择
function CheckCheckBox(formname,checkname,item) {
    var checkinfo = eval("document."+formname+"."+checkname+"");
    if(checkinfo==null) {
      alert("没有"+item+"信息!");
      return false;
    }
    var flag = "0";
    if(checkinfo.length==null) {
       if(checkinfo.checked==false)
           {  alert("请选择"+item+"!");
              return false;
           }
     }
    else {
        for(var i=0;i<checkinfo.length;i++) {
          if(checkinfo[i].checked) {
            flag = "1";
            break;
         }
        }
        if(flag=="0"){
           alert("请选择"+item+"!");
           return false;
        }
    }
    return true;
}
//检查仅仅选择一个
function CheckCheckBoxOnly(formname,checkname,item) {
    var checkinfo = eval("document."+formname+"."+checkname+"");
    if(checkinfo==null) {
      alert("没有"+item+"信息!");
      return false;
    }
    var flag = 0;
    if(checkinfo.length==null) {
       if(checkinfo.checked==false)
           {  alert("请选择"+item+"!");
              return false;
           }
     }
    else {
        for(var i=0;i<checkinfo.length;i++) {
          if(checkinfo[i].checked) {
            flag++;
          }
         }
        if(flag>1){
           alert("只能选择一个"+item+"!");
           return false; 
         }
    }
    return true;
}
//判断不是数字
function notNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	if(value != null && value.length > 0){
	  if(value.charAt(0)!='-') {
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("请输入数字！")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return true;
			}
		}
	  }
	  else {
	   if(value.length==1) {
	     alert("请输入数字！")
	     return true;
	   }
	   for(i = 1 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("请输入数字！")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return true;
			}
		}
	  }
	  if(value.charAt(0) == '0' && value.length >= 2){
			alert("请输正确数字！")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return true;
	   }
	}
	return false;
}
//判断IP地址合法性
function verifyIP(textObj){
  var tmpIPValue=trim(textObj.value);
  var tmpLength=tmpIPValue.length;
  
  if (tmpLength == 0){
    return true;   
  }
  for (var i = 0; i < tmpLength;i++){
    var aChar = tmpIPValue.substring(i,i+1);
    if(aChar != "." && (aChar < "0" || aChar > "9")) {
      alert ("输入的IP地址错误(格式:xxx.xxx.xxx.xxx)");
      textObj.focus(this);
      textObj.select(this);
      return false;
    }
  }
  
  var count=0;
  for (var j = 0; j < tmpLength;j++){
    var aChar = tmpIPValue.substring(j,j+1);
    if(aChar == ".") {
        count++;
    }
  }
  if (count!=3){
    alert("输入的IP地址错误(格式:xxx.xxx.xxx.xxx)");
	textObj.focus(this);
    textObj.select(this);
	return false;
  }
  var index1 = tmpIPValue.indexOf(".",0);
  var first = tmpIPValue.substring(0,index1)-0;
  if(first<0||first>255) {
    alert ("输入的IP地址第一位错误(格式:xxx.xxx.xxx.xxx)");
    return false;
  }
  var index2 = tmpIPValue.indexOf(".",index1+1);
  var second = tmpIPValue.substring(index1+1,index2);
  if(second<0||second>255) {
    alert ("输入的IP地址第二位错误(格式:xxx.xxx.xxx.xxx)");
    return false;
  }
  var index3 = tmpIPValue.indexOf(".",index2+1);
  var third = tmpIPValue.substring(index2+1,index3);
  if(third<0||third>255) {
    alert ("输入的IP地址第三位错误(格式:xxx.xxx.xxx.xxx)");
    return false;
  }
  var four = tmpIPValue.substring(index3+1);
  if(four<0||four>255) {
    alert ("输入的IP地址第四位错误(格式:xxx.xxx.xxx.xxx)");
    return false;
  }
  return true;

}