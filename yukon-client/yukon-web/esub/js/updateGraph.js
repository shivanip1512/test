var timePeriod = 0;
var startDay;
var view;
var w;
var timer;
var parentWin;
var svgElement;
var svgDoc;
var dataRequest;

function updateGraphChange(evt) {
    svgElement = findParentGraph(evt.getTarget());
    svgDoc = svgElement.getOwnerDocument();
    parentWin = window.parent;
    
    var view = svgElement.getAttribute('view');
    var period = svgElement.getAttribute('period');
    var start = svgElement.getAttribute('start'); 
    var url = 	"/esub/jsp/graph_settings.jsp" +
    		  	"?view=" + view +
    		  	"&period=" + period +
    		  	"&start=" + start;
    
    var width = 300;
	var height = 240;
	var winl = (screen.width - width) / 2; 
	var wint = (screen.height - height) / 2; 
			  	
    if (evt) {
        w = parentWin.open(url, 'GraphSettings', 'width='+width+',height='+height+',top='+wint+',left='+winl+',resizable=yes');
        window.setTimeout("chckSubmit()", 250);
    }

} //end updateGraphChange

function chckSubmit() {
    if (w.closed) { 
        if(parentWin.dataRequest != null) {	
                               
	        var data = parentWin.dataRequest.split(":");

            var view = data[0];
            var period = data[1];
            var start = data[2];
		
            svgElement.setAttributeNS(null,'view',view);
            svgElement.setAttributeNS(null,'period',period);
			svgElement.setAttributeNS(null,'start',start);
			
            updateAllGraphs();
        }
    } 
    else {
    	//reschedule this function to be called again
        window.setTimeout("chckSubmit()", 250);
    }
} // end chckSubmit

function updateGraph2(node, url, v, p) {
    url = url + "gdefid=" + node.getAttribute('gdefid') +
		  "&view=" + v +
          "&period=" + p +
          "&width=" + node.getAttribute('width') +
          "&height=" + node.getAttribute('height') +
          "&format=" + node.getAttribute('format') +
          "&start=" + node.getAttribute('start') + 
    	  "&period=" + node.getAttribute('period') +
          "&db=" + node.getAttribute('db') +
          "&tab=graph" +
          "&loadfactor=" + node.getAttribute('loadfactor') +
          getURL(url, fn2);
    
    
    function fn2(obj) {
        var Newnode = parseXML(obj.content, SVGDoc);
        var gdefid = node.getAttribute('gdefid');
 		var view = v;
        var period = p;
        var width = node.getAttribute('width');
        var height = node.getAttribute('height');
        var format = node.getAttribute('format');
        var start = node.getAttribute('start');
        var period = node.getAttribute('period');
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
				svgElem.setAttributeNS(null, 'view', view);
                svgElem.setAttributeNS(null, 'period', period);
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
                svgElem.setAttributeNS(null, 'onclick', 'updateGraph2Change(evt)');                
            }
            else {
                continue;                
            } 
        }
    } //end fn2
} // end updateGraph2

function update() {
    var view = document.MForm.view.value;
    var period = document.MForm.period.value;
	var start = document.MForm.start.value;

    opener.dataRequest =  view + ":" + period + ":" + start;
    self.close();

} //end update

/* Recursively attempt to find the root graph element */
function findParentGraph(elem) {
	if(elem.getAttribute('object') == 'graph') {
		return elem;
	}
	else {
		return findParentGraph(elem.getParentNode());
	}
} //end findParentGraph

