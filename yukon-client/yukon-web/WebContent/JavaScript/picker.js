Picker = Class.create();

Picker.prototype = {
	debugMode: true,
	rememberedSearches: {},

	initialize: function (pickerType, destinationFieldName, pickerId,
			extraDestinationFields) {
		this.pickerType = pickerType;
		this.destinationFieldName = destinationFieldName;
		this.pickerId = pickerId;
		this.excludeIds = new Array();
		this.multiSelectMode = false;
		this.immediateSelectMode = false;
		this.memoryGroup = false;
		this.reset(false);

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
	},

	log: function(msg) {
		if (!Picker.prototype.debugMode) {
			return;

		}

		var debugDiv = $('debugDiv');
		if (!debugDiv) {
			var bodyElem = document.documentElement.getElementsByTagName('body')[0];
			debugDiv = document.createElement('pre');
			debugDiv.id = 'debugDiv';
			bodyElem.appendChild(debugDiv);
		}

		var now = new Date();
		debugDiv.appendChild(
				document.createTextNode(now.getMinutes() + ':' +
						now.getSeconds() + '.' + now.getMilliseconds() + ' ' +
						this.pickerId + ' ' + msg + "\n"));
	},

	reset: function(clearRecent) {
		if (clearRecent && this.memoryGroup) {
			Picker.prototype.rememberedSearches[this.memoryGroup] = '';
		}
		this.currentSearch = '';
		this.inSearch = false;
		this.previousIndex = -1;
		this.nextIndex = -1;
		var pickerSS = $('picker_' + this.pickerId + '_ss');
		if (pickerSS) {
			pickerSS.value = '';
		}
		this.selectedItems = new Array();
	},

	doKeyUp: function() {
		var pickerThis = this;
		var timerFunction = function() {
			var ss = escape($('picker_' + pickerThis.pickerId + '_ss').value);
			if (!pickerThis.inSearch && pickerThis.currentSearch != ss) {
				pickerThis.doSearch();
			} else {
				pickerThis.hideBusy();
			}
		};
		var quietDelay = 300;
		var ss = escape($('picker_' + pickerThis.pickerId + '_ss').value);
		if (pickerThis.currentSearch != ss) {
			setTimeout(timerFunction, quietDelay);
			this.showBusy();
		}
	},

	doSearch: function(start) {
		this.inSearch = true;
		this.showBusy();
		var ss = escape($('picker_' + this.pickerId + '_ss').value);
		if (ss) {
			$('picker_' + this.pickerId + '_showAllLink').show();
		} else {
			$('picker_' + this.pickerId + '_showAllLink').hide();
		}
		this.currentSearch = ss;
		if (this.memoryGroup) {
			Picker.prototype.rememberedSearches[this.memoryGroup] = ss;
		}

		var url = '/picker/v2/search?type=' + this.pickerType + '&pickerId=' +
		this.pickerId + '&ss=' + ss;
		if (start) {
			url += '&start=' + start;
		}

		new Ajax.Request(url,
				{'method': 'get',
				'onComplete': this.updateSearchResults.bind(this),
				'onFailure': this.ajaxError.bind(this)});
	},

	show: function() {
		this.reset(false);
		var pickerThis = this;
		var doShow = function(transport, json) {
			if (pickerThis.memoryGroup && Picker.prototype.rememberedSearches[pickerThis.memoryGroup]) {
				$('picker_' + pickerThis.pickerId + '_ss').value =
					unescape(Picker.prototype.rememberedSearches[pickerThis.memoryGroup]);
			}
			$(pickerThis.pickerId).show();
			$('picker_' + pickerThis.pickerId + '_ss').focus();
			if (json) {
				pickerThis.outputColumns = json.outputColumns;
				pickerThis.idFieldName = json.idFieldName;
			}
			pickerThis.doSearch();
		};
		var errorCallback = function() {
			alert('could not load picker dialog');
		};
		if (!$(this.pickerId)) {
			var bodyElem = document.documentElement.getElementsByTagName('body')[0];
			var pickerDialogDivContainer = document.createElement('div');
			bodyElem.appendChild(pickerDialogDivContainer);
			new Ajax.Updater(pickerDialogDivContainer, '/picker/v2/build', {
				'parameters': {
					'type' : this.pickerType,
					'id' : this.pickerId,
					'immediateSelectMode' : this.immediateSelectMode
				},
				'onFailure' : errorCallback,
				'onComplete': doShow
			});
		} else {
			doShow();
		}
	},

	hide: function() {
		$(this.pickerId).hide();
	},

	showBusy: function() {
		$(this.pickerId).getElementsBySelector('.busyBox')[0].show();
	},

	hideBusy: function() {
		$(this.pickerId).getElementsBySelector('.busyBox')[0].hide();
	},

	okPressed: function() {
		if (this.selectedItems.size() == 0) {
			alert('You have not selected any Devices.');	
		} else {
			if (this.destinationFieldId) {
				$(this.destinationFieldId).value =
					this.selectedItems.pluck(this.idFieldName).join(',');
			} else {
				var pickerThis = this;
				var inputArea = $('picker_' + this.pickerId + '_inputArea');
				inputArea.innerHTML = '';
				this.selectedItems.each(function(selectedItem) {
					var inputElement = document.createElement('input');
					inputElement.type = 'hidden';
					inputElement.value = selectedItem[pickerThis.idFieldName];
					inputElement.name = pickerThis.destinationFieldName;
					inputArea.appendChild(inputElement);
				});
			}

			if (!this.endAction || this.endAction(this.selectedItems)) {
				var hit = this.selectedItems[0];
				for (var index = 0; index < this.extraDestinationFields.length; index++) {
					extraDestinationField = this.extraDestinationFields[index];
					$(extraDestinationField.fieldId).innerHTML =
						hit[extraDestinationField.property];
				}
				this.hide();
			}
		}
	},

	previous: function() {
		if (this.previousIndex == -1) {
			return;
		}
		this.doSearch(this.previousIndex);
	},

	next: function() {
		if (this.nextIndex == -1) {
			return;
		}
		this.doSearch(this.nextIndex);
	},

	showAll: function() {
		this.reset(true);
		$('picker_' + this.pickerId + '_ss').focus();
		this.doSearch();
	},

	updateSearchResults: function(transport, json) {
		var newResultArea = this.renderTableResults(json);
		this.updatePagingArea(json);
		var oldResultArea = $('picker_' + this.pickerId + '_resultArea');
		var resultHolder = $('picker_' + this.pickerId + '_results');
		if (oldResultArea) {
			resultHolder.removeChild(oldResultArea);
		}
		var oldError = $('picker_' + this.pickerId + '_errorHolder');
		if (oldError) {
			resultHolder.removeChild(oldError);
		}
		resultHolder.appendChild(newResultArea);

		var ss = escape($('picker_' + this.pickerId + '_ss').value);
		if (this.currentSearch != ss) {
			// do another search
			this.doSearch();
		} else {
			this.inSearch = false;
			this.hideBusy();
		}
	},

	ajaxError: function(transport, json) {
		this.inSearch = false;
		this.hideBusy();
		$('picker_' + this.pickerId + '_results').innerHTML = '';
		errorHolder = document.createElement('div');
		errorHolder.id = 'picker_' + this.pickerId + '_errorHolder';
		errorHolder.innerHTML = 'There was a problem searching the index: ' +
		transport.responseText;
		$('picker_' + this.pickerId + '_results').appendChild(errorHolder);
	},

	renderTableResults: function(json) {
		var hitList = json.hitList;
		var resultArea = document.createElement('div');
		resultArea.id = 'picker_' + this.pickerId + '_resultArea';
		var resultAreaFixed = document.createElement('div');
		resultAreaFixed.id = 'picker_' + this.pickerId + '_resultAreaFixed';
		resultArea.appendChild(resultAreaFixed);

		if (hitList && hitList.length && hitList.length > 0) {
			var pickerThis = this;
			var createItemLink = function(hit, link) {
				if (pickerThis.excludeIds.indexOf(hit[pickerThis.idFieldName]) != -1) {
					return null;
				} else {
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
		 			$(rowElement).setAttribute("title", "Already Selected");
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
			var emptyResultDiv = document.createElement('div');
			emptyResultDiv.id = 'picker_' + this.pickerId + '_noResult';
			var noResultText = 'No results found';
			emptyResultDiv.appendChild(document.createTextNode(noResultText));
			resultAreaFixed.appendChild(emptyResultDiv);
		}

		return resultArea;
	},

	updatePagingArea: function(json) {
		var pickerDiv = $(this.pickerId);
		this.previousIndex = -1;
		this.nextIndex = -1;

		if (json.startIndex > 0) {
			this.previousIndex = json.previousIndex;
			pickerDiv.getElementsBySelector('.previousLink .enabledAction')[0].show();
			pickerDiv.getElementsBySelector('.previousLink .disabledAction')[0].hide();
		} else {
			pickerDiv.getElementsBySelector('.previousLink .enabledAction')[0].hide();
			pickerDiv.getElementsBySelector('.previousLink .disabledAction')[0].show();
		}
		if (json.endIndex < json.hitCount) {
			this.nextIndex = json.nextIndex;
			pickerDiv.getElementsBySelector('.nextLink .enabledAction')[0].show();
			pickerDiv.getElementsBySelector('.nextLink .disabledAction')[0].hide();
		} else {
			pickerDiv.getElementsBySelector('.nextLink .enabledAction')[0].hide();
			pickerDiv.getElementsBySelector('.nextLink .disabledAction')[0].show();
		}

		var pageNumText = (json.startIndex + 1) + ' - '
			+ json.endIndex + ' of ' + json.hitCount;
		pickerDiv.getElementsBySelector('.pageNumText')[0].innerHTML = pageNumText;
	},

	selectThisItem: function(hit, link) {
		if (this.immediateSelectMode) {
	 		this.selectedItems = [hit];
	 		this.okPressed();
			return;
		}

		var parentRow = $($(link).parentNode.parentNode);
		if (parentRow.hasClassName('highlighted')){
			// unselect
			parentRow.removeClassName('highlighted');
			this.selectedItems = this.selectedItems.without(hit);
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
			}
			parentRow.addClassName('highlighted');
		}
	}
}
