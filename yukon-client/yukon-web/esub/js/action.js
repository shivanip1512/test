function refreshDrawing() {
	location.reload(true);
}

var ERROR = 'error';

function editValue(evt) {
	var node = evt.getTarget();
	var newVal = prompt("Enter the new value:",node.getFirstChild.getData());
	if (newVal) {
		url = '/servlet/UpdateAttribute' + '?' +'id=' + node.getAttribute('id')  + '&dattrib=' + node.getAttribute('dattrib') + '&value=' + newVal;
		getURL(url, fn);
	}
	
	function fn(obj) {
	if (!(obj.content == ERROR)) 
		node.getFirstChild.setData(newVal);
	}
}

function acknowledgeAlarm(evt) {
	var node = evt.getTarget();
	if(confirm("Are you sure you want to clear all current alarms for " + node.getAttribute('devicename') +"?")) {
		url = '/servlet/ClearAlarm?deviceid=' + node.getAttribute('deviceid');
		getURL(url,fn);
	}
	
	function fn(obj) {
		setTimeout('updateAllTables()',4000);
	}
}

function controlPoint(evt) {
	alert('sorry, control not implemented');
}