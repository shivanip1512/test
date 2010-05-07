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
			extraDestinationFields) {
		this.pickerType = pickerType;
		this.destinationFieldName = destinationFieldName;
		this.pickerId = pickerId;
		this.excludeIds = new Array();
		this.multiSelectMode = false;
		this.immediateSelectMode = false;
		this.memoryGroup = false;
		this.reset(false, true);

		this.extraDestinationFields = new Array();
		if (extraDestinationFields) {
			var pairs = extraDestinationFields.split(/;/);
			for (index = 0; index < pairs.length; index++) {
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

		this.resultAreaId = 'picker_' + this.pickerId + '_resultArea';
		this.resultAreaFixedId = 'picker_' + this.pickerId + '_resultAreaFixed';
		this.errorHolderId = 'picker_' + this.pickerId + '_errorHolder';
	},

	/**
	 * Reset things that need to be reset when first popping up the picker
	 * or when clearing it.
	 * 
	 * - clearRecent is true for showAll but false for just showing the
	 *   picker (where we want to re-use the previous search).
	 */
	reset: function(clearRecent, clearSelectedItems) {
		if (clearRecent && this.memoryGroup) {
			Picker.rememberedSearches[this.memoryGroup] = '';
		}
		this.currentSearch = '';
		this.inSearch = false;
		this.previousIndex = -1;
		this.nextIndex = -1;
		if (this.ssInput) {
			this.ssInput.value = '';
		}
		if (clearSelectedItems) {
			this.selectedItems = new Array();
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
		var ss = pickerThis.ssInput.value;
		if (pickerThis.currentSearch != ss) {
			setTimeout(timerFunction, quietDelay);
			showBusy();
		}
	},

	/**
	 * Actually do the search.  This method sets up a request to the server
	 * for new data based on the entered search text.
	 * 
	 * - start is the index of the first result to show.
	 */
	doSearch: function(start) {
		this.inSearch = true;
		showBusy();
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

		new Ajax.Request('/picker/v2/search', {
			'method': 'get',
			'parameters': parameters,
			'onComplete': this.updateSearchResults.bind(this),
			'onFailure': this.ajaxError.bind(this)
			});
	},

	/**
	 * This is the primary externally called method.  It pops the picker up
	 * and does the initial search.
	 */
	show: function() {
		if (this.immediateSelectMode && this.multiSelectMode) {
			alert('immediateSelectMode cannot be used with multiSelectMode; ' + 
				'turning multiSelectMode off');
			this.multiSelectMode = false;
		}
		this.reset(false, true);
		this.clearSearchResults();
		if (!$(this.pickerId)) {
			showBusy();
			var bodyElem = document.documentElement.getElementsByTagName('body')[0];
			var pickerDialogDivContainer = document.createElement('div');
			bodyElem.appendChild(pickerDialogDivContainer);
			var parameters = {
					'type' : this.pickerType,
					'id' : this.pickerId,
					'multiSelectMode' : this.multiSelectMode,
					'immediateSelectMode' : this.immediateSelectMode
				};
			new Ajax.Updater(pickerDialogDivContainer, '/picker/v2/build', {
				'parameters': parameters,
				'evalScripts': true,
				'onComplete': this.onComplete.bind(this)
			});
		} else {
			this.doShow();
		}
	},

	doShow : function() {
		adjustDialogSizeAndPosition(this.pickerId);
		if (this.memoryGroup && Picker.rememberedSearches[this.memoryGroup]) {
			this.ssInput.value =
				Picker.rememberedSearches[this.memoryGroup];
		}
		$(this.pickerId).show();
		this.ssInput.focus();
		this.doSearch();
	},

	onComplete : function(transport, json) {
		hideBusy();
		this.ssInput = $('picker_' + this.pickerId + '_ss');
		this.showAllLink = $('picker_' + this.pickerId + '_showAllLink');
		this.inputAreaDiv = $('picker_' + this.pickerId + '_inputArea');
		this.resultsDiv = $('picker_' + this.pickerId + '_results');
		this.noResultsDiv = $('picker_' + this.pickerId + '_noResults');
		this.nothingSelectedDiv = $('picker_' + this.pickerId + '_nothingSelected');
		this.selectAllCheckBox = $('picker_' + this.pickerId + '_selectAll');
		this.outputColumns = json.outputColumns;
		this.idFieldName = json.idFieldName;
		this.doShow();
	},

	/**
	 * Hide the picker.
	 */
	hide: function() {
		$(this.pickerId).hide();
	},

	okPressed: function() {
		if (this.selectedItems.size() == 0) {
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

			if (!this.endAction || this.endAction(this.selectedItems)) {
				var hit = this.selectedItems[0];
				for (var index = 0; index < this.extraDestinationFields.length; index++) {
					extraDestinationField = this.extraDestinationFields[index];
					
					// support for both innerHTML and value setting
					$(extraDestinationField.fieldId).innerHTML = hit[extraDestinationField.property];
					$(extraDestinationField.fieldId).value = hit[extraDestinationField.property];
				}
				this.hide();
			}
		}
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
		this.reset(true, false);
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
		hideBusy();
		if (this.currentSearch != ss) {
			// do another search
			this.doSearch();
		} else {
			this.inSearch = false;
		}
	},

	ajaxError: function(transport) {
		this.inSearch = false;
		hideBusy();
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

	/**
	 * Update the paging area of the form for the current search results.
	 */
	updatePagingArea: function(json) {
		var pickerDiv = $(this.pickerId);
		this.previousIndex = -1;
		this.nextIndex = -1;

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

	selectAll: function() {
		var pickerThis = this;
		this.allLinks.each(function(hitRow) {
			var parentRow = $($(hitRow.link).parentNode.parentNode);
			if (pickerThis.selectAllCheckBox.checked) {
				parentRow.addClassName('highlighted');
				pickerThis.selectedItems.push(hitRow.hit);
			} else {
				parentRow.removeClassName('highlighted');
				pickerThis.selectedItems = pickerThis.selectedItems.without(hitRow.hit);
			}
		});
	},

	updateSelectAllCheckbox: function() {
		if (!this.multiSelectMode) {
			return;
		}
		var allSelected = true;
		this.allLinks.each(function(hitRow) {
			if (!$($(hitRow.link).parentNode.parentNode).hasClassName('highlighted')) {
				allSelected = false;
			}
		});
		this.selectAllCheckBox.checked = allSelected;
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
			this.selectedItems = this.selectedItems.without(hit);
			this.selectAllCheckBox.checked = false;
		} else {
			// select
			if (!this.multiSelectMode) {
				// not multi-select mode; unselect all others
				var rows = parentRow.parentNode.childNodes;
				for (var index = 0; index < rows.length; index++) {
					rows[index].removeClassName('highlighted');
				}
				this.selectedItems = [hit];
			} else {
				this.selectedItems.push(hit);
				this.updateSelectAllCheckbox();
			}
			parentRow.addClassName('highlighted');
		}
	}
}
