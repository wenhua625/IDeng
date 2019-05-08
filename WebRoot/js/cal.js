function fPopUpCalendarDlg() {
	if( arguments.length == 0 ){
		return;
	}

	var ctl = arguments[0];

    showx = event.screenX - event.offsetX - 4 ;// + deltaX;
    showy = event.screenY - event.offsetY + 18;// + deltaY;

	var qry = "";
	if(arguments.length>1){
		qry += arguments[1];
	}
	
    retval = window.showModalDialog("/js/calendar.htm", "", "dialogWidth:220px; dialogHeight:175px; dialogLeft:"+showx+"px; dialogTop:"+showy+"px; status:no; directories:yes; help:no;"  );
    if( retval != null ){
        ctl.value = retval;
    }else{
    }
}


function openRefWin(url)
{
    var windowx = 800;
    var windowy = 600;
    var l= (screen.availWidth-windowx)/2;
    var t= (screen.availHeight-windowy)/2;
    var win=window.open(url,"",'scrollbars=yes,resizable=no,status=no,toolbar=no,location=no,menubar=no,directories=no,top='+t+',left='+l+',width='+windowx+',height='+windowy+'');
    //win.resizeTo(windowx,windowy);
    //win.moveTo(l,t);
    //win.focus();
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}


function checkMZenKaKu(c) {
	var tmp = escape(c);
	if (tmp.length == 1) {
		return false;
	}
	return (tmp.charAt(1) == 'u');
}

function checkEngNum(str) {
	if( str == null || str == "" ){
		return true;
	}
	var c = new RegExp();
	c = /^[\d|a-zA-Z|\s|\-]+$/;
	if (c.test(str))
		return true;
	else
		return false;
}

function checkLeapYear(year) {
	if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
		return true;
	}
	return false;
}

function removeChar(str, c) {
	if( str == null || str == "" )
		return str;
	var i = str.indexOf(c);
	while (i >= 0) {
		str = str.substring(0, i) + str.substring(i + 1, str.length);
		i = str.indexOf(c);
	}
	return str;
}

function convDate(sDate, sSep) {
	var pos = 0;
	var str = sDate;
	var len = str.length;
	if ((len < 8) || (len > 10)) {
		return str;
	}
	else if (str.indexOf(sSep) == 4) {
		pos = str.indexOf(sSep, 5);
		if (pos == 6) {
			if (len == 8) {
				return str.substring(0, 4) + "0" + str.substring(5, 6) + "0" + str.substring(7, 8);
			}
			else {
				return str.substring(0, 4) + "0" + str.substring(5, 6) + str.substring(7, 9);
			}
		}
		else if (pos == 7) {
			if (len == 9) {
				return str.substring(0, 4) + str.substring(5, 7) + "0" + str.substring(8, 9);
			}
			else {
				return str.substring(0, 4) + str.substring(5, 7) + str.substring(8, 10);
			}
		}
		else {
			return str;
		}
	}
	else {
		return str;
	}
}

function checkDate(str) {
	str = convDate(str, "/");
	if ((str.length != 8) || !checkNumber(str))
		return false;
	var year  = str.substring(0, 4);
	var month = str.substring(4, 6);
	var day   = str.substring(6, 8);
	dayOfMonth = new Array(31,29,31,30,31,30,31,31,30,31,30,31);
	if ((month < 1) || (month > 12))
		return false;
	if ((day < 1) || (day > dayOfMonth[month - 1]))
		return false;
	if (!checkLeapYear(year) && (month == 2) && (day == 29))
		return false;
	return true;
}

function checkEngWord(str){
    var i;
    var len = str.length;
    var chkStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    if (len == 1) {
		if (chkStr.indexOf(str.charAt(i)) < 0) {
		    return false;
		}
	} else {
		if ((chkStr.indexOf(str.charAt(0)) < 0) ) {
		    return false;
		}
	    for (i = 1; i < len; i++) {
			if (chkStr.indexOf(str.charAt(i)) < 0) {
			    return false;
			}
		}
	}
    return true;
}



