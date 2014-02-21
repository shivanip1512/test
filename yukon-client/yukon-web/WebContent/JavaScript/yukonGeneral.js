/**
 * Singleton that contains more ui utility functionality
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ui');
yukon.namespace('yukon.ui.aux');

yukon.ui.aux = (function () {
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
            copy = jQuery.makeArray(options);
            //empty the whole select so that ie doesn't complain unable to insert
            while (selectElement.options.length > 0) { 
                selectElement.remove(0);
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
            var newOpt,
                firstOptGroup,
                firstGroupOptions,
                topOption;
            // new option
            newOpt = document.createElement("option");

            // get first option group - there will always be at least one [prototype function]
            firstOptGroup = jQuery(selectObj).find("optgroup")[0];

            // get first option in first group
            firstGroupOptions = jQuery(firstOptGroup).find("option");

            // either stick new opt in before the opt that is currently first in the first option group [DOM function] 
            // or, if there are no current opts, just append it to that first group
            if (firstGroupOptions.length > 0) {

                topOption = firstGroupOptions[0];

                // prevent duplicates from getting added to top of dropdown for each run of callback
                if (topOption.text !== optText) {
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
        
        cancelCommands : function (resultId, url, ccid, cancelingText, finishedText) {

            // save button text for restore on error
            var orgCancelButtonText = jQuery('#cancelButton' + ccid).val(),
                args;

            // swap to wait img, disable button
            jQuery('#waitImg' + ccid).show();
            jQuery('#cancelButton' + ccid).prop('disabled', true);
            jQuery('#cancelButton' + ccid).val(cancelingText);

            // run cancel    
            var args = {};
            args.resultId = resultId;
            // setup callbacks
            jQuery.ajax({
                type: "POST",
                url: url,
                data: args
            }).done( function (data, textStatus, jqXHR) {
                var errorMsg = data['errorMsg'];
                if (errorMsg != null) {
                    handleError(ccid, errorMsg, orgCancelButtonText);
                    return;
                } else {
                    uiAux.showCancelResult(ccid, finishedText);
                    jQuery('#cancelButton' + ccid).hide();
                }
            }).fail( function (jqXHR, textStatus, errorThrown) {
                uiAux.showCancelResult(ccid, textStatus);//transport.responseText);
                jQuery('#cancelButton' + ccid).val(orgCancelButtonText);
                jQuery('#cancelButton' + ccid).prop('disabled', false);
            });
        },
        showCancelResult : function (ccid, msg) {

            jQuery('#waitImg' + ccid).hide();
            jQuery('#cancelArea' + ccid).html(msg);
            jQuery('#cancelArea' + ccid).show();
        },
        // pass table css selectors
        // columns in each table will be made to have the same width as the widest element in that column across all tables
        alignTableColumnsByTable : function () {

            var tableSelectors = Array.prototype.slice.call(arguments, 0),
                tablesToAlign,
                columnSizes;
            jQuery( function () {
                columnSizes = [];
                tablesToAlign = jQuery(tableSelectors).map( function (ind, tab) {
                    return jQuery(tab)[0];
                });
                tablesToAlign.each( function (ind, table) {
                    var rowsToAlign = jQuery(table).find('tr');
                    rowsToAlign.each( function (idx, tr) {
                        var cells = jQuery(tr).find('td'),
                            cell,
                            index;
                        for (index = 0; index < cells.length - 1; index += 1) {
                            cell = cells[index];
                            if (!columnSizes[index] || cell.getWidth() > columnSizes[index]) {
                                columnSizes[index] = cell.getWidth();
                            }
                        };
                    });
                });
                tablesToAlign.each( function (ind, table) {
                    var rowsToAlign = jQuery(table).find('tr');
                    rowsToAlign.each( function(idx, tr) {
                        var cells = jQuery(tr).find('td'),
                            cell,
                            index = 0;
                        for (index = 0; index < cells.length - 1; index += 1) {
                            cell = cells[index];
                            cell.setStyle({width: columnSizes[index]+'px'});
                        };
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
            var input = document.getElementById(inputElement),
                input2 = document.createElement('input');
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
        },
        /*
         * This allows the picker (and anything else that might need it) to distinguish
         * between a tag being called on a main page (window.loadComplete will be false)
         * and a tag being used in page loaded as a result of an openSimpleDialog call
         * (which happens after the window has loaded completely).
         */
        loadComplete : false,
        /**
         * Use this method to call a function after a page has been completely loaded.  If this method
         * is called after the window has already been loaded, the method is called immediately.
         */
        callAfterMainWindowLoad : function (func) {
            if (uiAux.loadComplete) {
                func();
            } else {
                jQuery(function() {
                    func();
                });
            }
        }
    };
    return uiAux;
})();

jQuery(function() {
    yukon.ui.aux.loadComplete = true;
});

/**
 * Flashes the background of an element 'Yukon yellow'
 * @param element    [DOM node] we want to give a temporary splash of yellow
 * @param duration  [float] how long the effect should last in seconds
 */
function flashYellow(element, duration) {
    jQuery(element).flashYellow(duration);
}

/** Section containers with show/hide behavior */
jQuery(function() {
    jQuery(document).on('click', '.toggle-title', function (event) {
        jQuery(event.currentTarget).closest('.titled-container').each(function(index, elem){
            jQuery(elem).toggleClass("collapsed");
       });
    });
});

