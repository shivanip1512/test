yukon.namespace('yukon.rfSupportBundle');

/**
 * Module for the RF support bundle page
 * @module yukon.infrastructurewarnings.detail
 * @requires JQUERY
 * @requires yukon
 */
yukon.rfSupportBundle = (function () {
    
    'use strict';
    
    var
    _initialized = false,
   
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
        	$(function() {
        	    checkUpdate();
   
        	});

    
    return mod;
})();

function checkUpdate(){
    $.getJSON(yukon.url("/support/rfbundleInProgress")).done(function(json) {
        refreshContent();
        if(json.inProgress) {
            setTimeout(checkUpdate,1000);
        } else{
           
            $("#submit :button").removeAttr("busy");
        }
    });
}


function refreshContent(){
    $("#mainDiv").load(yukon.url("/support/getRfBundleProgress"));
}
$(function () { yukon.rfSupportBundle.init(); });