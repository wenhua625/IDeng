//该函数为模块菜单用
function switchoicqBar(itemID){
	var oicqItemNums=document.all.item("oicqItemNum").value;

	for (var j=1;j<=oicqItemNums;j++ ){

	   if(itemID == j){

		 document.all("menubar_"+j).style.display='block';
		 document.all("td_menubar_"+j).height="100%";
		}
	   else{
			document.all("menubar_"+j).style.display='none';
			document.all("td_menubar_"+j).height="1";
		}
    }
}

function makevisible(cur,which){
	if (which==0){
		cur.filters.alpha.opacity=100;
	}
	else{
		cur.filters.alpha.opacity=60;
	}
}

function MakeBtnTable(t,baseclassname,n){
if(n==1){
  t.className =baseclassname + "_mouseover";
}
else{
 if(n==2){
   t.className =baseclassname + "_mousedown";
 }
 else{
   t.className =baseclassname + "_mouseout";
 }
}


}
