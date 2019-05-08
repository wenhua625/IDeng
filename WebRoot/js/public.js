

function isDate( sDate ){
	if(sDate.length==0){
		return false
	}
	var index1 = sDate.indexOf("/");
	var index2 = sDate.lastIndexOf("/");
	if(index1==-1||index2==-1||index1==index2) return false;
	var year = sDate.substring(0,index1);
	var month = sDate.substring(index1+1,index2);
	var day = sDate.substring(index2+1,sDate.length);
	if(isNumber(year,false)&&isNumber(month,true)&&isNumber(day,true)){
		bVal = true;
	}
	else{
		bVal = false;
	}
	year = parseInt(year,10);
	month=parseInt(month,10);
	day = parseInt(day,10);
	if(month<1||month>12){
		bVal = bVal&&false;
	}

	if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
		if(day>0&&day<=31){
			bVal = bVal&&true;
		}
		else{
			bVal = bVal&&false;
		}
	}
	if(month==4||month==6||month==9||month==11){
		if(day>0&&day<=30){
			bVal = bVal&&true;
		}
		else{
			bVal = bVal&&false;
		}
	}
	if(month==2){
		if(isLeapYear(year)){
			if(day>0&&day<=29){
				bVal = bVal&&true;
			}
			else{
				bVal = bVal&&false;
			}
		}
		else{
			if(day>0&&day<=28){
				bVal = bVal&&true;
			}
			else{
				bVal = bVal&&false;
			}
		}
	}
	return bVal;
}


/*
	Function Name:				isNumber
	Description	 :				to check whether the string is representing a number
	Params:
		sNumber							one number value string
		prefixZeroAllowed		whether allow a prefix 0 in the number string.
	Return Value:
		true								if the string represents a number
		false							if not		
*/
function isNumber(sNumber,prefixZeroAllowed){
	var sVal = true
	var s = ""
	var len = sNumber.length;
	if(len==0){
		return false
	}
	for(var i=0;i<len;i++){
		var c = sNumber.charAt(i)
		if(c>='0'&&c<='9'){
			if(i==0){
				if(prefixZeroAllowed==false){
					if(c=='0'&&len>1){
						sVal=false
						break
					}
				}
				else{
					if(c=='0'&&len>1){ c="" }
				}
			}
			s = s+c;
		}
		else{
			sVal = false
			break
		}
	}
	return sVal
}


/*
	Function Name:				isInteger
	Description	 :				to check whether the string is representing an integer
	Params:
		sNum								one number value string
	Return Value:
		true								if the string represents an integer
		false								if not		
*/
function isInteger(sNum)
{
    var re=/^([+-]?\d+)$/;
    return re.test(sNum);
}

function isYear(sYear)
{
    var re=/^([0-9]+[0-9]+[0-9]+[0-9])$/;
    return re.test(sYear);
}


/*
	Function Name:				isNegative
	Description	 :				to check whether the string is representing a negative number
	Params:
		sNum								one number value string
	Return Value:
		true								if the string represents a negative number
		false								if not
*/
function isNegative(sNum)
{
    var re=/^[-](\d+|([0]?[\.]\d*)|([1-9]+[0-9]+[\.]\d*)|([1-9]+[\.]\d*))$/;
    return re.test(sNum);
}

/*
	Function Name:				isData
	Description	 :				to check whether the string is representing a data
	Params:
		sData								one number value string
	Return Value:
		true								if the string represents a data
		false								if not
*/
function isData( sData )
{
  var re=/^[+-]?(\d+|([0]?[\.]\d*)|([1-9]+[0-9]+[\.]\d*)|([1-9]+[\.]\d*))$/;
  return re.test(sData);
}


/*
	Function Name:	trim
	Description	 :	to trim leading and ending blanks of string
	Params:					str	-- input string
	Return Value:		trimmed string
*/
function trim(str){
	while((str.charCodeAt(0)==32)&&(str.length>=1)){
  	str=str.substring(1,str.length);
	}
	while((str.charCodeAt(str.length-1)==32)&&(str.length>=1)){
		str=str.substring(0,str.length-1);
	}
	return str;
}

/*
	Function Name:				IsAphameric
	Description	 :				检查是否为字母数字或字符。
	Params:								str		input  string
	Return Value:
		如果是中文，返回false
	Author:					
	CreatedTime:			
*/
function IsAphameric(str)
{
  for(var i=0;i<str.length;i++){
    if(str.charAt(i)>"z"){
      return false;
    }
  }
  return true;
}

