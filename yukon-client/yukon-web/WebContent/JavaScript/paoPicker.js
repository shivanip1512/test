// see itemPicker.js for the general structure of item picker dialogs

PaoPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
PaoPicker.prototype = Object.extend(new ItemPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = 'Same Type';
		this.triggerFinalAction = triggerAction;
	},
	
	//controllerMethod should match name of appropriate method in the xxPickerController java class
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/picker/pao/' + controllerMethod + '?';
		return url;
	},
	
	//controllerInParameterLabel should match name of appropriate parameter request in xxPickerController java class
	setUrlIntParameter: function(url) {
		var controllerIntParameterLabel = 'currentPaoId';
		if( lastItemId != -1 )
			url += '&' + controllerIntParameterLabel + '=' + lastItemId;
		else
			url += '&' + controllerIntParameterLabel + '=' + $(this.destItemIdFieldId).value;
		//could add more parameters here
		return url;
	},
	
	doSameItemSearch: function(start) {
	    this.lastMethod = this.doSameItemSearch;
	    this.inSearch = true;
	    this.currentSearch = '';
	    
	    var url = this.setUrlPrefix('sameType');
	    url = this.setUrlIntParameter(url);
	    
	    url += '&criteria=' + this.criteria;
	    url += '&start=' + start;
	    new Ajax.Request(url, {'method': 'get', 'onComplete': this.onComplete.bind(this), 'onFailure': this.ajaxError.bind(this)});
	},
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit, link) {
	        return function() {
	        	pickerThis.setDestItemIdFieldId( hit.paoId );
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
	        {"title": "Type", "field": "type", "link": null},
	        {"title": "Pao Id", "field": "paoId", "link": null},
	        {"title": "Pao", "field": "paoName", "link": createItemLink}
	      ];
	    
	    return this.renderTableResults(json, this.processRowForRender.bind(this));
	},
	
	// This method should be implemented to apply any css to each row in the 
	// picker's results area as the results are being drawn
	processRowForRender: function (rowElement, data) {
	    var currentId = $(this.destItemIdFieldId).value;

        if (data.paoId == currentId) {
            rowElement.className = "itemPicker_currentPaoRow";
        }
	},
	
	onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.showAll();
	}
});
