/**
 * Handles loading control history event lists
 * 
 * @requires JQUERY
 * @requires JQUERYUI
 */

yukon.namespace('yukon.assets.controlHistory');

yukon.assets.controlHistory = (function () {
    var _initialized = false, 
        mod;
    
    mod = {
        init: function () {
            if (_initialized) {
                return;
            }
            
            yukon.ui.elementGlass.show($('.js-current-enrollment-history'));
            yukon.ui.elementGlass.show($('.js-past-enrollment-history'));
            accountId = $('[data-account-id]').data('accountId');
            
            $('.js-current-enrollment-history').load(yukon.url('/stars/operator/program/controlHistory/currentEnrollment'),
                {accountId: accountId},
                function () {
                    yukon.ui.elementGlass.hide($('.js-current-enrollment-history'));
                });
            $('.js-past-enrollment-history').load(yukon.url('/stars/operator/program/controlHistory/pastEnrollment'),
                {accountId: accountId},
                function () {
                    yukon.ui.elementGlass.hide($('.js-past-enrollment-history'));
                });
            
            _initialized = true;
        }
    };
    
    return mod;
}());

$(function () { yukon.assets.controlHistory.init(); });