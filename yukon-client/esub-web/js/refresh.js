function refresh(evt) {	
	SVGDoc = evt.getTarget().getOwnerDocument();
	dynText = SVGDoc.getElementsByTagName('text');
	dynImages = SVGDoc.getElementsByTagName('image');
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	root = evt.getTarget();	
		
	updateAll();
	
	setInterval('updateAll()',30000);
} //end refresh

function updateAll() {
	updatePoints();
	updateGraphs();
	updateTables();
}
function updatePoints() {
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

function updateGraphs() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	
	var url = '/servlet/GraphGenerator?';

	for (i=0; i < dynSVG.getLength(); i++) {
		var svgElement = dynSVG.item(i);
		if (svgElement.getAttribute('object') == 'graph') {
			updateGraph(svgElement, url);
		}
		else {
			continue;
		}
	}
} //end updateGraphs

function updateTables() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	
	var url = '/servlet/AlarmsTableGenerator?';
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('object') == 'table') {
			updateAlarmsTable(svgElement,url);
		}
		else {
			continue;
		}
	}
} //end updateTables
 
function updateGraph(node, url) {
	url = url + "gdefid=" + node.getAttribute('gdefid') +
      		"&model=" + node.getAttribute('model') +
			"&width=" + node.getAttribute('width') +
			"&height=" + node.getAttribute('height') +
			"&format=" + node.getAttribute('format') +
			"&start=" + node.getAttribute('start') + 
			"&end=" + node.getAttribute('end') +
			"&db=" + node.getAttribute('db') +
			"&loadfactor=" + node.getAttribute('loadfactor');	
			
	getURL(url, fn2);
	
	function fn2(obj) {   
		var Newnode = parseXML(obj.content, SVGDoc);
		var gdefid = node.getAttribute('gdefid');
		var model = node.getAttribute('model');
		var width = node.getAttribute('width');
		var height = node.getAttribute('height');
		var format = node.getAttribute('format');
		var start = node.getAttribute('start');
		var end = node.getAttribute('end');
		var loadfactor = node.getAttribute('loadfactor');
		var db = node.getAttribute('db');
		var x = node.getAttribute('x');
		var y = node.getAttribute('y');

		SVGDoc.documentElement.removeChild(node);
		SVGDoc.documentElement.appendChild(Newnode);

		var svgElements = Newnode.getOwnerDocument().documentElement.getElementsByTagName('svg');

		for (j = 0; j<svgElements.getLength(); j++) {
			var svgElem = svgElements.item(j);
			if (svgElem.getAttribute('gdefid') == gdefid) {     	
     			svgElem.setAttributeNS(null, 'model', model);
     			svgElem.setAttributeNS(null, 'width', width);
     			svgElem.setAttributeNS(null, 'height', height);
			 	svgElem.setAttributeNS(null, 'format', format);
			 	svgElem.setAttributeNS(null, 'start', start);
			 	svgElem.setAttributeNS(null, 'end', end);
			 	svgElem.setAttributeNS(null, 'db', db);
			 	svgElem.setAttributeNS(null, 'loadfactor', loadfactor);
			 	svgElem.setAttributeNS(null, 'x', x);
			 	svgElem.setAttributeNS(null, 'y', y);
			 	svgElem.setAttributeNS(null, 'object', 'graph');     	
     		}
			else  {
	  			continue;	  
	    	} 
		}
	} // end f2
} // end updateGraph

function updateAlarmsTable(node,url) {
	url = url + 'deviceid=' + node.getAttribute('deviceid') +
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
