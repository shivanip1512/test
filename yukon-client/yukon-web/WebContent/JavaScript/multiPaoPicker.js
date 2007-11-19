// see itemPicker.js for the general structure of item picker dialogs

MultiPaoPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
MultiPaoPicker.prototype = Object.extend(new PaoPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction, selectionLinkName) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = 'Same Type';
		this.triggerFinalAction = triggerAction;
		this.selectionLinkName = selectionLinkName;
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
	
	// override paoPicker.js
	processRowForRender: function (rowElement, data) {
		
		var selectedItems = this.selectedItems;
		
        this.selectedItems.each(function(item){
	        if (data.paoId == item.paoId) {
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
