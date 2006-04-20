var svgNS    = "http://www.w3.org/2000/svg";
var xlinkNS  = "http://www.w3.org/1999/xlink";

/* If true we'll do updates */
var doUpdates = true;

var dynamicTextID = 'dynamicText';
var stateImageID = 'stateImage';
var dynamicGraphID = 'dynamicGraph';
var alarmsTableID = 'alarmsTable';
var alarmTextID = 'alarmText';

/* refresh rates of various elements in milliseconds */
var graphRefreshRate = 360 * 1000;
var pointRefreshRate  = 15 * 1000; 
var tableRefreshRate  = 15 * 1000; 
var alarmAudioCheckRate = 5 * 1000;

var allDynamicTextElem = new Array();
var allAlarmTextElem = new Array();
var allStateImageElem = new Array();
var allDynamicGraphElem = new Array();

/* Onload, store all the things that we might be displaying alarms for */
var allAlarmDeviceIds = "";
var allAlarmPointIds = "";
var allAlarmAlarmCategoryIds = "";

/* rectangle to indicate an image is a link */
var selectedRect = null;

/* refresh needs to be called once and it will set up
   periodic updates */   
function refresh(evt) {	
	SVGDoc = evt.getTarget().getOwnerDocument();
	
	/* Find all the dynamic elements and store them so 
	   we don't have to look them up again. */
	dynText = SVGDoc.getElementsByTagName('text');
	for (var i=0; i<dynText.getLength(); i++) 
	{		
		var elemID = dynText.item(i).getAttribute('elementID');
    	if(elemID == dynamicTextID) {  
    		allDynamicTextElem.push(dynText.item(i));  
		}
		
		if(elemID == alarmTextID) {
			allAlarmTextElem.push(dynText.item(i));
			allAlarmDeviceIds += dynText.item(i).getAttribute('deviceid') + ',';
			allAlarmPointIds += dynText.item(i).getAttribute('pointid') + ',';
			allAlarmAlarmCategoryIds += dynText.item(i).getAttribute('alarmcategoryid') +',';
		}
	}
	
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('elementID') == alarmsTableID) {
			allAlarmDeviceIds += svgElement.getAttribute('deviceid') + ',';
			allAlarmPointIds += svgElement.getAttribute('pointid') + ',';
			allAlarmAlarmCategoryIds += svgElement.getAttribute('alarmcategoryid') +',';			
		}
	}
		
	dynImages = SVGDoc.getElementsByTagName('image');	
	for(var j=0; j<dynImages.getLength(); j++)
	{
		var elemID = dynImages.item(j).getAttribute('elementID');
		if(elemID == stateImageID) {
			allStateImageElem.push(dynImages.item(j));
		}
		else
		if(elemID == dynamicGraphID) {
			allDynamicGraphElem.push(dynImages.item(j));
		}		
	}
	
	updateAllTables();
	
	setInterval('updateAllPoints()', pointRefreshRate);
	setInterval('updateAllGraphs()', graphRefreshRate); 
	setInterval('updateAllTables()', tableRefreshRate);
	setInterval('checkAlarmAudio(allAlarmDeviceIds, allAlarmPointIds, allAlarmAlarmCategoryIds)', alarmAudioCheckRate);
} //end refresh

function updateAllPoints() {
	var i;
	for(i = 0; i < allDynamicTextElem.length; i++) {
		updateNode(allDynamicTextElem[i]);
	}
	
	for(i = 0; i < allAlarmTextElem.length; i++) {
		updateAlarmText(allAlarmTextElem[i]);
	}

	for(i = 0; i < allStateImageElem.length; i++) {
		updateImage(allStateImageElem[i]);
	}
} //end updatePoints

function updateAllGraphs() {
	var i;
	for(i = 0; i < allDynamicGraphElem.length; i++) {
		updateGraph(allDynamicGraphElem[i]);
	}
} //end updateGraphs

function updateAllTables() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('elementID') == alarmsTableID) {
			updateAlarmsTable(svgElement);
		}
		else {
			continue;
		}
	}
} //end updateTables

function checkAlarmAudio(deviceIds, pointIds, alarmCategoryIds) {
	var url = 	"/servlet/AlarmAudioServlet?" +
			"deviceid=" + deviceIds +
			"&pointid=" + pointIds +
			"&alarmcategoryid=" + alarmCategoryIds +
			"&rand=" + Math.random();

	getURL(url, fn3);

	function fn3(obj) {
		if(!obj.success) {
			alert('Alarm audio request failed.  Please restart your browser if this continues.');
		}
		else		
		if(obj.content == 'true') {
			playAlarmAudio();
		}
		else {
			stopAlarmAudio();
		}
	}	 
}

