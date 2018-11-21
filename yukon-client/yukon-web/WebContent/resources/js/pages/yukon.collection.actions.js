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

    _appendViewAllDevices =  function () {
        var numDevices = $('#deviceCollectionCount').val();
        var isNumDevicesEmpty = $.isEmptyObject(numDevices);
        if ($("#collectionActionsAccordion").find(".js-device-collection-template").exists() && isNumDevicesEmpty === false) {
            var clone = $("#collectionActionsAccordion").find(".js-device-collection-template").clone();
            clone.removeClass("js-device-collection-template");
            $("#collectionActionsAccordion").find(".js-view-selected-devices").empty();
            $("#collectionActionsAccordion").find(".js-view-selected-devices").append(clone.html());
        } else if(isNumDevicesEmpty === true) {
            $("#collectionActionsAccordion").find(".js-view-selected-devices").empty();
        }
    },
    
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
                    var actionInputsDiv = $('#actionInputsDiv');
                    yukon.ui.unbusy(btn);
                    actionInputsDiv.html(xhr.responseText);
                    yukon.ui.initContent(actionInputsDiv);
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
                $('.js-device-description').text($('#deviceCollectionDescription').val());
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
                    actionInputsDiv = $('#actionInputsDiv'),
                    collectionActionsDiv = $('#collectionActionsDiv');
                yukon.ui.block(collectionActionsDiv);
                collectionActionsDiv.find('button, a').each(function() {
                   $(this).css('pointer-events', 'none');
                });
                $.ajax({ 
                    url: href
                }).done(function (data) {
                    yukon.ui.unblock(collectionActionsDiv);
                    collectionActionsDiv.find('button, a').each(function() {
                        $(this).css('pointer-events', 'auto');
                     });
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

            $("#collectionActionsAccordion").on("accordionbeforeactivate", function(event, ui) {
                var clickedOnHeader = ui.newHeader;
                if (event.originalEvent) {
                    var element = event.originalEvent.target;
                    if (element.classList.contains('icon-magnifier')) {
                        event.preventDefault();
                    }
                }
            });

            $("#collectionActionsAccordion").on("accordionactivate", function(event, ui) {
                _appendViewAllDevices();
            });

            $(document).on('click', '.js-view-selected-devices .icon-magnifier', function () {
                var activeAccordion = $("#collectionActionsAccordion").accordion("option", "active");
                if (activeAccordion === 2) {
                    $('#collectionActionsContainer').removeClass('dn');
                }
            });

            var activeAccordion = $("#collectionActionsAccordion").accordion("option", "active");
            if (activeAccordion > 0) {
                _appendViewAllDevices();
            }

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.collection.actions.init(); });