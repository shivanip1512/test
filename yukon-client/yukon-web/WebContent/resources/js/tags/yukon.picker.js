
/** 
 * Prototype for Picker, from which all Picker instances are derived.
 * @class
 * @requires jQuery
 * @requires yukon
 * @requires yukon.ui.util 
 * @requires yukon.dialog
 */
yukon.protoPicker = function (okText, 
                              cancelText, 
                              noneSelectedText, 
                              pickerType, 
                              destinationFieldName, 
                              pickerId, 
                              extraDestinationFields, 
                              container) {
    /**
     * Reset things that need to be reset when first popping up the picker
     * or when clearing it.
     */
    var resetSearchFields = function () {
        
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
    initialize = function (okText, 
                           cancelText, 
                           noneSelectedText, 
                           pickerType, 
                           destinationFieldName, 
                           pickerId, 
                           extraDestinationFields, 
                           container) {
        
        var index, pairs, pair, extraDestinationField;
        
        this.okText = okText;
        this.cancelText = cancelText;
        this.noneSelectedText = noneSelectedText;
        this.pickerType = pickerType;
        this.destinationFieldName = destinationFieldName;
        this.pickerId = pickerId;
        this.excludeIds = [];
        this.disabledIds = [];
        this.multiSelectMode = false;
        this.immediateSelectMode = false;
        this.memoryGroup = false;
        resetSearchFields.call(this);
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
        this.resultAreaId = 'picker-' + this.pickerId + '-result-area';
        this.resultAreaFixedId = 'picker-' + this.pickerId + '-result-area-fixed';
        this.errorHolderId = 'picker-' + this.pickerId + '-error-holder';
        this.primed = false;
        this.useInitialIdsIfEmpty = false;
        this.container = container;
        this.inline = (container === null || typeof container === 'undefined') ? false : true;
    },
    
    block = function () {
        this.block = true;
        setTimeout(function () {
            if (this.block) {
                yukon.ui.block($(document.getElementById(this.pickerId)).find('.js-block-this'));
            }
        }, 200);
    },
    
    unblock = function () {
        this.block = false;
        yukon.ui.unblock($(document.getElementById(this.pickerId)).find('.js-block-this'));
    },
    
    updateSelectAllCheckbox = function () {
        
        if (!this.multiSelectMode) return;
        
        var allSelected = this.allLinks.length > 0;
        this.allLinks.forEach(function (hitRow, index, arr) {
            if (!hitRow.link.closest('tr').hasClass('highlighted')) {
                allSelected = false;
            }
        });
        this.selectAllCheckBox.checked = allSelected;
        this.jqSelectAllPagesLinkParentNode.hide();
        this.jqAllPagesSelectedParentNode.hide();
        this.jqClearEntireSelectionLinkParentNode.hide();
        this.jqEntireSelectionCleared.hide();
    },
    
    okPressed = function () {
        
        var fieldName, pickerThis, destinationFieldElem, dialogElem;

        if (!this.allowEmptySelection && this.selectedItems.length === 0) {
            $(this.nothingSelectedDiv).show();
            $(this.tooManySelectionsDiv).hide();
        } else if (this.maxNumSelections !== null && this.maxNumSelections < this.selectedItems.length) {
            $(this.tooManySelectionsDiv).show();
            $(this.nothingSelectedDiv).hide();
        } else {
            if (this.destinationFieldId) {
                fieldName = this.idFieldName;
                destinationFieldElem = document.getElementById(this.destinationFieldId);
                $(destinationFieldElem).val(
                    $.map(this.selectedItems, function (val, index) {
                        return val[fieldName];
                    }).join(',')
                );
            } else {
                pickerThis = this;
                this.inputAreaDiv.innerHTML = '';
                $.each(this.selectedItems, function (key, selectedItem) {
                    var inputElement = document.createElement('input');
                    inputElement.type = 'hidden';
                    inputElement.value = selectedItem[pickerThis.idFieldName];
                    inputElement.name = pickerThis.destinationFieldName;
                    pickerThis.inputAreaDiv.appendChild(inputElement);
                });
            }
            
            if (updateOutsideFields.call(this, false)) {
                if (!this.container) {
                    dialogElem = document.getElementById(this.pickerId);
                    try {
                        $(dialogElem).dialog('close');
                    } catch (error) { /* Dialog may not have been initialized yet. */ }
                }
            }
        }
    },
    
    /** Called when the user clicks on a row for selection. */
    selectThisItem = function (hit) {
        
        if (this.maxNumSelections) {
            $(this.tooManySelectionsDiv).hide();
        }
        
        var 
        selectedId = hit[this.idFieldName],
        row = $('#' + this.pickerId + ' tr[data-id="' + selectedId + '"]'),
        onThisPage = row.length,
        alreadySelected = false;
        
        this.selectedItems.forEach(function (item, index, arr) {
            if (JSON.stringify(hit) === JSON.stringify(item)) {
                alreadySelected = true;
            }
        });
        
        $(this.nothingSelectedDiv).hide();
        $(this.tooManySelectionsDiv).hide();
        
        if (this.immediateSelectMode) {
            this.selectedItems = [hit];
            okPressed.call(this);
            return;
        }
        
        if (alreadySelected) {
            // unselect
            if (onThisPage) row.removeClass('highlighted');
            removeFromSelectedItems.call(this, hit);
            if (this.selectAllCheckBox != null && typeof this.selectAllCheckBox !== 'undefined') {
                this.selectAllCheckBox.checked = false;
            }
        } else {
            // select
            if (!this.multiSelectMode) {
                // not multi-select mode; unselect all others
                this.selectedItems = [hit];
                if (onThisPage) {
                    row.addClass('highlighted').siblings().removeClass('highlighted');
                } else {
                    $('#' + this.pickerId + ' tr[data-id]').removeClass('highlighted');
                }
            } else {
                if (this.maxNumSelections && this.maxNumSelections <= this.selectedItems.length) {
                    $(this.tooManySelectionsDiv).show();
                    return;
                }
                this.selectedItems.push(hit);
                if (onThisPage) row.addClass('highlighted');
                updateSelectAllCheckbox.call(this);
            }
        }
    },
    
    /** Render the table portion of the search results. */
    renderTableResults = function (json) {
        
        var hitList = json.hits.resultList,
            resultArea = $('<div>').attr('id', this.resultAreaId),
            resultAreaFixed = $('<div>').attr('id', this.resultAreaFixedId),
            pickerThis = this,
            selectItemLink = function (hit, link) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) !== -1) {
                    return null;
                } else {
                    pickerThis.allLinks.push({'hit' : hit, 'link' : link});
                    return function () {
                        selectThisItem.call(pickerThis, hit);
                    };
                }
            },
            outputColumns = [],
            processRowForRender = function (row, data) {
                
                row = $(row);
                row.attr('data-id', data[pickerThis.idFieldName]);
                
                if (pickerThis.excludeIds.indexOf(data[pickerThis.idFieldName]) !== -1) {
                    row.addClass('disabled');
                } else if (pickerThis.disabledIds.indexOf(data[pickerThis.idFieldName]) !== -1) {
                   row.addClass('disabled-look');
                } else {
                    pickerThis.selectedItems.forEach(function (item, index, arr){
                        if (data[pickerThis.idFieldName] === item[pickerThis.idFieldName]) {
                            row.addClass('highlighted');
                        }
                    });
                }
            },
            resultTable;
            
        resultArea.append(resultAreaFixed);
        this.allLinks = [];
        if (hitList && hitList.length && hitList.length > 0) {
            $(this.noResultsDiv).hide();
            $(this.resultsDiv).show();
            this.outputColumns.forEach(function (outputColumn, index, arr) {
                var translatedColumn = {
                     'title': outputColumn.title,
                     'field': outputColumn.field,
                     'link': selectItemLink
                 };
                 if (outputColumn.maxCharsDisplayed > 0) {
                     translatedColumn.maxLen = outputColumn.maxCharsDisplayed;
                 }
                 outputColumns.push(translatedColumn);
                 // only the first column is a link
                 selectItemLink = null;
             });
            
            resultTable = yukon.ui.util.createTable({
                data: hitList, 
                columns: outputColumns, 
                callback: processRowForRender
            });
            
            resultTable.addClass('compact-results-table picker-results-table');
            resultAreaFixed.append(resultTable);
        } else {
            $(this.noResultsDiv).show();
            $(this.resultsDiv).hide();
        }
        
        return resultArea;
    },
    
    /** Update the paging area for the current search results. */
    updatePagingArea = function (json) {
        
        var pickerDiv = document.getElementById(this.pickerId);
        
        this.previousIndex = -1;
        this.nextIndex = -1;
        
        if (json) {
            this.hitCount = json.hits.hitCount;
        }
        if (json && json.hits.startIndex > 0) {
            this.previousIndex = json.hits.previousStartIndex;
            $('.previous-page', $(pickerDiv)).visible();
        } else {
            $('.previous-page', $(pickerDiv)).invisible();
        }
        if (json && json.hits.endIndex < json.hits.hitCount) {
            this.nextIndex = json.hits.endIndex;
            $('.next-page', $(pickerDiv)).visible();
        } else {
            $('.next-page', $(pickerDiv)).invisible();
        }
        $('.page-num-text', $(pickerDiv))[0].innerHTML = json ? json.pages : '';
    },
    
    /**
     * Update the UI with data from the search results.  This method calls
     * renderTalbeResults to update the table itself and then updates the
     * previous and next buttons appropriately.
     */
    updateSearchResults = function (xhr) {
        
        var json = JSON.parse(xhr.responseText),
            newResultArea = renderTableResults.call(this, json),
            resultHolder = $(this.resultsDiv),
            ss;
        
        updatePagingArea.call(this, json);
        this.selectAllPagesMsg = json.selectAllPages;
        this.allPagesSelectedMsg = json.allPagesSelected;
        $('#' + this.resultAreaId).remove();
        $('#' + this.errorHolderId).remove();
        resultHolder.append(newResultArea);
        updateSelectAllCheckbox.call(this);
        
        ss = this.ssInput.value;
        unblock.call(this);
        if (this.currentSearch !== ss) {
            // do another search
            doSearch.call(this);
        } else {
            this.inSearch = false;
        }
    },
    
    ajaxError = function (transport, textStatus, errorThrown) {
        
        debug.log(transport.responseText);
        this.inSearch = false;
        unblock();
        this.resultsDiv.innerHTML = '';
        errorHolder = document.createElement('div');
        errorHolder.id = this.errorHolderId;
        errorHolder.innerHTML = yg.text.ajaxError;
        this.resultsDiv.appendChild(errorHolder);
    },
    
    /**
     * Actually do the search.  This method sets up a request to the server
     * for new data based on the entered search text.
     * 
     * @param {Number} start - The index of the first result to show.
     * @param {Number} count - The number of items per page.
     */
    doSearch = function (start, count, onComplete, endCallback) {
        var ss,
            parameters = {},
            onFailure = null;
        
        this.inSearch = true;
        block.call(this);
        ss = this.ssInput.value;
        $(this.showAllLink).toggleClass('dn', !ss);
        this.currentSearch = ss;
        if (this.memoryGroup) {
            yukon.protoPicker.rememberedSearches[this.memoryGroup] = ss;
        }
        
        parameters = {
            'type' : this.pickerType,
            'ss' : ss
        };
        if (start) parameters.start = start;
        if (this.extraArgs) parameters.extraArgs = this.extraArgs;
        if (count) parameters.count = count;
        if (!onComplete) onComplete = updateSearchResults.bind(this);
        
        function doOnComplete(xhr) {
            try {
                onComplete(xhr);
            } catch(callbackEx) {
                debug.log('Error executing the onComplete callback.');
                debug.log(callbackEx);
            }
            
            if (endCallback) endCallback();
            
        }
        if (null === onFailure) {
            onFailure = ajaxError.bind(this);
        }
        $.ajax({
            dataType: "json",
            url: yukon.url('/picker/search'),
            data: parameters
        }).done(function (data, status, xhr) {
            doOnComplete(xhr);
        }).fail(function (xhrobj, textStatus, errorThrown) {
            onFailure(xhrobj, textStatus, errorThrown);
        });
        
    },
    
    updateOutsideFields = function (isInitial) {
        
        var hit,
            labelMsg,
            index,
            value,
            extraDestinationField,
            extraDestSelector,
            extraDestElem;
        
        // protect from calling endAction for empty lists on first run.
        if (this.selectedItems.length === 0 && isInitial) {
            return;
        }
        
        hit = null;
        if (this.selectedItems.length > 0) {
            hit = this.selectedItems[0];
            if (this.showSelectedLink) $(this.showSelectedLink).show();
        } else {
            if (this.showSelectedLink) $(this.showSelectedLink).hide();
        }
        
        if (this.selectionProperty) {
            if (hit === null) {
                this.selectionLabel.innerHTML = this.noneSelectedText;
                $(this.selectionLabel).addClass('noSelectionPickerLabel');
                $(this.removeIcon).addClass("dn");
            } else {
                var linkText = hit[this.selectionProperty];
                if (this.selectionProperty === 'paoPoint') {
                    linkText = hit['deviceName'] + ': ' + hit['pointName'];
                }
                labelMsg = $('<div>').text(linkText).html();
                if (this.selectedItems.length > 1) {
                    labelMsg +=  ' ' + this.selectedAndMsg + ' ' +
                        (this.selectedItems.length - 1) + ' ' +
                        this.selectedMoreMsg;
                }
                this.selectionLabel.innerHTML = labelMsg;
                $(this.selectionLabel).removeClass('noSelectionPickerLabel');
                $(this.removeIcon).removeClass("dn");
            }
        }
        
        for (index = 0; index < this.extraDestinationFields.length; index += 1) {
            extraDestinationField = this.extraDestinationFields[index];
            value = hit === null ? '' : hit[extraDestinationField.property];
            // support for both innerHTML and value setting
            extraDestSelector = extraDestinationField.fieldId;
            extraDestElem = document.getElementById(extraDestSelector);
            if (extraDestElem.tagName === 'INPUT') {
                extraDestElem.value = value;
            }
            else {
                extraDestElem.innerHTML = value;
            }
        }
        if (!isInitial) {
            if (this.endAction) {
                this.endAction(this.selectedItems, this);
            }
            if (this.endEvent) {
                $(document).trigger(this.endEvent, [this.selectedItems, this]);
            }
        }
        return true;
    },
    
    onIdSearchComplete = function (xhr) {
        
        var json = JSON.parse(xhr.responseText);
        
        if (json && json.hits && json.hits.resultList) {
            this.selectedItems = json.hits.resultList;
            if (this.showSelectedLink) $(this.showSelectedLink).show();
        }
        updateOutsideFields.call(this, true);
    },
    
    doIdSearch = function (selectedIds) {
        if (selectedIds && selectedIds.length > 0) {
            var onIdSearchCompleteFunc = null,
                onFailure = null,
                parameters = {
                    'type' : this.pickerType,
                    'id' : this.pickerId,
                    'initialIds' : selectedIds
                };
            if (this.extraArgs) {
                parameters.extraArgs = this.extraArgs;
            }
            if (null === onIdSearchCompleteFunc) {
                onIdSearchCompleteFunc = onIdSearchComplete.bind(this);
            }
            if (null === onFailure) {
                onFailure = ajaxError.bind(this);
            }
            $.ajax({
                type: 'POST',
                url: yukon.url('/picker/idSearch'),
                traditional: true, // see jQuery docs for discussion of this option
                data: parameters
            }).done(function (data, status, xhr) {
                onIdSearchCompleteFunc(xhr);
            }).fail(function (xhrobj, textStatus, errorThrown) {
                onFailure(xhrobj, textStatus, errorThrown);
            });
            
        }
    },
    
    /** Cancel the current picker and pop it down. */
    cancel = function () {
        
        this.selectedItems = this.lastSelectedItems;
        if (!this.container) {
            $(document.getElementById(this.pickerId)).dialog('close');
        }
        if (this.cancelAction) {
            this.cancelAction(this);
        }
    },
    
    doShow = function (skipFocus) {
        
        var that;
        
        if (this.memoryGroup && yukon.protoPicker.rememberedSearches && yukon.protoPicker.rememberedSearches[this.memoryGroup]) {
            this.ssInput.value = yukon.protoPicker.rememberedSearches[this.memoryGroup];
        }
        if (this.nothingSelectedDiv) {
            $(this.nothingSelectedDiv).hide();
        }
        if (this.tooManySelectionsDiv) {
            $(this.tooManySelectionsDiv).hide();
        }
        if ('undefined' !== typeof skipFocus && !skipFocus) {
            this.ssInput.focus();
        }
        that = this;
        doSearch.call(this, false, false, null, function () {
            var buttons;
            if (!that.container) {
                buttons = [{'text' : that.cancelText, 'click' : function () {cancel.call(that);}},
                           {'text' : that.okText, 'click' : function () {okPressed.call(that);}, 'class': 'primary action'}];
                if (!that.inline) {
                    $(document.getElementById(that.pickerId)).dialog({buttons : buttons, width : 600, height : 'auto', modal: true});
                }
            }
        });
    },
    
    onPrimeComplete = function (showPicker, initialIds, skipFocus, data, textStatus, xhr) {
        // handles gross errors from the server and assumes the string 'error'
        // is the 4th argument. Better than silently failing.
        if (arguments.length > 4 && 'undefined' !== typeof textStatus && "error" === textStatus) {
            errString = "Server error";
            if ('undefined' !== typeof data) {
                errString += ": '" + data + "'";
            }
            console.log(errString);
            return;
        }
        this.ssInput = document.getElementById('picker-' + this.pickerId + '-ss');
        this.showAllLink = document.getElementById('picker-' + this.pickerId + '-show-all-link');
        this.resultsDiv = document.getElementById('picker-' + this.pickerId + '-results');
        this.noResultsDiv = document.getElementById('picker-' + this.pickerId + '-no-results');
        this.nothingSelectedDiv = document.getElementById('picker-' + this.pickerId + '-nothing-selected');
        this.tooManySelectionsDiv = document.getElementById('picker-' + this.pickerId + '-too-many-selections');
        this.selectAllCheckBox = document.getElementById('picker-' + this.pickerId + '-select-all');
        this.selectAllPagesLink = document.getElementById('picker-' + this.pickerId + '-select-all-pages');
        this.allPagesSelected = document.getElementById('picker-' + this.pickerId + '-all-pages-selected');
        this.clearEntireSelectionLink = document.getElementById('picker-' + this.pickerId + '-clear-entire-selection');
        this.entireSelectionCleared = document.getElementById('picker-' + this.pickerId + '-entire-selection-cleared');
        this.jqSelectAllPagesLinkParentNode = this.selectAllPagesLink ? $(this.selectAllPagesLink.parentNode) : null;
        this.jqAllPagesSelectedParentNode = this.allPagesSelected ? $(this.allPagesSelected.parentNode) : null;
        this.jqClearEntireSelectionLinkParentNode = this.clearEntireSelectionLink ? $(this.clearEntireSelectionLink.parentNode) : null;
        this.jqEntireSelectionCleared = $(this.entireSelectionCleared);

        doIdSearch.call(this, initialIds);

        if (showPicker) {
            doShow.call(this, skipFocus);
        }
        this.primed = true;
    },
    
    /**
     * This method primes the picker, performing the initial search if necessary
     * and populating the dialog with the results of pickerDialog.jsp.  If the
     * picker has initially selected ids, this needs to be called on initial
     * page load to correctly populate the data outside the picker but otherwise,
     * it doesn't need to be done until the picker is first used (which may
     * never happen).
     */
    prime = function (showPicker, initialIds, skipFocus) {
        
        var bodyElem = document.documentElement.getElementsByTagName('body')[0],
            pickerDialogDivContainer,
            parameters = {
                'type' : this.pickerType,
                'id' : this.pickerId,
                'multiSelectMode' : this.multiSelectMode,
                'immediateSelectMode' : this.immediateSelectMode,
                'maxNumSelections' : this.maxNumSelections
            },
            onCompleteBind;
        
        if (this.container) {
            pickerDialogDivContainer = this.container;
        } else {
            pickerDialogDivContainer = document.createElement('div');
            bodyElem.appendChild(pickerDialogDivContainer);
        }
        if (this.extraArgs) {
            parameters.extraArgs = this.extraArgs;
        }
        if (this.container) {
            parameters.mode = 'inline';
        }
        
        onCompleteBind = onPrimeComplete.bind(this, showPicker, initialIds, skipFocus);
        $(pickerDialogDivContainer).load(yukon.url('/picker/build'), parameters, onCompleteBind);
        
    },
    
    /** Clear any old search results (used when the dialog is first popped up). */
    clearSearchResults = function () {
        
        var oldResultArea, resultHolder;
        
        if (!this.resultsDiv) {
            // our first time up; nothing to do
            return;
        }
        updatePagingArea.call(this);
        oldResultArea = document.getElementById(this.resultAreaId);
        resultHolder = this.resultsDiv;
        if (oldResultArea) {
            resultHolder.removeChild(oldResultArea);
        }
    },
    
    removeFromSelectedItems = function (hit) {
        
        var oldSelectedItems = this.selectedItems,
            index;
        
        this.selectedItems = [];
        for (index = 0; index < oldSelectedItems.length; index++) {
            if (oldSelectedItems[index][this.idFieldName] !== hit[this.idFieldName]) {
                this.selectedItems.push(oldSelectedItems[index]);
            }
        }
    };
    
    /** Globally-accessible functions hung off the prototype. */
    
    /**
     * This is the primary externally called method.  It pops the picker up
     * and does the initial search.
     */
    yukon.protoPicker.prototype.show = function (skipFocus) {
       
        if (this.immediateSelectMode && this.multiSelectMode) {
            if (this.maxNumSelections !== null) {
                alert('immediateSelectMode cannot be used with multiSelectMode or maxNumSelections; ' + 
                'turning multiSelectMode and maxNumSelections off');
                this.maxNumSelections = null;
            } else {            
                alert('immediateSelectMode cannot be used with multiSelectMode; ' + 
                'turning multiSelectMode off');
            }
            this.multiSelectMode = false;
        }
        
        if (!this.multiSelectMode && this.maxNumSelections !== null) {
            alert('multiSelectMode must be enabled in order to set maxNumSelections; turning maxNumSelections off');
            this.maxNumSelections = null;
        }

        // forget jQuery here. We know selectedItems is an Array, so just
        // make a shallow copy
        this.lastSelectedItems = this.selectedItems.slice(0);
        resetSearchFields.call(this);
        clearSearchResults.call(this);
        if (!this.primed) {
            prime.call(this, true, null, skipFocus);
        } else {
            doShow.call(this, skipFocus);
        }
    };
    
    yukon.protoPicker.prototype.cancel = function () {
        cancel.call(this);
    };
    
    yukon.protoPicker.prototype.select = function (hit) {
        selectThisItem.call(this, hit);
    };
    
    /** Attached to the onkeyup event of the search text input field. */
    yukon.protoPicker.prototype.doKeyUp = function () {
        
        var quietDelay = 300,
            ss,
            pickerThis = this,
            timerFunction = function () {
                var ss = pickerThis.ssInput.value;
                // Don't do the search if it hasn't changed.  This could be
                // because they type a character and deleted it.
                if (!pickerThis.inSearch && pickerThis.currentSearch !== ss) {
                    doSearch.call(pickerThis);
                }
            };
            
        $(this.nothingSelectedDiv).hide();
        $(this.tooManySelectionsDiv).hide();
        // Don't do the search if it hasn't changed.  This can happen if
        // the use the cursor key or alt-tab to another window and back.
        ss = this.ssInput.value;
        if (this.currentSearch !== ss) {
            block.call(this);
            setTimeout(timerFunction, quietDelay);
        }
    };
    
    /** Used to disable an item in the selection list. */
    yukon.protoPicker.prototype.disableItem = function (selectedId) {
        var row = $('#' + this.pickerId + ' tr[data-id="' + selectedId + '"]');
        row.addClass('disabled-look');
        this.disabledIds.push(selectedId);
    };
    
    /** Used to enable an item in the selection list. */
    yukon.protoPicker.prototype.enableItem = function (selectedId) {
        var row = $('#' + this.pickerId + ' tr[data-id="' + selectedId + '"]');
        row.removeClass('disabled-look');
        var index = this.disabledIds.indexOf(selectedId);
        this.disabledIds.splice(index, 1);
    };
    
    /** Invoked from jsp */
    yukon.protoPicker.prototype.showAll = function () {
        if (this.memoryGroup) {
            yukon.protoPicker.rememberedSearches[this.memoryGroup] = '';
        }
        resetSearchFields.call(this);
        this.ssInput.focus();
        doSearch.call(this);
    };
    
    yukon.protoPicker.prototype.previous = function () {
        if (this.previousIndex === -1) {
            return;
        }
        this.ssInput.focus();
        doSearch.call(this, this.previousIndex);
    };
    
    yukon.protoPicker.prototype.next = function () {
        if (this.nextIndex === -1) {
            return;
        }
        this.ssInput.focus();
        doSearch.call(this, this.nextIndex);
    };
    
    yukon.protoPicker.prototype.selectAll = function () {
        
        var pickerThis = this,
            numSelectedBefore = this.selectedItems.length,
            currentlySelected = {};
        
        pickerThis.selectedItems.forEach(function (item, index, arr) {
            currentlySelected[item[pickerThis.idFieldName]] = item;
        });
        
        this.allLinks.forEach(function (item, index, arr) {
            var row = item.link.closest('tr'),
                alreadyThere = currentlySelected[item.hit[pickerThis.idFieldName]];
            if (pickerThis.disabledIds.indexOf(item.hit[pickerThis.idFieldName]) === -1) {
                if (pickerThis.selectAllCheckBox.checked) {
                    row.addClass('highlighted');
                    if (!alreadyThere) {
                        pickerThis.selectedItems.push(item.hit);
                    }
                } else {
                    row.removeClass('highlighted');
                    removeFromSelectedItems.call(pickerThis, item.hit);
                }
            }
        });
        
        this.jqClearEntireSelectionLinkParentNode.hide();
        this.jqEntireSelectionCleared.hide();
        this.jqAllPagesSelectedParentNode.hide();
        // Cap "select all on every page" at 5000.
        if (this.selectAllCheckBox.checked && this.nextIndex !== -1 && this.hitCount <= 5000) {
            this.selectAllPagesLink.innerHTML = this.selectAllPagesMsg;
            this.jqSelectAllPagesLinkParentNode.show();
        } else {
            this.jqSelectAllPagesLinkParentNode.hide();
            if (numSelectedBefore > this.selectedItems.length && this.selectedItems.length > 0) {
                this.jqClearEntireSelectionLinkParentNode.show();
            }
        }
        
        this.ssInput.focus();
    };
    
    yukon.protoPicker.prototype.selectAllOnComplete = function (transport) {
        
        var json =  JSON.parse(transport.responseText),
            hitList = json.hits.resultList,
            pickerThis;
        
        // remove "exclude items"
        if (this.excludeIds && this.excludeIds.length > 0) {
            this.selectedItems = [];
            pickerThis = this;
            hitList.forEach(function (hit, index, arr) {
                if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) === -1) {
                    pickerThis.selectedItems.push(hit);
                }
            });
        } else {
            this.selectedItems = hitList;
        }
        this.inSearch = false;
        this.ssInput.focus();
        this.jqSelectAllPagesLinkParentNode.hide();
        this.allPagesSelected.innerHTML = this.allPagesSelectedMsg;
        this.jqAllPagesSelectedParentNode.show();
        this.unblock();
    };
    
    yukon.protoPicker.prototype.selectAllPages = function () {
        doSearch.call(this, 0, -1, this.selectAllOnComplete.bind(this));
    };
    
    yukon.protoPicker.prototype.clearEntireSelection = function () {
        this.selectedItems = [];
        if (this.jqAllPagesSelectedParentNode) {
            this.jqAllPagesSelectedParentNode.hide();
        }
        if (this.jqClearEntireSelectionLinkParentNode) {
            this.jqClearEntireSelectionLinkParentNode.hide();
        }
        if (this.jqEntireSelectionCleared) {
            this.jqEntireSelectionCleared.show();
        }
        if (this.allLinks) {
            this.allLinks.forEach(function (item, index, arr) {
                item.link.closest('tr').removeClass('highlighted');
            });
        }
        if (this.selectAllCheckBox) {
            this.selectAllCheckBox.checked = false;
        }
    };
    
    yukon.protoPicker.prototype.clearSelected = function () {
        yukon.protoPicker.prototype.clearSelected('');
    };
    
    yukon.protoPicker.prototype.clearSelected = function (setValue) {
        if (this.destinationFieldId) {
            $(document.getElementById(this.destinationFieldId)).val(setValue);
        } else {
            this.inputAreaDiv.innerHTML = '';
        }
        if (this.selectionProperty) {
            this.selectionLabel.innerHTML = this.originalSelectionLabel;
            $(this.selectionLabel).addClass('noSelectionPickerLabel');
            $(this.removeIcon).addClass("dn");

        }
    };
    
    /**
     * Get the id(s) of the selected item(s).
     * If this is a multi-select picker, an array will be returned, 
     * but in single select mode a single selected item will be returned.
     */
    yukon.protoPicker.prototype.getSelected = function () {
        var retVal = this.destinationFieldId
            ? $(document.getElementById(this.destinationFieldId)).val().split(',')
            : $.map($(':input', this.inputAreaDiv), function (val, index) {
                return val.value;
            });
        return this.multiSelectMode ? retVal : retVal[0];
    };
    
    /**
     * This method does secondary initialization which needs to happen after
     * the HTML elements in the tag file have been fully created.  This method
     * should only be called from the pickerDialog.tag.  Use viewMode when
     * the picker should be a read-only view.
     */
    yukon.protoPicker.prototype.init = function (viewMode) {
        
        var showSelectedImg,
            initialIds = [],
            destFieldSelector;
        
        this.inputAreaDiv = document.getElementById('picker-' + this.pickerId + '-input-area');
        if (!viewMode) {
            if (this.selectionProperty) {
                this.selectedItemsPopup = document.getElementById('picker-' + this.pickerId + '-selected-items-popup');
                this.selectedItemsDisplayArea = document.getElementById('picker-' + this.pickerId + '-selected-items-display-area');
                showSelectedImg = document.getElementById('picker-' + this.pickerId + '-show-selected-icon');
                if (showSelectedImg) {
                    this.showSelectedLink = showSelectedImg.parentNode;
                    $(this.showSelectedLink).hide();
                }
            }
        }
        if (this.selectionProperty) {
            this.selectionLabel = $('span', $(document.getElementById('picker-' + this.pickerId + '-btn')))[0];
            this.removeIcon = document.getElementById('picker-' + this.pickerId + '-remove-selected-icon');
            this.originalSelectionLabel = this.selectionLabel.innerHTML;
        }
        
        if (this.destinationFieldId) {
            destFieldSelector = this.destinationFieldId;
            if ($(document.getElementById(destFieldSelector)).val()) {
                initialIds = $(document.getElementById(destFieldSelector)).val().split(',');
            }
        } else if (this.inputAreaDiv) {
            initialIds = $.map($(':input', this.inputAreaDiv), function (val, index) {
                return val.value;
            });
        }
        
        if (viewMode) {
            doIdSearch.call(this, initialIds);
        } else {
            if (initialIds && initialIds.length > 0) {
                prime.call(this, false, initialIds);
            } else if (this.useInitialIdsIfEmpty) {
                this.selectedItems = [];
            }
        }
    };
    
    yukon.protoPicker.prototype.showSelected = function () {
        
        var outputColumns = [], resultTable;
        
        this.outputColumns.forEach(function (outputColumn, index, arr) {
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
        
        resultTable = yukon.ui.util.createTable({ data: this.selectedItems, columns: outputColumns, focusIndicator: false })
        .addClass('compact-results-table');
        $(this.selectedItemsDisplayArea).empty();
        $(this.selectedItemsDisplayArea).append(resultTable);
        $(this.selectedItemsDisplayArea).addClass('scroll-xl');
        $(this.selectedItemsPopup).dialog({ minWidth: 400 });
        $(this.selectedItemsPopup).css({'max-height' : '', 'height' : ''});
        $(this.selectedItemsPopup).addClass('scroll-xl');
    };
    
    /** 
     * The following data members must be globally accessible due to existing
     * code that depends on them. see pickerDialog.tag 
     */
    yukon.protoPicker.prototype.multiSelectMode = true;
    yukon.protoPicker.prototype.immediateSelectMode = true;
    yukon.protoPicker.prototype.endAction = null;
    yukon.protoPicker.prototype.endEvent = null;
    yukon.protoPicker.prototype.cancelAction = null;
    yukon.protoPicker.prototype.destinationFieldId = null;
    yukon.protoPicker.prototype.memoryGroup = false;
    yukon.protoPicker.prototype.extraArgs = null;
    yukon.protoPicker.prototype.selectionProperty = null;
    yukon.protoPicker.prototype.allowEmptySelection = false;
    yukon.protoPicker.prototype.maxNumSelections = null;
    yukon.protoPicker.prototype.selectedAndMsg = '';
    yukon.protoPicker.prototype.selectedMoreMsg = '';
    yukon.protoPicker.prototype.useInitialIdsIfEmpty = false;
    yukon.protoPicker.prototype.excludeIds = [];
    yukon.protoPicker.prototype.disabledIds = [];
    
    /** 
     * If "memoryGroup" is set to something other than false, a picker will
     * check here for the last search using memoryGroup as the key.  This allows
     * multiple pickers on the same page to share the last search text.
     */
    if ('undefined' === typeof yukon.protoPicker.rememberedSearches) {
        yukon.protoPicker.rememberedSearches = {};
    }
    
    initialize.call(this, okText, cancelText, noneSelectedText, pickerType, destinationFieldName, pickerId, extraDestinationFields, container);
    
    // Circumvent default processing of ctrl-left-click and shift-left-click events
    $(document).on('click', '#' + this.pickerId, function (ev) {
        if (ev.ctrlKey || ev.shiftKey) {
            ev.preventDefault();
        }
    });
    
};

/** 
 * Constructor for Picker
 * @constructor
 * @name Picker 
 */
function Picker(okText, cancelText, noneSelectedText, pickerType, destinationFieldName, pickerId, extraDestinationFields, 
        container) {
    
    // JQuery wrap the container, it should be a string (css selector) or null at this point.
    if (container) container = $(container);
    
    yukon.protoPicker.call(this, okText, cancelText, noneSelectedText, pickerType, destinationFieldName, pickerId, extraDestinationFields, container);
};

/**
 * Set Picker prototype inheritance.
 * Called once to assign methods and properties accessible to all instances of Picker.
 */
yukon.inheritPrototype(Picker, yukon.protoPicker);

/**
 * Add general behavior binding for all pickers.
 * 
 * Add keyboard binding for up, down left right and enter.
 * up - Move focus up.
 * down - Move focus down.
 * enter - Select the focused row or select the only row if there is only one.
 * left - Goto to previous page.
 * right - Goto to next page.
 */ 
$(function () {
    
    /** Fire the keyup behavior on search field keyup. */
    $(document).on('keyup', '.js-picker-search-field', function (ev) {
        var pickerId = $(this).closest('[data-picker]').data('picker');
        yukon.pickers[pickerId].doKeyUp();
    });
    
    /** Clear the filtering when clear button clicked */
    $(document).on('click', '.js-picker-show-all', function (ev) {
        var pickerId = $(this).closest('[data-picker]').data('picker');
        yukon.pickers[pickerId].showAll();
    });
    
    $(document).on('keydown', '.js-picker-dialog', function (ev) {
        
        var key = ev.which,
            dialog = $(this),
            table = dialog.find('.picker-results-table tbody'),
            next = dialog.find('.next-page'),
            prev = dialog.find('.previous-page'),
            focus = $(':focus'),
            searching = focus.is('.js-picker-search');
        
        if (yukon.values(yg.keys).indexOf(key) != -1) {
            
            ev.stopPropagation();
            ev.preventDefault();
            
            if (key == yg.keys.down) {
                if (searching) {
                    table.find('tr:first-child').focus();
                } else if (focus.is('tr')) {
                    if (focus.is(':last-child')) {
                        dialog.find('.js-picker-search').focus();
                    } else {
                        focus.next().focus();
                    }
                }
            } else if (key == yg.keys.up) {
                if (searching) {
                    table.find('tr:last-child').focus();
                } else if (focus.is('tr')) {
                    if (focus.is(':first-child')) {
                        dialog.find('.js-picker-search').focus();
                    } else {
                        focus.prev().focus();
                    }
                }
            } else if (key == yg.keys.right) {
                if (next.css('visibility') !== 'hidden') next[0].click();
            } else if (key == yg.keys.left) {
                if (prev.css('visibility') !== 'hidden') prev[0].click();
            } else if (key == yg.keys.enter) {
                if (focus.is('tr')) {
                    focus.find('a').trigger('click');
                } else if (searching) {
                    if (table.find('tr').length === 1) {
                        table.find('a').trigger('click');
                    }
                }
            }
        }
        
    });
});