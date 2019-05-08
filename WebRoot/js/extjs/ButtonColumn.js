
Ext.grid.ButtonColumn = function(config){  
    Ext.apply(this, config);  
    if(!this.id){  
        this.id = Ext.id();  
    }  
    this.renderer = this.renderer.createDelegate(this);  
};  
  
Ext.grid.ButtonColumn.prototype ={  
      
    buttonWidth:"95%",  
    buttenText:'Button',      
    init : function(grid){        
        this.grid = grid;  
        this.grid.on('render', function(){  
            var view = this.grid.getView();  
            view.mainBody.on('mousedown', this.onMouseDown, this);  
        }, this);  
    },  
      
    /* 
     * ButtonColumn:当前列, 
     * grid:当前的grid, 
     * record:当前点击行的记录, 
     * field:当前被点击的列的dataindex, 
     * value:当前被点击列所对应的dataindex的值 
     * */  
    onClick:function(ButtonColumn,grid,record,field,value){  
        alert('请在此列中添加onClick:function(ButtonColumn,grid,record,field,value)的实现');  
    },  
    onMouseDown : function(e, t){         
        if( t.id ==this.id){  
            e.stopEvent();  
            var index = this.grid.getView().findRowIndex(t);  
            var record = this.grid.store.getAt(index);  
            this.onClick(this,this.grid,record,this.dataIndex,record.data[this.dataIndex]);       
        }  
    },  
    renderer : function(v, p, record) {  
        var s='<table border="0" width="{2}"  cellpadding="0" cellspacing="0" class="x-btn-wrap x-btn x-btn-text-icon"><tr>'+  
            '<td class="x-btn-left" style="padding:0px" mce_style="padding:0px" ></td><td id="{1}" style="vertical-align:middle;LINE-HEIGHT: 22px" mce_style="vertical-align:middle;LINE-HEIGHT: 22px" class="x-btn-center">{0}</td><td  class="x-btn-right"></td>'+  
            "</tr></table>";  
         return String.format(s,this.buttenText,this.id,this.buttonWidth);                      
    }  
};  
