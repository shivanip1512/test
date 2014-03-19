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
            $('#addSurveyBtn').click(function () {
                $('#ajaxDialog').load('editDetails', function () {
                    $('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
                });
            });

            $(document).on('yukonDetailsUpdated', function (event, newUrl) {
                $('#ajaxDialog').dialog('close');
                window.location = newUrl;
            });
        }
    };
    return mod;
}());

$(function () {
    yukon.surveys.list.init();
});