function FixedLength(str, len){    
    var i = 0; 
    var addNum =0;
    var k = str.length;
    for(i=0; i<k; i++){
        
        if(str.charAt(i)>'z'){ //为汉字
            addNum = addNum + 1;
        }
    }
    k = len - (k+addNum);  
    for (i=0; i< k; i++){
        str += " ";
    }
    return str;
    
}

function checkNumber(sNumber) {
    if((sNumber.value.length > 0) && (!isNumber(sNumber.value))) {
        alert("请输入整数！");
        sNumber.focus();
        sNumber.value = ""; 
	return;
    }
}

//校验输入框是否为数字，允许内容为空串
function checkData(sData) {
    if((sData.value.length > 0) && (!isData(sData.value))) {
        alert("请输入数字！");
        sData.focus();
        sData.value = "";        
	return;
	}
}

//校验输入框是否为数字，不允许内容为空串
function checkDataAndNull(sData) {
    if(!isData(sData.value)) {
        alert("请输入数字！");
        sData.focus();
        sData.value = "";        
	return;
	}    
}

function checkPlusData(sData) {
    if((sData.value.length > 0) && (!isData(sData.value) || (sData.value < 0))) {
        alert("请输入正数！");
        sData.focus();
        sData.value = "";
	return;
    }  
}

function checkPlusNumber(sNumber) {
    if((sNumber.value.length > 0) && (!isNumber(sNumber.value) || (sNumber.value < 0))) {
        alert("请输入正整数！");
        sNumber.focus();
        sNumber.value = "";        
	return;
    }  
}

function checkDate(startDate, endDate){
  var index = 0;
  var startDate = startDate.value;
  index = startDate.search('/');
  var date1 = startDate.substring(0, index);
  startDate = startDate.substring(index+1);
  index = startDate.search('/');
  var date2 = startDate.substring(0, index);
  var date3 = startDate.substring(index+1);
  var nStart = new Number(date1 + date2 + date3);
    
  var endDate = endDate.value;
  index = endDate.search('/');
  date1 = endDate.substring(0, index);
  endDate = endDate.substring(index+1);
  index = endDate.search('/');
  date2 = endDate.substring(0, index);
  date3 = endDate.substring(index+1);
  var nEnd = new Number(date1 + date2 + date3);  
  
  if(nStart > nEnd){
  	alert("起始日期不能大于结束日期!");
  	return false;
  }
  return true;
}

//根据指定开始年,填充select下拉框的选项
//使用方法: 在 selectWnd 定义后面 并且在</body>前面调用此函数
//参数说明: selectWnd-要填充下拉框, beginYear-开始年, selectedYearMonth-默认选中年月

// ....
//  <select name="testSelectWnd" ></select>
// ....
//<script language="JavaScript">
//    fillYearMonth(formList.testSelectWnd, 2002, 2003, 2 );
//</script>
//</body>

function fillYearMonth( selectWnd, beginYear, selectedYearMonth ){
   var currDate = new Date();
   var currYear = currDate.getYear();
   var currMonth = currDate.getMonth();
  
   for ( var i = beginYear; i < currYear; i++ ){
      // 1月~9月
      for( var j = 1; j<10; j++ ){
          var option1=document.createElement("Option"); 
          option1.value = "" + i + "0" + j;
          option1.text = "" + i + "年0" + j + "月";  
          selectWnd.add(option1);
          if ( option1.value == selectedYearMonth ){
              option1.selected = "selected";  
          }  
      }
      //10月~12月
      for( var k = 10; k <= 12; k ++ ){
          var option2=document.createElement("Option"); 
          option2.value = "" + i + k;
          option2.text = "" + i + "年" + k + "月";
          selectWnd.add(option2);
          if ( option2.value == selectedYearMonth ){
              option2.selected = "selected";  
          } 
      }
   }
   //当前年
   for( var m = 1; m <= currMonth; m++ ){
      var option3=document.createElement("Option"); 
      if ( m < 10 ) {
          option3.value = "" + currYear + "0" + m;
          option3.text = "" + currYear + "年0" + m + "月";
      }else{
          option3.value = "" + currYear + m;
          option3.text = "" + currYear + "年" + m + "月";
      }
      selectWnd.add(option3);
      if ( option3.value == selectedYearMonth ){
          option3.selected = "selected";  
      }
   }
}

