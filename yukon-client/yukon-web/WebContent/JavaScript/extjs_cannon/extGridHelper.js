
var ExtGridHelper = Class.create();

ExtGridHelper.prototype = { 
    
    // INIT
    // No init required
    //----------------------------------------------------------------------------------------------
    initialize: function() {
    },
    
    getJsonStore: function(columnInfo, dataUrl) {
    
        var columnStore = new Array();
        for(var i = 0; i < columnInfo.length; i++) {
            columnStore[i] = columnInfo[i]['columnId'];
        }
    
        var store = new Ext.data.Store({
            url: dataUrl,
        
            // The data should be an Json string with an elemnt named root in the form:
            // {"root":[{"row1col1ID":"row1col1Val","row1col2ID":"row1Col2Val"},
            //          {"row2col1ID":"row2col1Val","row2col2ID":"row2Col2Val"}]}
            reader: new Ext.data.JsonReader(
                {
                    root: 'root'
                }, 
                columnStore),
            autoLoad: true
        });
    
        return store;
    },
    
    getColumns: function(columnInfo, gridWidth, sortable) {
    
        var columns = new Array();
        
        for(var i = 0; i < columnInfo.length; i++) {
            
    	    var colHash = new Hash();
    	    colHash['header'] =  columnInfo[i]['columnName'];
    	    
    	    if (gridWidth > 0) {
    	    	colHash['width'] =  Math.floor((columnInfo[i]['columnWidthPercentage'] / 100) * gridWidth);
    	    }
    	    
    	    colHash['sortable'] =  sortable;
    	    colHash['dataIndex'] =  columnInfo[i]['columnId'];
            colHash['id'] =  colHash['dataIndex'];
    	    columns[i] = colHash;
    	}
     
        return columns;
    }
} 

