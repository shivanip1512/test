function updateEnabled(objectId) {

    var disableLink = $('disableLink_' + objectId);
    var enableLink = $('enableLink_' + objectId);
    var actionSpan = $('actionSpan_' + objectId);
    
    return function(data) {
	        
        if(data.state == 'true') {
            // enabled
            $(disableLink).show();
            $(enableLink).hide();
            $(actionSpan).show();
        } else if(data.state == 'false') {
            // disabled
            $(disableLink).hide();
            $(enableLink).show();
            $(actionSpan).show();
        } else {
            // load management doesn't know about this object
            $(actionSpan).hide();
        }
    };
}