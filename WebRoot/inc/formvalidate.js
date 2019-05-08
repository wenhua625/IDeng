////////////////////////////////////////////////////////////////////////////////
//	name: formValidate.js
//	version: 1.0
//	author:黄宝颖
//	description: Check form inputs at client side before submit a form to server side.
//	note: 使用时需要在被判断的Input对象中加入属性值。
//			must="true"									说明该Input对象必填。
//			isNumeric="true"							说明该Input对象必须填写数字类型的值（不要求必填）。
//			isDate="true"								说明该Input对象必须填写日期类型（YYYY-MM-DD）的值（不要求必填）。默认是必须完整的，如2002-02-01,而2002-2-1就不允许
//			isIntegral="false"							说明该Input对象必须填写日期类型（YYYY-MM-DD）的值可以不是完整的，如2002-02-01和2002-2-1都可以
//			maxlength="22"								最大长度
//			minlength="15"								最小长度
//			isTel="true"								电话类型，包括电话，传呼，手机号码，传真号码
//			isZip="true"								邮政编码
//			isInt="true"								是否为整数。
//			isEmail="true"								判断是否email地址有效
//			isAlphanumeric="true"						字母或者数字，比如证件号
//			maxlength4float="7"							float类型的整数最长位数
//			minValue="0"								isInt或isNumeric="true"才有效，must="true"时,值>0，must="true"没有时,值>=0
//      	maxValue="100"								isInt或isNumeric="true"才有效，值<=100
//		此处还提供了另外几个函数：
//			isDate(objTgt)							判断传入对象objTgt中填写的值（objTgt.value）是否为日期类型。
//			judgeLeapYear(yearStr)					是否为闰年。
//			judgeSmallMonth(monthStr)				是否为小月。
//			isModified(eForm)						页面是否有过修改。
// 还提供一个javascript 校验库
// JavaScript library ，用于校验表单数据
// 简要目录：
// 1.是否为空或全是空字符 
// 2.字符串是否只包含0到9的数字
// 3.是否整数(允许带正负号)
// 4.是否数字
// 5.小数（m,n），忽略小数首尾的0，不允许正负号
// 6.字符串是否全是字母
// 7.字符串是否全是字母和数字
// 8.日期
// 9.居民身份证号码
// 10.电话号码
// 11.邮政编码
// 12.是否合法的email
// 18.个人公积金帐号(上海尾数205)
// 19.单位公积金帐号(上海尾数205)
// 20.划款/还款帐号
////////////////////////////////////////////////////////////////////////////////


