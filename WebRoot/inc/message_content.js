var isClose=0;

function showHideMenu() {
	if (frmMenu.style.display=="") {
		frmMenu.style.display="none"
		switchPoint.innerText=4
		}
	else {
		frmMenu.style.display=""
		switchPoint.innerText=3
		}
}
function getContent() {
   var message = document.getElementById("messageTip");
	
	DwrComm.parseTip("000",{
		callback:function(list){
		         if (isClose == 1){
    				document.getElementById('eMeng').style.visibility='hidden';
    				}
   				else{
    				if(list.length > 0){
    					document.getElementById('eMeng').style.visibility='visible';
    				}else
      				document.getElementById('eMeng').style.visibility='hidden';
    			}
		         if(list.length>0){
		        	 message.innerHTML = list;
		         }
	    },
	    async:false
	});
	
	
} 


function RemoveRow()
{
var iRows = tripTable.rows.length;
for(var i=0;i<iRows;i++)
 {
    tripTable.deleteRow(i);
 }
}

function closeDiv()
{
    document.getElementById('eMeng').style.visibility='hidden';
    if(objTimer) window.clearInterval(objTimer)
    isClose=1;
    
}
function showDiv()
{
    document.getElementById('eMeng').style.visibility='visible';
    if(objTimer) window.clearInterval(objTimer)
    isClose=0;
    
}

