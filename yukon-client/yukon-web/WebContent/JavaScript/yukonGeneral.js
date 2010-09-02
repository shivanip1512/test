
function yukonGeneral_setSelectToValue(selectId, selectedValue) {
	
	for (i = 0; i < $(selectId).options.length; i++) {
		if ($(selectId).options[i].value == selectedValue) {
			$(selectId).selectedIndex = i;
			break;
		}
	}
}

function yukonGeneral_moveOptionPositionInSelect(selectElement, direction) {
	
	//this contains all the selected indexes
	var index = new Array(); 
	index[0] = -1;
	//the array index to remember all the selection of the user :)
	indexNo = 0; 
	var selectList = selectElement;
	
	//loop to remember all the selection in the select element
	for (var i = 0; i < selectElement.options.length; i++){	
		if (selectElement.options[ i ].selected){
			index[indexNo] = i;
			indexNo ++;
		}
	}
	
	//if no options are selected, quit
	var routeIndex = selectElement.selectedIndex; 
    if (index[0] == -1) { //none selected
        return false;
    }
    
    //if selection is at the top, can't move up anymore
    if ( index[0] + direction == -1 && direction == -1){ 
    	return false;
    }
    
    //if selection is at the bottom and can't move down anymore
    if ( index.last() == selectElement.options.length - 1 && direction == 1){ 
    	return false;
    }
    
    //all the options of the select
    var options = selectElement.options;
    
    //clone the select element - for ie fix 
	var copy = $A(options).clone(); 

	//empty the whole select so that ie doesn't complain unable to insert
    while (selectElement.options.length > 0) { 
		selectElement.options[0] = null;
	}
	
	//the logic is going down = going up but reversed...
	
	//going down
	if (direction == 1){ 
		j = index.length;
		for (i = 0; i< index.length; i++){
			
			//we do it reverse of the going up
			var routeIndex = index[j - 1]; 
			var routeIndex2 = routeIndex + direction;
			
			var temp1 = copy[routeIndex]; 
			var temp2 = copy[routeIndex2];
			
			//swap the elements
			copy[routeIndex] = temp2;
			copy[routeIndex2] = temp1;
			
			//the reverse index is decremented
			j --; 
		}
	}
	//going up
	else{
		for (i = 0; i < index.length; i++){ 
		
			//simple swapping
			var routeIndex = index[i];
			var routeIndex2 = routeIndex + direction;
			
			var temp1 = copy[routeIndex];
			var temp2 = copy[routeIndex2];
				
			copy[routeIndex] = temp2;
			copy[routeIndex2] = temp1;
		}
	}
	
	//copy the array back to the select element
	for (var x = 0; x < copy.length; x++) { 
		selectElement.options[x] = copy[x];
	}
	
	//highlight all the previously selected elements in their new position
	for (j = 0; j < index.length; j++){ 
		selectList.options[index[j] + direction].selected = true;
	}
	
    return true;
}

function yukonGeneral_addOtpionToTopOfSelect(selectObj,optValue,optText) {
    
    // new option
    var newOpt = document.createElement("option");

    // get first option group - there will always be at least one [prototype function]
    var firstOptGroup = selectObj.immediateDescendants()[0];
    
    // get first option in first group
    var firstGroupOptions = firstOptGroup.immediateDescendants();
    
    // either stick new opt in before the opt that is currently first in the first option group [DOM function] 
    // or, if there are no current opts, just append it to that first group
    if(firstGroupOptions.length >0) {
    
        topOption = firstGroupOptions[0];
    
        // prevent duplicates from getting added to top of dropdown for each run of callback
        if(topOption.text != optText) {
            firstOptGroup.insertBefore(newOpt,topOption);
        }
    }
    else {
        firstOptGroup.appendChild(newOpt);
    }
    
    // why set the option value and text now instead of when we made it? IE..
    newOpt.value = optValue;
    newOpt.text = optText;

}

function stickyCheckboxes_setup(id, defaultValue) {
    var state = YukonClientPersistance.getState("stickyCheckboxes", id, defaultValue);
    YukonClientPersistance.persistState("stickyCheckboxes", id, state);
    $(id).checked = state;
    
    $(id).observe('change', function(event) {
        var state = Event.element(event).checked;
        YukonClientPersistance.persistState("stickyCheckboxes", id, state);
    });
}

function stickyCheckboxes_retrieve(id) {
    var state = YukonClientPersistance.getState("stickyCheckboxes", id);
    return state;
}

