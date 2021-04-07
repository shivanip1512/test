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
            	
            	$(document).on('click', '.js-execute-command', function() {
            		var date = $("input[name=date]", "#createRfBundleForm").val();
            		var customerName = $("input[name=customerName]", "#createRfBundleForm").val();
            	
            		$.ajax({
            		url: yukon.url('/support/createRfBundle'),
            		type: "POST",
            		dataType: "json",
            		data: {customerName: customerName , date: date}
            		}).done( function(data){
            		yukon.ui.alertSuccess(data);
            		}).fail(function(data){
            		yukon.ui.alertError(data);
            		});
            		});
                
               /* $(document).on('click', '.js-execute-command', function() {
                	alert("Hi");
                    var controlWindowRow = $(this).closest('tr'),
                        useControlWindow = controlWindowRow.find('.switch-btn-checkbox').prop('checked');
                    $('#dailyStartTimeInMinutes').prop('disabled', !useControlWindow);
                    $('#dailyStopTimeInMinutes').prop('disabled', !useControlWindow);
                });
                */
                
                _initialized = true;
            }
        };

        return mod;
    })();

$(function() {
	yukon.rfSupportBundle.init();
});
