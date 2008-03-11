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
	
	    // The data should be an XML file with a set of 'row' elements, each
	    // containing one element per column with the column name as the element
	    // name. For example, the following bit of xml could be used to generate
	    // a grid with 2 columns - name and type:
	    // <data> 
	    //     <row>
	    //         <name>Device 1</name>
	    //         <type>MCT 470</type>
        //     </row>
	    //     <row>
	    //         ...
	    reader: new Ext.data.XmlReader(
	        {
	            record: 'row'
	        }, 
	        columnStore),
	    autoLoad: true
	});
	
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