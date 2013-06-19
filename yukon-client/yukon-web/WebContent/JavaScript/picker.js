Picker = Class.create();

/** 
 * If "memoryGroup" is set to something other than false, a picker will
 * check here for the last search using memoryGroup as the key.  This allows
 * multiple pickers on the same page to share the last search text.
 */
Picker.rememberedSearches = {};

Picker.prototype = {
    /**
     * Instantiate the picker.
     * 
     * - okText is the text for the ok button
     * - cancelText is the text for the cancel button
     * - pickerType is the name of the picker bean.
     * - destinationFieldName is the name the picker will use when creating
     *   hidden form fields to store the selected items.
     * - pickerId is used to uniquely identify the picker.  This should
     *   be the same as the name of the Javascript variable the picker
     *   is stored in so the picker can close itself. 
     * - extraDestinationFields are used when a selection has been made
     *   and the picker is closed.  It's a semicolon separated list of:
     *     [property]:[fieldId]
     *   where fieldId specifies the id of an HTML element to be updated
     *   and property specifies a property of the data to be placed into
     *   this HTML element (using innerHTML).
     */
    initialize: function (okText, cancelText, pickerType, destinationFieldName, pickerId, extraDestinationFields, containerDiv) {
        var index,
            pairs,
            pair,
            extraDestinationField;
        this.okText = okText;
        this.cancelText = cancelText;
        this.pickerType = pickerType;
        this.destinationFieldName = destinationFieldName;
        this.pickerId = pickerId;
        this.excludeIds = [];
        this.multiSelectMode = false;
        this.immediateSelectMode = false;
        this.memoryGroup = false;
        this.resetSearchFields();
        this.selectedItems = [];
        this.extraDestinationFields = [];
        if (extraDestinationFields) {
            pairs = extraDestinationFields.split(/;/);
            for (index = 0; index < pairs.length; index++) {
                pair = pairs[index].split(/:/);
                if (pair.length == 2) {
                    extraDestinationField = {
                        'property': pair[0],
                        'fieldId': pair[1]
                    };
                    this.extraDestinationFields.push(extraDestinationField);
                }
            }
        }
        this.allowEmptySelection = false;

        this.resultAreaId = 'picker_' + this.pickerId + '_resultArea';
        this.resultAreaFixedId = 'picker_' + this.pickerId + '_resultAreaFixed';
        this.errorHolderId = 'picker_' + this.pickerId + '_errorHolder';
        this.primed = false;
        this.useInitialIdsIfEmpty = false;
        this.containerDiv = containerDiv;
        this.inline = containerDiv !== null;
        
        this.useNewBind = true; // TODO: debug, remove
        this.usejquery = true; // TODO: debug, remove
    },

    /**
     * Reset things that need to be reset when first popping up the picker
     * or when clearing it.
     */
    resetSearchFields: function() {
        this.currentSearch = '';
        this.inSearch = false;
        this.previousIndex = -1;
        this.nextIndex = -1;
        this.hitCount = 0;
        if (this.ssInput) {
            this.ssInput.value = '';
        }
    },

    /**
     * Attached to the onkeyup event of the search text input field.
     */
    doKeyUp: function() {
        var pickerThis = this,
            timerFunction = function() {
                var ss = pickerThis.ssInput.value;
                console.log("timerFunction: ss=" + ss);
                // Don't do the search if it hasn't changed.  This could be
                // because they type a character and deleted it.
                if (!pickerThis.inSearch && pickerThis.currentSearch != ss) {
                    pickerThis.doSearch();
                }
            },
            quietDelay = 300,
            ss;
        jQuery(this.nothingSelectedDiv).hide();
        // Don't do the search if it hasn't changed.  This can happen if
        // the use the cursor key or alt-tab to another window and back.
        ss = this.ssInput.value;
        if (this.currentSearch != ss) {
            this.block();
            setTimeout(timerFunction, quietDelay);
        }
    },

    /**
     * Actually do the search.  This method sets up a request to the server
     * for new data based on the entered search text.
     * 
     * - start is the index of the first result to show.
     */
    doSearch: function(start, count, onComplete, endCallback) {
        var ss,
            parameters = {},
            onFailure = null;
        this.inSearch = true;
        this.block();
        ss = this.ssInput.value;
        if (ss) {
            jQuery(this.showAllLink).show();
        } else {
            jQuery(this.showAllLink).hide();
        }
        this.currentSearch = ss;
        if (this.memoryGroup) {
            Picker.rememberedSearches[this.memoryGroup] = ss;
        }

        parameters = {
            'type' : this.pickerType,
            'ss' : ss
        };
        if (start) {
            parameters.start = start;
        }
        if (this.extraArgs) {
            parameters.extraArgs = this.extraArgs;
        }

        if (count) {
            parameters.count = count;
        }

        if (!onComplete) {
            if(this.useNewBind)
                onComplete = myBind(this.updateSearchResults, this);
            else
                onComplete = this.updateSearchResults.bind(this);
        }

        function doOnComplete(transport) {
            try {
                onComplete(transport);
            } catch(callbackEx) { console.log("callback exception: " + callbackEx); }
            if (endCallback) {
                endCallback();
            }
        }

        if (null === onFailure) {
            if (this.useNewBind)
                onFailure = myBind(this.ajaxError, this);
            else
                onFailure = this.ajaxError.bind(this);
        } 
        new Ajax.Request('/picker/v2/search', { // TODO: jQuerify
            'method': 'get',
            'parameters': parameters,
            'onComplete': doOnComplete,
            'onFailure': onFailure
            });
    },

    /**
     * This method does secondary initialization which needs to happen after
     * the HTML elements in the tag file have been fully created.  This method
     * should only be called from the pickerDialog.tag.  Use viewMode when
     * the picker should be a read-only view.
     */
    init : function(viewMode) {
        //this.inputAreaDiv = $('picker_' + this.pickerId + '_inputArea');
        this.inputAreaDiv = jQuery('#picker_' + this.pickerId + '_inputArea')[0];
        if (!viewMode) {
            if (this.selectionProperty) {
                //this.selectedItemsPopup = $('picker_' + this.pickerId + '_selectedItemsPopup'); // TODO: jQuerify
                //this.selectedItemsDisplayArea = $('picker_' + this.pickerId + '_selectedItemsDisplayArea'); // TODO: jQuerify
                //var showSelectedImg = $('picker_' + this.pickerId + '_showSelectedImg'); // TODO: jQuerify
                this.selectedItemsPopup = jQuery('#picker_' + this.pickerId + '_selectedItemsPopup')[0]; // TODO: jQuerify
                this.selectedItemsDisplayArea = jQuery('#picker_' + this.pickerId + '_selectedItemsDisplayArea')[0]; // TODO: jQuerify
                var showSelectedImg = jQuery('#picker_' + this.pickerId + '_showSelectedImg')[0]; // TODO: jQuerify
                console.log("showSelectedImg is " + (showSelectedImg ? " " : "NOT ") + "truthy");
                if (showSelectedImg) {
                    //this.showSelectedLink = $(showSelectedImg.parentNode);
                    this.showSelectedLink = showSelectedImg.parentNode; // TODO: jQuerify
                    //this.showSelectedLink.hide(); // TODO: jQuerify
                    jQuery(this.showSelectedLink).hide(); // TODO: jQuerify
                }
            }
        }
        if (this.selectionProperty) {
            this.selectionLabel = $('picker_' + this.pickerId + '_label').getElementsBySelector('span')[0]; // TODO: jQuerify
            //this.originalSelectionLabel = this.selectionLabel.innerHTML; // TODO: jQuerify
            jqLabel = jQuery('#picker_' + this.pickerId + '_label');
            jqSpan = jQuery('span', jQuery('#picker_' + this.pickerId + '_label'))[0]; // TODO: jQuerify
            console.log("proto label span");
            console.dir(this.selectionLabel);
            console.log("jquery label span");
            console.dir(jqSpan);
            console.log("proto span html=" + this.selectionLabel.innerHTML);
            console.log("jquery span html=" + jQuery(jqSpan).html());
            this.originalSelectionLabel = jQuery(jqSpan).html(); //this.selectionLabel.innerHTML; // TODO: jQuerify
        }

        var initialIds = []; // TODO: move var up
        if (this.destinationFieldId) {
            console.log("I knew it. destinationFieldId always tests true: this.destinationFieldId=" +
                this.destinationFieldId + " $F(this.destinationFieldId)=");
            console.dir($F(this.destinationFieldId));
            if ($F(this.destinationFieldId)) { // TODO: jQuerify
                console.log("$F(this.destinationFieldId) was also true");
                initialIds = $F(this.destinationFieldId).split(','); // TODO: jQuerify
            }
        } else {
            console.log("destinationFieldId tested false! proceeding with pluck");
            initialIds = this.inputAreaDiv.getElementsBySelector('input').pluck('value'); // TODO: jQuerify
            console.log("proto pluck");
            console.dir(initialIds);
            console.log("jQuery fake pluck");
            console.dir(jQuery.map(jQuery(':input',this.inputAreaDiv), function(val, index) {
                return val.value;
            }));
            if(0 < initialIds.length) {
                console.log("got populated pluck! length=" + initialIds.length);
            }
        }

        if (viewMode) {
            this.doIdSearch(initialIds);
        } else {
            if (initialIds && initialIds.length > 0) {
                this.prime(false, initialIds);
            } else if (this.useInitialIdsIfEmpty) {
                this.selectedItems = [];
            }
        }
    },

    /**
     * This method primes the picker, performing the initial search if necessary
     * and populating the dialog with the results of pickerDialog.jsp.  If the
     * picker has initially selected ids, this needs to be called on initial
     * page load to correctly populate the data outside the picker but otherwise,
     * it doesn't need to be done until the picker is first used (which may
     * never happen).
     */
    prime: function(showPicker, initialIds, skipFocus) {
        var bodyElem = document.documentElement.getElementsByTagName('body')[0],
            pickerDialogDivContainer,
            parameters = {
                'type' : this.pickerType,
                'id' : this.pickerId,
                'multiSelectMode' : this.multiSelectMode,
                'immediateSelectMode' : this.immediateSelectMode
            },
            onCompleteBind;
        if (this.containerDiv) {
            pickerDialogDivContainer = this.containerDiv;
        } else {
            pickerDialogDivContainer = document.createElement('div');
            bodyElem.appendChild(pickerDialogDivContainer);
        }
        if (this.extraArgs) {
            parameters.extraArgs = this.extraArgs;
        }
        if (this.containerDiv) {
            parameters.mode = 'inline';
        }

        if(this.useNewBind) {
            console.log("calling myBind for onPrimeComplete");
            onCompleteBind = myBind(this.onPrimeComplete, this, showPicker, initialIds, skipFocus);
        } else {
            console.log("calling prototype.js bind for onPrimeComplete");
            onCompleteBind = this.onPrimeComplete.bind(this, showPicker, initialIds, skipFocus);
        }
        //new Ajax.Updater(pickerDialogDivContainer, '/picker/v2/build', { // TODO: jQuerify
        //    'parameters': parameters,
        //    'evalScripts': true,
        //    'onComplete': onCompleteBind // this.onPrimeComplete.bind(this, showPicker, initialIds, skipFocus) // TODO: jQuerify
        //});
        jQuery(pickerDialogDivContainer).load('/picker/v2/build', parameters,
            onCompleteBind);
            //myBind(this.onPrimeComplete, this, showPicker, initialIds, skipFocus));
            //this.onPrimeComplete.bind(this, showPicker, initialIds, skipFocus));
    },

    /**
     * This is the primary externally called method.  It pops the picker up
     * and does the initial search.
     */
    show: function(skipFocus) {
        if (this.immediateSelectMode && this.multiSelectMode) {
            alert('immediateSelectMode cannot be used with multiSelectMode; ' + 
                'turning multiSelectMode off');
            this.multiSelectMode = false;
        }

        // forget jQuery here. We know selectedItems is an Array, so just
        // make a shallow copy
        this.lastSelectedItems = this.selectedItems.slice(0); //this.selectedItems.clone(); // TODO: jQuerify
        this.resetSearchFields();
        this.clearSearchResults();
        if (!this.primed) {
            this.prime(true, null, skipFocus);
        } else {
            this.doShow(skipFocus);
        }
    },

    doShow : function(skipFocus) {
        if (this.memoryGroup && Picker.rememberedSearches[this.memoryGroup]) {
            this.ssInput.value = Picker.rememberedSearches[this.memoryGroup];
        }
        //this.nothingSelectedDiv.hide(); // TODO: jQuerify
        jQuery(this.nothingSelectedDiv).hide(); // TODO: jQuerify
        if (!skipFocus) {
            //this.ssInput.focus(); // TODO: jQuerify
            jQuery(this.ssInput).focus(); // TODO: jQuerify
        }
        var that = this;
        this.doSearch(false, false, null, function() {
            if (!this.containerDiv) {
                var buttons = [{'text' : that.cancelText, 'click' : function() {that.cancel();}},
                               {'text' : that.okText, 'click' : function() {that.okPressed();}, 'class': 'primary action'}];
                if (!that.inline) {
                    jQuery('#' + that.pickerId).dialog({buttons : buttons, width : 600, height : 'auto'});
                }
            }
        });
    },

    //onPrimeComplete : function(showPicker, initialIds, skipFocus, transport, json) {
    onPrimeComplete : function(showPicker, initialIds, skipFocus, xhr, unused, req) {
        var json = getHeaderJSON(req);
        var ai;
        console.log("onPrimeComplete : dumping " + arguments.length + " arguments");
        for(ai = 0; ai < arguments.length; ai += 1) {
            console.dir(arguments[ai]);
        }
        if (this.usejquery) {
            this.ssInput = jQuery('#picker_' + this.pickerId + '_ss')[0]; // TODO: jQuerify
            this.showAllLink = jQuery('#picker_' + this.pickerId + '_showAllLink')[0]; // TODO: jQuerify
            this.resultsDiv = jQuery('#picker_' + this.pickerId + '_results')[0]; // TODO: jQuerify
            this.noResultsDiv = jQuery('#picker_' + this.pickerId + '_noResults')[0]; // TODO: jQuerify
            this.nothingSelectedDiv = jQuery('#picker_' + this.pickerId + '_nothingSelected')[0]; // TODO: jQuerify
            this.selectAllCheckBox = jQuery('#picker_' + this.pickerId + '_selectAll')[0]; // TODO: jQuerify
            this.selectAllPagesLink = jQuery('#picker_' + this.pickerId + '_selectAllPages')[0]; // TODO: jQuerify
            this.allPagesSelected = jQuery('#picker_' + this.pickerId + '_allPagesSelected')[0]; // TODO: jQuerify
            this.clearEntireSelectionLink = jQuery('#picker_' + this.pickerId + '_clearEntireSelection')[0]; // TODO: jQuerify
            this.entireSelectionCleared = jQuery('#picker_' + this.pickerId + '_entireSelectionCleared')[0]; // TODO: jQuerify
        } else {
            this.ssInput = $('picker_' + this.pickerId + '_ss'); // TODO: jQuerify
            this.showAllLink = $('picker_' + this.pickerId + '_showAllLink'); // TODO: jQuerify
            this.resultsDiv = $('picker_' + this.pickerId + '_results'); // TODO: jQuerify
            this.noResultsDiv = $('picker_' + this.pickerId + '_noResults'); // TODO: jQuerify
            this.nothingSelectedDiv = $('picker_' + this.pickerId + '_nothingSelected'); // TODO: jQuerify
            this.selectAllCheckBox = $('picker_' + this.pickerId + '_selectAll'); // TODO: jQuerify
            this.selectAllPagesLink = $('picker_' + this.pickerId + '_selectAllPages'); // TODO: jQuerify
            this.allPagesSelected = $('picker_' + this.pickerId + '_allPagesSelected'); // TODO: jQuerify
            this.clearEntireSelectionLink = $('picker_' + this.pickerId + '_clearEntireSelection'); // TODO: jQuerify
            this.entireSelectionCleared = $('picker_' + this.pickerId + '_entireSelectionCleared'); // TODO: jQuerify
        }
        if (json !== null) {
            this.outputColumns = json.outputColumns;
            this.idFieldName = json.idFieldName;
        }

        this.doIdSearch(initialIds);

        if (showPicker) {
            this.doShow(skipFocus);
        }
        this.primed = true;
    },

    doIdSearch : function(selectedIds) {
        if (selectedIds && selectedIds.length > 0) {
            var parameters = {
                    'type' : this.pickerType,
                    'id' : this.pickerId,
                    'initialIds' : selectedIds
                };
            if (this.extraArgs) {
                parameters.extraArgs = this.extraArgs;
            }

            new Ajax.Request('/picker/v2/idSearch', {
                'method' : 'post',
                'parameters': parameters,
                'onComplete': myBind(this.onIdSearchComplete, this) // this.onIdSearchComplete.bind(this) // TODO: jQuerify
            });
        }
    },

    onIdSearchComplete : function(transport) {
        var json = JSON.parse(transport.responseText); //transport.responseText.evalJSON(); // TODO: use JSON.parse
        if (json && json.hits && json.hits.resultList) {
            this.selectedItems = json.hits.resultList;
            if (this.showSelectedLink) jQuery(this.showSelectedLink).show(); //this.showSelectedLink.show(); // TODO: jQuerify
        }
        this.updateOutsideFields(true);
    },

    /**
     * Cancel the current picker and pop it down.
     */
    cancel: function() {
        this.selectedItems = this.lastSelectedItems;
        if (!this.containerDiv) {
            jQuery('#' + this.pickerId).dialog('close');
        }
        if (this.cancelAction) {
            this.cancelAction(this);
        }
    },

    okPressed: function() {
        var fieldName,
            pickerThis;

        console.dir(this.selectedItems);
        if (!this.allowEmptySelection && this.selectedItems.size() === 0) { // TODO: what is size()?
            //this.nothingSelectedDiv.show(); // TODO: jQuerify
            console.log("showing nothing selected div"); // have not been able to trigger this codepath
            jQuery(this.nothingSelectedDiv).show();
        } else {
            if (this.destinationFieldId) { // TODO: true if "": is this intended?
                console.log("this.destinationFieldId set! : " + this.destinationFieldId);
                fieldName = this.idFieldName;
                //$(this.destinationFieldId).value =
                jQuery('#' + this.destinationFieldId).val(
                    jQuery.map(this.selectedItems, function(val, index) {
                        return val[fieldName];
                    }).join(',')
                );
                console.log("awesome call to jQuery.map, result=" + // TODO: remove
                        jQuery('#' + this.destinationFieldId).val());
            } else {
                pickerThis = this;
                this.inputAreaDiv.innerHTML = '';
                this.selectedItems.each(function(selectedItem) { // TODO: jQuerify
                    var inputElement = document.createElement('input');
                    inputElement.type = 'hidden';
                    inputElement.value = selectedItem[pickerThis.idFieldName];
                    inputElement.name = pickerThis.destinationFieldName;
                    pickerThis.inputAreaDiv.appendChild(inputElement);
                });
            }

            if (this.updateOutsideFields(false)) {
                if (!this.containerDiv) {
                    jQuery('#' + this.pickerId).dialog('close');
                }
            }
        }
    },

    updateOutsideFields : function(isInitial) {
        var hit,
            labelMsg,
            index,
            value;
        // protect from calling endAction for empty lists on first run.
        if (this.selectedItems.length === 0 && isInitial) {
            return;
        }

        hit = null;
        if (this.selectedItems.length > 0) {
            hit = this.selectedItems[0];
            if (this.showSelectedLink) jQuery(this.showSelectedLink).show(); // TODO: jQuerify
        } else {
            if (this.showSelectedLink) jQuery(this.showSelectedLink).hide(); // TODO: jQuerify
        }

        if (this.selectionProperty) {
            if (hit === null) {
                this.selectionLabel.innerHTML = this.originalSelectionLabel;
                jQuery(this.selectionLabel).addClass('noSelectionPickerLabel'); // TODO: jQuerify, or not
            } else {
                var jqlabel = jQuery("<div>").text(hit[this.selectionProperty].toString()).html();
                labelMsg = hit[this.selectionProperty].toString().escapeHTML(); // TODO: jQuerify
                console.log("updateOutsideFields: labelMsg=" + labelMsg + " jqlabel=" + jqlabel);
                if (this.selectedItems.length > 1) {
                     labelMsg +=  ' ' + this.selectedAndMsg + ' ' +
                         (this.selectedItems.length - 1) + ' ' +
                         this.selectedMoreMsg;
                }
                this.selectionLabel.innerHTML = labelMsg;
                this.selectionLabel.removeClassName('noSelectionPickerLabel'); // TODO: jQuerify, or not
            }
        }

        for (index = 0; index < this.extraDestinationFields.length; index++) {
            extraDestinationField = this.extraDestinationFields[index];
            value = hit === null ? '' : hit[extraDestinationField.property];
            
            // support for both innerHTML and value setting
            if ($(extraDestinationField.fieldId).tagName === 'INPUT') { // TODO: jQuerify
                $(extraDestinationField.fieldId).value = value; // TODO: jQuerify
            }
            else {
                $(extraDestinationField.fieldId).innerHTML = value; // TODO: jQuerify
            }
        }
        if (this.endAction) {
            this.endAction(this.selectedItems, this);
        }
        return true;
    },

    clearSelected: function() {
        if (this.destinationFieldId) {
            $(this.destinationFieldId).value = ''; // TODO: jQuerify
        } else {
            this.inputAreaDiv.innerHTML = ''; // TODO: jQuerify
        }
        if (this.selectionProperty) {
            this.selectionLabel.innerHTML = this.originalSelectionLabel; // TODO: jQuerify
            this.selectionLabel.addClassName('noSelectionPickerLabel'); // TODO: jQuerify, or not
        }
    },

    /**
     * Get the id(s) of the selected item(s).
     * If this is a multi-select picker, an array will be returned, 
     * but in single select mode a single selected item will be returned.
     */
    getSelected: function() {
        var retVal = this.destinationFieldId
            ? $F(this.destinationFieldId).split(',') // TODO: jQuerify
            : this.inputAreaDiv.getElementsBySelector('input').pluck('value'); // TODO: jQuerify
        return this.multiSelectMode ? retVal : retVal[0];
    },

    previous: function() {
        if (this.previousIndex == -1) {
            return;
        }
        this.ssInput.focus(); // TODO: jQuerify, or not
        this.doSearch(this.previousIndex);
    },

    next: function() {
        if (this.nextIndex == -1) {
            return;
        }
        this.ssInput.focus(); // TODO: jQuerify, or not
        this.doSearch(this.nextIndex);
    },

    showAll: function() {
        if (this.memoryGroup) {
            Picker.rememberedSearches[this.memoryGroup] = '';
        }
        this.resetSearchFields();
        this.ssInput.focus(); // TODO: jQuerify, or not
        this.doSearch();
    },

    /**
     * Clear any old search results (used when the dialog is first popped up).
     */
    clearSearchResults: function() {
        var oldResultArea,
            resultHolder;
        if (!this.resultsDiv) {
            // our first time up; nothing to do
            return;
        }
        this.updatePagingArea();
        oldResultArea = $(this.resultAreaId); // TODO: jQuerify
        resultHolder = this.resultsDiv;
        if (oldResultArea) {
            resultHolder.removeChild(oldResultArea);
        }
    },

    /**
     * Update the UI with data from the search results.  This method calls
     * renderTalbeResults to update the table itself and then updates the
     * previous and next buttons appropriately.
     */
    updateSearchResults: function(transport) { // TODO: move vars to top of function
        var json = transport.responseText.evalJSON(); // TODO: use JSON.parse
        var newResultArea = this.renderTableResults(json);
        this.updatePagingArea(json);
        this.selectAllPagesMsg = json.selectAllPages;
        this.allPagesSelectedMsg = json.allPagesSelected;
        var oldResultArea = $(this.resultAreaId); // TODO: jQuerify
        var resultHolder = this.resultsDiv;
        if (oldResultArea) {
            resultHolder.removeChild(oldResultArea);
        }
        var oldError = $(this.errorHolderId); // TODO: jQuerify
        if (oldError) {
            resultHolder.removeChild(oldError);
        }
        resultHolder.appendChild(newResultArea);
        this.updateSelectAllCheckbox();

        var ss = this.ssInput.value;
        this.unblock();
        if (this.currentSearch != ss) {
            // do another search
            this.doSearch();
        } else {
            this.inSearch = false;
        }
    },

    ajaxError: function(transport) { // TODO: jQuerify
        this.inSearch = false;
        this.unblock();
        this.resultsDiv.innerHTML = ''; // TODO: jQuerify
        errorHolder = document.createElement('div');
        errorHolder.id = this.errorHolderId;
        errorHolder.innerHTML = 'There was a problem searching the index: ' + // TODO: jQuerify
        transport.responseText;
        this.resultsDiv.appendChild(errorHolder);
    },

    /**
     * Render the table portion of the search results.
     */
    renderTableResults: function(json) {
        var hitList = json.hits.resultList;
        var resultArea = document.createElement('div');
        resultArea.id = this.resultAreaId;
        var resultAreaFixed = document.createElement('div');
        resultAreaFixed.id = this.resultAreaFixedId;
        resultArea.appendChild(resultAreaFixed);

        this.allLinks = [];
        if (hitList && hitList.length && hitList.length > 0) {
            this.noResultsDiv.hide(); // TODO: jQuerify
            this.resultsDiv.show(); // TODO: jQuerify
            var pickerThis = this;
            var createItemLink = function(hit, link) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) !== -1) {
                    return null;
                } else {
                    pickerThis.allLinks.push({'hit' : hit, 'link' : link});
                    return function() {
                        pickerThis.selectThisItem(hit, link);
                    };
                }
            };

            var outputColumns = [];
            this.outputColumns.each(function(outputColumn) {
                var translatedColumn = {
                     'title': outputColumn.title,
                     'field': outputColumn.field,
                     'link': createItemLink
                 };
                 if (outputColumn.maxCharsDisplayed > 0) {
                     translatedColumn.maxLen = outputColumn.maxCharsDisplayed;
                 }
                 outputColumns.push(translatedColumn);
                 // only the first column is a link
                 createItemLink = null;
             });
             var alternateRow = false;
             var processRowForRender = function (rowElement, rowObject) {
                 if (pickerThis.excludeIds.indexOf(rowObject[pickerThis.idFieldName]) != -1) {
                     $(rowElement).addClassName("disabled"); // TODO: jQuerify
                     $(rowElement).setAttribute('title', Picker.alreadySelectedHoverMessage); // TODO: jQuerify
                 } else {
                     pickerThis.selectedItems.each(function(item){
                         if (rowObject[pickerThis.idFieldName] == item[pickerThis.idFieldName]) {
                             $(rowElement).addClassName("highlighted"); // TODO: jQuerify
                         }
                     });
                 }
                 if (alternateRow) {
                     $(rowElement).addClassName("altRow"); // TODO: jQuerify
                 }
                 alternateRow = !alternateRow;
             };

            var resultTable = createHtmlTableFromJson(hitList, outputColumns,
                    processRowForRender);
            resultTable.className = 'compactResultsTable pickerResultTable';
            resultAreaFixed.appendChild(resultTable);
        } else {
            this.noResultsDiv.show(); // TODO: jQuerify
            this.resultsDiv.hide(); // TODO: jQuerify
        }

        return resultArea;
    },

    showSelected: function() {
        var outputColumns = [];
        this.outputColumns.each(function(outputColumn) {
            var translatedColumn = {
                 'title': outputColumn.title,
                 'field': outputColumn.field,
                 'link': null
             };
             if (outputColumn.maxCharsDisplayed > 0) {
                 translatedColumn.maxLen = outputColumn.maxCharsDisplayed;
             }
             outputColumns.push(translatedColumn);
         });
         var alternateRow = false;
         var processRowForRender = function (rowElement, rowObject) {
             if (alternateRow) {
                 $(rowElement).addClassName("altRow"); // TODO: jQuerify
             }
             alternateRow = !alternateRow;
         };

         var tmptable = 
             createHtmlTableFromJson(this.selectedItems, outputColumns, processRowForRender);
         console.log("typeof tmptable=" + (typeof tmptable));
        var resultTable = $(createHtmlTableFromJson(this.selectedItems, // TODO: jQuerify
                outputColumns, processRowForRender));
        resultTable.addClassName('compactResultsTable');
        resultTable.addClassName('pickerResultTable');
        resultTable.addClassName('rowHighlighting');
        this.selectedItemsDisplayArea.innerHTML = ''; // TODO: jQuerify
        this.selectedItemsDisplayArea.appendChild(resultTable);
        jQuery(this.selectedItemsPopup).dialog({minWidth: 400});
    },

    /**
     * Update the paging area of the form for the current search results.
     */
    updatePagingArea: function(json) {
        var pickerDiv = $(this.pickerId); // TODO: jQuerify
        this.previousIndex = -1;
        this.nextIndex = -1;

        if (json) {
            this.hitCount = json.hits.hitCount;
        }
        if (json && json.hits.startIndex > 0) {
            this.previousIndex = json.hits.previousStartIndex;
            pickerDiv.getElementsBySelector('.previousLink.enabledAction')[0].show();
            pickerDiv.getElementsBySelector('.previousLink.disabledAction')[0].hide();
        } else {
            pickerDiv.getElementsBySelector('.previousLink.enabledAction')[0].hide();
            pickerDiv.getElementsBySelector('.previousLink.disabledAction')[0].show();
        }
        if (json && json.hits.endIndex < json.hits.hitCount) {
            this.nextIndex = json.hits.endIndex;
            pickerDiv.getElementsBySelector('.nextLink.enabledAction')[0].show();
            pickerDiv.getElementsBySelector('.nextLink.disabledAction')[0].hide();
        } else {
            pickerDiv.getElementsBySelector('.nextLink.enabledAction')[0].hide();
            pickerDiv.getElementsBySelector('.nextLink.disabledAction')[0].show();
        }

        pickerDiv.getElementsBySelector('.pageNumText')[0].innerHTML = json ? json.pages : ''; // TODO: jQuerify
    },

    removeFromSelectedItems : function(hit) {
        var oldSelectedItems = this.selectedItems;
        this.selectedItems = [];
        for (var index = 0; index < oldSelectedItems.length; index++) {
            if (oldSelectedItems[index][this.idFieldName] != hit[this.idFieldName]) {
                this.selectedItems.push(oldSelectedItems[index]);
            }
        }
    },

    selectAll: function() {
        var pickerThis = this;
        var numSelectedBefore = this.selectedItems.length;
        this.allLinks.each(function(hitRow) {
            var parentRow = $($(hitRow.link).parentNode.parentNode); // TODO: jQuerify
            if (pickerThis.selectAllCheckBox.checked) {
                parentRow.addClassName('highlighted');
                pickerThis.selectedItems.push(hitRow.hit);
            } else {
                parentRow.removeClassName('highlighted');
                pickerThis.removeFromSelectedItems(hitRow.hit);
            }
        });

        $(this.clearEntireSelectionLink.parentNode).hide(); // TODO: jQuerify
        this.entireSelectionCleared.hide();
        $(this.allPagesSelected.parentNode).hide();
        // Cap "select all on every page" at 5000.
        if (this.selectAllCheckBox.checked && this.nextIndex != -1 && this.hitCount <= 5000) {
            this.selectAllPagesLink.innerHTML = this.selectAllPagesMsg; // TODO: jQuerify
            $(this.selectAllPagesLink.parentNode).show(); // TODO: jQuerify
        } else {
            $(this.selectAllPagesLink.parentNode).hide(); // TODO: jQuerify
            if (numSelectedBefore > this.selectedItems.length && this.selectedItems.length > 0) {
                $(this.clearEntireSelectionLink.parentNode).show(); // TODO: jQuerify
            }
        }

        this.ssInput.focus(); // TODO: jQuerify
    },

    selectAllPages: function() {
        this.doSearch(0, -1, this.selectAllOnComplete.bind(this)); // TODO: jQuerify
    },

    selectAllOnComplete: function(transport) {
        var json = transport.responseText.evalJSON(); // TODO: use JSON.parse
        var hitList = json.hits.resultList;
        // remove "exclude items"
        if (this.excludeIds && this.excludeIds.length > 0) {
            this.selectedItems = [];
            var pickerThis = this;
            hitList.each(function(hit) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) === -1) {
                    pickerThis.selectedItems.push(hit);
                }
            });
        } else {
            this.selectedItems = hitList;
        }
        this.inSearch = false;
        this.ssInput.focus();
        $(this.selectAllPagesLink.parentNode).hide(); // TODO: jQuerify
        this.allPagesSelected.innerHTML = this.allPagesSelectedMsg;
        $(this.allPagesSelected.parentNode).show(); // TODO: jQuerify
        this.unblock();
    },

    clearEntireSelection : function() {
        this.selectedItems = [];
        $(this.allPagesSelected.parentNode).hide(); // TODO: jQuerify
        $(this.clearEntireSelectionLink.parentNode).hide(); // TODO: jQuerify
        this.entireSelectionCleared.show(); // TODO: jQuerify
        this.allLinks.each(function(hitRow) {
            $($(hitRow.link).parentNode.parentNode).removeClassName('highlighted'); // TODO: jQuerify
        });
        this.selectAllCheckBox.checked = false;
    },

    updateSelectAllCheckbox: function() {
        if (!this.multiSelectMode) {
            return;
        }
        var allSelected = this.allLinks.length > 0;
        this.allLinks.each(function(hitRow) {
            if (!$($(hitRow.link).parentNode.parentNode).hasClassName('highlighted')) { // TODO: jQuerify
                allSelected = false;
            }
        });
        this.selectAllCheckBox.checked = allSelected;
        $(this.selectAllPagesLink.parentNode).hide(); // TODO: jQuerify
        $(this.allPagesSelected.parentNode).hide(); // TODO: jQuerify
        $(this.clearEntireSelectionLink.parentNode).hide(); // TODO: jQuerify
        this.entireSelectionCleared.hide();
    },

    /**
     * This method called when the user clicks on a row for selection. 
     */
    selectThisItem: function(hit, link) {
        this.nothingSelectedDiv.hide();

        if (this.immediateSelectMode) {
             this.selectedItems = [hit];
             this.okPressed();
            return;
        }

        var parentRow = $($(link).parentNode.parentNode); // TODO: jQuerify
        if (parentRow.hasClassName('highlighted')) {
            // unselect
            parentRow.removeClassName('highlighted');
            this.removeFromSelectedItems(hit);
            this.selectAllCheckBox.checked = false;
        } else {
            // select
            if (!this.multiSelectMode) {
                // not multi-select mode; unselect all others
                var rows = parentRow.parentNode.childNodes;
                for (var index = 0; index < rows.length; index++) {
                    $(rows[index]).removeClassName('highlighted'); // TODO: jQuerify // TODO: jQuerify
                }
                this.selectedItems = [hit];
                parentRow.addClassName('highlighted');
            } else {
                this.selectedItems.push(hit);
                parentRow.addClassName('highlighted');
                this.updateSelectAllCheckbox();
            }
        }
    },

    block : function() {
        Yukon.uiUtils.elementGlass.show(jQuery('#' + this.pickerId + ' .f_block_this'));
    },

    unblock : function() {
        Yukon.uiUtils.elementGlass.hide(jQuery('#' + this.pickerId + ' .f_block_this'));
    }
};

