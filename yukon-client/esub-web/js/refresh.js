function refresh(evt)

{
	
	SVGDoc = evt.getTarget().getOwnerDocument();
	dynText = SVGDoc.getElementsByTagName('text');
	dynImages = SVGDoc.getElementsByTagName('image');
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	setInterval('doRefresh()', 60000);  //set to every minute 
	setInterval('changeSVGGraph()', 300000); //every five minutes
	root = evt.getTarget();
	
}



function changeSVGGraph() {
 
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


}

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



}


}




function doRefresh() {
	
	for (var i=0; i<dynText.getLength(); i++) 
	{
		updateNode(dynText.item(i));
	}
	for(var j=0; j<dynImages.getLength(); j++)
	{
		updateImage(dynImages.item(j));
	}
}


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
}


function updateImage(node) {
	var imageID = node.getAttribute('id');
    if( !isNaN(imageID) ) {        
        alert(imageID);
        url = '/servlet/StateImageServlet' + '?' + 'id=' + node.getAttribute('id');
        getURL(url, fn);
    }
    
	function fn(obj) {
		//confirm(obj.content);
	    if (obj.content) {  
	    node.setAttribute('xlink\:href', obj.content);
		}
		
		
	
	}


}