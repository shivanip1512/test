
var gridHelper = new ExtGridHelper();

ExtGridMaker = Class.create();

ExtGridMaker.prototype = { 

    // Constructor
	initialize: function(id, static) {
	},
    
    // Basic grid - no title, toolbar
    getBasicGrid: function(height, width, columnInfo, dataUrl) {
    
        var store = gridHelper.getJsonStore(columnInfo, dataUrl)
        var columns = gridHelper.getColumns(columnInfo, width, true);
        
        var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading data...", store:store});
        loadMask.show();
        
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
        
        return grid;
    },
    
    // Report grid - title and export buttons on toolbar
    getReportGrid: function(title, height, width, columnInfo, dataUrl, csvUrl, pdfUrl) {
    
        var csvButton = new Ext.Button({text:'CSV', handler:function(){window.location = csvUrl}, icon:'/WebConfig/yukon/Icons/excel.gif', cls: 'x-btn-text-icon' });
        var pdfButton = new Ext.Button({text:'PDF', handler:function(){window.location = pdfUrl}, icon:'/WebConfig/yukon/Icons/pdf.gif', cls: 'x-btn-text-icon'});
        
        var basicGrid = this.getBasicGrid(height, width, columnInfo, dataUrl);
        
        var grid = basicGrid.cloneConfig({
        
            title: title,
            tbar: ['->', 'Export: ', csvButton, '-', pdfButton]
        });
        
        return grid;
    }
}
