yukon.namespace('yukon.support.rfSupportBundle');

/**
 * Module for the RF support bundle section on the Support page
 * 
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.rfSupportBundle = (function() {
    
    'use strict';
    
    var _initialized = false,
    
    _checkUpdate = function() {
        var btn = $('.js-execute-command-rf'), classValue;
        $.getJSON(yukon.url("/support/rfBundleInProgress")).done(function(json) {
            if (!json.isCompleted) {
                classValue = "success";
                setTimeout(_checkUpdate, 60000);
            } else {
                if (json.status == "FAILED" || json.status == "TIMEOUT" || json.status == null) {
                    classValue = "error";
                } else if (json.status == "COMPLETED") {
                    classValue = "success";
                }
                yukon.ui.unbusy(btn);
                $('#rfCustomerName').val("");
                $("#rfCustomerName").prop("disabled", false);
            }
            $('#rf-js-message').addMessage({
                message : json.message,
                messageClass : classValue
            });
        });
    },

    mod = {
        init : function() {
            if (_initialized)
                return;
            
            $.getJSON(yukon.url("/support/rfBundleInProgress")).done(function(json) {
                if (!json.isCompleted) {
                    yukon.ui.busy($('.js-execute-command-rf'));
                    $("#rfCustomerName").prop("disabled", true);
                    _checkUpdate();
                }
            });
            $(document).on('click', '.js-execute-command-rf', function() {
                $('#rfSupportBundle-form').ajaxSubmit({
                    success : function(data, status, xhr, $form) {
                        $('#rf-support-bundle-section').html(data);
                        yukon.ui.initContent('#rf-support-bundle-section');
                        yukon.ui.initDateTimePickers();
                        yukon.ui.busy($('.js-execute-command-rf'));
                        $("#rfCustomerName").prop("disabled", true);
                        _checkUpdate();
                    },
                    error : function(xhr, status, error, $form) {
                        $('#rf-support-bundle-section').html(xhr.responseText);
                        yukon.ui.initContent('#rf-support-bundle-section');
                        yukon.ui.initDateTimePickers();
                        yukon.ui.highlightErrorTabs();
                    }
                });
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function() {
    yukon.support.rfSupportBundle.init();
});
