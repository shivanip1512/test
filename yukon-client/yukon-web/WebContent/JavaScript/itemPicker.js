// the structure of item picker dialogs looks something like this
var lastItemId = -1;
//div#itemPickerContainer (generated by js, itemPicker_showPicker)
//    div#itemPicker_popup (inner.jsp)
//        random stuff, including query field, links, and indicator (inner.jsp)
//        div#itemPicker_results (inner.jsp)
//            div#itemPicker_resultArea (generated by js, renderHtmlResult)
//                div#itemPicker_resultAreaFixed (generated by js, renderHtmlResult)
//                    table.itemPickerResultTable (generated by js, createHtmlTableFromJson)
//                table#itemPicker_navTable (generated by js, renderHtmlResult)
ItemPicker = Class.create();

ItemPicker.prototype = { 
	baseInitialize: function (destItemIdFieldId, criteria, extraMapping, pickerId, context) {
		this.destItemIdFieldId = destItemIdFieldId;
		this.currentSearch = '';
		this.inSearch = false;
		this.criteria = criteria;
		this.lastMethod = function() {};
		this.context = context;
		this.pickerId = pickerId;
		//format: "property1:field1id;property2:field2id"
	    this.extraInfo = [];
	    if (extraMapping) {
		    var pairs = extraMapping.split(/;/);
		    for (i = 0; i < pairs.length; i += 1) {
		        var pair = pairs[i].split(/:/);
		        if (pair.length == 2) {
		            var info = {'property': pair[0], 'fieldid': pair[1]};
		            this.extraInfo.push(info);
		        }
		    }
	    }
	    this.sameItemLink = 'Same Device';
	    //picker subclass should fill this in with appropriately named columns
	    this.outputCols = [];
	    //this can be a string specifying a JavaScript call for eval on the selectThisItem finale
	    //format: "divToRefresh:divId;url:controllerString"
	    this.triggerFinalAction = Prototype.emptyFunction;
	
		//trigger picker shown action
		triggerPickerShownAction = Prototype.emptyFunction;
	},
	
	initialize: function (destItemIdFieldId, criteria, extraMapping, pickerId, context) {
		this.baseInitialize(destItemIdFieldId, criteria, extraMapping, pickerId, context);
	},

	//should be overridden by picker subclass
	//controllerMethod should match name of appropriate method in the xxPickerController java class
	setUrlPrefix: function(controllerMethod) {
		var url = this.context + '/itemPicker/' + controllerMethod + '?';
		return url;
	},
	
	//should be overridden by picker subclass
	//controllerInParameterLabel should match name of appropriate parameter request in xxPickerController java class
	setUrlIntParameter: function(url) {
		var controllerIntParameterLabel = 'currentItemId';
		if( lastItemId != null )
			url += '&' + controllerIntParameterLabel + '=' + lastItemId;
		else
			url += '&' + controllerIntParameterLabel + '=' + $(this.destItemIdFieldId).value;
		return url;
	},
	
	showPicker: function() {
	    // create div to store result
	    var bodyElem = document.documentElement.getElementsByTagName("body")[0];
	    var newDivElem = document.createElement("div");
	    newDivElem.setAttribute("id", "itemPickerContainer");
	    bodyElem.appendChild(newDivElem);
	    
	    var url = this.setUrlPrefix('initial');
	    url += this.showPickerValues();

	    new Ajax.Updater('itemPickerContainer', url, {'method': 'get', 'onComplete': this.onPickerShown.bind(this), 'onFailure': this.ajaxError.bind(this)});
	},
	
	showPickerValues: function() {
	    var values = '';
	    values += 'pickerId=' + this.pickerId;
	    values += '&sameItemLink=' + this.sameItemLink;
		
		return values;
	},
	
	doPartialSearch: function(start) {
	    this.lastMethod = this.doPartialSearch;
	    this.inSearch = true;
	    var ss = escape($('itemPicker_query').value);
	    this.currentSearch = ss;
	    
	    var url = this.setUrlPrefix('search');
	    url += 'ss=' + ss;
	    url = this.setUrlIntParameter(url);
	    
	    url += '&criteria=' + this.criteria;
	    if (start) {
	        url += '&start=' + start;
	    }
	    new Ajax.Request(url, {'method': 'get', 'onComplete': this.onComplete.bind(this), 'onFailure': this.ajaxError.bind(this)});
	},
	
	setDestItemIdFieldId: function( id ) {
		$(this.destItemIdFieldId).value = id;
		//Setting this default id will make it so other pickers on the same page
		//will open up with the last selected device as 'Same Device'
		//only the point picker uses this feature right now.	
		lastItemId = id;
	},

	selectThisItem: function(hit, link) {
	    
	    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
	    for (i=0; i < this.extraInfo.length; i+=1) {
	        info = this.extraInfo[i];
	        $(info.fieldid).innerHTML = hit[info.property];
	    }
		this.triggerEndAction(hit);
	},
	
	renderTableResults: function(json, selectCurrent) {
		var hitList = json.hitList;
	    var resultArea = document.createElement("div");
	    resultArea.id = "itemPicker_resultArea";
	    var resultAreaFixed = document.createElement("div");
	    resultAreaFixed.id = "itemPicker_resultAreaFixed";
	    resultArea.appendChild(resultAreaFixed);
	
	    if (hitList && hitList.length && hitList.length > 0) {
		    var resultTable = createHtmlTableFromJson(hitList, this.outputCols, selectCurrent);
	        resultTable.className = "itemPickerResultTable";
	        resultAreaFixed.appendChild(resultTable);
	    } else {
	        var emptyResultDiv = document.createElement("div");
	        emptyResultDiv.id = "itemPicker_noResult";
	        var noResultText = "No results found";
	        emptyResultDiv.appendChild(document.createTextNode(noResultText));
	        resultAreaFixed.appendChild(emptyResultDiv);
	    }
		         
	    // navigation
        var prevLink = "<img src=\"/WebConfig/yukon/Icons/resultset_previous_disabled.gif\">";
	    if (json.startIndex > 0) {
	        prevLink = "<img src=\"/WebConfig/yukon/Icons/resultset_previous.gif\" onclick=\"javascript:" + this.pickerId + ".previous(" + json.previousIndex + ")\">";
        }
        $('itemPicker_popup_titleBar_prevLink').innerHTML = prevLink;
	    
        var nextLink = "<img src=\"/WebConfig/yukon/Icons/resultset_next_disabled.gif\">";
	    if (json.endIndex < json.hitCount) {
            nextLink = "<img src=\"/WebConfig/yukon/Icons/resultset_next.gif\" onclick=\"javascript:" + this.pickerId + ".next(" + json.nextIndex + ")\">";
        }
        $('itemPicker_popup_titleBar_nextLink').innerHTML = nextLink;
	    
	    var pageNum = (json.startIndex + 1) + " - " + json.endIndex + " of " + json.hitCount;
        $('itemPicker_popup_titleBar_pageNum').innerHTML = pageNum;
        
	    return resultArea;
	},
	
	//this should be overridden by the picker subclass
	//it should do what is necessary to select the specific current row and guide any picker-
	//specific output to the results table generator.
	renderHtmlResult: function(json) {
	    var selectCurrent = function(rowElement, data) {}
	    return this.renderTableResults(json, selectCurrent);
	},
	
	onComplete: function(transport, json) {
	    var newResultArea = this.renderHtmlResult(json);
	    var oldResultArea = $("itemPicker_resultArea");
	    var resultHolder = $("itemPicker_results");
	    if (oldResultArea) {
	      resultHolder.removeChild(oldResultArea);
	    }
	    var oldError = $("itemPicker_errorHolder");
	    if (oldError) {
	      resultHolder.removeChild(oldError);
	    }
	    resultHolder.appendChild(newResultArea);
	    var ss = escape($('itemPicker_query').value);
	    if (this.currentSearch != ss) {
	        //do another search
	        this.doPartialSearch(0);
	    } else {
	        this.inSearch = false;
	        $('itemPicker_indicator').hide();
	    }
	},
	
	ajaxError: function(transport, json) {
	    this.inSearch = false;
	    $('itemPicker_indicator').hide();
	    $("itemPicker_results").innerHTML = "";
	    errorHolder = document.createElement("div");
	    errorHolder.id = "itemPicker_errorHolder";
	    errorHolder.innerHTML = "There was a problem searching the index: " + transport.responseText;
	    $("itemPicker_results").appendChild(errorHolder);
	},
	
	doKeyUp: function() {
	    var pickerThis = this;
	    var timerFunction = function() {
	        var ss = escape($('itemPicker_query').value);
	        if (!this.inSearch && this.currentSearch != ss) {
	            pickerThis.doPartialSearch(0);
	        } else {
	            $('itemPicker_indicator').hide();
	        }
	    };
	    var quietDelay = 300;
	    setTimeout(timerFunction, quietDelay);
	    $('itemPicker_indicator').show();
	},
	
	showAll: function() {
	    $('itemPicker_indicator').show();
	    $('itemPicker_query').value = '';
	    this.doPartialSearch();
	},
	
	doSameItemSearch: function(start) {
	    this.lastMethod = this.doSameItemSearch;
	    this.inSearch = true;
	    this.currentSearch = '';
	    
	    var url = this.setUrlPrefix('sameDevice');
	    url = this.setUrlIntParameter(url);
	    
	    url += '&criteria=' + this.criteria;
	    if (start) {
	        url += '&start=' + start;
	    }
	    new Ajax.Request(url, {'method': 'get', 'onComplete': this.onComplete.bind(this), 'onFailure': this.ajaxError.bind(this)});
	},
	
	cancel: function() {
	    $('itemPickerContainer').parentNode.removeChild($('itemPickerContainer'));
	},
	
	previous: function(index) {
	    $('itemPicker_indicator').show();
	    this.lastMethod(index);
	},
	
	next: function(index) {
	    $('itemPicker_indicator').show();
	    this.lastMethod(index);
	},
	
	sameParentItem: function() {
	    $('itemPicker_indicator').show();
	    $('itemPicker_query').value = '';
	    this.doSameItemSearch(0);
	},
	
	onPickerShown: function(transport, json) {
	    $('itemPicker_query').focus();
	    this.sameParentItem();
	},
	
	triggerEndAction: function(selectedItem) {
		this.triggerFinalAction(selectedItem);
	},
	
	triggerPickerShown: function () {
		this.triggerPickerShownAction();
	}
}