function activeResultsTable_highLightRow(row) {
	row = $(row);
	row.addClassName('hover');
}

function activeResultsTable_unHighLightRow(row){
	row = $(row);
	row.removeClassName('hover');
}

function cancelCommands(resultId, url, ccid, cancelingText, finishedText) {
    
    // save button text for restore on error
    var orgCancelButtonText = $F('cancelButton' + ccid);
    
    // swap to wait img, disable button
    $('waitImg' + ccid).show();
    $('cancelButton' + ccid).disable();
    $('cancelButton' + ccid).value = cancelingText;
    
    // setup callbacks
    var onComplete = function(transport, json) {
        
        var errorMsg = json['errorMsg'];
        if (errorMsg != null) {
            handleError(ccid, errorMsg, orgCancelButtonText);
            return;
        } else {
            showCancelResult(ccid, finishedText);
            $('cancelButton' + ccid).hide();
        }
    };
    
    var onFailure = function(transport, json) {
    	showCancelResult(ccid, transport.responseText);
        $('cancelButton' + ccid).value = orgCancelButtonText;
        $('cancelButton' + ccid).enable();
    };

    // run cancel    
    var args = {};
    args.resultId = resultId;
    new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': onComplete, 'onFailure': onFailure, 'onException': onFailure, 'parameters': args});
}

function showCancelResult(ccid, msg) {

    $('waitImg' + ccid).hide();
    $('cancelArea' + ccid).innerHTML = msg;
    $('cancelArea' + ccid).show();
}

function getViewportDimensions() {
	var viewportwidth;
	var viewportheight;

	if (typeof window.innerWidth != 'undefined') {
		// the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight
		viewportwidth = window.innerWidth;
		viewportheight = window.innerHeight;
	}
	else if (typeof document.documentElement != 'undefined'
			&& typeof document.documentElement.clientWidth != 'undefined'
			&& document.documentElement.clientWidth != 0) {
		// IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
		viewportwidth = document.documentElement.clientWidth;
		viewportheight = document.documentElement.clientHeight;
	}
	else {
		// older versions of IE
		viewportwidth = document.getElementsByTagName('body')[0].clientWidth;
		viewportheight = document.getElementsByTagName('body')[0].clientHeight;
	}
	return { 'width' : viewportwidth, 'height' : viewportheight };
}

// pass table css selectors
// columns in each table will be made to have the same width as the widest element in that column across all tables
function alignTableColumnsByTable() {
	
	var tableSelectors = $A(arguments);
    
    Event.observe(window, "load", function() {
    	
    	var tablesToAlign = $$(tableSelectors);
    	
    	var columnSizes = $A();
    	tablesToAlign.each(function(table) {
    		
    		var rowsToAlign = table.getElementsBySelector("tr");
    		rowsToAlign.each(function(tr) {
        		
        		var cells = tr.getElementsBySelector("td");
        		for (var index = 0; index < cells.length - 1; ++index) {
        			var cell = cells[index];
        			if (!columnSizes[index] || cell.getWidth() > columnSizes[index]) {
        				columnSizes[index] = cell.getWidth();
        			}
        		}
        	});
    	});
    	
    	
    	tablesToAlign.each(function(table) {
    		
    		var rowsToAlign = table.getElementsBySelector("tr");
    		rowsToAlign.each(function(tr) {
        		
        		var cells = tr.getElementsBySelector("td");
        		for (var index = 0; index < cells.length - 1; ++index) {
        			var cell = cells[index];
        			cell.setStyle({'width': columnSizes[index]+'px'});
        		}
        	});
    	});
    	
	});
}

function showBusy() {
	$('busyBox').show();
}

function hideBusy() {
	$('busyBox').hide();
}

var YEvent = new Object();

Object.extend(YEvent, {
    clickMemory: [],

    observeSelectorClick: function(selector, callback) {
        YEvent.clickMemory.push({'selector': selector, 'callback': callback});
    },
    
    _handleClickEvent: function(event) {
        YEvent.clickMemory.each(function(memItem) {
            var element = Event.element(event);
            if (element.match(memItem.selector)) {
                memItem.callback(event);
            }
        });
    },
    
    markBusy: function(event) {
        Event.element(event).addClassName("ajaxBusy");
    },
    
    unmarkBusy: function(event) {
        Event.element(event).removeClassName("ajaxBusy");
    }
});

Event.observe(document, 'click', YEvent._handleClickEvent);
