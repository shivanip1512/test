/**
 *  camera.js - Autopans a camera to preset positions.  
 *  			When a preset position button is pressed, autopanning is 
 *				cancelled for a given period of time before resuming.
 *				Originally developed for TVA, Flir camera and Moxa video server
 */
// Array of preset positions
var gPresetPositions = new Array(0);

// Amountof time to spend at each preset
var gAutoPanDwell = 15;

// Amount of time before we go back into autopan mode
var gAutoPanTimeout = 120;

// Variable arguments set the name of each preset
function setPresets(presets) {
	gPresetPositions = presets;
}

// How long in seconds to spend at each preset
function setAutoPanDwell(secs) {
  gAutoPanDwell = secs;
}

// How long in seconds before we go back to autopan mode
function setAutoPanTimeout(secs) {
  gAutoPanTimeout = secs;
}


// var used to cancel panning if necessary
var gPanTimeout = null;
var gCurrentPreset = 0;

// Main loop
// Start autopanning until a preset is clicked
function startAutoPan() {
  gPanTimeout = setTimeout('doAutoPan()', gAutoPanDwell*1000);     
}
function doAutoPan() {

  if(gPresetPositions.length == 0) {
    return;
  }

  if(++gCurrentPreset >= gPresetPositions.length) {
    gCurrentPreset = 0;
  }
//alert(gPresetPositions[gCurrentPreset]);
  panToPreset(gPresetPositions[gCurrentPreset]);

  gPanTimeout = setTimeout('doAutoPan()', gAutoPanDwell*1000);     
}

function cancelAutoPan() {
	clearTimeout(gPanTimeout);
	gPanTimeout = setTimeout('doAutoPan()', gAutoPanTimeout*1000);

}

function manualPanToPreset(preset) {
	cancelAutoPan();
	panToPreset(preset);
}

function panToPreset(preset) {

 url = '/servlet/CallbackServlet?url=' + encodeURIComponent('http://66.172.242.31/cgi-bin/recall.cgi?recall=' + preset);
 loadXMLDoc(url, handleCallback);
 
	function handleCallback() {
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
				//always do this
				freeReq( xmlHttp_msgId );
   	     }
	        else
	        {
	            alert("There was a problem retrieving the XML data:\n" + req.statusText);
	        }
    	}
	} 
}