// 表单提交之前，在页面上判断输入的合法性（必填must、数字isNumeric、日期isDate等等）。
function formValidate(formName){
	var isLeapYear=false; /*闰年[二月为29天]*/
	var isSmallMonth=false; /*小月[该月为30天]*/
	
	if (!document.forms(formName)) return false;

	for (var i=0;i<document.forms(formName).length;i++)	{
		objTgt = document.forms(formName).item(i);
		//alert(objTgt.type);
		// 对于所有 Text 输入框中的值进行去空操作。
		//if(objTgt.type=="text")objTgt.value=objTgt.value.trim();

		if (objTgt.must=='true'&& isWhitespace(objTgt.value)){
			if (objTgt.disabled==true){
				continue;
			}else{
				if(objTgt.title!=""){
					errAlert ("必填项 ["+objTgt.title+"] 未填写 !","为了保证数据的完整性，系统要求某些值必须填写，\n现在您没有填写其中的 ["+objTgt.title+"] 这一项。","重新填写 ["+objTgt.title+"] 这一项。");
				}else{
					errAlert("必填项未填写 !","为了保证数据的完整性，系统要求某些值必须填写，\n现在您没有填写其中的某几项。","返回重新填写。");
				}
				if(objTgt.type!="select-one"){
					focusItem(objTgt);
				}
				return false;
			}
		}
			
		if(objTgt.value==null){continue;}
		var formValidate_valueLength=objTgt.value.length;	
		//判断最大长度		
		var formValidate_maxlength=objTgt.maxlength;
		if(formValidate_maxlength!=""){			
			if(parseFloat(formValidate_maxlength)<parseFloat(formValidate_valueLength)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中长度超过最大值:"+formValidate_maxlength+"!","为了保证数据的准确性，系统要求某些长度不超过最大值，\n现在您在 ["+objTgt.title+"] 项中长度超过最大值。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该长度不超过最大值:"+formValidate_maxlength+"!","为了保证数据的准确性，系统要求某些长度不超过最大值，\n现在您在某几项中长度超过最大值。","重新填写。");
					}
					
					focusItem(objTgt);
					return false;
			}
		}
		//判断最小长度		
		
		var formValidate_minlength=objTgt.minlength;
		if(formValidate_minlength!=""){			
			if(parseFloat(formValidate_minlength)>parseFloat(formValidate_valueLength)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中长度必须超过最小值:"+formValidate_minlength+"!","为了保证数据的准确性，系统要求某些长度必须超过最小值，\n现在您在 ["+objTgt.title+"] 项中长度必须超过最小值。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该长度必须超过最小值:"+formValidate_minlength+"!","为了保证数据的准确性，系统要求某些长度必须超过最小值，\n现在您在某几项中长度必须超过最小值。","重新填写。");
					}
					
					focusItem(objTgt);
					return false;
			}
		}
		//判断float类型的整数部分的最大长度			
		var formValidate_valueLength4float=objTgt.value.length;	
		var pos2=objTgt.value.indexOf (".")
		if(pos2>-1){
			formValidate_valueLength4float=(objTgt.value.substring(0,pos2)).length;
		}		
		var formValidate_maxlength4float=objTgt.maxlength4float;
		if(formValidate_maxlength4float!=""){			
			if(parseFloat(formValidate_maxlength4float)<parseFloat(formValidate_valueLength4float)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中整数部分不能超过 "+formValidate_maxlength4float+"位!","为了保证数据的准确性，系统要求某些长度不超过最大值，\n现在您在 ["+objTgt.title+"] 项中长度超过最大值。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("整数部分不能超过 "+formValidate_maxlength4float+"位!","为了保证数据的准确性，系统要求某些长度不超过最大值，\n现在您在某几项中长度超过最大值。","重新填写。");
					}
					
					focusItem(objTgt);
					return false;
			}
		}		
		
		
		if(objTgt.value!=''){
			//开始各种判断
			//判断是数字和数字在一定的范围
			if (objTgt.isNumeric=='true'){
				if (!isFloat(objTgt.value)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写数字 !","为了保证数据的准确性，系统要求某些值必须填写数字，\n现在您在 ["+objTgt.title+"] 项中没有填写数字。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该填写数字 !","为了保证数据的准确性，系统要求某些值必须填写数字，\n现在您在某几项中没有填写数字。","重新填写。");
					}
					
					focusItem(objTgt);
					return false;
				}
				var objTgtValue=new Number(objTgt.value);
				if (objTgt.maxValue>=0){
					var maxV=objTgt.maxValue;
					if(objTgtValue.valueOf()>maxV){//输入值不能大于最大值，可以相等
						errAlert("["+objTgt.title+"] 项的值不能大于"+maxV);
						focusItem(objTgt);
						return false;	
					}
				}
				if (objTgt.minValue>=0){
					var minV=objTgt.minValue;
					if(objTgtValue.valueOf() ==0&&objTgt.must=='true'){//如果是必须项则不能为0
						errAlert("["+objTgt.title+"] 项的值必须大于"+0);
						focusItem(objTgt);
						return false;
					}
					if(objTgtValue.valueOf()<minV){//输入值可以等于最小值
						errAlert("["+objTgt.title+"] 项的值不能小于"+minV);
						focusItem(objTgt);
						return false;
					}
				}
					
				//if(objTgt.isPow==1){
				//	if (Math.abs(objTgtValue)>=Math.pow(10,maxV)||Math.abs(objTgtValue)<Math.pow(10,(minV-1))){
				//		errAlert ("所填数字超出规定范围!\n数字范围从\""+minV+"\"到\""+maxV+"\"");
				//		
				//		focusItem(objTgt);
				//		return false;
				//	}
				//}else{
				//	if(objTgtValue.valueOf()>maxV||objTgtValue.valueOf()<minV){
				//		errAlert ("所填数字超出规定范围!\n数字范围从\""+minV+"\"到\""+maxV+"\"");
				//		
				//		focusItem(objTgt);
				//		return false;
				//	}
				//}
				continue;
			}
			//合法邮件地址检测			
			else if (objTgt.isEmail=="true"){
				if(isEmail(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写有效的电子邮件地址 !","为了保证数据的准确性，系统要求某些值必须填写电子邮件地址，\n现在您在 ["+objTgt.title+"] 项中没有填写电子邮件地址。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该填写有效的电子邮件地址 !","为了保证数据的准确性，系统要求某些值必须填写有效的电子邮件地址，\n现在您在某几项中没有填写有效的电子邮件地址。","重新填写。");
					}
						focusItem(objTgt);
						return false;
				}
				continue;
			}
			//整数类型
			else if (objTgt.isInt=="true"){
				if(isInt(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写整数!","为了保证数据的准确性，系统要求某些值必须填写整数，\n现在您在 ["+objTgt.title+"] 项中没有填写整数。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该填写整数 !","为了保证数据的准确性，系统要求某些值必须填写整数，\n现在您在某几项中没有填写整数。","重新填写。");
					}	
					focusItem(objTgt);
					return false;
				}
				var objTgtValue=new Number(objTgt.value);
				if (objTgt.maxValue>=0){
					var maxV=objTgt.maxValue;
					if(objTgtValue.valueOf()>maxV){//输入值不能大于最大值，可以相等
						errAlert("["+objTgt.title+"] 项的值必须不能大于"+maxV);
						focusItem(objTgt);
						return false;	
					}
				}
				if (objTgt.minValue>=0){
					var minV=objTgt.minValue;
					if(objTgtValue.valueOf() ==0&&objTgt.must=='true'){//如果是必须项则不能为0
						errAlert("["+objTgt.title+"] 项的值必须大于"+0);
						focusItem(objTgt);
						return false;
					}
					if(objTgtValue.valueOf()<minV){//输入值可以等于最小值
						errAlert("["+objTgt.title+"] 项的值不能小于"+minV);
						focusItem(objTgt);
						return false;
					}
				}
				continue;
			}
			//数字和字母，比如证件号
			else if (objTgt.isAlphanumeric=="true"){
				if(isAlphanumeric(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写数字或者字母!","为了保证数据的准确性，系统要求某些值必须填写数字或者字母，\n现在您在 ["+objTgt.title+"] 项中没有填写有误。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该填写数字或者字母 !","为了保证数据的准确性，系统要求某些值必须填写数字或者字母，\n现在您在某几项中没有填写有误。","重新填写。");
					}	
					focusItem(objTgt);
					return false;
				}
				continue;
			}			
			//日期类型
			else if (objTgt.isDate=="true"){
				if(isDate(objTgt)==false){
					focusItem(objTgt);
					return false;
				}
				continue;
			}
				
			//电话,手机号码
			else if (objTgt.isTel=="true"){
				if(isPhoneNumber(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写正确的电话号码!","为了保证数据的准确性，电话号码只能是数字和-()+，\n现在您在 ["+objTgt.title+"] 项中有非法字符。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该正确的电话号码 !","为了保证数据的准确性，电话号码只能是数字和-()+，\n现在您在电话号码中有非法字符。","重新填写。");
					}					
					focusItem(objTgt);
					return false;
				}
				continue;
			}
			//邮政编码
			else if (objTgt.isZip=="true"){
				if((isPostCode(objTgt.value))==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] 项中应该填写正确的邮编!","为了保证数据的准确性，邮编只能是6位数字，\n现在您在 ["+objTgt.title+"] 项中填写有误。","重新填写 ["+objTgt.title+"] 这一项。");
					}else{
						errAlert ("应该填写正确的邮编 !","为了保证数据的准确性，邮编只能是6位数字。","重新填写。");
					}
					focusItem(objTgt);
					return false;
				}
				continue;
			}
		}
	
	}
	return true;
}

