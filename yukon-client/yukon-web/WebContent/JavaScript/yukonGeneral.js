Yukon.namespace('Yukon.ui.aux');
Yukon.ui.aux = (function () {
    var uiAux;
    uiAux = {
        yukonGeneral_moveOptionPositionInSelect : function (selectElement, direction) {
            //this contains all the selected indexes
            var index = [],
                i,
                j,
                copy,
                options,
                selectList,
                x,
                indexNo,
                routeIndex,
                routeIndex2,
                temp1,
                temp2;

            index[0] = -1;
            //the array index to remember all the selection of the user :)
            indexNo = 0; 
            selectList = selectElement;
            
            //loop to remember all the selection in the select element
            for (i = 0; i < selectElement.options.length; i++) {
                if (selectElement.options[ i ].selected) {
                    index[indexNo] = i;
                    indexNo ++;
                }
            }
            
            //if no options are selected, quit
            if (index[0] == -1) { //none selected
                return false;
            }
            
            //if selection is at the top, can't move up anymore
            if (index[0] + direction == -1 && direction == -1) { 
                return false;
            }
            
            //if selection is at the bottom and can't move down anymore
            if (index.last() == selectElement.options.length - 1 && direction == 1) { 
                return false;
            }
            
            //all the options of the select
            options = selectElement.options;
            
            //clone the select element - for ie fix 
            copy = $A(options).clone(); 

            //empty the whole select so that ie doesn't complain unable to insert
            while (selectElement.options.length > 0) { 
                selectElement.options[0] = null;
            }

            //the logic is going down = going up but reversed...

            //going down
            if (direction == 1) { 
                j = index.length;
                for (i = 0; i < index.length; i++) {
                    
                    //we do it reverse of the going up
                    routeIndex = index[j - 1]; 
                    routeIndex2 = routeIndex + direction;
                    
                    temp1 = copy[routeIndex]; 
                    temp2 = copy[routeIndex2];
                    
                    //swap the elements
                    copy[routeIndex] = temp2;
                    copy[routeIndex2] = temp1;
                    
                    //the reverse index is decremented
                    j --; 
                }
            } else { //going up
                for (i = 0; i < index.length; i++) { 

                    //simple swapping
                    routeIndex = index[i];
                    routeIndex2 = routeIndex + direction;

                    temp1 = copy[routeIndex];
                    temp2 = copy[routeIndex2];

                    copy[routeIndex] = temp2;
                    copy[routeIndex2] = temp1;
                }
            }

            //copy the array back to the select element
            for (x = 0; x < copy.length; x++) { 
                selectElement.options[x] = copy[x];
            }

            //highlight all the previously selected elements in their new position
            for (j = 0; j < index.length; j++) { 
                selectList.options[index[j] + direction].selected = true;
            }

            return true;
        },
        yukonGeneral_addOptionToTopOfSelect : function (selectObj,optValue,optText) {
            // new option
            var newOpt = document.createElement("option");

            // get first option group - there will always be at least one [prototype function]
            var firstOptGroup = jQuery(selectObj).find("optgroup")[0];
            
            // get first option in first group
            var firstGroupOptions = jQuery(firstOptGroup).find("option");
            
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
        },
        stickyCheckboxes_setup : function (id, defaultValue) {
            var state = YukonClientPersistance.getState("stickyCheckboxes", id, defaultValue);
            YukonClientPersistance.persistState("stickyCheckboxes", id, state);
            $(id).checked = state;
            
            $(id).observe('change', function(event) {
                var state = Event.element(event).checked;
                YukonClientPersistance.persistState("stickyCheckboxes", id, state);
            });
        },
        stickyCheckboxes_retrieve : function (id) {
            var state = YukonClientPersistance.getState("stickyCheckboxes", id);
            return state;
        },
        activeResultsTable_highLightRow : function (row) {
            row = $(row);
            row.addClassName('hover');
        },
        activeResultsTable_unHighLightRow : function (row){
            row = $(row);
            row.removeClassName('hover');
        },
        cancelCommands : function (resultId, url, ccid, cancelingText, finishedText) {
            
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
                    uiAux.showCancelResult(ccid, finishedText);
                    $('cancelButton' + ccid).hide();
                }
            };
            
            var onFailure = function(transport, json) {
                uiAux.showCancelResult(ccid, transport.responseText);
                $('cancelButton' + ccid).value = orgCancelButtonText;
                $('cancelButton' + ccid).enable();
            };

            // run cancel    
            var args = {};
            args.resultId = resultId;
            new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': onComplete, 'onFailure': onFailure, 'onException': onFailure, 'parameters': args});
        },
        showCancelResult : function (ccid, msg) {
        
            $('waitImg' + ccid).hide();
            $('cancelArea' + ccid).innerHTML = msg;
            $('cancelArea' + ccid).show();
        },
        // pass table css selectors
        // columns in each table will be made to have the same width as the widest element in that column across all tables
        alignTableColumnsByTable : function () {

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
        },
        generateMessageCode : function (prefix, input) {
            // This regular expression must match the one in MessageCodeGenerator.java.
            return prefix + input.replace(/\W+/g, '');
        },
        /**
         * This function takes in an inputElement and an inputType and changes the
         * current node over to the desired type.
         *
         * String inputElement - The id of the element.
         * String inputType - The desired input type.
         */ 
        changeInputType : function (inputElement, inputType) {
            var input = document.getElementById(inputElement);
            var input2 = document.createElement('input');
                input2.id = input.id;
                input2.name = input.name;
                input2.value = input.value;
                input2.type = inputType;
            input.parentNode.replaceChild(input2,input);
        },
        getHeaderJSON : function (xhr) {
            var json;
            try { json = xhr.getResponseHeader('X-Json'); }
            catch(e) {}

            if (json) {
                return eval('(' + json + ')');
            }
            return {};
        }
    };
    return uiAux;
})();


/**
 * Flashes the background of an element 'Yukon yellow'
 * @param element    [DOM node] we want to give a temporary splash of yellow
 * @param duration  [float] how long the effect should last in seconds
 */
function flashYellow(element, duration) {
    jQuery(element).flashYellow(duration);
}

/*
 * This allows the picker (and anything else that might need it) to distinguish
 * between a tag being called on a main page (window.loadComplete will be false)
 * and a tag being used in page loaded as a result of an openSimpleDialog call
 * (which happens after the window has loaded completely).
 */
window.loadComplete = false;
jQuery(function() {
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
        jQuery(function() { func();});
    }
}

/** Section containers with show/hide behavior */
jQuery(function() {
    jQuery(document).on('click', '.toggle-title', function (event) {
        jQuery(event.currentTarget).closest('.titledContainer').each(function(index, elem){
            jQuery(elem).toggleClass("collapsed");
            jQuery(elem).find('.content').toggle();
       });
    });
});

/**  Button dropdown menu behavior (google+) */
jQuery(function() {
    jQuery(".dropdown .button, .dropdown button").click(function () {
        if (!jQuery(this).find('span.toggle').hasClass('active')) {
            jQuery('.dropdown-slider').slideUp();
            jQuery('span.toggle').removeClass('active');
        }
        
        jQuery(this).parent().find('.dropdown-slider').slideToggle('fast');
        jQuery(this).find('span.toggle').toggleClass('active');
        
        return false;
    });
});
jQuery(document).bind('click', function (e) {
    if (e.target.id != jQuery('.dropdown').attr('class')) {
        jQuery('.dropdown-slider').slideUp();
        jQuery('span.toggle').removeClass('active');
    }
});