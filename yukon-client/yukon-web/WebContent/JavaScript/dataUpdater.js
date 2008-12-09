/* see the dataUpdateEnabler.tag to see where this is referenced */

var disableHighlight = false;
var cannonDataUpdateRegistrations = $A();

function initiateCannonDataUpdate(url, delayMs) {
    var lastUpdate = 0;
    var failureCount = 0;
    var processResponseCallback = function(transport) {
        var someValueHasUpdated = false;
        
        // looks like stuff is working, hide error div
        $('cannonUpdaterErrorDiv').hide();
        failureCount = 0;
        
        var content = transport.responseText;
        var responseStruc = content.evalJSON(); // fails when server is shutdown if this is an onSuccess callback!
        // find all of the updatable elements
        var updatableElements = $$('span[cannonUpdater]');
        updatableElements.each(function(it) {
            var id = it.readAttribute('cannonUpdater');
            // use the cannonUpdater "id" to look up value in response
            var newData = responseStruc.data[id];
            if (newData && newData != it.innerHTML) {
                // data was sent and is different than current
                it.innerHTML = newData;
                someValueHasUpdated = true;
                // make it glow
                if (!disableHighlight) {
                    new Effect.Highlight(it, {'duration': 3.5, 'startcolor': '#FFE900'});
                }    
            }
           
        });
        
        // update the colors
        var updatableColorElements = $$('span[cannonColorUpdater]');
        updatableColorElements.each(function(it) {
            var id = it.readAttribute('cannonColorUpdater');
            // use the cannonUpdater "id" to look up value in response
            var newData = responseStruc.data[id];
            if (newData && newData != it.style.color) {
                // data was sent and is different than current
                it.style.color = newData;
                it.childElements().each(function(child) {
                    child.style.color = newData;
                });
            }
        });
        
        // find all of the callbacks
        cannonDataUpdateRegistrations.each(function(it) {
            
            var idMap = $H(it['identifierMap']);
            var allIdentifierValues = $H();
            
            if( idMap.keys().length == 0 && someValueHasUpdated) {
            	//callback for any value change when no identifiers provided
            	it.callback();
            	return;
            }
           
            idMap.keys().each(function(idFieldName) {
            	var newData = responseStruc.data[idMap[idFieldName]];
            	if(newData) {
                    allIdentifierValues[idFieldName] = responseStruc.data[idMap[idFieldName]];
            	}
            });
            
            if(allIdentifierValues.keys().length >0) {
                it.callback(allIdentifierValues);
            } 
        });
        // save latest date
        lastUpdate = responseStruc.toDate;
        // schedule next update
        setTimeout(doUpdate, delayMs);
    };
    
    var failureCallback = function() {
        // something bad happened, show user that updates are off
        failureCount += 1;
        $('cannonUpdaterErrorDivCount').innerHTML = failureCount;
        if (failureCount > 1) {
            $('cannonUpdaterErrorDiv').show();
        }
        // schedule another update incase the server comes back, but slow it down a bit
        setTimeout(doUpdate, delayMs * 5);
    };
    var doUpdate = function() {
        // if none exist on this page, get out
        // build up JS object to be used for request
        var requestData = $H();
        requestData.fromDate = lastUpdate;

        // get all elements that have the cannonUpdater attribute on them
        var updatableElements = $$('span[cannonUpdater]');
        // create an array of strings, with the value of the cannonUpdater attribute for each element
        // use readAttribute to avoid IE weirdness
        requestData.data = updatableElements.invoke('readAttribute', 'cannonUpdater');
        
        var updatableColorElements = $$('span[cannonColorUpdater]');
        requestData.data = requestData.data.concat(updatableColorElements.invoke('readAttribute', 'cannonColorUpdater'));
        
        // add elements from JS registrations
        cannonDataUpdateRegistrations.each(function(it) {
        
        	var idMap = it['identifierMap'];
        	requestData.data = requestData.data.concat(idMap.values());
        });
        
        // trim down to removed duplicates (there probably won't be any)
        requestData.data = requestData.data.uniq();
        
        if (requestData.data.length == 0) {
            // schedule next update
            setTimeout(this, delayMs);
            return;
        }
        
        var requestJson = requestData.toJSON();
        
        
        new Ajax.Request(url, {
            method: 'post',
            postBody: requestJson,
            contentType: 'application/json',
            on200: processResponseCallback, // this odd combination seems to be the only
            onSuccess: failureCallback,     // way to detect that the server is shutdown
            onFailure: failureCallback,     // note: the onSuccess will not be called when 
            onException: failureCallback    // the on200 is called
        });
        
        requestData = null;
        updatableElements = null;
    };
    setTimeout(doUpdate, delayMs);
}

function cannonDataUpdateRegistration(callback, identifierMap) {
  // callback will include the formatted string as its one argument
  var theData = $H();
  theData['identifierMap'] = $H(identifierMap);
  theData['callback'] = callback;
  
  cannonDataUpdateRegistrations.push(theData);    
}

