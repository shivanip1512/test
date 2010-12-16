var currentOrderByColum = 'STRATEGY';
var currentAscendingOrder = true;

var setMappedNameId = function() {
	
	if ($('strategyName').value.strip() == '' || $('substationName').value.strip() == ''){
		alert('Strategy and Substation names are required.');
		return null;
	}
	
	toggleLmMappingsWaitIndicators(true, $('addButton'));
	if ($('mappedNameId').value.strip() == '') {
		alert('Select a Program/Scenario name.');
		return;
	}

	getMappedName(function(mappedName) {
	
		var overwrite = true;
		if (mappedName != null && mappedName != $('mappedName').innerHTML) {
			overwrite = confirm('Strategy/Substation ' + $('strategyName').value + '/' + $('substationName').value + ' is already mapped to "' + mappedName + '", are you want to overwrite it with "' + $('mappedName').innerHTML + '"?');
		} 
	
		if (overwrite) {
	
			var url = '/spring/multispeak/setup/lmMappings/addOrUpdateMapping';
			var params = $H({
			    'strategyName': $('strategyName').value,
			    'substationName': $('substationName').value,
			    'mappedNameId': $('mappedNameId').value
			});
	    
			new Ajax.Request(url, {
				'parameters': params,
				'evalScripts': true,
				'onSuccess': function(transport, json) {
					$('mappedNameDisplay').innerHTML = $('mappedName').innerHTML;
					
					toggleLmMappingsWaitIndicators(false, $('addButton'));
					flashYellow($('mappedNameDisplay'));
					reloadAllMappingsTable(null, false);
				},
				'onException': function(e) {
					
					toggleLmMappingsWaitIndicators(false, $('addButton'));
					alert('Error adding mapping: ' + e.responseText);
				}
			});
		} else {
			toggleLmMappingsWaitIndicators(false, $('addButton'));
		}
	
	});
}

function getMappedName(callback) {

	var url = '/spring/multispeak/setup/lmMappings/findMapping';
	var params = $H({
	    'strategyName': $('strategyName').value,
	    'substationName': $('substationName').value
	});
	
	 new Ajax.Request(url, {
	   'parameters': params,
	   'asynchronous': false,
	   'onSuccess': function(transport, json) {
		 	var mappedName = json['mappedName'];
			callback(mappedName);
	   },
	   'onException': function(e) {
			alert('Error searching for Strategy/Substation: ' + e.responseText);
		   }
		 });

}
  

function doLmMappingNameSearch(){
	
	toggleLmMappingsWaitIndicators(true, $('searchButton'));

	getMappedName(function(mappedName) {
	
		if (mappedName != null) {
			$('mappedNameDisplay').innerHTML = mappedName;
		} else {
			$('mappedNameDisplay').innerHTML = 'Not Found';
		}
		
		toggleLmMappingsWaitIndicators(false, $('searchButton'));
		flashYellow($('mappedNameDisplay'));
	
	});
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
	var params = $H({
	    'col': currentOrderByColum,
	    'ascending': currentAscendingOrder
	});
	
	new Ajax.Updater('allMappingsTableDiv', url, {
	   'parameters': params,
	   'evalScripts': true,
	   'onSuccess': function(transport, json) {
	   },
	   'onException': function(e) {
	   }
	 });
}


function removeLmMapping(mspLMInterfaceMappingId) {
	
	if (!confirm('Are you sure you want to remove this mappping?')) {
		return;
	}
	
	var url = '/spring/multispeak/setup/lmMappings/removeMapping';
	var params = $H({
	    'mspLMInterfaceMappingId': mspLMInterfaceMappingId
	});
	
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

function toggleLmMappingsWaitIndicators(isWaiting, buttonEl) {
	
	if (isWaiting) {
		$('waitImg').show();
		buttonEl.disable();
	} else {
		$('waitImg').hide();
		buttonEl.enable();
	}
}