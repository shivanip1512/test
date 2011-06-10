
var gridHelper = new ExtGridHelper();

ExtGridMaker = Class.create();

ExtGridMaker.prototype = { 

    // Constructor
	initialize: function(id, staticElem) {
	},
    
    // Basic grid - no title, toolbar
    getBasicGrid: function(height, width, columnInfo, dataUrl, showLoadMask, refreshRate) {
    
        var store = gridHelper.getJsonStore(columnInfo, dataUrl);
        var columns = gridHelper.getColumns(columnInfo, width, true);
        
        if (showLoadMask && refreshRate <= 0) {
	        var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading data...", store:store});
	        loadMask.show();
        }
        
        if (width <= 0) {
        	width = 'auto';
        }
        
        var grid = new Ext.grid.GridPanel({
            store: store,
            columns: columns,
            viewConfig:{
                forceFit:true
            },
            stripeRows: true,
            height:height,
            width: width,
            enableColumnMove: true
        });
        
        if (refreshRate > 0) {
        	new PeriodicalExecuter(function(){store.reload();}, refreshRate);
        }
        
        return grid;
    },
    
    // Report grid - title and export buttons on toolbar
    getReportGrid: function(title, height, width, columnInfo, dataUrl, csvUrl, pdfUrl, showLoadMask, refreshRate) {
        
        var basicGrid = this.getBasicGrid(height, width, columnInfo, dataUrl, showLoadMask, refreshRate);
        
        var csvButton = new Ext.Button({
            text:'CSV', 
            handler:function(){
                window.location = csvUrl;
            }, 
            icon:'/WebConfig/yukon/Icons/excel.gif', 
            cls: 'x-btn-text-icon'
        });
        var pdfButton = new Ext.Button({
            text:'PDF', 
            handler:function(){
                window.location = pdfUrl;
            }, 
            icon:'/WebConfig/yukon/Icons/pdf.gif', 
            cls: 'x-btn-text-icon'
        });
        var refreshButton = new Ext.Button({
            text:'Reload', 
            handler:function(){
                basicGrid.getStore().reload();
            }, 
            icon:'/WebConfig/yukon/Icons/table_refresh.gif', 
            cls: 'x-btn-text-icon'
        });
        
        var grid = basicGrid.cloneConfig({
            title: title,
            tbar: [refreshButton, '->', 'Export: ', csvButton, '-', pdfButton]
        });
        
        return grid;
    }
};
