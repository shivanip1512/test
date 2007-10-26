// see itemPicker.js for the general structure of item picker dialogs

UserPicker = Class.create();

//must extend ItemPicker; this will not work as a standalone object
UserPicker.prototype = Object.extend(new ItemPicker(), { 
	initialize: function(destItemIdFieldId, criteria, extraMapping, pickerId, context, triggerAction) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
		this.sameItemLink = '';   
		this.triggerFinalAction = triggerAction;
	},
	
	//controllerMethod should match name of appropriate method in the xxPickerController java class
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/picker/user/' + controllerMethod + '?';
		return url;
	},
	
	//controllerInParameterLabel should match name of appropriate parameter request in xxPickerController java class
	setUrlIntParameter: function(url) {
		var controllerIntParameterLabel = 'currentUserId';
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
	    //
	    var url = this.setUrlPrefix('sameLoginGroup');
	    url = this.setUrlIntParameter(url);
	    
	    url += '&criteria=' + this.criteria;
	    url += '&start=' + start;
	},
	
	//it should do what is necessary to select the specific current row and guide any pickertype-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var pickerThis = this;
	    var createItemLink = function(hit) {
	        return function() {
	        	pickerThis.setDestItemIdFieldId( hit.userId );
	            pickerThis.selectThisItem(hit);
	        };
	    };
	
	    var currentId = $(this.destItemIdFieldId).value;
	    var selectCurrent = function(rowElement, data) {
	        if (data.userId == currentId) {
	            rowElement.className = "itemPicker_currentUserRow";
	        }
	    };
	    
	    ///////////////////////////////////////////////////////////////////
	    // The following array refers to properties that are available in
	    // the UltraLightYukonUser interface. If additional properties are added
	    // to that class, they will automatically be added to the JSON 
	    // data structure that is returned. The link attribute, when set,
	    // must refer to a function that will return an appropriate onClick
	    // function for the row. Again, the "createItemLink" function is NOT
	    // the onClick function, instead it generates the onClick function.
	    /////////////////////////////////////////////////////////////////////////////
	    this.outputCols = [
	       	{"title": "User", "field": "userName", "link": createItemLink},
	        {"title": "Login Group", "field": "groupName", "link": null},
	        {"title": "User Id", "field": "userId", "link": null}
	      ];
	    
	    return this.renderTableResults(json, selectCurrent);
	},
	
	onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.showAll();
	}
});
