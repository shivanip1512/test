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
                if (pair.length === 2) {
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
        this.inline = (containerDiv === null || typeof containerDiv === 'undefined') ? false : true;
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
        var quietDelay = 300,
            ss,
            pickerThis = this,
            timerFunction = function() {
                var ss = pickerThis.ssInput.value;
                // Don't do the search if it hasn't changed.  This could be
                // because they type a character and deleted it.
                if (!pickerThis.inSearch && pickerThis.currentSearch !== ss) {
                    pickerThis.doSearch();
                }
            };
        this.nothingSelectedDiv.hide();
        // Don't do the search if it hasn't changed.  This can happen if
        // the use the cursor key or alt-tab to another window and back.
        ss = this.ssInput.value;
        if (this.currentSearch !== ss) {
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
            this.showAllLink.show();
        } else {
            this.showAllLink.hide();
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
            onComplete = Yukon.doBind(this.updateSearchResults, this);
        }

        function doOnComplete(transport) {
            try {
                onComplete(transport);
            } catch(callbackEx) {
            }
            if (endCallback) {
                endCallback();
            }
        }

        if (null === onFailure) {
            onFailure = Yukon.doBind(this.ajaxError, this);
        }
        jQuery.ajax({
            dataType: "json",
            url: '/picker/v2/search',
            data: parameters
        }).done(function(data, status, xhrobj) {
            doOnComplete(xhrobj);
        }).fail(function(xhrobj, textStatus, errorThrown) {
            onFailure(xhrobj, textStatus, errorThrown);
        });

    },

    /**
     * This method does secondary initialization which needs to happen after
     * the HTML elements in the tag file have been fully created.  This method
     * should only be called from the pickerDialog.tag.  Use viewMode when
     * the picker should be a read-only view.
     */
    init : function(viewMode) {
        var showSelectedImg,
            initialIds = [],
            destFieldSelector;
        this.inputAreaDiv = jQuery('#picker_' + this.pickerId + '_inputArea')[0];
        if (!viewMode) {
            if (this.selectionProperty) {
                this.selectedItemsPopup = jQuery('#picker_' + this.pickerId + '_selectedItemsPopup')[0];
                this.selectedItemsDisplayArea = jQuery('#picker_' + this.pickerId + '_selectedItemsDisplayArea')[0];
                showSelectedImg = jQuery('#picker_' + this.pickerId + '_showSelectedImg')[0];
                if (showSelectedImg) {
                    this.showSelectedLink = showSelectedImg.parentNode;
                    this.showSelectedLink.hide();
                }
            }
        }
        if (this.selectionProperty) {
            this.selectionLabel = jQuery('span', jQuery('#picker_' + this.pickerId + '_label'))[0];
            this.originalSelectionLabel = this.selectionLabel.innerHTML;
        }

        if (this.destinationFieldId) {
            destFieldSelector = '#' + this.destinationFieldId;
            if (jQuery(destFieldSelector).val()) {
                initialIds = jQuery(destFieldSelector).val().split(',');
            }
        } else {
            initialIds = jQuery.map(jQuery(':input',this.inputAreaDiv), function(val, index) {
                return val.value;
            });
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

        onCompleteBind = Yukon.doBind(this.onPrimeComplete, this, showPicker, initialIds, skipFocus);
        jQuery(pickerDialogDivContainer).load('/picker/v2/build', parameters, onCompleteBind);
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
        this.lastSelectedItems = this.selectedItems.slice(0);
        this.resetSearchFields();
        this.clearSearchResults();
        if (!this.primed) {
            this.prime(true, null, skipFocus);
        } else {
            this.doShow(skipFocus);
        }
    },

    doShow : function(skipFocus) {
        var that;
        if (this.memoryGroup && Picker.rememberedSearches[this.memoryGroup]) {
            this.ssInput.value = Picker.rememberedSearches[this.memoryGroup];
        }
        this.nothingSelectedDiv.hide();
        if (!skipFocus) {
            this.ssInput.focus();
        }
        that = this;
        this.doSearch(false, false, null, function() {
            var buttons;
            if (!this.containerDiv) {
                buttons = [{'text' : that.cancelText, 'click' : function() {that.cancel();}},
                           {'text' : that.okText, 'click' : function() {that.okPressed();}, 'class': 'primary action'}];
                if (!that.inline) {
                    jQuery('#' + that.pickerId).dialog({buttons : buttons, width : 600, height : 'auto'});
                }
            }
        });
    },

    onPrimeComplete : function(showPicker, initialIds, skipFocus, xhr, unused, req) {
        var json = JSON.parse(req.getResponseHeader('X-JSON')), errString = '';
        // handles gross errors from the server and assumes the string 'error'
        // is the 4th argument. Better than silently failing.
        if (arguments.length > 4 && 'undefined' !== typeof arguments[4] && "error" === arguments[4]) {
            errString = "Server error";
            if ('undefined' !== typeof arguments[3]) {
                errString += ": '" + arguments[3] + "'";
            }
            alert(errString);
            return;
        }
        this.ssInput = jQuery('#picker_' + this.pickerId + '_ss')[0];
        this.showAllLink = jQuery('#picker_' + this.pickerId + '_showAllLink')[0];
        this.resultsDiv = jQuery('#picker_' + this.pickerId + '_results')[0];
        this.noResultsDiv = jQuery('#picker_' + this.pickerId + '_noResults')[0];
        this.nothingSelectedDiv = jQuery('#picker_' + this.pickerId + '_nothingSelected')[0];
        this.selectAllCheckBox = jQuery('#picker_' + this.pickerId + '_selectAll')[0];
        this.selectAllPagesLink = jQuery('#picker_' + this.pickerId + '_selectAllPages')[0];
        this.allPagesSelected = jQuery('#picker_' + this.pickerId + '_allPagesSelected')[0];
        this.clearEntireSelectionLink = jQuery('#picker_' + this.pickerId + '_clearEntireSelection')[0];
        this.entireSelectionCleared = jQuery('#picker_' + this.pickerId + '_entireSelectionCleared')[0];
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
            var idx,
                initialIdsObj = '',
                parameters,
                onIdSearchComplete = null,
                onFailure = null;

            // build initialIdsObj from array values
            for (idx = 0; idx < selectedIds.length; idx += 1) {
                initialIdsObj += selectedIds[idx];
                if ((selectedIds.length - 1) > idx) {
                    initialIdsObj += ',';
                }
            }
            parameters = {
                    'type' : this.pickerType,
                    'id' : this.pickerId,
                    'initialIds' : initialIdsObj
            };
            if (this.extraArgs) {
                parameters.extraArgs = this.extraArgs;
            }

            if (null === onIdSearchComplete) {
                onIdSearchComplete = Yukon.doBind(this.onIdSearchComplete, this);
            }
            if (null === onFailure) {
                onFailure = Yukon.doBind(this.ajaxError, this);
            }
            jQuery.ajax({
                type: 'POST',
                dataType: "json",
                url: '/picker/v2/idSearch',
                data: parameters
            }).done(function(data, status, xhrobj) {
                onIdSearchComplete(xhrobj);
            }).fail(function(xhrobj, textStatus, errorThrown) {
                onFailure(xhrobj, textStatus, errorThrown);
            });

        }
    },

    onIdSearchComplete : function(transport) {
        var json = JSON.parse(transport.responseText);
        if (json && json.hits && json.hits.resultList) {
            this.selectedItems = json.hits.resultList;
            if (this.showSelectedLink) this.showSelectedLink.show();
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

        if (!this.allowEmptySelection && this.selectedItems.length === 0) {
            this.nothingSelectedDiv.show();
        } else {
            if (this.destinationFieldId) {
                fieldName = this.idFieldName;
                jQuery('#' + this.destinationFieldId).val(
                    jQuery.map(this.selectedItems, function(val, index) {
                        return val[fieldName];
                    }).join(',')
                );
            } else {
                pickerThis = this;
                this.inputAreaDiv.innerHTML = '';
                jQuery.each(this.selectedItems, function(key, selectedItem) {
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
            value,
            extraDestinationField,
            extraDestSelector;
        // protect from calling endAction for empty lists on first run.
        if (this.selectedItems.length === 0 && isInitial) {
            return;
        }

        hit = null;
        if (this.selectedItems.length > 0) {
            hit = this.selectedItems[0];
            if (this.showSelectedLink) this.showSelectedLink.show();
        } else {
            if (this.showSelectedLink) this.showSelectedLink.hide();
        }

        if (this.selectionProperty) {
            if (hit === null) {
                this.selectionLabel.innerHTML = this.originalSelectionLabel;
                jQuery(this.selectionLabel).addClass('noSelectionPickerLabel');
            } else {
                labelMsg = jQuery('<div>').text(hit[this.selectionProperty].toString()).html();
                if (this.selectedItems.length > 1) {
                    labelMsg +=  ' ' + this.selectedAndMsg + ' ' +
                        (this.selectedItems.length - 1) + ' ' +
                        this.selectedMoreMsg;
                }
                this.selectionLabel.innerHTML = labelMsg;
                jQuery(this.selectionLabel).removeClass('noSelectionPickerLabel');
            }
        }

        for (index = 0; index < this.extraDestinationFields.length; index += 1) {
            extraDestinationField = this.extraDestinationFields[index];
            value = hit === null ? '' : hit[extraDestinationField.property];
            // support for both innerHTML and value setting
            extraDestSelector = '#' + extraDestinationField.fieldId;
            if (jQuery(extraDestSelector)[0].tagName === 'INPUT') {
                jQuery(extraDestSelector).val(value);
            }
            else {
                jQuery(extraDestSelector).html(value);
            }
        }
        if (this.endAction) {
            this.endAction(this.selectedItems, this);
        }
        return true;
    },

    clearSelected: function() {
        if (this.destinationFieldId) {
            jQuery('#' + this.destinationFieldId).val('');
        } else {
            this.inputAreaDiv.innerHTML = '';
        }
        if (this.selectionProperty) {
            this.selectionLabel.innerHTML = this.originalSelectionLabel;
            jQuery(this.selectionLabel).addClass('noSelectionPickerLabel');
        }
    },

    /**
     * Get the id(s) of the selected item(s).
     * If this is a multi-select picker, an array will be returned, 
     * but in single select mode a single selected item will be returned.
     */
    getSelected: function() { // called from zoneWizardDetails.js
        var retVal = this.destinationFieldId
            ? jQuery('#' + this.destinationFieldId).val().split(',')
            : jQuery.map(jQuery(':input', this.inputAreaDiv), function(val, index) {
                return val.value;
            });
        return this.multiSelectMode ? retVal : retVal[0];
    },

    previous: function() {
        if (this.previousIndex === -1) {
            return;
        }
        this.ssInput.focus();
        this.doSearch(this.previousIndex);
    },

    next: function() {
        if (this.nextIndex === -1) {
            return;
        }
        this.ssInput.focus();
        this.doSearch(this.nextIndex);
    },

    showAll: function() {
        if (this.memoryGroup) {
            Picker.rememberedSearches[this.memoryGroup] = '';
        }
        this.resetSearchFields();
        this.ssInput.focus();
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
        oldResultArea = jQuery('#' + this.resultAreaId)[0];
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
    updateSearchResults: function(transport) {
        var json = JSON.parse(transport.responseText),
            newResultArea = this.renderTableResults(json),
            oldResultArea,
            resultHolder,
            oldError,
            ss;
        this.updatePagingArea(json);
        this.selectAllPagesMsg = json.selectAllPages;
        this.allPagesSelectedMsg = json.allPagesSelected;
        oldResultArea = jQuery('#' + this.resultAreaId)[0];
        resultHolder = this.resultsDiv;
        if (oldResultArea) {
            resultHolder.removeChild(oldResultArea);
        }
        oldError = jQuery('#' + this.errorHolderId)[0];
        if (oldError) {
            resultHolder.removeChild(oldError);
        }
        resultHolder.appendChild(newResultArea);
        this.updateSelectAllCheckbox();

        ss = this.ssInput.value;
        this.unblock();
        if (this.currentSearch !== ss) {
            // do another search
            this.doSearch();
        } else {
            this.inSearch = false;
        }
    },

    ajaxError: function(transport, textStatus, errorThrown) {
        this.inSearch = false;
        this.unblock();
        this.resultsDiv.innerHTML = '';
        errorHolder = document.createElement('div');
        errorHolder.id = this.errorHolderId;
        errorHolder.innerHTML = 'There was a problem searching the index: ' +
            transport.responseText;
        this.resultsDiv.appendChild(errorHolder);
    },

    /**
     * Render the table portion of the search results.
     */
    renderTableResults: function(json) {
        var hitList = json.hits.resultList,
            resultArea = document.createElement('div'),
            resultAreaFixed = document.createElement('div'),
            pickerThis = this,
            createItemLink = function(hit, link) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) !== -1) {
                    return null;
                } else {
                    pickerThis.allLinks.push({'hit' : hit, 'link' : link});
                    return function() {
                        pickerThis.selectThisItem(hit, link);
                    };
                }
            },
            outputColumns = [],
            processRowForRender = function (rowElement, rowObject) {
                if (pickerThis.excludeIds.indexOf(rowObject[pickerThis.idFieldName]) !== -1) {
                    jQuery(rowElement).addClass('disabled');
                    jQuery(rowElement).attr('title', Picker.alreadySelectedHoverMessage);
                } else {
                    pickerThis.selectedItems.forEach(function(item, index, arr){
                        if (rowObject[pickerThis.idFieldName] === item[pickerThis.idFieldName]) {
                            jQuery(rowElement).addClass('highlighted"');
                        }
                    });
                }
            },
            resultTable;

        resultArea.id = this.resultAreaId;
        resultAreaFixed.id = this.resultAreaFixedId;
        resultArea.appendChild(resultAreaFixed);
        this.allLinks = [];
        if (hitList && hitList.length && hitList.length > 0) {
            this.noResultsDiv.hide();
            this.resultsDiv.show();
            this.outputColumns.forEach(function(outputColumn, index, arr) {
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

            resultTable = createHtmlTableFromJson(hitList, outputColumns,
                processRowForRender);
            resultTable.className = 'compactResultsTable pickerResultTable';
            resultAreaFixed.appendChild(resultTable);
        } else {
            this.noResultsDiv.show();
            this.resultsDiv.hide();
        }

        return resultArea;
    },

    showSelected: function() {
        var outputColumns = [],
            resultTable;
        this.outputColumns.forEach(function(outputColumn, index, arr) {
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

        resultTable = createHtmlTableFromJson(this.selectedItems,
            outputColumns);
        jQuery(resultTable).addClass('compactResultsTable');
        jQuery(resultTable).addClass('pickerResultTable');
        jQuery(resultTable).addClass('rowHighlighting');
        this.selectedItemsDisplayArea.innerHTML = '';
        this.selectedItemsDisplayArea.appendChild(resultTable);
        jQuery(this.selectedItemsPopup).dialog({minWidth: 400});
    },

    /**
     * Update the paging area of the form for the current search results.
     */
    updatePagingArea: function(json) {
        var pickerDiv = jQuery('#' + this.pickerId);
        this.previousIndex = -1;
        this.nextIndex = -1;

        if (json) {
            this.hitCount = json.hits.hitCount;
        }
        if (json && json.hits.startIndex > 0) {
            this.previousIndex = json.hits.previousStartIndex;
            jQuery('.previousLink.enabledAction', jQuery(pickerDiv)).show();
            jQuery('.previousLink.disabledAction', jQuery(pickerDiv)).hide();
        } else {
            jQuery('.previousLink.enabledAction', jQuery(pickerDiv)).hide();
            jQuery('.previousLink.disabledAction', jQuery(pickerDiv)).show();
        }
        if (json && json.hits.endIndex < json.hits.hitCount) {
            this.nextIndex = json.hits.endIndex;
            jQuery('.nextLink.enabledAction', jQuery(pickerDiv)).show();
            jQuery('.nextLink.disabledAction', jQuery(pickerDiv)).hide();
        } else {
            jQuery('.nextLink.enabledAction', jQuery(pickerDiv)).hide();
            jQuery('.nextLink.disabledAction', jQuery(pickerDiv)).show();
        }
        jQuery('.pageNumText', jQuery(pickerDiv))[0].innerHTML = json ? json.pages : '';
    },

    removeFromSelectedItems : function(hit) {
        var oldSelectedItems = this.selectedItems,
            index;
        this.selectedItems = [];
        for (index = 0; index < oldSelectedItems.length; index++) {
            if (oldSelectedItems[index][this.idFieldName] !== hit[this.idFieldName]) {
                this.selectedItems.push(oldSelectedItems[index]);
            }
        }
    },

    selectAll: function() {
        var pickerThis = this,
            numSelectedBefore = this.selectedItems.length;
        this.allLinks.forEach(function(hitRow, index, arr) {
            // hitRow IS an element, so we don't need any jQuery sugar around it
            var parentRow = hitRow.link.parentNode.parentNode;
            if (pickerThis.selectAllCheckBox.checked) {
                jQuery(parentRow).addClass('highlighted');
                pickerThis.selectedItems.push(hitRow.hit);
            } else {
                jQuery(parentRow).removeClass('highlighted');
                pickerThis.removeFromSelectedItems(hitRow.hit);
            }
        });

        this.clearEntireSelectionLink.parentNode.hide();
        this.entireSelectionCleared.hide();
        this.allPagesSelected.parentNode.hide();
        // Cap "select all on every page" at 5000.
        if (this.selectAllCheckBox.checked && this.nextIndex !== -1 && this.hitCount <= 5000) {
            this.selectAllPagesLink.innerHTML = this.selectAllPagesMsg;
            this.selectAllPagesLink.parentNode.show();
        } else {
            this.selectAllPagesLink.parentNode.hide();
            if (numSelectedBefore > this.selectedItems.length && this.selectedItems.length > 0) {
                this.clearEntireSelectionLink.parentNode.show(); 
            }
        }

        this.ssInput.focus();
    },

    selectAllPages: function() {
        this.doSearch(0, -1, Yukon.doBind(this.selectAllOnComplete, this));
    },

    selectAllOnComplete: function(transport) {
        var json =  JSON.parse(transport.responseText),
            hitList = json.hits.resultList,
            pickerThis;
        // remove "exclude items"
        if (this.excludeIds && this.excludeIds.length > 0) {
            this.selectedItems = [];
            pickerThis = this;
            hitList.forEach(function(hit, index, arr) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) === -1) {
                    pickerThis.selectedItems.push(hit);
                }
            });
        } else {
            this.selectedItems = hitList;
        }
        this.inSearch = false;
        this.ssInput.focus();
        this.selectAllPagesLink.parentNode.hide();
        this.allPagesSelected.innerHTML = this.allPagesSelectedMsg;
        this.allPagesSelected.parentNode.show();
        this.unblock();
    },

    clearEntireSelection : function() {
        this.selectedItems = [];
        this.allPagesSelected.parentNode.hide();
        this.clearEntireSelectionLink.parentNode.hide();
        this.entireSelectionCleared.show();
        this.allLinks.forEach(function(hitRow, index, arr) {
            jQuery(hitRow.link.parentNode.parentNode).removeClass('highlighted');
        });
        this.selectAllCheckBox.checked = false;
    },

    updateSelectAllCheckbox: function() {
        if (!this.multiSelectMode) {
            return;
        }
        var allSelected = this.allLinks.length > 0;
        this.allLinks.forEach(function(hitRow, index, arr) {
            if (! jQuery(hitRow.link.parentNode.parentNode).hasClass('highlighted')) {
                allSelected = false;
            }
        });
        this.selectAllCheckBox.checked = allSelected;
        this.selectAllPagesLink.parentNode.hide();
        this.allPagesSelected.parentNode.hide();
        this.clearEntireSelectionLink.parentNode.hide();
        this.entireSelectionCleared.hide();
    },

    /**
     * This method called when the user clicks on a row for selection. 
     */
    selectThisItem: function(hit, link) {
        var parentRow,
            rows,
            index;
        this.nothingSelectedDiv.hide();

        if (this.immediateSelectMode) {
            this.selectedItems = [hit];
            this.okPressed();
            return;
        }

        parentRow = link.parentNode.parentNode;
        if (jQuery(parentRow).hasClass('highlighted')) {
            // unselect
            jQuery(parentRow).removeClass('highlighted');
            this.removeFromSelectedItems(hit);
            this.selectAllCheckBox.checked = false;
        } else {
            // select
            if (!this.multiSelectMode) {
                // not multi-select mode; unselect all others
                rows = parentRow.parentNode.childNodes;
                for (index = 0; index < rows.length; index++) {
                    jQuery(rows[index]).removeClass('highlighted');
                }
                this.selectedItems = [hit];
                jQuery(parentRow).addClass('highlighted');
            } else {
                this.selectedItems.push(hit);
                jQuery(parentRow).addClass('highlighted');
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

