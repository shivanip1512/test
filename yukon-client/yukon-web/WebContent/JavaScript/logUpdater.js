var updatePaused = false;
function initiateCannonLogUpdate(url, periodSecs) {
	var fileLength = 0;
    var processResponseCallback = function(transport) {
    
        // retrieves the callback information and sets them
    	// to local instance variables
        var content = transport.responseText;
        var responseStruc = content.evalJSON();
        var logContentsJSON = responseStruc.logContent;
        var logLastMod = responseStruc.fileDateMod;
        var numLines = responseStruc.numLines;
		fileLength = responseStruc.fileLength;
		
		if((logContentsJSON != null) && (logContentsJSON != "")){
			/* This part of the function updates the log last modified 
			 * field shown on the screen
			 */
			 
			 if((logLastMod != null) && 
			   (logLastMod != "") &&
			   (logLastMod != $('lastMod').innerHTML)){
				$('lastMod').innerHTML = logLastMod;
				var tempFileLength = (fileLength/1024).toString();
				$('fileLength').innerHTML = (tempFileLength.split(".")[0]);
				new Effect.Highlight('lastMod', {startcolor:'#ffff88', endcolor:'#ffffff'});
				new Effect.Highlight('fileLength', {startcolor:'#ffff88', endcolor:'#ffffff'});
			}		
		
			/* This part of the function updates the log contents shown
			 * on the screen
			 */
			logContentsJSON.each(function(newLogLine) {
				appendLogEntry(newLogLine);
			});
        }
        setTimeout(doUpdate, periodSecs * 1000);   
	};
    
    var failureCallback = function(transport) {
    	// stick error in the log
    	appendLogEntry("Unknown error while downloading log data...");
        // schedule another update incase the server comes back, but slow it down a bit
        setTimeout(doUpdate, periodSecs * 1000 * 5);
    };
    
    var appendLogEntry = function(text) {
    	var logLineDiv = document.createElement('div');
		var textNode = document.createTextNode(text + "\n");
		logLineDiv.appendChild(textNode);
		$$('#logOutput div').shift().remove();
		$('logOutput').appendChild(logLineDiv);
    };
    
    var doUpdate = function() {
    	if(updatePaused){
    		setTimeout(doUpdate, periodSecs * 1000);
    	}else{
    
	        // if none exist on this page, get out
	        // build up JS object to be used for request
	        var requestData = $H({
	            'fileLength': fileLength,
	            'numLines': $('numLines').value,
	            'file': $('file').value
	        });
         
	        new Ajax.Request(url, {
   	         	method: 'post',
            	postBody: Object.toJSON(requestData),
            	contentType: 'application/json',
            	on200: processResponseCallback,
            	onSuccess: failureCallback,
            	onFailure: failureCallback,
            	onException: failureCallback
        	});
        
        	requestData = null;
        	updatableElements = null;
        }
    };
    setTimeout(doUpdate, periodSecs * 1000);
}

function pauseUpdate(){
	updatePaused = true;
	$('pauseButton').innerHTML = "Start";
}

function startUpdate(){
	updatePaused = false;
	$('pauseButton').innerHTML = "Pause";
}

function startOrPauseUpdate(){
	if(updatePaused){
		startUpdate();
	}else{
		pauseUpdate();
	}
}

function cannonLogUpdateRegistration(identifier, callback) {
  // callback will include the formatted string as its one argument
  var theData = [];
  theData.identifier = identifier;
  theData.callback = callback;
  cannonLogUpdateRegistrations.push(theData);
}

