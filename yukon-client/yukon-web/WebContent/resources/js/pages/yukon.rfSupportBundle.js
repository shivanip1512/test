yukon.namespace('yukon.rfSupportBundle');

/**
 * Module for the RF support bundle page
 * 
 * @requires JQUERY
 * @requires yukon
 */
yukon.rfSupportBundle = (function() {
    
    'use strict';
    
    var _initialized = false,

    mod = {
        init : function() {
            
            $(document).on('click', '.js-execute-command-rf', function() {
                var date = $("input[name=date]").val();
                var customerName = $("input[name=rfCustomerName]").val();
                
                $.ajax({
                    url : yukon.url('/support/createRfBundle'),
                    type : "POST",
                    dataType : "json",
                    data : {
                        customerName : customerName,
                        date : date
                    }
                }).done(function(data) {
                    
                    $('.js-rf-customer-name-errors').hide();
                    $('.js-rf-customer-name').removeClass('error');
                    
                    if (data.isSuccess) {
                        yukon.ui.alertSuccess(data.message);
                        checkUpdate();
                    } else if (data.isFieldError) {
                        var btn = $('.js-execute-command-rf');
                        yukon.ui.alertError(data.message);
                        $('.js-rf-customer-name').show().addClass('error');
                        $('.js-rf-customer-name-errors').text(data.fieldError);
                        yukon.ui.highlightErrorTabs();
                        yukon.ui.unbusy(btn);
                    } 
                    
                })
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

function checkUpdate() {
    var btn = $('.js-execute-command-rf');
    $.getJSON(yukon.url("/support/rfBundleInProgress")).done(function(json) {
        //TODO: Handle timeout and error case after backend changes.
        if (json.inProgress) {
            setTimeout(checkUpdate, 60000);
        } else {
            yukon.ui.unbusy(btn);
        }
    });
}

$(function() {
    yukon.rfSupportBundle.init();
});
