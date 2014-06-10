/**
 * Handles loading control history event lists
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
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
            $(document).on('click', '.js-show-past-enrollment-history', function (ev) {
                var accountId, 
                    link = $(this);
                
                yukon.ui.elementGlass.show(ev);
                accountId = link.parent().find('input[name=accountId]').val();
                
                $.get(yukon.url('stars/operator/program/controlHistory/pastEnrollment'), 
                        {accountId: accountId},
                        function(data){
                            $('.js-past-enrollment-history').html(data);
                            yukon.ui.elementGlass.hide(ev);
                        }
                );
            });
            _initialized = true;
        }
    };
    
    return mod;
}());

$(function () { yukon.assets.controlHistory.init(); });