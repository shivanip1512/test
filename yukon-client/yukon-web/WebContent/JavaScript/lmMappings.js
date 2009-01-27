var currentOrderByColum = 'STRATEGY';
var currentAscendingOrder = true;
var highlightDuration = 0.8;

var setMappedNameId = function() {
	
	if ($('strategyName').value.strip() == '' || $('substationName').value.strip() == ''){
		alert('Strategy and Substation names are required.');
		return null;
	}
	
	toggleWait(true, $('addButton'));
	if ($('mappedNameId').value.strip() == '') {
		alert('Select a Program/Scenario name.');
		return;
	}

	var mappedName = getMappedName();
	var overwrite = true;
	if (mappedName != null) {
		overwrite = confirm('Strategy/Substation ' + $('strategyName').value + '/' + $('substationName').value + ' is already mapped to "' + mappedName + '", are you want to overwrite it with "' + $('mappedName').innerHTML + '"?');
	} 

	if (overwrite) {

		var url = '/spring/multispeak/setup/lmMappings/addOrUpdateMapping';
		var params = $H();
		params['strategyName'] = $('strategyName').value;
		params['substationName'] = $('substationName').value;
		params['mappedNameId'] = $('mappedNameId').value;
    
		new Ajax.Request(url, {
			'parameters': params,
			'evalScripts': true,
			'onSuccess': function(transport, json) {
				$('mappedNameDisplay').innerHTML = $('mappedName').innerHTML;
				
				toggleWait(false, $('addButton'));
				new Effect.Highlight($('mappedNameDisplay'), {'duration': highlightDuration, 'startcolor': '#FFE900'});
				reloadAllMappingsTable(null, false);
			},
			'onException': function(e) {
				
				toggleWait(false, $('addButton'));
				alert('Error adding mapping: ' + e.responseText);
			}
		});
	} else {
		toggleWait(false, $('addButton'));
	}
}

function getMappedName() {

	var mappedName = null;
	
	var url = '/spring/multispeak/setup/lmMappings/findMapping';
	var params = $H();
	params['strategyName'] = $('strategyName').value;
	params['substationName'] = $('substationName').value;
	
	 new Ajax.Request(url, {
	   'parameters': params,
	   'evalScripts': true,
	   'asynchronous': false,
	   'onSuccess': function(transport, json) {
			mappedName = json['mappedName'];
	   },
	   'onException': function(e) {
			alert('Error searching for Program/Scenario: ' + e.responseText);
		   }
		 });

		 return mappedName;
	}
  

function doSearch(){
	
	toggleWait(true, $('searchButton'));

	var mappedName = getMappedName();
	if (mappedName != null) {
		$('mappedNameDisplay').innerHTML = mappedName;
	} else {
		$('mappedNameDisplay').innerHTML = 'Not Found';
	}
	
	toggleWait(false, $('searchButton'));
	new Effect.Highlight($('mappedNameDisplay'), {'duration': highlightDuration, 'startcolor': '#FFE900'});
}

function reloadAllMappingsTable(col, isReorder) {

	if (isReorder) {
		if (col != currentOrderByColum) {
			currentAscendingOrder = true;
		} else {
			currentAscendingOrder = !currentAscendingOrder;
		}
		currentOrderByColum = col;
	}

	// call reload
	var url = '/spring/multispeak/setup/lmMappings/reloadAllMappingsTable';
	var params = $H();
	params['col'] = currentOrderByColum;
	params['ascending'] = currentAscendingOrder;
	
	new Ajax.Updater('allMappingsTableDiv', url, {
	   'parameters': params,
	   'evalScripts': true,
	   'onSuccess': function(transport, json) {
	   },
	   'onException': function(e) {
	   }
	 });
}


function removeMapping(mspLMInterfaceMappingId) {
	
	if (!confirm('Are you sure you want to remove this mappping?')) {
		return;
	}
	
	var url = '/spring/multispeak/setup/lmMappings/removeMapping';
	var params = $H();
	params['mspLMInterfaceMappingId'] = mspLMInterfaceMappingId;
	
	 new Ajax.Request(url, {
	   'parameters': params,
	   'evalScripts': true,
	   'onSuccess': function(transport, json) {
		 	reloadAllMappingsTable(null, false);
	   },
	   'onException': function(e) {
			alert('Error removing mapping: ' + e.responseText);
	   }
	});

}

function toggleWait(isWaiting, buttonEl) {
	
	if (isWaiting) {
		$('waitImg').show();
		buttonEl.disable();
	} else {
		$('waitImg').hide();
		buttonEl.enable();
	}
}