// 判断是否是日期。
function isDate(objTgt){
	var inDate=objTgt.value;
	var isIntegral="false";
	if (objTgt.isIntegral=="false"){
		isIntegral="false";
	}else{
		isIntegral="true";
	}
	//alert(isIntegral);

	if (inDate.length>=6 && inDate.length<=10 && inDate.indexOf("-")!=-1 && inDate.indexOf("-")!=inDate.lastIndexOf("-") && inDate.substring(inDate.indexOf("-")+1,inDate.lastIndexOf("-")).indexOf("-")==-1){
		var inYear=parseInt(inDate.substring(0,inDate.indexOf("-")));
		var inMonth=inDate.substring(inDate.indexOf("-")+1,inDate.lastIndexOf("-"));
			if(isIntegral=="true"){
				if(inMonth.length!=2){
					errAlert("月份填写有误 !表示月份的数字必须是完整的，如1月用01表示.","表示月份的数字必须是完整的，如一月用01表示，可是您在 ["+objTgt.title+"] 项中填写的 "+inMonth+" 显然不在这个范围内。","重新填写 ["+objTgt.title+"] 这一项。");
					return false;
				}
			}
			if (inMonth.substring(0,1)=="0" && inMonth.length>1)
				inMonth=inMonth.substring(1,inMonth.length);
			inMonth=parseInt(inMonth);
		var inDay=inDate.substring(inDate.lastIndexOf("-")+1,inDate.length);
			if(isIntegral=="true"){
				if(inDay.length!=2){
					errAlert("日期填写有误 !表示日期的数字必须是完整的，如1日用01表示.","表示日期的数字必须处于1到31之间，而且必须满足闰年和大小月规则，\n可是您在 ["+objTgt.title+"] 项中填写的 "+inDay+" 显然不满足要求。","重新填写 ["+objTgt.title+"] 这一项。");
					return false;
				}
			}
			if (inDay.substring(0,1)=="0" && inDay.length>1)
				inDay=inDay.substring(1,inDay.length);
			inDay=parseInt(inDay);
		
		//alert(inYear+"-"+inMonth+"-"+inDay);
		if (inYear && !(isNaN(inYear)) && inYear.toString().length==4){
			var year=inYear;
			isLeapYear=judgeLeapYear(inYear);
		}else if(inYear.toString().length!=4){
			errAlert("对不起，系统需要四位年 !","为了保证日期存储的准确性，系统要求所有的日期输入都使用四位数字表示年份，\n可是您在 ["+objTgt.title+"] 项中没有使用四位年输入。","重新输入 ["+objTgt.title+"] 这一项。");
			
			focusItem(objTgt);
			return false;
		}else{
			errAlert("年份填写有误 !","","");
			
			focusItem(objTgt);
			return false;
		}
		
		if (inMonth && !(isNaN(inMonth)) && inMonth<=12 && inMonth >=1){
			var month=inMonth;
			isSmallMonth=judgeSmallMonth(inMonth);

		}else{
			errAlert("月份填写有误 !","表示月份的数字必须处于1到12之间，可是您在 ["+objTgt.title+"] 项中填写的 "+inMonth+" 显然不在这个范围内。","重新填写 ["+objTgt.title+"] 这一项。");
			
			focusItem(objTgt);
			return false;
		}
	
		if (inDay && !(isNaN(inDay)) && inDay>=1 && (month==2?(isLeapYear?inDay<=29:inDay<=28):(isSmallMonth?inDay<=30:inDay<=31))){
			var day=inDay;

		}else{
			errAlert("日期填写有误 !","表示日期的数字必须处于1到31之间，而且必须满足闰年和大小月规则，\n可是您在 ["+objTgt.title+"] 项中填写的 "+inDay+" 显然不满足要求。","重新填写 ["+objTgt.title+"] 这一项。");
			
			focusItem(objTgt);
			return false;
		}
		
		var inputDate=new Date();
		if (!(Date.parse(month + "-" + day + "-" + year))){
			errAlert("日期填写有误 !","为了保证日期存储的准确性，系统要求所有的日期输入都按照 YYYY-MM-DD 的格式输入，\n可是您在 ["+objTgt.title+"] 项中的输入值 ["+objTgt.value+"] 不满足输入要求。","重新输入 ["+objTgt.title+"] 这一项。");
			
			focusItem(objTgt);
			return false;
		}
		
	}else{
		errAlert("您输入的不是一个完整的日期值 !","为了保证日期存储的准确性，系统要求所有的日期输入都按照 YYYY-MM-DD 的格式输入，\n可是您在 ["+objTgt.title+"] 项中的输入值 ["+objTgt.value+"] 不满足输入要求。","重新输入 ["+objTgt.title+"] 这一项。");
		
		focusItem(objTgt);
		return false;
	}
}


