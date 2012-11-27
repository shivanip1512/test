
function swapSelectionTypeParamers() {
	
	var val = $('selectionTypeSelect').options[$('selectionTypeSelect').selectedIndex].value;
	
	alert(val);
}

// GROUP
function loadGroupPaoIdsPickerComplete() {
	
	if ($('loadGroupPaoIds').value != '') {
		checkBySelectionRadio();
	}
}

function previewByLoadGroup(previewImgId) {
	
	if ($('loadGroupPaoIds').value == '') {
		alert('Select one or more Load Groups.');
		return;
	}
	
	var url = '/stars/operator/deviceReconfig/preview?selectionType=LOAD_GROUP&loadGroupPaoIds=' + $('loadGroupPaoIds').value;
	
	openSimpleDialog('deviceReconfigSharedPreviewDialog', url, 'Inventory By Load Group');
}

// PROGRAM
function loadProgramPaoIdsPickerComplete() {
	
	if ($('loadProgramPaoIds').value != '') {
		checkBySelectionRadio();
	}
}

function previewByProgram() {
	
	if ($('loadProgramPaoIds').value == '') {
		alert('Select one or more Programs.');
		return;
	}
	
	var url = '/stars/operator/deviceReconfig/preview?selectionType=PROGRAM&loadProgramPaoIds=' + $('loadProgramPaoIds').value;
	
	openSimpleDialog('deviceReconfigSharedPreviewDialog', url, 'Inventory By Program');
}

// RE-CONFIG STYLE
function reconfigStyleByLoadGroupIdPickerComplete() {
	
	if ($('reconfigStyleByLoadGroupId').value != '') {
		checkReconfigurationStyleByLoadGroupRadio();
	}
}

// PREVIEW DEVICES
function previewDevices() {
	
	
}

// BACTH CALC
function recalcBatch() {
	
	var inventoryIdsCount = $('inventoryIdsCount').value;
	var batchCount = $('batchCount').value;
	
	if (!isNaN(batchCount)) {
		
		 var remainder = inventoryIdsCount % batchCount;
		 var quotient = (inventoryIdsCount - remainder) / batchCount;

		 if (quotient > 0) {
			 
			 $('batchCountDisplay').innerHTML = batchCount;
			 if (remainder > 0) {
				 $('perBatchCountDisplay').innerHTML = '(roughly) ' + quotient;
			 } else {
				 $('perBatchCountDisplay').innerHTML = quotient;
			 }
			 
			 $('splitInfoSpan').show();
			 $('splitErrorSpan').hide();
			 
		 } else {
			 
			 $('splitInfoSpan').hide();
			 $('splitErrorSpan').show();
		 }
	} else {
		
		$('splitInfoSpan').hide();
		$('splitErrorSpan').show();
	}
}

// PREVIEW SCHEDULE
function previewSchedule() {
	
	var url = '/stars/operator/deviceReconfig/previewSchedule?';
	url += 'inventoryIdsCount=' + $('inventoryIdsCount').value + '&';
	url += 'startDate=' + $('startDate').value + '&';
	url += 'startHour=' + $('startHour').options[$('startHour').selectedIndex].value + '&';
	url += 'startMinute=' + $('startMinute').options[$('startMinute').selectedIndex].value + '&';
	url += 'startAmPm=' + $('startAmPm').options[$('startAmPm').selectedIndex].value + '&';
	url += 'pageDelay=' + $('pageDelay').value + '&';
	url += 'batchCount=' + $('batchCount').value + '&';
	url += 'batchDelay=' + $('batchDelay').value;
	
	openSimpleDialog('deviceReconfigPreviewScheduleDialog', url, 'Schedule Preview');
}


// MISC
function checkBySelectionRadio() {
	
	$('deviceSelectionStyleBySelectionRadio').checked = true;
	$('deviceSelectionStyleByImportRadio').checked = false;
	$('previewSelectedDeviceTr').show();
}

function checkByImportRadio() {
	
	$('deviceSelectionStyleySelectionRadio').checked = false;
	$('deviceSelectionStyleByImportRadio').checked = true;
	$('previewSelectedDeviceTr').hide();
}

function checkReconfigurationStyleByLoadGroupRadio() {
	
	$('reconfigurationStyleByLoadGroupRadio').checked = true;
	$('reconfigurationStyleByCurrentSettingsRadio').checked = false;
}