function checkFLTNUM(str) {
   	var len = str.length;
   	var firstChar="-";
   	if(str.charAt(0) == firstChar&len>1){
   		str = str.substring(1,len);
   	}
   	if(!checkNumber(str)){
   		return false;
   	}
   	if(str<0||str>99){
  		return false;
	}

   return true;
}
function checkNumber(str) {
    	var i;
    	var len = str.length;
    	var chkStr = "1234567890";
	for (i = 0; i < len; i++) {
		if (chkStr.indexOf(str.charAt(i)) < 0) {
			return false;
		}
	}
    	return true;
}
function checkDoubleNumber(str) {
	var i;
	var len = str.length;
	if(len>9 || len<1){
		return false;
	}	
	var chkStr = "1234567890.";
	var dot="."
	for (i = 0; i < len; i++) {
		if (chkStr.indexOf(str.charAt(i)) < 0) {
			return false;
		}
	}
	
    if((str.substring(0,1)==dot) || (str.substring(len-1,len)==dot)){
    	return false;
	}	
    return true;
}

function getAppVer() {
	var sVer = navigator.appVersion;
	var nVer = sVer.indexOf("MSIE");
	var appVer = "";
	if (nVer > 0) {
		appVer = "M" + sVer.substring(nVer + 5, nVer + 9);
	}
	else {
		appVer = "N" + sVer.substring(0, 4);
	}
	if (appVer.charAt(4) == " ") {
		appVer = appVer.substring(0, 4) + "0";
	}
	return appVer;
}

var appVer = getAppVer();

function checkSpaceChar(c) {
	return ((c == ' ') || (c == '\t') || (c == '　'));
}

function trim(str) {
	var len = str.length;
	var begin = 0;
	var end = len -1;
	var chkOne = true;
	var chkTwo = true;
   	if ((appVer.charAt(0) == 'M') || (appVer > "N4.03")) {
   		for (begin; (begin < len) && checkSpaceChar(str.charAt(begin)); begin++);
		for (end; (end >= 0) && checkSpaceChar(str.charAt(end)); end--);
	}
	else if (appVer.charAt(0) == 'N') {
		while (chkOne || chkTwo) {
			if (begin < len) {
				if (checkSpaceChar(str.charAt(begin))) {
					begin++;
					chkOne = true;
				}
				else {
					chkOne = false;
				}
			}
			else {
				chkOne = false;
			}
			if (begin < len - 1) {
				if (str.substring(begin, begin + 2) == "　") {
					begin += 2;
					chkTwo = true;
				}
				else {
					chkTwo = false;
				}
			}
			else {
				chkTwo = false;
			}
		}
		chkOne = true;
		chkTwo = true;
		while (chkOne || chkTwo) {
			if (end > -1) {
				if (checkSpaceChar(str.charAt(end))) {
					end--;
					chkOne = true;
				}
				else {
					chkOne = false;
				}
			}
			else {
				chkOne = false;
			}
			if (end > 0) {
				if (str.substring(end - 1, end + 1) == "　") {
					end -= 2;
					chkTwo = true;
				}
				else {
					chkTwo = false;
				}
			}
			else {
				chkTwo = false;
			}
		}
	}
	if (begin > end) {
		return "";
	}
	return str.substring(begin,end + 1);
}

function formatMsg(){
	if(arguments.length == 0){
		return "メッセージIDがありません！";
	}

	var id = arguments[0];
	var cont="エラーがあります。(ID="+ id + ")";
	for(loop=0;loop<MsgIdList.length;loop++){
		if(MsgIdList[loop]==id){
			cont=MsgContList[loop];
			break;
		}
	}

	if(arguments.length >1 ){
		for(idx=1;idx<arguments.length;idx++){
			var param = arguments[idx];
			re = new RegExp("%"+idx,"i");
			cont=cont.replace(re,param);
		}
	}
	return cont;

}


