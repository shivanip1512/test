function updateEnabled(objectId) {

    var disableLink = $('disableLink_' + objectId);
    var enableLink = $('enableLink_' + objectId);
    var actionSpan = $('actionSpan_' + objectId);
    var actionSpanDisabled = $('actionSpanDisabled_' + objectId);
    
    return function(data) {
	        
        if(data.state == 'true') {
            // enabled
        	try {
	            $(disableLink).show();
	            $(enableLink).hide();
        	} catch (e) {}
            $(actionSpan).show();
            $(actionSpanDisabled).hide();
        } else if(data.state == 'false') {
            // disabled
            try {
	        	$(disableLink).hide();
	            $(enableLink).show();
            } catch (e) {}
            $(actionSpan).show();
            $(actionSpanDisabled).hide();
        } else {
            // load management doesn't know about this object
            $(actionSpan).hide();
            $(actionSpanDisabled).show();
        }
    };
}