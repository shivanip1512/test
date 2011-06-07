/* see the dataUpdateEnabler.tag to see where this is referenced */

var disableHighlight = false;
var cannonDataUpdateRegistrations = $A();
var _updaterTimeout = null;

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
            if (newData != null && newData != it.innerHTML) {
                // data was sent and is different than current
                it.innerHTML = newData.escapeHTML();
                someValueHasUpdated = true;
                // make it glow
                if (!disableHighlight) {
                    flashYellow(it, 3.5);
                }    
            }
           
        });
        
        // update the classes
        var updatableClassElements = $$('span[cannonClassUpdater]');
        updatableClassElements.each(function(it) {
            var id = it.readAttribute('cannonClassUpdater');
            // use the cannonUpdater "id" to look up value in response
            var newData = responseStruc.data[id];
            if (newData && it.className != newData) {
                // data was sent and is different than current
            	it.className = newData;
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
        // cannonDataUpdateRegistrations is a $A {prototype array} of $H {prototype hash} objects
        cannonDataUpdateRegistrations.each(function(it) {
            
            var idMap = $H(it.get('identifierMap'));
            var allIdentifierValues = $H();
            
            if( idMap.keys().length == 0 && someValueHasUpdated) {
            	//callback for any value change when no identifiers provided
            	it.get('callback')();
            	return;
            }
           
            idMap.keys().each(function(idFieldName) {
            	var newData = responseStruc.data[idMap.get(idFieldName)];
            	if(newData) {
                    allIdentifierValues.set(idFieldName, responseStruc.data[idMap.get(idFieldName)]);
            	}
            });
            
            if(allIdentifierValues.keys().length >0) {
                it.get('callback')(allIdentifierValues);
            } 
        });
        // save latest date
        lastUpdate = responseStruc.toDate;
        // schedule next update
        if(_updaterTimeout) {
            clearTimeout(_updaterTimeout);
        }
        _updaterTimeout = setTimeout(doUpdate, delayMs);
    };
    
    var failureCallback = function() {
        // something bad happened, show user that updates are off
        failureCount += 1;
        $('cannonUpdaterErrorDivCount').innerHTML = failureCount;
        if (failureCount > 1) {
            $('cannonUpdaterErrorDiv').show();
        }
        // schedule another update incase the server comes back, but slow it down a bit
        if(_updaterTimeout) {
            clearTimeout(_updaterTimeout);
        }
        _updaterTimeout = setTimeout(doUpdate, delayMs * 5);
    };
    var doUpdate = function() {
        // if none exist on this page, get out
        // build up JS object to be used for request
        var requestData = $H({
            'fromDate': lastUpdate
        });

        // get all elements that have the cannonUpdater attribute on them
        var updatableElements = $$('span[cannonUpdater]');
        // create an array of strings, with the value of the cannonUpdater attribute for each element
        // use readAttribute to avoid IE weirdness
        requestData.set('data', updatableElements.invoke('readAttribute', 'cannonUpdater'));
        
        var updatableClassElements = $$('span[cannonClassUpdater]');
        requestData.set('data', requestData.get('data').concat(updatableClassElements.invoke('readAttribute', 'cannonClassUpdater')));
        
        var updatableColorElements = $$('span[cannonColorUpdater]');
        requestData.set('data', requestData.get('data').concat(updatableColorElements.invoke('readAttribute', 'cannonColorUpdater')));
        
        // add elements from JS registrations
        cannonDataUpdateRegistrations.each(function(it) {
        
        	var idMap = it.get('identifierMap');
        	requestData.set('data', requestData.get('data').concat(idMap.values()));
        });
        
        if (requestData.get('data').length == 0) {
            // schedule next update
            if(_updaterTimeout) {
                clearTimeout(_updaterTimeout);
            }
            _updaterTimeout = setTimeout(doUpdate, delayMs);
            return;
        }
        
        new Ajax.Request(url, {
            method: 'post',
            postBody: Object.toJSON(requestData),
            contentType: 'application/json',
            on200: processResponseCallback, // this odd combination seems to be the only
            onSuccess: failureCallback,     // way to detect that the server is shutdown
            onFailure: failureCallback,     // note: the onSuccess will not be called when 
            onException: failureCallback    // the on200 is called
        });
        
        requestData = null;
        updatableElements = null;
    };
    if(_updaterTimeout) {
        clearTimeout(_updaterTimeout);
    }
    _updaterTimeout = setTimeout(doUpdate, delayMs);
}

/**
 * @param	callback		{function}
 * @param	identifierMap	{Object} cloned as a new Prototype Hash object
 */
function cannonDataUpdateRegistration(callback, identifierMap) {
	// callback will include the formatted string as its one argument
	cannonDataUpdateRegistrations.push($H({
        'identifierMap': $H(identifierMap),
        'callback': callback
    }));
}

/**
 * 
 * @param callback		{function}
 * @param identifier	{DOM id}
 */
function cannonDataUpdateEventRegistration(callback, identifier) {
	var didIt = false;
	var callbackWrapper = function(data) {
	    // data is assumed to be a $H
		if (!didIt && data.get('boolean') == 'true') {
			didIt = true;
		    callback();
		}
	};
    cannonDataUpdateRegistration(callbackWrapper, {'boolean': identifier});  
}

