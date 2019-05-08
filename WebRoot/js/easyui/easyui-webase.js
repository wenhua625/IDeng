 function saveData(dg,kjm)
	   {
	      var rows = dg.datagrid('getRows');  
                for ( var i = 0; i < rows.length; i++) {  
                   dg.datagrid('endEdit', i);  
           }
	      
						if (dg.datagrid('getChanges').length) {  
                            var inserted = dg.datagrid('getChanges', "inserted");  
                            
                            var deleted = dg.datagrid('getChanges', "deleted");  
                            var updated = dg.datagrid('getChanges', "updated");
                              
                            var effectRow = new Object();  
                            if (inserted.length) {  
                               effectRow["inserted"] = encodeURI(JSON.stringify(inserted));
                               
                            }  
                            if (deleted.length) {  
                               effectRow["deleted"] = encodeURI(JSON.stringify(deleted)); 
                              
                            }
                            if (updated.length) {  
                               effectRow["updated"] = encodeURI(JSON.stringify(updated));
                            }
                            $.post("work?proname="+kjm, effectRow, function(rsp) {
                               
                               if(rsp == "ok"){  
                                 alert("保存成功！");  
                                 dg.datagrid('acceptChanges');  
                               }else{
                                 alert(rsp);  
                               }  
                            });  

                     } 
	   }				
		
		function deleteData(dg)
		{
		    var row = dg.datagrid('getSelected');
					    if(row == null)
					    {
					      alert("请选择要删除的行!");
					      return false;  
					    }
					     
                        if (row) {
                         if(!confirm("你确认要删除吗？")) return false;  
                         var rowIndex = dg.datagrid('getRowIndex', row);  
                         dg.datagrid('deleteRow', rowIndex);  
                        }else{
                           alert("请选择要删除的行!");
                        } 
		}

		function openWin(win)
		{
		    win.window('open');
		}
		
		function closeWin(win)
		{
		    win.window('close');
		}
		
function padLeft(det,lenght,str){ 
  if(str.length >= lenght) 
    return str; 
   else 
  return padLeft(det +str,lenght); 
}  
 function execUpdate(kjm,action)
 {
     if (typeof window['DWRUtil'] == 'undefined') window.DWRUtil = dwr.util;
     var formMap = DWRUtil.getValues("form1");
     var ret=false;
     DwrComm.parseIN(kjm,formMap,action,{callback:function(data){
                     if (data != 'ok'){
                         alert(data);
                         
                     }else
                     {
                        ret= true;
                     }
      },async:false});
      return ret;
 }