// 判断闰年。
function judgeLeapYear(yearStr){
	if(!(isNaN(yearStr)) && yearStr.toString().length==4 && (yearStr%100==0?yearStr%400==0:yearStr%4==0))
		return true;
	return false;
}

// 判断小月。
function judgeSmallMonth(monthStr){
	if(monthStr==4||monthStr==6||monthStr==9||monthStr==11)
		return true;
	return false;	
}

// 是否整数。
function isInt(objNumber){	
	if(!isNaN(objNumber)){
		if(Math.round(objNumber)==objNumber){
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}

// 使INPUT对象获取焦点。
function focusItem(objTgt){
	objTgt.select();
	objTgt.focus();
}

// 判断页面是否修改过。
// 传入要判断页面的对象 -- eForm。
// 返回值：
// true -- 曾经修改过
// false -- 没有修改过
function isModified(eForm){
	var iNumElems = eForm.elements.length;
	for (var i=0;i<iNumElems;i++){
		var eElem = eForm.elements[i];
		// 输入框是否修改。
		if ("text" == eElem.type || "TEXTAREA" == eElem.tagName){
			if (eElem.value != eElem.defaultValue) return true;
		}
		// checkBox和radioBox是否修改。
		else if ("checkbox" == eElem.type || "radio" == eElem.type){
			if (eElem.checked != eElem.defaultChecked) return true;
		}
		// select是否修改。
		else if ("SELECT" == eElem.tagName){
			var cOpts = eElem.options;
			var iNumOpts = cOpts.length;
			for (var j=0;j<iNumOpts;j++){
				var eOpt = cOpts[j];
				if (eOpt.selected != eOpt.defaultSelected) return true;
			}
		}
	}
	return false;
}
/**
 * 合法邮件地址检测
 */
 function isEmail(email) {
	//var regu = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$";
	//var isFomatMail = new RegExp(regu);

	var isFomatMail = new RegExp('^[_\.0-9a-z-]+@([0-9a-z][0-9a-z-]+\.)+[a-z]{2,3}$','');
	
	if (email.search(isFomatMail) >= 0) {
		return true;
	}
	return false;
}
// JavaScript library ，用于校验表单数据
// 简要目录：
// 1.是否为空或全是空字符 
// 2.字符串是否只包含0到9的数字
// 3.是否整数(允许带正负号)
// 4.是否数字
// 5.小数（m,n），忽略小数首尾的0，不允许正负号
// 6.字符串是否全是字母
// 7.字符串是否全是字母和数字
// 8.日期
// 9.居民身份证号码
// 10.电话号码
// 11.邮政编码
// 12.是否合法的email
// 13.一列checkbox中是否至少有一个checked 
// 14.一列checkbox中是否只有一个checked
// 15.一列checkbox中是否全部checked 
// 16.多个下拉框不能一样
// 17.部分选中一列checkbox
// 其它未编号的function为辅助，也可以单独使用

// 注意：除固定格式的输入项如日期，邮政编码和居民身份证号码外，其余均未判断长度和是否为空。

var digits = "0123456789";
var lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
var uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
var whitespace = " \t\n\r";
var defaultEmptyOK = true


//是否 null 或者 空 或者全空格
function isEmpty(s)
{
   if ((s == null) || (s.length == 0) ) return true;
   
   for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }    

    return true;   	
}

//1.是否为空或全是空字符
function isWhitespace (s)

{   var i;

    if (isEmpty(s)) return true;

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }

    return true;
}