// replacement for prototype.js bind call
function myBind(func, context) {
    console.log("myBind: applying context to func");
    var nargs = arguments.length,
        args = Array.prototype.slice.call(arguments),
        extraArgs = args.slice(2, args.length);
    console.log("myBind: nargs=" + nargs + " extraArgs.length=" + extraArgs.length);
    return function() {
        var addIndex,
            argLength;
        if(nargs > 2) {
            // tack on addition arguments
            console.log("we have " + (nargs - 2) + " additional args to add");
            console.log("arguments.length=" + arguments.length + " extraArgs.length=" +
                extraArgs.length);
            argLength = arguments.length;
            // to emulate the way prototype adds extra arguments, we must prepend
            // additional arguments to whatever arguments this function is called with
            for(addIndex = extraArgs.length - 1; addIndex >= 0; addIndex -= 1, argLength += 1) {
                // the following allows us to "steal" an Array method and
                // apply it to the passed in arguments object, which is, as they say,
                // "array-like" but not an array proper
                // passed to this function:
                // func(a, b, ...)
                // passed to myBind:
                // myBind(func, context, arg1, arg2, ...)
                // we want the following arguments passed to func, the callback
                // func(arg1, arg2, ..., a, b, ...)
                [].unshift.call(arguments, extraArgs[addIndex]);
            }
            console.log("added " + extraArgs.length + " arguments arguments.length=" + arguments.length);
        } else {
            console.log("we have " + nargs + " arguments");
        }
        func.apply(context, arguments);
    };
}


