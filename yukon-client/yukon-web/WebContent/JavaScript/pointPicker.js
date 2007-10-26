// see itemPicker.js for the general structure of item picker dialogs

PointPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
PointPicker.prototype = Object.extend(new ItemPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction, pickerShown) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = 'Same Device';
		this.triggerFinalAction = triggerAction;
		this.triggerPickerShownAction = pickerShown;
		
	},
	
	//controllerMethod should match name of appropriate method in the xxPickerController java class
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/picker/point/' + controllerMethod + '?';
		return url;
	},
	
	//controllerInParameterLabel should match name of appropriate parameter request in xxPickerController java class
	setUrlIntParameter: function(url) {
		var controllerIntParameterLabel = 'currentPointId';
		var defaultSearchId = 'defaultSearchId';
		if( lastItemId != -1 )
			url += '&' + controllerIntParameterLabel + '=' + lastItemId;
		else
			url += '&' + controllerIntParameterLabel + '=' + $(this.destItemIdFieldId).value;
		//could add more parameters here
		return url;
	},
	
	
   //over-ride onPickerShown to allow attachment of event handler
   onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.sameParentItem();
	    this.triggerPickerShown();
	},
	
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit) {
	        return function() {
	        	pickerThis.setDestItemIdFieldId( hit.pointId );
	            pickerThis.selectThisItem(hit);
	        };
	    };
	
	    var currentId = $(this.destItemIdFieldId).value;
	    var selectCurrent = function(rowElement, data) {
	        if (data.pointId == currentId) {
	            rowElement.className = "itemPicker_currentPointRow";
	        }
	    };
	    
	    ///////////////////////////////////////////////////////////////////
	    // The following array refers to properties that are available in
	    // the UltraLightPoint interface. If additional properties are added
	    // to that class, they will automatically be added to the JSON 
	    // data structure that is returned. The link attribute, when set,
	    // must refer to a function that will return an appropriate onClick
	    // function for the row. Again, the "createItemLink" function is NOT
	    // the onClick function, instead it generates the onClick function.
	    /////////////////////////////////////////////////////////////////////////////
	    this.outputCols = [
	        {"title": "Device", "field": "deviceName", "link": null},
	        {"title": "Point Id", "field": "pointId", "link": null},
	        {"title": "Point", "field": "pointName", "link": createItemLink}
	      ];
	    
	    return this.renderTableResults(json, selectCurrent);
	}
});
