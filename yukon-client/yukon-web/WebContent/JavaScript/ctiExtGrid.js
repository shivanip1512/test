// Function to create an ext grid.  The grid will be rendered into the div
// with id divId.  The columns will be based on the columnInfo param.  The
// data will be requested from the dataUrl. This method returns the grid object.
function initializeGrid(divId, columnInfo, dataUrl) {
    
    // Save grid state via cookies
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	// Process the column info into 2 arrays for use in the data store
	// and grid
	var columnStore = new Array();
	var columnGrid = new Array();
	for(var i = 0; i < columnInfo.length; i++) {
	    // Column definition for data store
	    columnStore[i] = columnInfo[i]['columnId'];
	    
	    // Column definition for grid
	    var colHash = new Hash();
	    colHash['header'] =  columnInfo[i]['columnName'];
	    colHash['width'] =  columnInfo[i]['columnWidth'];
	    colHash['sortable'] =  true;
	    colHash['dataIndex'] =  columnInfo[i]['columnId'];
	    columnGrid[i] = colHash;
	}
	
	// create the Data Store
	var store = new Ext.data.Store({
	    // load using HTTP
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
	
    var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading data...", store:store});
    loadMask.show();

	// create the Grid
	var grid = new Ext.grid.GridPanel({
	    store: store,
	    columns: columnGrid,
	    stripeRows: true,
	    height:350,
	    width: 1000,
	    viewConfig:{forceFit:true},
	    enableColumnMove: true
	});
	
	grid.render(divId);
	
	return grid;

}