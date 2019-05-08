var dataGbird_isKeyCtr = false;
$.extend($.fn.datagrid.methods, {
	statistics: function (jq) {
		var opt=$(jq).datagrid('options').columns;
		var rows = $(jq).datagrid("getRows");
		
		var footer = new Array();
		footer['sum'] = "";
		footer['avg'] = "";
		footer['max'] = "";
		footer['min'] = "";
		
		for(var i=0; i<opt[0].length; i++){
			if(opt[0][i].sum){
				footer['sum'] = footer['sum'] + sum(opt[0][i].field)+ ',';
			}
			if(opt[0][i].avg){
				footer['avg'] = footer['avg'] + avg(opt[0][i].field)+ ',';
			}
			if(opt[0][i].max){
				footer['max'] = footer['max'] + max(opt[0][i].field)+ ',';
			}
			if(opt[0][i].min){
				footer['min'] = footer['min'] + min(opt[0][i].field)+ ',';
			}
		}

		var footerObj = new Array();
		
		if(footer['sum'] != ""){
			var tmp = '{' + footer['sum'].substring(0,footer['sum'].length - 1) + "}";
			var obj = eval('(' + tmp + ')');
			if(obj[opt[0][0].field] == undefined){
				footer['sum'] += '"' + opt[0][0].field + '":"<b>Total:</b>"';
				obj = eval('({' + footer['sum'] + '})');
			}else{
				obj[opt[0][0].field] = "<b>Total:</b>" + obj[opt[0][0].field];
			}
			footerObj.push(obj);
		}
		
		if(footer['avg'] != ""){
			var tmp = '{' + footer['avg'].substring(0,footer['avg'].length - 1) + "}";
			var obj = eval('(' + tmp + ')');
			if(obj[opt[0][0].field] == undefined){
				footer['avg'] += '"' + opt[0][0].field + '":"<b>Avg:</b>"';
				obj = eval('({' + footer['avg'] + '})');
			}else{
				obj[opt[0][0].field] = "<b>Avg:</b>" + obj[opt[0][0].field];
			}
			footerObj.push(obj);
		}
		
		if(footer['max'] != ""){
			var tmp = '{' + footer['max'].substring(0,footer['max'].length - 1) + "}";
			var obj = eval('(' + tmp + ')');
			
			if(obj[opt[0][0].field] == undefined){
				footer['max'] += '"' + opt[0][0].field + '":"<b>Max:</b>"';
				obj = eval('({' + footer['max'] + '})');
			}else{
				obj[opt[0][0].field] = "<b>Max:</b>" + obj[opt[0][0].field];
			}
			footerObj.push(obj);
		}
		
		if(footer['min'] != ""){
			var tmp = '{' + footer['min'].substring(0,footer['min'].length - 1) + "}";
			var obj = eval('(' + tmp + ')');
			
			if(obj[opt[0][0].field] == undefined){
				footer['min'] += '"' + opt[0][0].field + '":"<b>Min:</b>"';
				obj = eval('({' + footer['min'] + '})');
			}else{
				obj[opt[0][0].field] = "<b>Min:</b>" + obj[opt[0][0].field];
			}
			footerObj.push(obj);
		}
		
		
		
		if(footerObj.length > 0){
			$(jq).datagrid('reloadFooter',footerObj); 
		}
		
		
		function sum(filed){
			var sumNum = 0;
			for(var i=0;i<rows.length;i++){
				sumNum += Number(rows[i][filed]);
			}
			return '"' + filed + '":"' + sumNum.toFixed(2) +'"';
		};
		
		function avg(filed){
			var sumNum = 0;
			for(var i=0;i<rows.length;i++){
				sumNum += Number(rows[i][filed]);
			}
			return '"' + filed + '":"'+ (sumNum/rows.length).toFixed(2) +'"';
		}

		function max(filed){
			var max = 0;
			for(var i=0;i<rows.length;i++){
				if(i==0){
					max = Number(rows[i][filed]);
				}else{
					max = Math.max(max,Number(rows[i][filed]));
				}
			}
			return '"' + filed + '":"'+ max +'"';
		}
		
		function min(filed){
			var min = 0;
			for(var i=0;i<rows.length;i++){
				if(i==0){
					min = Number(rows[i][filed]);
				}else{
					min = Math.min(min,Number(rows[i][filed]));
				}
			}
			return '"' + filed + '":"'+ min +'"';
		}
	},	
	keyCtr : function (jq) {
		return jq.each(function () {
			var grid = $(this);
			if(!dataGbird_isKeyCtr){
		        grid.datagrid('getPanel').panel('panel').attr('tabindex', 1).bind('keydown', function (e) {
		        var rowLength = grid.datagrid("getRows").length; 
		        switch (e.keyCode) {
		        	case 38: // up
		        		var selected = grid.datagrid('getSelected');
		        		if (selected) {
		        			var index = grid.datagrid('getRowIndex', selected);
		                    if(index == 0){
		                		grid.datagrid('selectRow', rowLength-1);	
		                	}else{
		                		grid.datagrid('selectRow', index - 1);
		                	}
		                } else {
		                    var rows = grid.datagrid('getRows');
		                    grid.datagrid('selectRow', rows.length - 1);
		                }
		                break;
		            case 40: // down
		                var selected = grid.datagrid('getSelected');
		                if (selected) {
		                	var index = grid.datagrid('getRowIndex', selected);
		                	if(index >= rowLength-1){
		                		grid.datagrid('selectRow', 0);	
		                	}else{
		                		grid.datagrid('selectRow', index + 1);
		                	}
		                } else {
		                    grid.datagrid('selectRow', 0);
		                }
		                break;
		        	}
		        });
		        dataGbird_isKeyCtr=true;
			}
		});
	}
});