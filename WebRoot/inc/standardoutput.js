
function errAlert(errContent,errReason,errSolve){
	var errAlertString="";
	if(errContent!=""&&errContent!="undefined"){
		errAlertString="错误：\n"+errContent+"\n";
		
		if(errReason!=null&&errReason!=""&&errReason!="undefined")
			errAlertString+="\n错误原因：\n"+errReason;

		if(errSolve!=null&&errSolve!=""&&errSolve!="undefined")
			errAlertString+="\n\n解决方法：\n"+errSolve;

		errAlertString+="\n";
		
		alert(errAlertString);
		
	}else{
		alert("调试期间错误 !\n\n错误内容为空 !");
	}
}


// 警告信息。
function warnAlert(warnContent){
	var warnAlertString="";
	
	if(warnContent!=""){
		warnAlertString="警告：\n"+warnContent+"\n";
		
		alert(warnAlertString);
		
	}else{
		alert("警告内容为空!");
	}
}
//下面两个方法用于log4js，按F1键显示/隐藏log信息，直接调用log(v)就可以用了。
function log(v)
    {
        var s = "<li><font color=blue>"+new Date()+"</font> "+v+"</li>";
        logPanel_log.innerHTML+=s;
    }

window.onhelp = function() {
      logPanel.style.display=logPanel.style.display=='none'?"block":"none";
      return false;
}
document.write("<div id=logPanel style='display:none'>"+
    "<button onclick=\"logPanel_log.innerHTML='';\">clear</button>&nbsp;"+
    "<button onclick=\"logPanel_log.innerHTML+='<hr>';\">HR</button>"+
    "<div id='logPanel_log'></div></div>");


