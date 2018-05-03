yukon.namespace('yukon.collection.actions');

/**
 * Module for the Collection Actions page
 * @module yukon.collection.actions
 * @requires JQUERY
 * @requires yukon
 */
yukon.collection.actions = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
            
        submitAction : function (btn) {
            var form = btn.closest('form');
            form.ajaxSubmit({
                success: function(data, status, xhr, $form) {
                    yukon.ui.unbusy(btn);
                    $('#progressReportDiv').html(data);
                    $('#collectionActionsAccordion').accordion("option", "active", 3);
                    yukon.collection.actions.progress.report.init();
                },
                error: function (xhr, status, error, $form) {
                    yukon.ui.unbusy(btn);
                    $('#actionInputsDiv').html(xhr.responseText);
                    $(document).scrollTop(0);
                },
            });
        },
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            //figure out which header should be selected
            var selectedHeader = 0;
            if ($('#collectionjson').length) {
                var deviceCollection = yukon.fromJson('#collectionjson');
                selectedHeader = 1;
                $('.js-count').html($('#deviceCollectionCount').val());
                $('.js-device-description').html($('#deviceCollectionDescription').val());
            }
            if ($('#action').length) {
                selectedHeader = 2;
                $('.js-action').html($('#action').val());
            }

            $('#collectionActionsAccordion').accordion({
                heightStyle: "content",
                collapsible: true,
                active: selectedHeader,
                beforeActivate : function(e, ui) {
                    // hide content in action inputs (this was causing an issue because of overflow visible)
                    $('#collectionActionsContainer').addClass('dn');
                },
                activate : function(e, ui) {
                    // redisplay content in action inputs (this was causing an issue because of overflow visible)
                    $('#collectionActionsContainer').removeClass('dn');
                }
            });
            
            $(document).on('click', '.js-collection-action', function () {
                var href = $(this).data('url'),
                    action = $(this).data('action'),
                    actionInputsDiv = $('#actionInputsDiv');
                $.ajax({ 
                    url: href
                }).done(function (data) {
                    actionInputsDiv.html(data);
                    yukon.ui.initContent(actionInputsDiv);
                    if (action == 'Device Configs') {
                        yukon.bulk.device.configs.init();
                    } else if (action == 'Mass Change') {
                        yukon.bulk.masschange.init();
                    } else if (action == 'Add Points' || action == 'Update Points' || action == 'Remove Points') {
                        yukon.bulk.point.init();
                    } else if (action == 'Configure Data Streaming') {
                        yukon.bulk.dataStreaming.init();
                    }
                    $('.js-action-separator').removeClass('dn');
                    $('.js-action').html(action);
                    $('#collectionActionsAccordion').accordion("option", "active", 2);
                    //clear out any old progress reports
                    var progressReportMsg = $('#progressReportMessage').val();
                    $('#progressReportDiv').html(progressReportMsg);
                    yukon.collection.actions.progress.report.cancelUpdating();
                });
            });
            
            $(document).on('click', '.js-action-submit', function () {
                mod.submitAction($(this));
            });
            
            //Use clicked on execute button on Send Command or Locate Route
            $(document).on('click', '.js-execute-command, .js-locate-route', function () {
                //set dropdown value
                $('#commandFromDropdown').val($('#commandSelectId option:selected').text());
                mod.submitAction($(this));
            });

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.collection.actions.init(); });