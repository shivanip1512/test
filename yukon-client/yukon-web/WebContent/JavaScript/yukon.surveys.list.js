/**
 * Singleton that manages the survey list page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.surveys');
yukon.namespace('yukon.surveys.list');

yukon.surveys.list = (function () {
    mod = {
        init: function () {
            jQuery('#addSurveyBtn').click(function () {
                jQuery('#ajaxDialog').load('editDetails', function () {
                    jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
                });
            });

            jQuery(document).on('yukonDetailsUpdated', function (event, newUrl) {
                jQuery('#ajaxDialog').dialog('close');
                window.location = newUrl;
            });
        }
    };
    return mod;
}());

jQuery(function () {
    yukon.surveys.list.init();
});