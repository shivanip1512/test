/**
 * Singleton that contains more ui utility functionality
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ui');
yukon.namespace('yukon.ui.util');

yukon.ui.util = (function () {
    var mod;
    mod = {
            
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
            if (index[index.length-1] == selectElement.options.length - 1 && direction == 1) { 
                return false;
            }
            
            //all the options of the select
            options = selectElement.options;
            
            //clone the select element - for ie fix 
            copy = $.makeArray(options);
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
            firstOptGroup = $(selectObj).find("optgroup")[0];

            // get first option in first group
            firstGroupOptions = $(firstOptGroup).find("option");

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
            var orgCancelButtonText = $('#cancelButton' + ccid).val(),
                args;

            // swap to wait img, disable button
            $('#waitImg' + ccid).show();
            $('#cancelButton' + ccid).prop('disabled', true);
            $('#cancelButton' + ccid).val(cancelingText);

            // run cancel    
            var args = {};
            args.resultId = resultId;
            // setup callbacks
            $.ajax({
                type: "POST",
                url: url,
                data: args
            }).done( function (data, textStatus, jqXHR) {
                var errorMsg = data['errorMsg'];
                if (errorMsg != null) {
                    handleError(ccid, errorMsg, orgCancelButtonText);
                    return;
                } else {
                    mod.showCancelResult(ccid, finishedText);
                    $('#cancelButton' + ccid).hide();
                }
            }).fail( function (jqXHR, textStatus, errorThrown) {
                mod.showCancelResult(ccid, textStatus);//transport.responseText);
                $('#cancelButton' + ccid).val(orgCancelButtonText);
                $('#cancelButton' + ccid).prop('disabled', false);
            });
        },
        
        showCancelResult : function (ccid, msg) {

            $('#waitImg' + ccid).hide();
            $('#cancelArea' + ccid).html(msg);
            $('#cancelArea' + ccid).show();
        },
        
        // pass table css selectors
        // columns in each table will be made to have the same width as the widest element in that column across all tables
        alignTableColumnsByTable : function () {

            var tableSelectors = Array.prototype.slice.call(arguments, 0),
                tablesToAlign,
                columnSizes;
            $( function () {
                columnSizes = [];
                tablesToAlign = $(tableSelectors).map( function (ind, tab) {
                    return $(tab)[0];
                });
                tablesToAlign.each( function (ind, table) {
                    var rowsToAlign = $(table).find('tr');
                    rowsToAlign.each( function (idx, tr) {
                        var cells = $(tr).find('td'),
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
                    var rowsToAlign = $(table).find('tr');
                    rowsToAlign.each( function(idx, tr) {
                        var cells = $(tr).find('td'),
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
        
        createHtmlTableFromJson: function (dataArray, outputCols, rowCallback) {
            var resultTable = document.createElement("table"),
                resultTableHead = document.createElement("thead"),
                resultTableBody,
                headRow,
                col,
                headCell,
                i,
                tableRow,
                tableCell,
                node,
                link,
                linkFuncGenerate,
                linkFunc,
                dataString,
                displayDataString,
                maxLen,
                text;
                
            resultTable.appendChild(resultTableHead);
            headRow = document.createElement("tr");
            resultTableHead.appendChild(headRow);
            for (col = 0; col < outputCols.length; col++) {
                headCell = document.createElement("th");
                headCell.appendChild(document.createTextNode(outputCols[col].title));
                headRow.appendChild(headCell);
            }
            resultTableBody = document.createElement("tbody");
            resultTable.appendChild(resultTableBody);
            for (i = 0; i < dataArray.length; i++) {
                tableRow = document.createElement("tr");
                (rowCallback || yukon.nothing)(tableRow, dataArray[i]);
                resultTableBody.appendChild(tableRow);
                for (col = 0; col < outputCols.length; col++) {
                    tableCell = document.createElement("td");
                    node = tableCell;
                    tableRow.appendChild(tableCell);
                    if (outputCols[col].link) {
                        link = document.createElement("a");
                        node = link;
                        tableCell.appendChild(link);
                        linkFuncGenerate = outputCols[col].link;
                        linkFunc = linkFuncGenerate(dataArray[i], link);
        
                        if (linkFunc !== null) {
                            link.setAttribute("href", "javascript:void(0)");
                            $(link).on('click', linkFunc);
                        }
                    }
        
                    dataString = dataArray[i][outputCols[col].field];
                    if (dataString === undefined) {
                        dataString = outputCols[col].field;
                    }
                    displayDataString = dataString;
                    maxLen = outputCols[col].maxLen;
                    if (maxLen && dataString.length > maxLen) {
                        displayDataString = dataString.truncate(maxLen, '...');
                        tableCell.setAttribute("title", dataString);
                    }
        
                    //in case we want to display static text but maintain a value
                    text = document.createTextNode(displayDataString);
                    node.appendChild(text);
                }
            }
            
            return resultTable;
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
        
        updateDynamicChoose: function (spanId) {
            //assumes data is of type Javascript object
            return function(data) {
                var showId = document.getElementById(spanId + data.state);
                $(document.getElementById(spanId)).children().hide();
                $(showId).show();
            };
        },
        
        deviceCollectionChosen: function(uniqueId, type) {
            $('#collection-type-' + uniqueId).val(type);
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
            if (mod.loadComplete) {
                func();
            } else {
                $(function() {
                    func();
                });
            }
        },
        
        cronExpFreqChange : function (id, sel) {
            var selectedFreqVal = sel.options[sel.selectedIndex].value,
                cronTime = $('#' + id + '_cronExpTimeDiv'),
                cronDaily = $('#' + id + '_cronExpDailyDiv'),
                cronWeekly = $('#' + id + '_cronExpWeeklyDiv'),
                cronMonthly = $('#' + id + '_cronExpMonthlyDiv'),
                cronOneTime = $('#' + id + '_cronExpOneTimeDiv'),
                cronCustom = $('#' + id + '_cronExpCustomDiv'),
                cronFuncs = [cronTime, cronDaily, cronWeekly, cronMonthly, cronOneTime, cronCustom],
                plan = [],
                cfi;
            switch (selectedFreqVal) {
            case 'DAILY':
                plan = ['s', 's', 'h', 'h', 'h', 'h'];
                break;
            case 'WEEKLY':
                plan = ['s', 'h', 's', 'h', 'h', 'h'];
                break;
            case 'MONTHLY':
                plan = ['s', 'h', 'h', 's', 'h', 'h'];
                break;
            case 'ONETIME':
                plan = ['s', 'h', 'h', 'h', 's', 'h'];
                break;
            case 'CUSTOM':
                plan = ['h', 'h', 'h', 'h', 'h', 's'];
                break;
            default:
                return;
            }
            for (cfi = 0; cfi < plan.length; cfi += 1) {
                cronFuncs[cfi]['s' === plan[cfi] ? 'show' : 'hide']();
            }
        },
        
        /** 
         * Jquery (and plain old javascript) returns color values in rgb format:
         *  rgb(0, 153, 51)
         * This function will convert it to this format:
         *  #009933
         */
        rgbToHex: function (rgb) {
            var compositeRgb,
                hex = function (x) {
                    return ('0' + parseInt(x, 10).toString(16)).slice(-2);
                };
            if ('undefined' === typeof rgb || '' === rgb || null === rgb) {
                return '#000000';
            }
            
            // IE8 returns color in hex
            if (rgb.match(/^#[\da-f]{6}$/)) {
                return rgb;
            }
            
            rgb = rgb.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+))?\)$/);
            compositeRgb = hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
            compositeRgb = compositeRgb.toLowerCase();
            return '#' + compositeRgb;
        }
        
    };
    
    return mod;
})();