function getLength(sCheck) {
   	if ((appVer.charAt(0) == 'M') || (appVer > "N4.05")) {
    	var n = 0;
    	var str = sCheck;
    	var len = str.length;
	    for (var i = 0; i < len; i++) {
       		n += checkMZenKaKu(str.charAt(i)) ? 2 : 1;
       	}
	  	return n;
   	}
   	else if (appVer.charAt(0) == 'N') {
	  	return sCheck.length;
    }
}

function checkFull(sCheck) {
    var i = 0;
    var str = sCheck;
    str = toHankaku(str);
    var len = str.length;
   	if ((appVer.charAt(0) == 'M') || (appVer > "N4.05")) {
	    for (i = 0; i < len; i++) {
       		if (!checkMZenKaKu(str.charAt(i))) {
       			return false;
       		}
    	}
   	} else if (appVer.charAt(0) == 'N') {
   		if ((len % 2) == 1) {
   			return false;
   		}
	    for (i = 0; i < len / 2; i++) {
       		if (!checkNZenKaKu(str.charAt(i * 2))) {
       			return false;
       		}
       	}
    }
  	return true;
}




function checkPhone(str) {
	var i = str.indexOf("--");
	var len = str.length;
	if (i >= 0) {
		return false;
	}
	i = str.indexOf("-");
	if ((i == 0) || (i == len - 1)) {
		return false;
	}
	else if (i > 0) {
		i = str.lastIndexOf("-");
       	if (i == len - 1) {
              	return false;
		}
		str = removeChar(str, "-");
	}
	//if (!checkNumberString(str)) {
	if (!checkNumber(str)) {
		return false;
	}
	else {
		return true;
	}
}


function checkMaxlength(obj, lenErr){
	var itemLength = getLength(obj.value);
    var maxLength;

	if (obj.type == "textarea"){
		if(typeof(obj.maxLength)!='undefined'){
			maxLength = obj.maxLength;
		}else{
    		maxLength = obj.cols * obj.rows;
    	}
    }else{
    	maxLength = obj.maxLength;
    }
	if ( itemLength > maxLength){
		if (obj.toZenkaku == null) {
			lenErr += formatMsg("COM_W_LengthEnough", obj.msg, maxLength) + "\n";
		} else {
			lenErr += formatMsg("COM_W_LengthEnough1", obj.msg, (parseInt(maxLength)/2)) + "\n";
		}
	}

	return lenErr;
}