//从s里去掉包含在bag中的字符，s="abcd" bag="ace" return "bd"
function stripCharsInBag (s, bag)

{   var i;
    var returnString = "";

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }

    return returnString;
}
//从s里去掉不包含在bag中的字符 s="abcd" bag="ace" return "ac"
function stripCharsNotInBag (s, bag)

{   var i;
    var returnString = "";

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) != -1) returnString += c;
    }

    return returnString;
}
//从s里去掉空字符，s="ab c d " return "abcd"
function stripWhitespace (s)

{   return stripCharsInBag (s, whitespace)
}

//字符c是否在字符串s中
function charInString (c, s)
{   for (i = 0; i < s.length; i++)
    {   if (s.charAt(i) == c) return true;
    }
    return false
}

//去掉字符串前面的空字符
function stripInitialWhitespace (s)

{   var i = 0;

    while ((i < s.length) && charInString (s.charAt(i), whitespace))
       i++;

    return s.substring (i, s.length);
}
//字符是否为字母
function isLetter (c)
{   return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
}

//字符是否为0-9
function isDigit (c)
{   return ((c >= "0") && (c <= "9"))
}

//字符是否为字母或数字
function isLetterOrDigit (c)
{   return (isLetter(c) || isDigit(c))
}
//字符是否为数字或"-"
function isTel(c)
{   return (((c >= "0") && (c <= "9"))|| (c=="-"))
}

//2.字符串是否只包含0到9的数字
function isInteger (s)

{   var i;

    if (isEmpty(s))
       if (isInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isInteger.arguments[1] == true);


    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (!isDigit(c)) return false;
    }

    return true;
}


//3.是否整数(允许带正负号)
function isSignedInteger (s)

{   if (isEmpty(s))
       if (isSignedInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedInteger.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedInteger.arguments.length > 1)
            secondArg = isSignedInteger.arguments[1];

        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") )
           startPos = 1;
        return (isInteger(s.substring(startPos, s.length), secondArg))
    }
}

//是否为大于零的整数（允许正负号）
function isPositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isPositiveInteger.arguments.length > 1)
        secondArg = isPositiveInteger.arguments[1];

    return (isSignedInteger(s, secondArg)&& ( (isEmpty(s) && secondArg)  || (parseInt (s,10) > 0) ) );
}

//是否为大于等于零的整数（允许正负号）
function isNonnegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonnegativeInteger.arguments.length > 1)
        secondArg = isNonnegativeInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) >= 0) ) );
}

//是否为小于零的整数（允许正负号）
function isNegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNegativeInteger.arguments.length > 1)
        secondArg = isNegativeInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) < 0) ) );
}

//是否为小于等于零的整数（允许正负号）
function isNonpositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonpositiveInteger.arguments.length > 1)
        secondArg = isNonpositiveInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) <= 0) ) );
}


//4.是否数字
function isFloat (s)
{   
    var i;
    var seenDecimalPoint = false;
    if (isEmpty(s))
       if (isFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isFloat.arguments[1] == true);
    if (s == ".") return false;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if ((c == ".") && !seenDecimalPoint) seenDecimalPoint = true;
        else if (!isDigit(c)) return false;
    }
    return true;
}

//是否数字（允许正负号）
function isSignedFloat (s)

{   if (isEmpty(s))
       if (isSignedFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedFloat.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedFloat.arguments.length > 1)
            secondArg = isSignedFloat.arguments[1];

        // skip leading + or -
        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") )
           startPos = 1;
        return (isFloat(s.substring(startPos, s.length), secondArg))
    }
}

//5.小数（m,n），忽略小数首尾的0，不允许正负号
function isDecimal(s,m,n){
	if(!isFloat(s)) return false;
	if(String(parseInt(s,10)).length > m-n) return false;
	var ss = String(parseFloat(s));
	if(ss.indexOf(".")>=0 && ss.substring( ss.indexOf(".") + 1, ss.length).length > n ) return false;
	return true;
}


//6.字符串是否全是字母
function isAlphabetic (s)
{   var i;

    if (isEmpty(s))
       if (isAlphabetic.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphabetic.arguments[1] == true);
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is letter.
        var c = s.charAt(i);
        if (!isLetter(c))
        return false;
    }
    // All characters are letters.
    return true;
}
//7.字符串是否全是字母和数字
function isAlphanumeric (s)

{   var i;

    if (isEmpty(s))
       if (isAlphanumeric.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphanumeric.arguments[1] == true);

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (! (isLetter(c) || isDigit(c) ) )
        return false;
    }

    return true;
}


