/**
 * updateGraph.js
 *
 * The purpose of this script is to handle updating esub svg graphs.
 * The entry point is updateGraphChange in response to a click on a graph.
 */
 
//The graph whose settings are currently being edited
var svgElement;

// The location of the mouse when it clicked on the graph
// Use this to position the graph settings popup
var graphx;
var graphy;
var graphw;
var graphh;

var pendingUpdate = false;

/**
 * Will display a graph settings pop up, pass in the 
 * click event.  
 */
function updateGraphChange(evt) {

	// Get a reference to the graph that was clicked on
    svgElement = findParentGraph(evt.getTarget());
    
    // Store the location and dimensions of the graph
    // so we can position the pop up
    graphx = parseInt(svgElement.getAttribute('x'));
    graphy = parseInt(svgElement.getAttribute('y'));
    graphw = parseInt(svgElement.getAttribute('width'));
    graphh = parseInt(svgElement.getAttribute('height'));
    
    // Build up the graphsetting.jsp url, then
    // we'll hit the server
    var view = svgElement.getAttribute('view');
    var period = svgElement.getAttribute('period');
    var start = svgElement.getAttribute('start'); 
    var events = svgElement.getAttribute('events'); 
    var url = 	"/esub/jsp/graph_settings.jsp" +
    		  	"?view=" + view +
    		  	"&period=" + period +
    		  	"&start=" + start +
    		  	"&events=" + events;
    
    loadXMLDoc(url, handleGraphSettingsReq);
} //end updateGraphChange

// -------------------------------------------
//Callback function for the xml HTTP request
// to return XML
// -------------------------------------------
function handleGraphSettingsReq()
{
    var xmlHTTPreq = getReq(xmlHttp_msgId);

    // only if req shows "complete"
    if( xmlHTTPreq != null
    	&& getReq(xmlHttp_msgId).req != null
    	&& getReq(xmlHttp_msgId).req.readyState == 4 )
    {    
    	var req = getReq(xmlHttp_msgId).req;

        // only if "OK"
        if( req.status == 200) 
        {
			//alert(xmlHTTPreq.responseText);

			graphSettingsPopup = new PopupWindow("graphsettings");
			graphSettingsPopup.offsetX= graphx + (graphw/2) - 150;
			graphSettingsPopup.offsetY= graphy + (graphh/2) - 88;

			graphSettingsPopup.autoHide();
			graphSettingsPopup.populate(req.responseText);
    		// A flag to tell the popup checker that the popup is showing
    		// TODO Should be moved into the popup code?
			graphSettingsPopup.isShowing = true;
			graphSettingsPopup.showPopup("popupanchor");
	
			// Store the popup in the window.parent so the dynamically
			// popped up jsp/html can communicate with us
			window.parent.graphSettingsPopup = graphSettingsPopup;
	
			// Fire up the popup checker
			pendingUpdate = true;
			window.setTimeout('checkPopup()', 250);
				
			//always do this
			freeReq( xmlHttp_msgId );
        }
        else
        {
            alert("There was a problem retrieving the XML data:\n" + req.statusText);
        }
    }
}

/**
 * Checks to see if the popup is still showing, when it isn't
 * it is time to update the graph with whatever the user selected.
 */
function checkPopup() {
	// If the pendingUpdate flag is false stop checking
	// This keeps multiple requests from building up
	// not fool proof but good enough
	if(pendingUpdate == false)
	{
		return;
	}
	
	if(graphSettingsPopup.isShowing != null &&
	   graphSettingsPopup.isShowing != true) {
	    // Time to update the graph, don't let other callbacks
	    // actually do an update.
		pendingUpdate = false;
		svgElement.setAttributeNS(null,'view',window.parent.graphSettingsPopup.graphType);
		svgElement.setAttributeNS(null,'period',window.parent.graphSettingsPopup.period);
		svgElement.setAttributeNS(null,'start',window.parent.graphSettingsPopup.startDate);	
		svgElement.setAttributeNS(null,'events',window.parent.graphSettingsPopup.events);	
		updateAllGraphs();		
	}
	else {
	    // Not time yet, check again later
		window.setTimeout('checkPopup()', 250);
	}
}

/**
 * This is called from graphsettings.jsp
 * Note that this runs in a different context which is why
 * we are communicating through window.parent
 */
function updateGraphSettings(startDate, period, graphType, events ) {
    window.parent.graphSettingsPopup.startDate = startDate;
    window.parent.graphSettingsPopup.period = period;
    window.parent.graphSettingsPopup.graphType = graphType;
    window.parent.graphSettingsPopup.events = events;
	window.parent.graphSettingsPopup.hidePopup();
	window.parent.graphSettingsPopup.isShowing = false;
}

/* Recursively attempt to find the root graph element */
function findParentGraph(elem) {
	if(elem.getAttribute('object') == 'graph') {
		return elem;
	}
	else {
		return findParentGraph(elem.getParentNode());
	}
} //end findParentGraph

