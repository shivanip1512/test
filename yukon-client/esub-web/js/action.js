function followLink(url) {
	location = url;
}

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
	alert('sorry not implemented yet');
}

function controlPoint(evt) {
	alert('sorry, control not implemented');
}