function checkAll(errMsg) {

	var objForm;

	if (arguments.length == 2) {
		objForm = arguments[1].elements;
	} else {
		objForm = document.forms(0).elements;
	}

    if(errMsg == null)
        errMsg = "";

    var lenErr = "";
    var mustErr = "";
    var dateErr = "";
    var numberErr = "";
    var numengErr = "";
    var engwordErr = "";
    var zenkakuErr = "";
    var fullkanaErr = "";
    var zipErr = "";
    var phoneErr="";
    var faxErr = "";

    var kanaErr = "";
    var japaneseErr = "";
    var emailErr = "";
    //var zipErr = "";
    //var phoneErr = "";
    //var faxErr = "";
    //var dateErr = "";
    var decimalErr = "";
    var fltNumErr = "";

	for(i=0;i<objForm.length;i++){
		var obj = objForm(i);
		var type = obj.type;
        //check select box
		if ((obj.tagName == "SELECT") && (obj.required == "TRUE")) {
			if (obj.value == "") {
           		mustErr += "    " + obj.msg + "\n";
			}
			continue;
		}
        //check input field
		if ((type == "text") || (type == "textarea") || (type == "hidden")  || (type == "password") ) {

            // step 1: trim space
            obj.value = trim(obj.value);

            //step 2: must input check
		    if (obj.required == "TRUE"){
		    	if (obj.value == ""){
		    		mustErr += "    " + obj.msg + "\n";
		    		break;
		    	}
		    }

		    //step 3: check length
		    if (type != "hidden"){
		        lenErr = checkMaxlength(obj,lenErr);
		        if (lenErr !=""){
		            break;
		        }
            }

		    //step 4: Check the type of input data and whether it is a must-input field
		    if (obj.chkRule == "") {
		    	continue;
			} else if (obj.chkRule == "DATE") {
				if (obj.value.length > 0) {
					if (!checkDate(obj.value)) {
            			dateErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			} else if (obj.chkRule == "ENGNUM") {
				if (obj.value.length > 0) {
					if (!checkEngNum(obj.value)) {
            			numengErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			} else if (obj.chkRule == "NUMBER") {
				if (obj.value.length > 0) {
					if (!checkNumber(obj.value)) {
            			numberErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			} else if (obj.chkRule == "ENGWORD"){
			    if (obj.value.length > 0) {
					if (!checkEngWord(obj.value)) {
            			engwordErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			}else if (obj.chkRule == "FULLKANA"){
			    if (obj.value.length > 0) {
					if (!checkFuRiGaNa(obj.value)) {
            			fullkanaErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			}else if (obj.chkRule == "ZIPNUM"){
			    if (obj.value.length > 0) {
					if (!checkPhone(obj.value)) {
            			zipErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			}else if (obj.chkRule == "PHONENUM"){
                if (obj.value.length > 0) {
					if (!checkPhone(obj.value)) {
            			phoneErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			}else if (obj.chkRule == "FAXNUM"){
			    if (obj.value.length > 0) {
					if (!checkPhone(obj.value)) {
            			faxErr += "    " + obj.msg + "\n";
			    		break;
					}
				}

			}else if (obj.chkRule == "FLTNUM"){
			    if (obj.value.length > 0) {
					if (!checkFLTNUM(obj.value)) {
            			fltNumErr += "    " + obj.msg + "\n";
			    		break;
					}
				}
			}
		}
	}
	// deal with the error info
	if (lenErr != "") {
	    errMsg += lenErr;
	}

	if (mustErr != "") {
	    errMsg += formatMsg("COM_W_001", mustErr) + "\n";
	}

	if (numberErr != "") {
	    errMsg += formatMsg("COM_W_013", numberErr) + "\n";
	}

	if (numengErr != "") {
	    errMsg += formatMsg("COM_W_003", numengErr) + "\n";
	}

	if (engwordErr != "") {
	    errMsg += formatMsg("COM_W_003", engwordErr) + "\n";
	}

	if (dateErr != "") {
	    errMsg += formatMsg("COM_W_007", dateErr) + "\n";
	}

	if (zenkakuErr != "") {
	    errMsg += formatMsg("COM_W_004", zenkakuErr) + "\n";
	}

	if (fltNumErr != "") {
	    errMsg += formatMsg("COM_W_013", fltNumErr) + "\n";
	}


	//if (fullkanaErr != ""){
	//    errMsg += formatMsg("COM_W_OnlyAllCorner", zenkakuErr) + "\n";
	//}

	//if (zipErr != "") {
	//    errMsg += formatMsg("COM_W_ZipFormat", zipErr) + "\n";
	//}

	//if (phoneErr != "") {
	//    errMsg += formatMsg("COM_W_PhoneFormat", phoneErr) + "\n";
	//}

	//if (faxErr != "") {
	//    errMsg += formatMsg("COM_W_FaxFormat", faxErr) + "\n";
	//}

	return errMsg;
}
 function goback(linkurl)
	{
		window.location.href=linkurl;
	}
  //
//judge char in [s]
function isCharsInBag (s, bag)
{ 
var i;
// Search through string's characters one by one.
// If character is in bag, append to returnString.
for (i = 0; i < s.length; i++)
{ 
// Check that current character isn't whitespace.
var c = s.charAt(i);
if (bag.indexOf(c) == -1) return false;
}
return true;
}

function isEmpty(s)
{ 
return ((s == null)||(s.length == 0)); 
}

//used to judge number
function isNum(s)
{
if (isEmpty(s)){ 
alert("Sorry,input please!")
return false;
}
if(!isCharsInBag (s, "0123456789.")){
alert("Sorry,you must input numbers｣｡");
return false;
}
 if (s<0){
 alert("Sorry,what you input must big than 0!");
 return false;
 }
 if (s>24){
 alert("Sorry,what you input must less than 24!");
 return false;
 }
return true;
}
