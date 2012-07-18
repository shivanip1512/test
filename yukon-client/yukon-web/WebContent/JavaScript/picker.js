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
	initialize: function (pickerType, destinationFieldName, pickerId,
			extraDestinationFields, containerDiv) {
		this.pickerType = pickerType;
		this.destinationFieldName = destinationFieldName;
		this.pickerId = pickerId;
		this.excludeIds = new Array();
		this.multiSelectMode = false;
		this.immediateSelectMode = false;
		this.memoryGroup = false;
		this.resetSearchFields();
		this.selectedItems = new Array();
		this.extraDestinationFields = new Array();
		if (extraDestinationFields) {
			var pairs = extraDestinationFields.split(/;/);
			for (var index = 0; index < pairs.length; index++) {
				var pair = pairs[index].split(/:/);
				if (pair.length == 2) {
					var extraDestinationField = {
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
		this.nothingSelectedDiv.hide();
		var pickerThis = this;
		var timerFunction = function() {
			var ss = pickerThis.ssInput.value;
			// Don't do the search if it hasn't changed.  This could be
			// because they type a character and deleted it.
			if (!pickerThis.inSearch && pickerThis.currentSearch != ss) {
				pickerThis.doSearch();
			}
		};
		var quietDelay = 300;
		// Don't do the search if it hasn't changed.  This can happen if
		// the use the cursor key or alt-tab to another window and back.
		var ss = this.ssInput.value;
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
	doSearch: function(start, count, onComplete) {
		this.inSearch = true;
        this.block();
		var ss = this.ssInput.value;
		if (ss) {
			this.showAllLink.show();
		} else {
			this.showAllLink.hide();
		}
		this.currentSearch = ss;
		if (this.memoryGroup) {
			Picker.rememberedSearches[this.memoryGroup] = ss;
		}

		var parameters = {
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
			onComplete = this.updateSearchResults.bind(this);
		}

		new Ajax.Request('/picker/v2/search', {
			'method': 'get',
			'parameters': parameters,
			'onComplete': onComplete,
			'onFailure': this.ajaxError.bind(this)
			});
	},

	/**
	 * This method does secondary initialization which needs to happen after
	 * the HTML elements in the tag file have been fully created.  This method
	 * should only be called from the pickerDialog.tag.  Use viewMode when
	 * the picker should be a read-only view.
	 */
	init : function(viewMode) {
	    this.inputAreaDiv = $('picker_' + this.pickerId + '_inputArea');
	    if (!viewMode) {
	        if (this.selectionProperty) {
	            this.selectedItemsPopup = $('picker_' + this.pickerId + '_selectedItemsPopup');
	            this.selectedItemsDisplayArea = $('picker_' + this.pickerId + '_selectedItemsDisplayArea');
	            var showSelectedImg = $('picker_' + this.pickerId + '_showSelectedImg');
	            if (showSelectedImg) {
	                this.showSelectedLink = $(showSelectedImg.parentNode);
	                this.showSelectedLink.hide();
	            }
	        }
	    }
	    if (this.selectionProperty) {
	        this.selectionLabel = $('picker_' + this.pickerId + '_label').getElementsBySelector('span')[0];
	        this.originalSelectionLabel = this.selectionLabel.innerHTML;
	    }

		var initialIds = [];
		if (this.destinationFieldId) {
			if ($F(this.destinationFieldId)) {
				initialIds = $F(this.destinationFieldId).split(',');
			}
		} else {
			initialIds = this.inputAreaDiv.getElementsBySelector('input').pluck('value');
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
		var bodyElem = document.documentElement.getElementsByTagName('body')[0];
		var pickerDialogDivContainer;
		if (this.containerDiv) {
		    pickerDialogDivContainer = this.containerDiv;
		} else {
		    pickerDialogDivContainer = document.createElement('div');
	        bodyElem.appendChild(pickerDialogDivContainer);
		}
		var parameters = {
				'type' : this.pickerType,
				'id' : this.pickerId,
				'multiSelectMode' : this.multiSelectMode,
				'immediateSelectMode' : this.immediateSelectMode
			};
		if (this.extraArgs) {
			parameters.extraArgs = this.extraArgs;
		}
        if (this.containerDiv) {
            parameters.mode = 'inline';
        }

		new Ajax.Updater(pickerDialogDivContainer, '/picker/v2/build', {
			'parameters': parameters,
			'evalScripts': true,
			'onComplete': this.onPrimeComplete.bind(this, showPicker, initialIds, skipFocus)
		});
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

		this.lastSelectedItems = this.selectedItems.clone();
		this.resetSearchFields();
		this.clearSearchResults();
		if (!this.primed) {
		    Yukon.ui.blockPage();
			this.prime(true, null, skipFocus);
		} else {
			this.doShow(skipFocus);
		}
	},

	doShow : function(skipFocus) {
		if (!this.containerDiv) {
		    adjustDialogSizeAndPosition(this.pickerId);
		}
		if (this.memoryGroup && Picker.rememberedSearches[this.memoryGroup]) {
			this.ssInput.value =
				Picker.rememberedSearches[this.memoryGroup];
		}
		this.nothingSelectedDiv.hide();
		if (!this.containerDiv) {
		    $(this.pickerId).show();
		}
		if (!skipFocus) {
		    this.ssInput.focus();
		}
		this.doSearch();
	},

	onPrimeComplete : function(showPicker, initialIds, skipFocus, transport, json) {
	    Yukon.ui.unblockPage();
		this.ssInput = $('picker_' + this.pickerId + '_ss');
		this.showAllLink = $('picker_' + this.pickerId + '_showAllLink');
		this.resultsDiv = $('picker_' + this.pickerId + '_results');
		this.noResultsDiv = $('picker_' + this.pickerId + '_noResults');
		this.nothingSelectedDiv = $('picker_' + this.pickerId + '_nothingSelected');
		this.selectAllCheckBox = $('picker_' + this.pickerId + '_selectAll');
		this.selectAllPagesLink = $('picker_' + this.pickerId + '_selectAllPages');
		this.allPagesSelected = $('picker_' + this.pickerId + '_allPagesSelected');
		this.clearEntireSelectionLink = $('picker_' + this.pickerId + '_clearEntireSelection');
		this.entireSelectionCleared = $('picker_' + this.pickerId + '_entireSelectionCleared');
		if (json != null) {
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
                'onComplete': this.onIdSearchComplete.bind(this)
            });
        }
	},

	onIdSearchComplete : function(transport) {
		var json = transport.responseText.evalJSON();
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
		    $(this.pickerId).hide();
		}
	},

	okPressed: function() {
		if (!this.allowEmptySelection && this.selectedItems.size() == 0) {
			this.nothingSelectedDiv.show();
		} else {
			if (this.destinationFieldId) {
				$(this.destinationFieldId).value =
					this.selectedItems.pluck(this.idFieldName).join(',');
			} else {
				var pickerThis = this;
				this.inputAreaDiv.innerHTML = '';
				this.selectedItems.each(function(selectedItem) {
					var inputElement = document.createElement('input');
					inputElement.type = 'hidden';
					inputElement.value = selectedItem[pickerThis.idFieldName];
					inputElement.name = pickerThis.destinationFieldName;
					pickerThis.inputAreaDiv.appendChild(inputElement);
				});
			}

			if (this.updateOutsideFields(false)) {
			    if (!this.containerDiv) {
			        $(this.pickerId).hide();
			    }
			}
		}
	},

	updateOutsideFields : function(isInitial) {
		// protect from calling endAction for empty lists on first run.
		if (this.selectedItems.length == 0 && isInitial) {
			return;
		}

		var hit = null;
		if (this.selectedItems.length > 0) {
			hit = this.selectedItems[0];
			if (this.showSelectedLink) this.showSelectedLink.show();
		} else {
			if (this.showSelectedLink) this.showSelectedLink.hide();
		}

		if (this.selectionProperty) {
			if (hit == null) {
				this.selectionLabel.innerHTML = this.originalSelectionLabel;
				this.selectionLabel.addClassName('noSelectionPickerLabel');
			} else {
				var labelMsg = hit[this.selectionProperty].toString().escapeHTML();
				if (this.selectedItems.length > 1) {
					 labelMsg +=  ' ' + this.selectedAndMsg + ' ' +
					 	(this.selectedItems.length - 1) + ' ' +
					 	this.selectedMoreMsg;
				}
				this.selectionLabel.innerHTML = labelMsg;
				this.selectionLabel.removeClassName('noSelectionPickerLabel');
			}
		}

		for (var index = 0; index < this.extraDestinationFields.length; index++) {
			extraDestinationField = this.extraDestinationFields[index];
			var value = hit == null ? '' : hit[extraDestinationField.property];
			
			// support for both innerHTML and value setting
			if ($(extraDestinationField.fieldId).tagName === 'INPUT') {
				$(extraDestinationField.fieldId).value = value;
			}
			else {
				$(extraDestinationField.fieldId).innerHTML = value;
			}
		}
        if (this.endAction) {
            this.endAction(this.selectedItems, this);
        }
		return true;
	},

	clearSelected: function() {
		if (this.destinationFieldId) {
			$(this.destinationFieldId).value = '';
		} else {
			this.inputAreaDiv.innerHTML = '';
		}
		if (this.selectionProperty) {
			this.selectionLabel.innerHTML = this.originalSelectionLabel;
			this.selectionLabel.addClassName('noSelectionPickerLabel');
		}
	},

	/**
	 * Get the id(s) of the selected item(s).
	 * If this is a multi-select picker, an array will be returned, 
	 * but in single select mode a single selected item will be returned.
	 */
	getSelected: function() {
	    var retVal = this.destinationFieldId
            ? $F(this.destinationFieldId).split(',')
            : this.inputAreaDiv.getElementsBySelector('input').pluck('value');
	    return this.multiSelectMode ? retVal : retVal[0];
	},

	previous: function() {
		if (this.previousIndex == -1) {
			return;
		}
		this.ssInput.focus();
		this.doSearch(this.previousIndex);
	},

	next: function() {
		if (this.nextIndex == -1) {
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
		if (!this.resultsDiv) {
			// our first time up; nothing to do
			return;
		}
		this.updatePagingArea();
		var oldResultArea = $(this.resultAreaId);
		var resultHolder = this.resultsDiv;
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
		var json = transport.responseText.evalJSON();
		var newResultArea = this.renderTableResults(json);
		this.updatePagingArea(json);
		this.selectAllPagesMsg = json.selectAllPages;
		this.allPagesSelectedMsg = json.allPagesSelected;
		var oldResultArea = $(this.resultAreaId);
		var resultHolder = this.resultsDiv;
		if (oldResultArea) {
			resultHolder.removeChild(oldResultArea);
		}
		var oldError = $(this.errorHolderId);
		if (oldError) {
			resultHolder.removeChild(oldError);
		}
		resultHolder.appendChild(newResultArea);
		this.updateSelectAllCheckbox();

		var ss = this.ssInput.value;
		Yukon.ui.unblockPage();
		this.unblock();
		if (this.currentSearch != ss) {
			// do another search
			this.doSearch();
		} else {
			this.inSearch = false;
		}
	},

	ajaxError: function(transport) {
		this.inSearch = false;
		Yukon.ui.unblockPage();
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
		var hitList = json.hits.resultList;
		var resultArea = document.createElement('div');
		resultArea.id = this.resultAreaId;
		var resultAreaFixed = document.createElement('div');
		resultAreaFixed.id = this.resultAreaFixedId;
		resultArea.appendChild(resultAreaFixed);

		this.allLinks = [];
		if (hitList && hitList.length && hitList.length > 0) {
			this.noResultsDiv.hide();
			this.resultsDiv.show();
			var pickerThis = this;
			var createItemLink = function(hit, link) {
				if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) != -1) {
					return null;
				} else {
					pickerThis.allLinks.push({'hit' : hit, 'link' : link});
					return function() {
						pickerThis.selectThisItem(hit, link);
					};
				}
			};

			var outputColumns = new Array();
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
		 			$(rowElement).addClassName("disabled");
		 			$(rowElement).setAttribute('title', Picker.alreadySelectedHoverMessage);
		 		} else {
		 			pickerThis.selectedItems.each(function(item){
		 				if (rowObject[pickerThis.idFieldName] == item[pickerThis.idFieldName]) {
		 					$(rowElement).addClassName("highlighted");
		 				}
		 			});
		 		}
		 		if (alternateRow) {
		 			$(rowElement).addClassName("altRow");
		 		}
		 		alternateRow = !alternateRow;
		 	};

			var resultTable = createHtmlTableFromJson(hitList, outputColumns,
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
		var outputColumns = new Array();
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
	 			$(rowElement).addClassName("altRow");
	 		}
	 		alternateRow = !alternateRow;
	 	};

		var resultTable = $(createHtmlTableFromJson(this.selectedItems,
				outputColumns, processRowForRender));
		resultTable.addClassName('compactResultsTable');
		resultTable.addClassName('pickerResultTable');
		resultTable.addClassName('rowHighlighting');
		this.selectedItemsDisplayArea.innerHTML = '';
		this.selectedItemsDisplayArea.appendChild(resultTable);
		adjustDialogSizeAndPosition(this.selectedItemsPopup);
		this.selectedItemsPopup.show();
	},

	/**
	 * Update the paging area of the form for the current search results.
	 */
	updatePagingArea: function(json) {
		var pickerDiv = $(this.pickerId);
		this.previousIndex = -1;
		this.nextIndex = -1;

		if (json) {
			this.hitCount = json.hits.hitCount;
		}
		if (json && json.hits.startIndex > 0) {
			this.previousIndex = json.hits.previousStartIndex;
			pickerDiv.getElementsBySelector('.previousLink .enabledAction')[0].show();
			pickerDiv.getElementsBySelector('.previousLink .disabledAction')[0].hide();
		} else {
			pickerDiv.getElementsBySelector('.previousLink .enabledAction')[0].hide();
			pickerDiv.getElementsBySelector('.previousLink .disabledAction')[0].show();
		}
		if (json && json.hits.endIndex < json.hits.hitCount) {
			this.nextIndex = json.hits.endIndex;
			pickerDiv.getElementsBySelector('.nextLink .enabledAction')[0].show();
			pickerDiv.getElementsBySelector('.nextLink .disabledAction')[0].hide();
		} else {
			pickerDiv.getElementsBySelector('.nextLink .enabledAction')[0].hide();
			pickerDiv.getElementsBySelector('.nextLink .disabledAction')[0].show();
		}

		pickerDiv.getElementsBySelector('.pageNumText')[0].innerHTML = json ? json.pages : '';
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
			var parentRow = $($(hitRow.link).parentNode.parentNode);
			if (pickerThis.selectAllCheckBox.checked) {
				parentRow.addClassName('highlighted');
				pickerThis.selectedItems.push(hitRow.hit);
			} else {
				parentRow.removeClassName('highlighted');
				pickerThis.removeFromSelectedItems(hitRow.hit);
			}
		});

		$(this.clearEntireSelectionLink.parentNode).hide();
		this.entireSelectionCleared.hide();
		$(this.allPagesSelected.parentNode).hide();
		// Cap "select all on every page" at 5000.
		if (this.selectAllCheckBox.checked && this.nextIndex != -1 && this.hitCount <= 5000) {
			this.selectAllPagesLink.innerHTML = this.selectAllPagesMsg;
			$(this.selectAllPagesLink.parentNode).show();
		} else {
			$(this.selectAllPagesLink.parentNode).hide();
			if (numSelectedBefore > this.selectedItems.length && this.selectedItems.length > 0) {
				$(this.clearEntireSelectionLink.parentNode).show();
			}
		}

		this.ssInput.focus();
	},

	selectAllPages: function() {
		this.doSearch(0, -1, this.selectAllOnComplete.bind(this));
	},

	selectAllOnComplete: function(transport) {
		var json = transport.responseText.evalJSON();
		var hitList = json.hits.resultList;
		// remove "exclude items"
		if (this.excludeIds && this.excludeIds.length > 0) {
			this.selectedItems = [];
			var pickerThis = this;
			hitList.each(function(hit) {
				if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) == -1) {
					pickerThis.selectedItems.push(hit);
				}
			});
		} else {
			this.selectedItems = hitList;
		}
		Yukon.ui.unblockPage();
		this.inSearch = false;
		this.ssInput.focus();
		$(this.selectAllPagesLink.parentNode).hide();
		this.allPagesSelected.innerHTML = this.allPagesSelectedMsg;
		$(this.allPagesSelected.parentNode).show();
	},

	clearEntireSelection : function() {
		this.selectedItems = [];
		$(this.allPagesSelected.parentNode).hide();
		$(this.clearEntireSelectionLink.parentNode).hide();
		this.entireSelectionCleared.show();
		this.allLinks.each(function(hitRow) {
			$($(hitRow.link).parentNode.parentNode).removeClassName('highlighted');
		});
		this.selectAllCheckBox.checked = false;
	},

	updateSelectAllCheckbox: function() {
		if (!this.multiSelectMode) {
			return;
		}
		var allSelected = this.allLinks.length > 0;
		this.allLinks.each(function(hitRow) {
			if (!$($(hitRow.link).parentNode.parentNode).hasClassName('highlighted')) {
				allSelected = false;
			}
		});
		this.selectAllCheckBox.checked = allSelected;
		$(this.selectAllPagesLink.parentNode).hide();
		$(this.allPagesSelected.parentNode).hide();
		$(this.clearEntireSelectionLink.parentNode).hide();
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

		var parentRow = $($(link).parentNode.parentNode);
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
					$(rows[index]).removeClassName('highlighted');
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
	    Yukon.uiUtils.elementGlass.show(document.getElementById(this.resultAreaId));
	},

	unblock : function() {
	    Yukon.uiUtils.elementGlass.hide(document.getElementById(this.resultAreaId));
	},
};
