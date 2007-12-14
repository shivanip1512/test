// see itemPicker.js for the general structure of item picker dialogs

MultiPaoPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
MultiPaoPicker.prototype = Object.extend(new PaoPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction, selectionLinkName, excludeIds) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = 'Same Type';
		this.triggerFinalAction = triggerAction;
		this.selectionLinkName = selectionLinkName;
		this.excludeIds = excludeIds.split(',');

		this.selectedItems = new Array();
	},
	
	// override paoPicker.js
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/picker/multi/' + controllerMethod + '?';
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
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit, link) {
        	if (pickerThis.excludeIds.indexOf(hit.paoId) != -1) {
        		return null;
	        } else {
		        return function() {
		        	pickerThis.setDestItemIdFieldId( hit.paoId );
		            pickerThis.selectThisItem(hit, link);
		        };
	        }
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
	        {"title": "Device Name", "field": "paoName", "link": createItemLink},
	        {"title": "Type", "field": "type", "link": null}
	      ];
	    
	    return this.renderTableResults(json, this.processRowForRender.bind(this));
	},
	
	// override paoPicker.js
	processRowForRender: function (rowElement, data) {
		
        if (this.excludeIds.indexOf(data.paoId) != -1) {
        	$(rowElement).addClassName("disabled");
        	$(rowElement).setAttribute("title", "Already Selected");
        } else {
	        this.selectedItems.each(function(item){
		        if (data.paoId == item.paoId) {
		            $(rowElement).addClassName("highlighted");
		        }
	        });
        }
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
	
	// override itemPicker.js
	setDestItemIdFieldId: function( id ) {
		// do nothing
	},
	
	// This method is called when the user is done selecting paos
	itemSelectionComplete: function() {
	    
	    if(this.selectedItems.size() == 0) {
	    	alert('You have not selected any Devices.');	
	    } else {
		    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
			this.triggerEndAction(this.selectedItems);
		    this.selectedItems = new Array();
	    }
	},
	
	// override itemPicker.js
	cancel: function() {
	    this.selectedItems = new Array();
	    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
	}
	
});
