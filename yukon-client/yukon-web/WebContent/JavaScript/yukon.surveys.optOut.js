yukon.namespace('yukon.surveys.optout');

/**
 * Module for survey opt out page.
 * @module yukon.surveys.optout
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires yukon
 */
yukon.surveys.optout = (function () {

    var mod = {
        init : function () {

            $('.js-add-survey').click(function () {
                var popup = $('#survey-popup');
            
                popup.load('edit', function (ev) {
                    yukon.ui.initDateTimePickers();
                    popup.dialog({
                        buttons: yukon.ui.buttons({ event: 'yukon_surveys_outout_edit'}),
                        title: popup.data('addTitle'),
                        width: 500
                    });
                });
            });

            $('.js-more-programs').click(function () {
                $('#more-programs-popup').load('programList', {
                    optOutSurveyId: $(this).data('surveyId') 
                }, function() {
                    $('#more-programs-popup').dialog({ width: 500 });
                });
            });
            
            /** Edit clicked in survey row menu, show edit popup. */
            $('.js-edit-survey').click(function (ev) {
                var popup = $('#survey-popup'),
                    surveyId = $(this).parent().data('surveyId');
                
                popup.load('edit', { optOutSurveyId: surveyId }, function (ev) {
                    yukon.ui.initDateTimePickers();
                    popup.dialog({
                        buttons: yukon.ui.buttons({ event: 'yukon_surveys_outout_edit'}),
                        title: popup.data('editTitle'),
                        width: 500
                    });
                });
            });
            
            /** Save button clicked on edit survey popup. */
            $(document).on('yukon_surveys_outout_edit', function (ev) {
                var popup = $('#survey-popup');
                popup.find('form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        $('#survey-popup').html(xhr.responseText);
                        yukon.ui.initDateTimePickers();
                    }
                });
            });
            
            /** Delete clicked in survey row menu, show delete popup. */
            $('.js-delete-survey').click(function (ev) {
                var popup = $('#survey-popup'),
                    surveyId = $(this).parent().data('surveyId');
                
                popup.load('confirmDelete', { optOutSurveyId: surveyId }, function (ev) {
                    yukon.ui.initDateTimePickers();
                    popup.dialog({
                        buttons: yukon.ui.buttons({ event: 'yukon_surveys_outout_delete'}),
                        title: popup.data('deleteTitle'),
                        width: 500
                    });
                });
            });
            
            /** Delete button clicked on delete survey popup. */
            $(document).on('yukon_surveys_outout_delete', function (ev) {
                var popup = $('#survey-popup');
                popup.find('form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href = window.location.href;
                    }
                });
            });
            
            $(document).on('change', '#specify-stopdate-cb', function (ev) {
                var cb = $(this),
                    checked = cb.is(':checked');
                
                $('#survey-stopdate').prop('disabled', !checked);
            });
        }
    };
    
    return mod;
}());

$(function () { yukon.surveys.optout.init(); });