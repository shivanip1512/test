yukon.namespace('yukon.rfSupportBundle');

/**
 * Module for the Infrastructure Warnings Detail page
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

        	    $("#ftpUploadBtn").click(function() {
        	        var chosenBundle = $("input[name=fileName]", "#uploadForm").val();
        	        openFtpPopup(chosenBundle);
        	    });
        	});

    
    return mod;
})();

function checkUpdate(){
    $.getJSON(yukon.url("/support/rfbundleInProgress")).done(function(json) {
        refreshContent();
        if(json.inProgress) {
            setTimeout(checkUpdate,1000);
        } else{
            $("input[name=fileName]", "#uploadForm").val(json.fileName);
            $("#submit :button").removeAttr("busy");
        }
    });
}


function refreshContent(){
    $("#mainDiv").load(yukon.url("/support/getRfBundleProgress"));
}
$(function () { yukon.rfSupportBundle.init(); });