$(function() { yukon.ui.util.loadComplete = true; });

/**
 * Module to add behavior to schedule file export inputs in the
 * scheduledFileExportInputs.tag file. 
 * 
 * Call yukon.tag.scheduledFileExportInputs.initializeFields after response
 * complete when using this tag in an ajax response.
 * 
 * @module yukon.tag.scheduledFileExportInputs
 * @requires JQUERY
 * @requires yukon
 */
yukon.namespace('yukon.tag.scheduledFileExportInputs');
yukon.tag.scheduledFileExportInputs = (function () {
    var 
    
    _lastDisplayName = false,
    
    _toggleField = function(checkBoxId, changeItemId) {
        if ($(checkBoxId).is(":checked")) {
            $(changeItemId).prop("disabled", false).closest("tr").show(250);
        } else {
            $(changeItemId).prop("disabled", true).closest("tr").hide();
        }
    },

    _toggleTimestampPatternField = function() {
        _toggleField("#appendDateToFileName", "#timestampPatternField");
    },

    _toggleFileExtensionField = function() {
        _toggleField("#overrideFileExtension", "#exportFileExtension");
    },

    _toggleExportPathField = function() {
        _toggleField("#includeExportCopy", "#exportPath");
    },

    _nameChanged = function() {
        if ($("#sameAsSchedName").is(":checked")) {
            $("#exportFileName").val($("#scheduleName").val());
        }
    },
    
    _sameAsNameClicked = function() {
        if ($("#sameAsSchedName").is(":checked")) {
            _lastDisplayName = $("#exportFileName").val();
            $("#exportFileName").val($("#scheduleName").val());
            $("#exportFileName").prop("disabled", true);
        } else {
            if (_lastDisplayName) {
                $("#exportFileName").val(_lastDisplayName);
            }
            $("#exportFileName").prop("disabled", false);
        }
    },

    _intializeAllFields = function () {
        _toggleTimestampPatternField();
        _toggleFileExtensionField();
        _toggleExportPathField();
        _sameAsNameClicked();
    },

    mod = {
        initializeFields: function () {
            _intializeAllFields();
        },
        
        init: function () {
            $(document).on('click', "#appendDateToFileName", _toggleTimestampPatternField);
            $(document).on('click', "#overrideFileExtension", _toggleFileExtensionField);
            $(document).on('click', "#includeExportCopy", _toggleExportPathField);
            $(document).on('keyup', "#scheduleName", _nameChanged);
            $(document).on('change', "#scheduleName", _nameChanged);
            $(document).on('click', "#sameAsSchedName", _sameAsNameClicked);
            _intializeAllFields();
        }
    };

    return mod;
}());
$(function () { yukon.tag.scheduledFileExportInputs.init(); });

/**
 * Flashes the background of an element 'Yukon yellow'
 * @param {DOM node} element - We want to give a temporary splash of yellow
 * @param {float} [duration] - How long the effect should last in seconds.
 */
function flashYellow(element, duration) {
    $(element).flashYellow(duration);
}

/** Section containers with show/hide behavior */
$(function() {
    $(document).on('click', '.toggle-title', function (event) {
        $(event.currentTarget).closest('.titled-container').each(function(index, elem){
            $(elem).toggleClass("collapsed");
       });
    });
});