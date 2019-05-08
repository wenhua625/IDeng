
    function CheckCardType(src,dest)
	{
	  var cardType;
	  var cardNo = src.value;
	  if(cardNo == ""){return false}
	  if(cardNo.substring(0,1) == "4")
	  {	  
	     cardType = "VISA";
	  }else if(cardNo.substring(0,1) == "5")
	  {
	     cardType = "MASTERCARD";
	  }else if((cardNo.substring(0, 2)) == "34"|| (cardNo.substring(0, 2)) == "37")
	  {
	     cardType = "AE";
	  }else if((cardNo.substring(0, 2)) == "35")
	  {
	     cardType = "JCB";
	  }else if((cardNo.substring(0, 2)) == "30"|| (cardNo.substring(0, 2)) == "36"|| (cardNo.substring(0, 2)) == "38")
	  {
	     cardType = "DINER";
	  }else
	  {
		 alert("对不起，您输入的卡号无效：" + cardNo);
		 src.focus;
		 src.select();
	  }	 
	  
	  dest.value = cardType;	  
	}
	
	//打开新窗口 500*500
	function NewWin(str)
	{
		window.open(str,'','height=500,width=500,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes',null);
	}
	//打开模式窗口
    function NewWinMD(path,title)
	{
		 window.showModalDialog(path,title,"status:no;  dialogHeight: 550px; dialogWidth: 700px;");
	}
	function NewWinCustMD(path,title,h,w)
	{
		 var a=window.showModalDialog(path+'&tmp1='+Math.random(),title,"status:no; fullscreen:3;help:0; dialogHeight: "+h+"px; dialogWidth: "+w+"px;");
		 return a;
	}
	
	
	//自定义打开新窗口 500*500
	function NewWinCustomer(str,h,w)
	{
		window.open(str,'','height='+h+',width='+w+',status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes',null);
	}
	
	//回写到商户信息中
	function WriteBack(merid,mername)
	{
		window.opener.form.merchantID.value = merid;
		window.opener.form.merchantName.value = mername;
		window.close();
	}
	//判断数量与金额
	 function checkInput(src)
    {
	  var num=src.value;
      if(num==null || num.length==0)
	  {
	      alert("必须输入正确数字!");
		  src.focus();
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			src.focus();
			return false;
		}
	  if(parseInt(num)<=0)
		{
		    alert("数据必须大于0!");
			src.focus();
			return false;
		}
	  return true;
	  
	}
	//判断数量与金额
	 function checkInput2(src)
    {
	  var num=src.value;
      if(num==null || num.length==0)
	  {
	      alert("必须输入正确数字!");
		  src.focus();
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			src.focus();
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("数据必须大于0!");
			src.focus();
			return false;
		}
	  return true;
	  
	}
	//判断数量与金额
	 function checkInput3(src)
    {
	  var num=src.value;
      
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			src.focus();
			return false;
		}
	  
	  return true;
	  
	}
	
	//处理调拔与入库
	 function checkInput0(num)
    {
	  
      if(num==null || num.length==0)
	  {
	      alert("必须输入正确数字!");
		  
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("数据必须大于0!");
			
			return false;
		}
	  return true;
	  
	}
	//判断数量与金额
	 function checkInput1(num)
    {
	  
      
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			
			return false;
		}
	  
	  return true;
	  
	}
	

	 function checkInput40(src)
    {
	  var num=src;
      if(num==null || num.length==0)
	  {
	      alert("必须输入正确数字!");
		  
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("请输入正确数字!");
			
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("数据必须大于0!");
			
			return false;
		}
	  return true;
	  
	}
	
