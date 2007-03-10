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
	
	//should involve the actions taken when the pickertype-specific item is selected
	selectThisItem: function(hit) {
	    $(this.destItemIdFieldId).value = hit.paoId;
	    
	    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
	    for (i=0; i < this.extraInfo.length; i+=1) {
	        info = this.extraInfo[i];
	        $(info.fieldid).innerHTML = hit[info.property];
	    }
	    
	   	this.triggerEndAction(hit);
	},
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit) {
	        return function() {
	            pickerThis.selectThisItem(hit);
	        };
	    };
	
	    var currentId = $(this.destItemIdFieldId).value;
	    var selectCurrent = function(rowElement, data) {
	        if (data.paoId == currentId) {
	            rowElement.className = "itemPicker_currentPaoRow";
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
	        {"title": "Type", "field": "type", "link": null},
	        {"title": "Pao Id", "field": "paoId", "link": null},
	        {"title": "Pao", "field": "paoName", "link": createItemLink}
	      ];
	    
	    return this.renderTableResults(json, selectCurrent);
	},
	
	onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.showAll();
	}
});
