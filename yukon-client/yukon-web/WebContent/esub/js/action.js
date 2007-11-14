function refreshDrawing() {
	location.reload(true);
}

var ERROR = 'error';

function editValue(evt) {
	var node = evt.getTarget();
	var newVal = prompt("Enter the new value:",node.getFirstChild.getData());
	if (newVal) {
		url = '/servlet/UpdateAttribute' + '?' +'id=' + node.getAttribute('id')  + '&dattrib=' + node.getAttribute('dattrib') + '&value=' + newVal;
		getCtiURL(url, fn);
	}
	
	function fn(obj) {
	if (!(obj.content == ERROR)) 
		node.getFirstChild.setData(newVal);
	}
}

function acknowledgeAlarm(deviceIds, pointIds, alarmCategoryIds) {
	if(confirm("Are you sure you want to acknowledge all of the alarms in this table?")) {	
		url = '/servlet/ClearAlarm?deviceid=' + deviceIds +
			  '&pointid=' + pointIds +
			  '&alarmcategoryid=' + alarmCategoryIds;

		getCtiURL(url,fn);
	}
	
	function fn(obj) {
		setTimeout('updateAllTables()',4000);
	}
}

function toggleMute(evt) {
	var audioElem = SVGDoc.getElementById('audioElement');
	var displayName = SVGDoc.getDocumentElement().getAttribute('displayName');

	if(audioElem.getAttribute('isPlaying') == 'true') {	
		stopAlarmAudio();
		getCtiURL('/servlet/AlarmAudioServlet?mute=true', fn);		
	}

	function fn(obj) {
		if(!obj.success) {
			alert('Toggle mute request failed.  Please restart your browser if this continues.');
		}
	}	
}

/**
 * Start playing alarm audio.
 */
function playAlarmAudio() {
	var audioElem = SVGDoc.getElementById('audioElement');
	
	if(audioElem.getAttribute('isPlaying') != 'true') {
		audioElem.setAttributeNS("http://www.w3.org/1999/xlink","href","alarm.wav");
		audioElem.setAttributeNS(null, 'isPlaying', 'true');		
	}
}

/**
 * Stop playing alarm audio
 */
function stopAlarmAudio() {
	var audioElem = SVGDoc.getElementById('audioElement');
	if(audioElem.getAttribute('isPlaying') == 'true') {
		audioElem.setAttributeNS("http://www.w3.org/1999/xlink","href","silence.wav");	
		audioElem.setAttributeNS(null, 'isPlaying', 'false');
	}
}

function controlPoint(evt) {
	alert('sorry, control not implemented');
}