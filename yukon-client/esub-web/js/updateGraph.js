var timePeriod = 0;
var startDay;
var view;
var w;
var timer;
var parentWin;
var svgElement;
var svgDoc;

function updateGraphChange(evt) {
	
	svgElement = evt.getTarget();
	svgDoc = svgElement.getOwnerDocument();
	parentWin = window.parent;
	if (evt) {
	w = parentWin.open("../js/GraphSettings.jsp", "GraphSettings", "width=300,height=375");
	window.setTimeout("chckSubmit()", 3000);
	}

}

function chckSubmit() {

 if (w.closed) {
	
	//put code to update the graph
	var data = parentWin.document.getElementById('dataRequest').value;
	data = data.split(":");
	if (data[2] == "true") {
	var view = data[0];
	var period = data[1];
	var url = '/servlet/GraphGenerator?';
	updateGraph(svgElement, url, view, period);
	
	}
	


}
else 
	window.setTimeout("chckSubmit()", 3000);

}




function updateGraph(node, url, v, p) {

	
			url = url + "gdefid=" + node.getAttribute('gdefid') +
		      "&model=" + v +
		      "&period=" + p +
			  "&width=" + node.getAttribute('width') +
			  "&height=" + node.getAttribute('height') +
			  "&format=" + node.getAttribute('format') +
			  "&start=" + node.getAttribute('start') + 
			  "&end=" + node.getAttribute('end') +
			  "&db=" + node.getAttribute('db') +
			  "&tab=graph" +
			  "&loadfactor=" + node.getAttribute('loadfactor') +
			   getURL(url, fn2);
			
			  
function fn2(obj) {


var Newnode = parseXML(obj.content, SVGDoc);
var gdefid = node.getAttribute('gdefid');
var model = v;
var period = p;
var width = node.getAttribute('width');
var height = node.getAttribute('height');
var format = node.getAttribute('format');
var start = node.getAttribute('start');
var end = node.getAttribute('end');
var loadfactor = node.getAttribute('loadfactor');
var db = node.getAttribute('db');
var x = node.getAttribute('x');
var y = node.getAttribute('y');



svgDoc.documentElement.removeChild(node);
svgDoc.documentElement.appendChild(Newnode);

var svgElements = Newnode.getOwnerDocument().documentElement.getElementsByTagName('svg');

for (j = 0; j<svgElements.getLength(); j++) {

     var svgElem = svgElements.item(j);
     if (svgElem.getAttribute('gdefid') == gdefid) {
     	
     	svgElem.setAttributeNS(null, 'model', model);
     	svgElem.setAttributeNS(null, 'period', period);
     	svgElem.setAttributeNS(null, 'width', width);
     	svgElem.setAttributeNS(null, 'height', height);
     	svgElem.setAttributeNS(null, 'format', format);
     	svgElem.setAttributeNS(null, 'start', start);
     	//svgElem.setAttributeNS(null, 'end', end);
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



}


}




















function update() {

var view;
var period;
var views = document.MForm.view;
var periods = document.MForm.period;

for (var i=0; i<views.length; i++) {
	if (views[i].checked) {
	view = views[i].value;
	
	break;
	}
}

for (var j=0; j<periods.length; j++) {

if (periods[j].checked) {
	period = periods[j].value;
	
	break;
}

}

opener.document.getElementById('dataRequest').value = view + ":" + period + ":" + "true";
self.close();
}



function fn(obj) {
	document.getElementById('dataRequest').innerHTML = "";
}

