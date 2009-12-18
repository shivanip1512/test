// see itemPicker.js for the general structure of item picker dialogs

DatabaseMigrationPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
DatabaseMigrationPicker.prototype = Object.extend(new ItemPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction, configurationName) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = 'Same Thing';
		this.triggerFinalAction = triggerAction;
		this.configurationName = configurationName;
		
		this.selectionLinkName = 'Select';
		this.selectedItems = new Array();
	},
	
	// override paoPicker.js
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/picker/databaseMigration/' + controllerMethod + '?configurationName='+this.configurationName+'&';
		return url;
	},
	
	// override itemPicker.js
	showPickerValues: function() {
	    var values = '';
	    values += 'pickerId=' + this.pickerId;
	    values += '&sameItemLink=' + this.sameItemLink;
	    if(this.selectionLinkName != null) {
	    	values += '&selectionLinkName=' + this.selectionLinkName;
	    }
		
		return values;
	},
	

	doSameItemSearch: function(start) {
	    this.lastMethod = this.doSameItemSearch;
	    this.inSearch = true;
	    this.currentSearch = '';
	    
	    var url = this.setUrlPrefix('showAll');
	    
	    new Ajax.Request(url, {'method': 'get', 'onComplete': this.onComplete.bind(this), 'onFailure': this.ajaxError.bind(this)});
	},
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit, link) {
	    	return function() {
	        	pickerThis.setDestItemIdFieldId( hit.databaseMigrationId );
	            pickerThis.selectThisItem(hit, link);
	        };
	    };

	    ///////////////////////////////////////////////////////////////////
	    // The following array refers to properties that are available in
	    // the UltraLightPao interface. If additional properties are added
	    // to that class, they will automatically be added to the JSON 
	    // data structure that is returned. The link attribute, when set,
	    // must refer to a function that will return an appropriate onClick
	    // function for the row. Again, the "createItemLink" function is NOT
	    // the onClick function, instead it generates the onClick function.
	    /////////////////////////////////////////////////////////////////////////////

	    this.outputCols = [
	        {"title": "Database Migration Items", "field": "databaseMigrationDisplay", "link": createItemLink}
	      ];

	    return this.renderTableResults(json, this.processRowForRender.bind(this));
	},
	
	// override paoPicker.js
	processRowForRender: function (rowElement, data) {
		this.selectedItems.each(function(item){
	        if (data.databaseMigrationId == item.databaseMigrationId) {
	            $(rowElement).addClassName("highlighted");
	        }
        });
	},
	
	// override paoPicker.js
	selectThisItem: function(hit, link) {
	    var parentRow = $($(link).parentNode.parentNode);
	    if(parentRow.hasClassName('highlighted')){
	    	// unselect
		    parentRow.removeClassName("highlighted");
		    this.selectedItems = this.selectedItems.without(hit);
	    	
	    } else {
	    	// select
		    this.selectedItems.push(hit);
		    parentRow.addClassName("highlighted");
		}
	},
	
	// This method is called when the user is done selecting paos
	itemSelectionComplete: function() {
	    
	    if(this.selectedItems.size() == 0) {
	    	alert('You have not selected any Items.');	
	    } else {
		    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
		    this.setDestItemIdFieldId(this.selectedItems.pluck("databaseMigrationId").join(","));
			this.triggerEndAction(this.selectedItems);
		    this.selectedItems = new Array();
	    }
	},
	
	// override itemPicker.js
	cancel: function() {
	    this.selectedItems = new Array();
	    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
	},
	
	onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.showAll();
	}

	
});
