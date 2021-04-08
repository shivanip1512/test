yukon.namespace('yukon.rfSupportBundle');

/**
 * Module for the RF support bundle page

 * @requires JQUERY
 * @requires yukon
 */
yukon.rfSupportBundle = (function () {
    
    'use strict';
    
    var
    _initialized = false,
   
    mod = {
            init : function() {
            	
            	$(document).on('click', '.js-execute-command-rf', function() {
            		var date = $("input[name=date]", "#createRfBundleForm").val();
            		var customerName = $("input[name=customerName]", "#createRfBundleForm").val();
            	
            		$.ajax({
            		url: yukon.url('/support/createRfBundle'),
            		type: "POST",
            		dataType: "json",
            		data: {customerName: customerName , date: date}
            		}).done( function(data){
            		yukon.ui.alertSuccess(data);
            		checkUpdate(); 
            		}).fail(function(data){
            		yukon.ui.alertError(data);
            		});
            		});
                
                
                _initialized = true;
            }
        };

        return mod;
    })();
function checkUpdate(){
    $.getJSON(yukon.url("/support/rfbundleInProgress")).done(function(json) {
        refreshContent();
        if(json.inProgress) {
            setTimeout(checkUpdate,1000);
        } else{
           
            $("#createRfBundleForm :button").removeAttr("disabled");
        }
    });
}

function refreshContent(){
    $("#section-container").load(yukon.url("/support/getBundleProgress"));
}

$(function() {
	yukon.rfSupportBundle.init();
});
