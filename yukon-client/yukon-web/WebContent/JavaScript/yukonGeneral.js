
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
        			cell.setStyle({width: columnSizes[index]+'px'});
        		}
        	});
    	});
    	
	});
}

// Flash an element yellow
function flashYellow(element, duration) {
    if(!duration) {
        duration = 0.8;
    }
    new Effect.Highlight(element, {'duration': duration, 'startcolor': '#FFFF00'});
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
            if (element.match(memItem.selector) || element.up(memItem.selector)) {
                memItem.callback(event);
            }
        });
    },
    
    markBusy: function(element) {
        var spinner = document.createElement("span");
        spinner.innerHTML = "<img src=\"/WebConfig/yukon/Icons/indicator_arrows.gif\">";
        var disabledElement = element.cloneNode(true);
        disabledElement.addClassName('ajaxBusy');
        try {
            disabledElement.disable();
            element.blur();
        } catch (e) {
            disabledElement.innerHTML = '';
        }
        disabledElement.appendChild(spinner);

        element.insert({before: disabledElement});
        element.hide();
    },
    
    unmarkBusy: function(element) {
        element.previous().remove();
        element.show();
    }
});

Event.observe(document, 'click', YEvent._handleClickEvent);

Event.observe(window, 'load', function() {
    $$('.csrfPasswordPrompt').each(function(element) {
        var passwordField = element.down('input[type=password]');
        var buttons = element.getElementsBySelector('.formSubmit');
        Event.observe(passwordField, 'keyup', function() {
            if ($F(passwordField) != '') {
                // not using prototype disable/enable because it does not support <button>
                buttons.each(function(button) {button.disabled = false});
            } else {
                buttons.each(function(button) {button.disabled = true});
            }
        });
        buttons.each(function(button) {button.disabled = true});
    });
});

function hideUpdateWarning() {
    $('updatedWarning').hide();
}

Event.observe(window, 'load', function() {
    $$('img.hoverableImage, input.hoverableImage').each(function(element) {
        var basePath = element.src;
        var extStart = basePath.lastIndexOf(".");
        var hoverPath = basePath.substring(0, extStart) + "_over" + basePath.substring(extStart);
        Event.observe(element, "mouseover", function() {
            element.src = hoverPath;
        });
        Event.observe(element, "mouseout", function() {
            element.src = basePath;
        });
    });
});

Event.observe(window, 'load', function() {
    $$('img.hoverableImage').each(function(element) {
        var basePath = element.src;
        var extStart = basePath.lastIndexOf(".");
        var hoverPath = basePath.substring(0, extStart) + "_over" + basePath.substring(extStart);
        var hoverTarget = element;
        var container = element.up(".hoverableImageContainer");
        if (container) {
            hoverTarget = container;
        }
        Event.observe(hoverTarget, "mouseover", function() {
            element.src = hoverPath;
        });
        Event.observe(hoverTarget, "mouseout", function() {
            element.src = basePath;
        });
    });
});

/*
 * This allows the picker (and anything else that might need it) to distinguish
 * between a tag being called on a main page (window.loadComplete will be false)
 * and a tag being used in page loaded as a result of an openSimpleDialog call
 * (which happens after the window has loaded completely).
 */
window.loadComplete = false;
Event.observe(window, 'load', function() {
    window.loadComplete = true;
});

/**
 * Use this method to call a function after a page has been completely loaded.  If this method
 * is called after the window has already been loaded, the method is called immediately.
 */
function callAfterMainWindowLoad(func) {
	if (window.loadComplete) {
	    func();
	} else {
	    Event.observe(window, 'load', func);
	}
}

function generateMessageCode(prefix, input) {
    // This regular expression must match the one in MessageCodeGenerator.java.
    return prefix + input.replace(/\W+/g, '');
}

/**
 * This function takes in an inputElement and an inputType and changes the
 * current node over to the desired type.
 *
 * String inputElement - The id of the element.
 * String inputType - The desired input type.
 */ 
function changeInputType(inputElement, inputType) {
    var input = document.getElementById(inputElement);
    debug(input);
    debug(inputElement);
    var input2 = document.createElement('input');
    with (input2){
        id = input.id;
        name = input.name;
        value = input.value;
        type = inputType;
    }
    input.parentNode.replaceChild(input2,input);
}