//8.日期，format 可设定为 "yyyy-MM-dd"（注意大小写），分隔符可变
function isValidateDate(str) {
	
	if (isEmpty(str)) return true;
	
	if (str.indexOf("-") != -1) {
		return toValidateDate(str, "yyyy-MM-dd");
	} else if(str.indexOf("/") != -1) {
		return toValidateDate(str, "yyyy/MM/dd");
	} else if(str.indexOf(".") != -1) {
		return toValidateDate(str, "yyyy.MM.dd");
	} else {
		return toValidateDate(str, "yyyyMMdd");
	}
	
	return false;
}

function toValidateDate(str,format) {
	if(str.length != format.length) return false;

	var year = 2000;
	var month = 1;
	var day = 1;
	var hour = 0;
	var minute = 0;
	var second = 0;

	if(format.indexOf("yyyy") != -1) {
		if(isNaNEx(year = SearchEx(str,format,"yyyy"))) return false;
		format = format.replace(/yyyy/,year);
	}

	if(format.indexOf("MM") != -1) {
		if(isNaNEx(month = SearchEx(str,format,"MM"))) return false;
		format = format.replace(/MM/,month);
	}

	if(format.indexOf("dd") != -1) {
		if(isNaNEx(day = SearchEx(str,format,"dd"))) return false;
		format = format.replace(/dd/,day);
	}

	if(format.indexOf("HH") != -1) {
		if(isNaNEx(hour = SearchEx(str,format,"HH"))) return false;
		if(parseInt(hour,10) < 0 || parseInt(hour,10) > 23) return false;
		format = format.replace(/HH/,hour);
	}

	if(format.indexOf("mm") != -1) {
		if(isNaNEx(minute = SearchEx(str,format,"mm"))) return false;
		if(parseInt(minute,10) < 0 || parseInt(minute,10) > 59) return false;
		format = format.replace(/mm/,minute);
	}

	if(format.indexOf("ss") != -1) {
		if(isNaNEx(second = SearchEx(str,format,"ss"))) return false;
		if(parseInt(second,10) < 0 || parseInt(second,10) > 59) return false;
		format = format.replace(/ss/,second);
	}

	if(format != str) return false;

	return isValidDate(year,month,day);
}
//日期
function isNaNEx(str) {
	if(str == "") return true;
	if(isNaN(str)) return true;
	if(str.indexOf(".") != -1) return true;
	return false;
}
//日期
function SearchEx(source,pattern,str) {
	var index = pattern.indexOf(str);
	if(index == -1) return "error";
	return source.substring(index,index + str.length);
}
//日期
function isValidDate(year,month,day) {
	month = parseInt(month,10);
	day = parseInt(day,10);

	if(month < 1 || month > 12) return false;
	if(day < 1 || day > 31) return false;
	if((month == 4 || month == 6 || month == 9 || month == 11) && (day == 31)) return false;
	if (month == 2) {
		var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
		if (day > 29 || (day == 29 && !leap)) return false;
	}
	return true;
}
//日期
function getCurrDate() {
	var str = "";
	var date = new Date();
	str = str + date.getYear();
	if (date.getMonth() < 9) {
		str = str + "-" + "0" + (date.getMonth() + 1);
	} else {
		str = str + "-" + (date.getMonth() + 1);
	}
	if (date.getDate() < 10) {
		str = str + "-" + "0" + (date.getDate());
	} else {
		str = str + "-" + (date.getDate());
	}
	
	return str;
}


//9.居民身份证号码(15或18位，最后一位数字或字母，其余数字，生日合法)
function isResidentID (s){
	if(isWhitespace(s)) return true;//如果为空返回true
	if(s.length!=15 && s.length!=18) return false;
	if(!isAlphanumeric(s)) return false;
	var birthday;
	if(s.length==18){
		if( !isInteger(s.substring(0,s.length-1)) ) return false;
		birthday = s.substring(6,14);
	}
	if(s.length==15){
		if( !isInteger(s) ) return false;
		birthday = "19"+s.substring(6,12);
	}
	if(!isValidDate(  birthday.substring(0,4), birthday.substring(4,6), birthday.substring(6,8)  )) return false;
	return true;
	
}

//字符串按格式重组(reformat("20021213","",4,"-",2,"-",2),会返回2002-12-13)
function reformat (s)

{   var arg;
    var sPos = 0;
    var resultString = "";

    for (var i = 1; i < reformat.arguments.length; i++) {
       arg = reformat.arguments[i];
       if (i % 2 == 1) resultString += arg;
       else {
           resultString += s.substring(sPos, sPos + arg);
           sPos += arg;
       }
    }
    return resultString;
}

//10.电话号码( 数字和+,-,() )
function isPhoneNumber(s){
	if(stripCharsInBag(s,"0123456789-()+")!="") return false;
	return true;
}

//11.邮政编码
function isPostCode(s){
	if (isEmpty(s)) return true;
	return(isInteger(s) && s.length==6);
}

//是否只包含合法的email字符
function isvalidEmailChar (s)
{   var i;

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (! (isLetter(c) || isDigit(c) || (c=='@') || (c=='.') || (c=='_') || (c=='-') || (c=='+')) ) {
       	return false;
		}
    }

    return true;
}

//12.是否合法的email





