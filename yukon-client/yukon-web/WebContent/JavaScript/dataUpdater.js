/* see the dataUpdateEnabler.tag to see where this is referenced */

function initiateCannonDataUpdate(url, periodSecs) {
    var lastUpdate = 0;
    var processResponseCallback = function(transport) {
        // looks like stuff is working, hide error div
        $('cannonUpdaterErrorDiv').hide();
        var content = transport.responseText;
        var responseStruc = content.evalJSON();
        // find all of the updatable elements
        var updatableElements = $$('span[cannonUpdater]');
        updatableElements.each(function(it) {
            var id = it.readAttribute('cannonUpdater');
            // use the cannonUpdater "id" to look up value in response
            var newData = responseStruc.data[id];
            if (newData && newData != it.innerHTML) {
                // data was sent and is different than current
                it.innerHTML = newData;
                // make it glow
                new Effect.Highlight(it, {'duration': 3.5, 'startcolor': '#FFE900'});
            }
           
        });
        // save latest date
        lastUpdate = responseStruc.toDate;
        // schedule next update
        setTimeout(doUpdate, periodSecs * 1000);
    };
    
    var failureCallback = function(transport) {
        // something bad happened, show user that updates are off
        $('cannonUpdaterErrorDiv').show();
        // schedule another update incase the server comes back, but slow it down a bit
        setTimeout(doUpdate, periodSecs * 1000 * 5);
    };
    var doUpdate = function() {
        // get all elements that have the cannonUpdater attribute on them
        var updatableElements = $$('span[cannonUpdater]');
        // trim down to removed duplicates (there probably won't be any)
        updatableElements = updatableElements.uniq();
        // if none exist on this page, get out
        if (updatableElements.length == 0) {
            return;
        }
        
        // build up JS object to be used for request
        var requestData = $H();
        requestData.fromDate = lastUpdate;
        // create an array of strings, with the value of the cannonUpdater attribute for each element
        // use readAttribute to avoid IE weirdness
        requestData.data = updatableElements.invoke('readAttribute', 'cannonUpdater');
        
        var requestJson = requestData.toJSON();
        
        
        new Ajax.Request(url, {
            method: 'post',
            postBody: requestJson,
            contentType: 'application/json',
            onSuccess: processResponseCallback,
            onFailure: failureCallback,
            onException: failureCallback
        });
        
        requestData = null;
        updatableElements = null;
    };
    setTimeout(doUpdate, periodSecs * 1000);
}