/* update an individual graph */
function updateGraph(node) {

	url =   "/servlet/GraphGenerator?" + 
			"gdefid=" + node.getAttribute('gdefid') +
			"&view=" + node.getAttribute('view') +
			"&width=" + node.getAttribute('width') +
			"&height=" + node.getAttribute('height') +
			"&format=" + node.getAttribute('format') +
			"&start=" + node.getAttribute('start') + 
			"&period=" + node.getAttribute('period') +
			"&db=" + node.getAttribute('db') +
			"&option=" + node.getAttribute('option') +
			"&loadfactor=" + node.getAttribute('loadfactor') +
			"&action=EncodeGraph" +
			"&rand=" + Math.random();  //The only reason this is here
			                      //is because unless the url changes
			                      //the plugin won't actually hit the server

	node.setAttributeNS(xlinkNS, 'xlink\:href', url);
} //end updateGraph

/* update an individual table */
function updateAlarmsTable(node,url) {
	var deviceIds = node.getAttribute('deviceid');
	var pointIds = node.getAttribute('pointid');
	var alarmCategoryIds = node.getAttribute('alarmcategoryid');
	
	url =		'/servlet/AlarmsTableGenerator?' +
				'deviceid=' + deviceIds +
				'&pointid=' + pointIds +
				'&alarmcategoryid=' + alarmCategoryIds +
				'&x=' + node.getAttribute('x') + 
				'&y=' + node.getAttribute('y') +
				'&width=' + node.getAttribute('width') +
				'&height=' + node.getAttribute('height') +
				'&rand=' + Math.random();		
				
				
	getURL(url,fn2);
	
	function fn2(obj) {   
		if(!obj.success) {
			alert('Alarm table request failed.  Please restart your browser if this continues.');		
			return;
		}
			
		var Newnode = parseXML(obj.content, SVGDoc);

		if(findChild(node) != null) {
			SVGDoc.documentElement.removeChild(node);
			// svg has no z-index that I can find and appears to use the painters algorithm 
			// to draw elements.  That is svg draws the elements in the order they appear in the dom.
			// If we just append the new table we will possibly cover up some buttons
			// But we have to insert it after the black background rect, phew.
			var b = SVGDoc.getElementById("backgroundRect");
			SVGDoc.documentElement.insertBefore(Newnode, b.getNextSibling());
		}
	} // end f2
} // end updateAlarmsTable

function updateNode(node) {
	if (node.getAttribute('dattrib')) {
	    
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + node.getAttribute('id') + '&' + 'dattrib=' + node.getAttribute('dattrib');
		getURL(url, fn);
	}

	function fn(obj) {
		if(obj.content) {
			node.getFirstChild().setData(obj.content);
		}	
	}
} //end updateNode

/* update a single state image */
function updateImage(node) {
	var pointId = node.getAttribute('id');
	if( !isNaN(pointId) ) {
		// dattrib = 1 is value attribute, TODO: don't hardcode this?
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + pointId + '&dattrib=1';
		getURL(url, fn);		
	}
	
	function fn(obj) {
		if(obj.content) {
			var value = obj.content;
			var imageName = node.getAttribute('image'+trim(value));			
			node.setAttributeNS(xlinkNS, 'xlink:\href', imageName);
		}
	}
} //end updateImage

/* update a single alarm text element */
function updateAlarmText(node) {

	var deviceIds = node.getAttribute('deviceid');
	var pointIds = node.getAttribute('pointid');
	var alarmCategoryIds = node.getAttribute('alarmcategoryid');

	var fill1 = node.getAttribute('fill1');
	var fill2 = node.getAttribute('fill2');

	url = encodeURI('/servlet/AlarmTextStyleServlet' + '?' + 'deviceid=' + deviceIds + '&pointid=' + pointIds + '&alarmcategoryid=' + alarmCategoryIds + '&fill1=' + fill1 + '&fill2=' + fill2);
	getURL(url,fn);	

	/* Handle the getURL to the AlarmTextStyleServlet */
	function fn(obj) {
		if(obj.content) {
			node.getStyle().setProperty('fill', obj.content);			
		}
	}
}

/**
 * Remove white space from a string.
 */
function trim (s) {
	return s.replace(/^\s+|\s+$/g, '');
} 