///18.个人公积金帐号
function checkPersonAccFundAcc(s){
	if(isWhitespace(s)) return true;//如果为空返回true
	if(s.length!=12) return false;
	if(!isAlphanumeric(s)) return false;
	if(s.substring(9,12)!="205") return false;
	var sum = 0;
	for(var i=0;i<8;i++){
		var number = Number(s.substring(7-i,8-i));
		sum += number*(i+2);
		//log("a"+Number(8-i)+"="+number);
	}
	//log("sum="+sum);
	var r = sum % 11;
	var s9 = Number(s.substring(8,9));
	//log("a9="+s9 +"//  r="+ r);
	if(r != s9) return false;
	return true;
}
///19.单位公积金帐号
function checkCorpAccFundAcc(s){
	if(isWhitespace(s)) return true;//如果为空返回true
	if(s.length!=12) return false;
	if(!isAlphanumeric(s)) return false;
	if(s.substring(9,12)!="205") return false;
	var sum = 0;
	for(var i=0;i<8;i++){
		var number = Number(s.substring(i,i+1));
		sum += number*(i+2);
		//log("a"+Number(i+1)+"="+number);
	}
	//log("sum="+sum);
	var r = sum % 11;
	var s9 = Number(s.substring(8,9));
	//log("a9="+s9 +"//  r="+ r);
	if(r != s9) return false;
	return true;
}
///20.划款/还款帐号
function checkAccount(s){
	if(isWhitespace(s)) return true;//如果为空返回true
	if(s.length!=17) return false;
	if(!isAlphanumeric(s)) return false;
	var s6 = s.substring(0,6);
	var s89 = s.substring(7,9);
	var s910 = s.substring(8,10);
	if(s6=="601428"||s6=="405512"){
		if(s910=="11"){
			return true;
		}else{
			return false;
		}
	}else if(s6=="622258"||s6=="622259"){
		if(s89=="11"){
			return true;
		}else{
			return false;
		}		
	}
	return false;
}

//////信用卡号码（注意，原程序为美国使用，未修改）

function isCreditCard(st) {
  // Encoding only works on cards with less than 19 digits
  if (st.length > 19)
    return (false);

  sum = 0; mul = 1; l = st.length;
  for (i = 0; i < l; i++) {
    digit = st.substring(l-i-1,l-i);
    tproduct = parseInt(digit ,10)*mul;
    if (tproduct >= 10)
      sum += (tproduct % 10) + 1;
    else
      sum += tproduct;
    if (mul == 1)
      mul++;
    else
      mul--;
  }

  if ((sum % 10) == 0)
    return (true);
  else
    return (false);

}


function isVisa(cc)
{
  if (((cc.length == 16) || (cc.length == 13)) &&
      (cc.substring(0,1) == 4))
    return isCreditCard(cc);
  return false;
}


function isMasterCard(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 16) && (firstdig == 5) &&
      ((seconddig >= 1) && (seconddig <= 5)))
    return isCreditCard(cc);
  return false;

}

function isAmericanExpress(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 15) && (firstdig == 3) &&
      ((seconddig == 4) || (seconddig == 7)))
    return isCreditCard(cc);
  return false;

}

function isDinersClub(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 14) && (firstdig == 3) &&
      ((seconddig == 0) || (seconddig == 6) || (seconddig == 8)))
    return isCreditCard(cc);
  return false;
}

function isCarteBlanche(cc)
{
  return isDinersClub(cc);
}


function isDiscover(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) && (first4digs == "6011"))
    return isCreditCard(cc);
  return false;

}


function isEnRoute(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 15) &&
      ((first4digs == "2014") ||
       (first4digs == "2149")))
    return isCreditCard(cc);
  return false;
}

function isJCB(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) &&
      ((first4digs == "3088") ||
       (first4digs == "3096") ||
       (first4digs == "3112") ||
       (first4digs == "3158") ||
       (first4digs == "3337") ||
       (first4digs == "3528")))
    return isCreditCard(cc);
  return false;

}

function isAnyCard(cc)
{
  if (!isCreditCard(cc))
    return false;
  if (!isMasterCard(cc) && !isVisa(cc) && !isAmericanExpress(cc) && !isDinersClub(cc) &&
      !isDiscover(cc) && !isEnRoute(cc) && !isJCB(cc)) {
    return false;
  }
  return true;

}

function isCardMatch (cardType, cardNumber)
{

	cardType = cardType.toUpperCase();
	var doesMatch = true;

	if ((cardType == "VISA") && (!isVisa(cardNumber)))
		doesMatch = false;
	if ((cardType == "MASTERCARD") && (!isMasterCard(cardNumber)))
		doesMatch = false;
	if ( ( (cardType == "AMERICANEXPRESS") || (cardType == "AMEX") )
                && (!isAmericanExpress(cardNumber))) doesMatch = false;
	if ((cardType == "DISCOVER") && (!isDiscover(cardNumber)))
		doesMatch = false;
	if ((cardType == "JCB") && (!isJCB(cardNumber)))
		doesMatch = false;
	if ((cardType == "DINERS") && (!isDinersClub(cardNumber)))
		doesMatch = false;
	if ((cardType == "CARTEBLANCHE") && (!isCarteBlanche(cardNumber)))
		doesMatch = false;
	if ((cardType == "ENROUTE") && (!isEnRoute(cardNumber)))
		doesMatch = false;
	return doesMatch;

}

