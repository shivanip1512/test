/* refresh needs to be called once and it will set up
   periodic updates */   
function refresh(evt) {	
	SVGDoc = evt.getTarget().getOwnerDocument();
	dynText = SVGDoc.getElementsByTagName('text');
	dynImages = SVGDoc.getElementsByTagName('image');
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	root = evt.getTarget();	
		
	updateAll();
	
	setInterval('updateAll()',120000); //5 minutes
} //end refresh

/* update everything! */
function updateAll() {
	updateAllPoints();
	updateAllGraphs();
	updateAllTables();
} //end updateAll

function updateAllPoints() {
	dynText = SVGDoc.getElementsByTagName('text');
	for (var i=0; i<dynText.getLength(); i++) 
	{
		updateNode(dynText.item(i));
	}
	
	dynImages = SVGDoc.getElementsByTagName('image');	
	for(var j=0; j<dynImages.getLength(); j++)
	{
		updateImage(dynImages.item(j));
	}
	
} //end updatePoints

function updateAllGraphs() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	
	for (i=0; i < dynSVG.getLength(); i++) {
		var svgElement = dynSVG.item(i);
		if (svgElement.getAttribute('object') == 'graph') {
			updateGraph(svgElement);
		}
		else {
			continue;
		}
	}
} //end updateGraphs

function updateAllTables() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('object') == 'table') {
			updateAlarmsTable(svgElement);
		}
		else {
			continue;
		}
	}
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
			"&loadfactor=" + node.getAttribute('loadfactor');	
			
	getURL(url, fn2);
	
	function fn2(obj) {   
		var Newnode = parseXML(obj.content, SVGDoc);
		var gdefid = node.getAttribute('gdefid');
		var view = node.getAttribute('view');
		var width = node.getAttribute('width');
		var height = node.getAttribute('height');
		var format = node.getAttribute('format');
		var start = node.getAttribute('start');
		var period = node.getAttribute('period');
		var loadfactor = node.getAttribute('loadfactor');
		var db = node.getAttribute('db');
		var x = node.getAttribute('x');
		var y = node.getAttribute('y');
		
		SVGDoc.documentElement.replaceChild(Newnode,node);
		
		var svgElements = Newnode.getOwnerDocument().documentElement.getElementsByTagName('svg');

		for (j = 0; j<svgElements.getLength(); j++) {
			var svgElem = svgElements.item(j);
			if (svgElem.getAttribute('gdefid') == gdefid) {     	
				svgElem.setAttributeNS(null, 'view', view);
     			svgElem.setAttributeNS(null, 'width', width);
     			svgElem.setAttributeNS(null, 'height', height);
			 	svgElem.setAttributeNS(null, 'format', format);
			 	svgElem.setAttributeNS(null, 'start', start);
				svgElem.setAttributeNS(null, 'period', period);
			 	svgElem.setAttributeNS(null, 'db', db);
			 	svgElem.setAttributeNS(null, 'loadfactor', loadfactor);
			 	svgElem.setAttributeNS(null, 'x', x);
			 	svgElem.setAttributeNS(null, 'y', y);
			 	svgElem.setAttributeNS(null, 'object', 'graph');     	
			 	svgElem.setAttributeNS(null, 'onclick', 'updateGraphChange(evt)');
     		}
			else  {
	  			continue;	  
	    	} 
		}
	} // end f2
} // end updateGraph

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

		SVGDoc.documentElement.removeChild(node);
		SVGDoc.documentElement.appendChild(Newnode);		
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
	    	node.setAttribute('xlink\:href', obj.content);
		}
	} //end fn
} //end updateImage


function go() {
alert("going!");
location = 'sublist.html';
}
