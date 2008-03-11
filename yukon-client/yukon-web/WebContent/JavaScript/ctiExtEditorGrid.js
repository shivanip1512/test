// Function to create an ext grid.  The grid will be rendered into the div
// with id divId.  The columns will be based on the columnInfo param.  The
// data will be requested from the dataUrl. This method returns the grid object.
function initializeGrid(divId, metaObject, dataUrl, editUrl) {
    
    // Save grid state via cookies
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	// Process the column info into 2 arrays for use in the data store
	// and grid
	var storeColumns = new Array();
	var gridColumns = new Array();
    var columns = metaObject.columns;
    for(var i = 0; i < columns.length; i++) {
        
        var columnId = columns[i]['columnId'];
        
        // Column definition for data store
        storeColumns.push(columnId);
	    
        // Column definition for grid
        if(columnId != metaObject.id) {
    	    var colHash = new Hash();
    	    colHash['header'] =  columns[i]['columnName'];
    	    colHash['width'] =  columns[i]['columnWidth'];
    	    colHash['sortable'] =  true;
    	    colHash['dataIndex'] =  columnId;
    	    colHash['editor'] =  new Ext.form.TextField({allowBlank: false});
    	    gridColumns.push(colHash);
        }
    }
    
	// create the Data Store
    var store = new Ext.data.JsonStore({
	    // load using HTTP
	    url: dataUrl,
        autoLoad: true,
        fields: storeColumns
	});
    
    // Create Grid buttons
    var saveButton = new Ext.Button({
        text: 'Save Changes',
        disabled: true,
        handler : function(){
            var data = [];
            var modifiedRecords = grid.store.getModifiedRecords();
            for(i=0; i<modifiedRecords.length; i++) {
              data.push(modifiedRecords[i].data);
            }
            var encodedJson = Ext.encode(data);
        
            Ext.Ajax.request({
                url: editUrl,
                success: function(){
                    window.location = '/spring/bulk/currentProcesses';
                },
                failure: function(){
                    alert('There was a problem saving your changes.');
                },
                params: {data:  encodedJson}
            });
        }
    });
        
    var undoButton = new Ext.Button({
        text: 'Undo All Changes',
        disabled: true,
        handler : function(){
            store.rejectChanges();
        }
    });
    
    var updateHandler = function(store, record, operation){
        if(Ext.data.Record.EDIT == operation) {
            undoButton.enable();
            saveButton.enable();
        } else if(Ext.data.Record.REJECT == operation) {
            undoButton.disable();
            saveButton.disable();
        }  else if(Ext.data.Record.COMMIT == operation) {
            undoButton.disable();
            saveButton.disable();
        }
    };
    store.on({
        'update': updateHandler
    }, this);
 
	
	// create the Grid
	var grid = new Ext.grid.EditorGridPanel({
        store: store,
        columns: gridColumns,
        stripeRows: true,
        height:350,
        width: 1000,
        viewConfig:{forceFit:true},
        enableColumnMove: true,
        clicksToEdit:1,
        bbar: [saveButton, undoButton]
    });
	
	grid.render(divId);
    
	return grid;
    
}
