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

var allDynamicTextElem = new Array();
var allAlarmTextElem = new Array();
var allStateImageElem = new Array();
var allDynamicGraphElem = new Array();

/* rectangle to indicate an image is a link */
var selectedRect = null;

/*
 * If set to true, dynamic elements will update
 * otherwise not.  Do this if you want to keep
 * the dom from moving while you wait for in put for example
 */
function setEnableUpdate(b) {
	doUpdates = b;
}

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
	
	setInterval('updateAllPoints()', pointRefreshRate);
	setInterval('updateAllGraphs()', graphRefreshRate); 
	setInterval('updateAllTables()', tableRefreshRate);
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
//	setTimeout('updateAllPoints()', pointRefreshRate, pointRefreshRate);
} //end updatePoints

function updateAllGraphs() {
	var i;
 //jalert("doing it");
	for(i = 0; i < allDynamicGraphElem.length; i++) {

		updateGraph(allDynamicGraphElem[i]);
	}
	
//	setTimeout('updateAllGraphs()', graphRefreshRate); 
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
//	setTimeout('updateAllTables()', tableRefreshRate, tableRefreshRate);
} //end updateTables



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
//alert("just set url");	
} //end updateGraph

/* update an individual table */
function updateAlarmsTable(node,url) {
	url =		'/servlet/AlarmsTableGenerator?' +
				'deviceid=' + node.getAttribute('deviceid') +
				'&x=' + node.getAttribute('x') + 
				'&y=' + node.getAttribute('y') +
				'&width=' + node.getAttribute('width') +
				'&height=' + node.getAttribute('height');		
	getURL(url,fn2);
	
	function fn2(obj) {   
		var Newnode = parseXML(obj.content, SVGDoc);

		if(findChild(node) != null) {
			SVGDoc.documentElement.removeChild(node);
			SVGDoc.documentElement.appendChild(Newnode);		
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
	var imageID = node.getAttribute('id');
    if( !isNaN(imageID) ) {        
        url = '/servlet/StateImageServlet' + '?' + 'id=' + node.getAttribute('id');
        getURL(url, fn);
    }
    
	function fn(obj) {
		//confirm(obj.content);
	    if (obj.content) {  
	    	node.setAttributeNS(xlinkNS, 'xlink\:href', obj.content);
		}
	} //end fn
} //end updateImage

/* update a single alarm text element */
function updateAlarmText(node) {
	var pointIDs = node.getAttribute('id');
	var fill1 = node.getAttribute('fill1');
	var fill2 = node.getAttribute('fill2');

	if(pointIDs.length > 0) {
		url = encodeURI('/servlet/AlarmTextStyleServlet' + '?' + 'id=' + pointIDs + '&fill1=' + fill1 + '&fill2=' + fill2);
		getURL(url,fn);	
	}
	
	/* Handle the getURL to the AlarmTextStyleServlet */
	function fn(obj) {
		if(obj.content) {
			node.getStyle().setProperty('fill', obj.content);
		}
	}
}



function suppressErrors() {
	return true;
}