//根据指定开始年,填充select下拉框的选项 
//使用方法: 在 selectWnd 定义后面 并且在</body>前面调用此函数
//参数说明: selectWnd-要填充下拉框, beginYear-开始年, endYear-结束年 selectedYear-默认选中年
function fillYear( selectWnd, beginYear, endYear, selectedYear ){
   for ( var year = beginYear; year <= endYear; year++ ){
       var option=document.createElement("Option"); 
       option.value = "" + year;
       option.text = "" + year + "年";  
       selectWnd.add(option);
       if ( year == selectedYear ){
           option.selected = "selected";  
       }  
   }
}

//根据指定开始年,填充select下拉框的选项 
//使用方法: 在 selectWnd 定义后面 并且在</body>前面调用此函数
//参数说明: selectWnd-要填充下拉框, selectedMonth-默认选中月
function fillMonth( selectWnd, selectedMonth ){
    for ( var month = 1; month <= 12; month++ ){// 1月~9月
        var option=document.createElement("Option"); 
        if( month < 10 ){
      	    option.value = "0" + month;
            option.text  = "0" + month + "月"; 
        }else{
            option.value = "" + month;
            option.text  = "" + month + "月"; 
        } 
        selectWnd.add(option);
        if ( month == selectedMonth ){
            option.selected = "selected";          
        }  
    }
}

//动态更新select选择框中的选项的顺序
//选中项上移
//selectWnd - select控件名
function select_up( selectWnd ){
    var selIndex = selectWnd.selectedIndex;
    if( selIndex == -1 ){
        alert("未选择选项，不能上移");
        return;
    }
       
    if( selIndex == 0 )
        return;
    
    var option = selectWnd.item(selIndex);
    selectWnd.remove(selIndex);
    selectWnd.add(option, (selIndex -1 ) );    
}

//动态更新select选择框中的选项的顺序
//选中项下移
//selectWnd - select控件名
function select_down(selectWnd){
    var selIndex = selectWnd.selectedIndex;
    
    if( selIndex == -1 ){
        alert("未选择选项，不能下移");
        return;
    }
    
    if( selIndex == selectWnd.length - 1 )
        return;
    
    var option = selectWnd.item(selIndex);
    selectWnd.remove(selIndex);
    selectWnd.add(option, selIndex+1 ); 
}

//对多选框中选中的选项组成字符串，以"|"相隔
//selectWnd - select多选框的名字
//返回 - 组好后的字符串
function getSelectedString(selectWnd){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		if(option.selected){
			str += option.value;
			str += "|";
		}
	}
	return str;
}

//对多选框中选中的选项组成字符串，以指定字符相隔
//selectWnd - select多选框的名字
//split - 分割字符串
//返回 - 组好后的字符串
function getSelectedStringS(selectWnd, split){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		if(option.selected){
			str += option.value;
			str += split;
		}
	}
	return str;
}

//对多选框中所有选项组成字符串，以"|"相隔
//selectWnd - select多选框的名字
//split - 分割字符串
//返回 - 组好后的字符串
function getSelectAllString(selectWnd){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		str += option.value;
		str += "|";
	}
	return str;
}

//对多选框中所有选项组成字符串(包括值和名)，以"|"相隔
//selectWnd - select多选框的名字
//split - 分割字符串
//返回 - 组好后的字符串
function getSelectAllString2(selectWnd){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		str += option.value;
		str += ".";
		str += option.text;
		str += "|";
	}
	return str;
}

//对多选框中选中的选项组成字符串，以指定字符相隔
//selectWnd - select多选框的名字
//split - 分割字符串
//返回 - 组好后的字符串
function getSelectAllStringS(selectWnd, split){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		str += option.value;
		str += split;
	}
	return str;
}

//取一个select控件的选中项的option文本
function getSelectOption(selectWnd){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var option = selectWnd.item(i);
		if(option.selected){
		    str = option.text;
		    break;
	    }
	}
	return str;
}

//取一个select控件的选中项的option文本
function getSelectOptionByValue(selectWnd, values){
	var str = "";
	var cnt = selectWnd.length;
	var i = 0;
	for( i = 0; i < cnt; i++ ){
		var myoption = selectWnd.item(i);
		if(values == myoption.value){
		    str = myoption.text;
		    break;
	    }
	}
	return str;
}