function IsCC (st) {
    return isCreditCard(st);
}

function IsVisa (cc)  {
  return isVisa(cc);
}

function IsVISA (cc)  {
  return isVisa(cc);
}

function IsMasterCard (cc)  {
  return isMasterCard(cc);
}

function IsMastercard (cc)  {
  return isMasterCard(cc);
}

function IsMC (cc)  {
  return isMasterCard(cc);
}

function IsAmericanExpress (cc)  {
  return isAmericanExpress(cc);
}

function IsAmEx (cc)  {
  return isAmericanExpress(cc);
}

function IsDinersClub (cc)  {
  return isDinersClub(cc);
}

function IsDC (cc)  {
  return isDinersClub(cc);
}

function IsDiners (cc)  {
  return isDinersClub(cc);
}

function IsCarteBlanche (cc)  {
  return isCarteBlanche(cc);
}

function IsCB (cc)  {
  return isCarteBlanche(cc);
}

function IsDiscover (cc)  {
  return isDiscover(cc);
}

function IsEnRoute (cc)  {
  return isEnRoute(cc);
}

function IsenRoute (cc)  {
  return isEnRoute(cc);
}

function IsJCB (cc)  {
  return isJCB(cc);
}

function IsAnyCard(cc)  {
  return isAnyCard(cc);
}

function IsCardMatch (cardType, cardNumber)  {
  return isCardMatch (cardType, cardNumber);
}

//四舍五入
//myFloat是要进行四舍五入的数字，mfNumber代表要取舍的位数
function floatRound(myFloat,mfNumber)
{
  var cutNumber = Math.pow(10,mfNumber-1);
  return Math.round(myFloat * cutNumber)/cutNumber;
}

function cmycurd(num){  //转成人民币大写金额形式
  var str1 = '零壹贰叁肆伍陆柒捌玖';  //0-9所对应的汉字
  var str2 = '万仟佰拾亿仟佰拾万仟佰拾元角分'; //数字位所对应的汉字
  var str3;    //从原num值中取出的值
  var str4;    //数字的字符串形式
  var str5 = '';  //人民币大写金额形式
  var i;    //循环变量
  var j;    //num的值乘以100的字符串长度
  var ch1;    //数字的汉语读法
  var ch2;    //数字位的汉字读法
  var nzero = 0;  //用来计算连续的零值是几个
  
  num = Math.abs(num).toFixed(2);  //将num取绝对值并四舍五入取2位小数
  str4 = (num * 100).toFixed(0).toString();  //将num乘100并转换成字符串形式
  j = str4.length;      //找出最高位
  if (j > 15){return '溢出';}
  str2 = str2.substr(15-j);    //取出对应位数的str2的值。如：200.55,j为5所以str2=佰拾元角分
  
  //循环取出每一位需要转换的值
  for(i=0;i<j;i++){
    str3 = str4.substr(i,1);   //取出需转换的某一位的值
    if (i != (j-3) && i != (j-7) && i != (j-11) && i != (j-15)){    //当所取位数不为元、万、亿、万亿上的数字时
   if (str3 == '0'){
     ch1 = '';
     ch2 = '';
  nzero = nzero + 1;
   }
   else{
     if(str3 != '0' && nzero != 0){
       ch1 = '零' + str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
  }
  else{
    ch1 = str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
        }
   }
 }
 else{ //该位是万亿，亿，万，元位等关键位
      if (str3 != '0' && nzero != 0){
        ch1 = "零" + str1.substr(str3*1,1);
        ch2 = str2.substr(i,1);
        nzero = 0;
      }
      else{
     if (str3 != '0' && nzero == 0){
          ch1 = str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
  }
        else{
    if (str3 == '0' && nzero >= 3){
            ch1 = '';
            ch2 = '';
            nzero = nzero + 1;
       }
       else{
      if (j >= 11){
              ch1 = '';
              nzero = nzero + 1;
   }
   else{
     ch1 = '';
     ch2 = str2.substr(i,1);
     nzero = nzero + 1;
   }
          }
  }
   }
 }
    if (i == (j-11) || i == (j-3)){  //如果该位是亿位或元位，则必须写上
        ch2 = str2.substr(i,1);
    }
    str5 = str5 + ch1 + ch2;
    
    if (i == j-1 && str3 == '0' ){   //最后一位（分）为0时，加上“整”
      str5 = str5 + '整';
    }
  }
  if (num == 0){
    str5 = '零元整';
  }
  
  return str5;
}

function Trim(str)
{
   if(str.charAt(0)==" ")
   {
      str =str.substring(1,str.length-1);
      str =Trim(str);
   }else if(str.charAt(str.length-1)==" ")
   {
      str =str.substring(0,str.length-1);
      str =Trim(str);
   }
   
   return str;
   
}
//////////////////