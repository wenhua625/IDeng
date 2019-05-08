// JavaScript Document
function openWindow(id,title,url,width,height,afterClose){
    var win = Ext.get(id);
    if (win) {
        win.close();
        return;
    }
    win = new top.Ext.Window({
        id:id,
        title:title,
        layout:'fit',
        width:width,
        height:height,
        closeAction:'close',
        collapsible:false,
        plain: false,
		modal: true,
        resizable: true,
        html : '<iframe id="'+id+'" name="'+id+'" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>',
		listeners:{
			     close:afterClose
			}

    });
    win.show();
 }

function openWindow1(id,title,url,width,height,afterClose){
    var win = Ext.get(id);
    if (win) {
        win.close();
        return;
    }
    win = new top.Ext.Window({
        id:id,
        title:title,
        layout:'fit',
        width:width,
        height:height,
        closeAction:'close',
        collapsible:false,
        plain: false,
		modal: true,
        resizable: true,
        html : '<iframe id="frame'+ id +'" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>',
		listeners:{
			     close:afterClose
			}

    });
    win.show();
 }
 


 function closewin(id){
       var win = parent.Ext.getCmp(id);
       if (win) { win.close();}
    }
 

 function importf(obj) {//导入
			    var wb;//读取完成的数据
            var rABS = false; //是否将文件读取为二进制字符串
                if(!obj.files) {
                    return;
                }
                var f = obj.files[0];
                var reader = new FileReader();
                reader.onload = function(e) {
                    var data = e.target.result;
                    if(rABS) {
                        wb = XLSX.read(btoa(fixdata(data)), {//手动转化
                            type: 'base64'
                        });
                    } else {
                        wb = XLSX.read(data, {
                            type: 'binary'
                        });
                    }
                    //wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
                    //wb.Sheets[Sheet名]获取第一个Sheet的数据
                    var  s_json= JSON.stringify( XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]) );
					var s_json = '{"root":'+s_json+'}';
					//var json_excel=JSON.parse(s_json);
					//alert("json:"+s_json);
					uploadreload(s_json);
					//return s_json;
                };
                if(rABS) {
                    reader.readAsArrayBuffer(f);
                } else {
                    reader.readAsBinaryString(f);
                }
            }


            function fixdata(data) { //文件流转BinaryString
                var o = "",
                    l = 0,
                    w = 10240;
                for(; l < data.byteLength / w; ++l) o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w, l * w + w)));
                o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w)));
                return o;
            }