yukon.namespace('yukon.historical.readings');

/**
 * Module that handles the behavior on the Historical Readings popup
 * @module yukon.historical.readings
 * @requires JQUERY
 * @requires yukon
 */
yukon.historical.readings = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _loadTrend = function (pointId) {
        var duration = $("#duration_" + pointId).val(),
            url = yukon.url('/meter/historicalReadings/trend'),
            parameters = {pointId : pointId, duration :  duration};

        $.post(url, parameters, function (response) {
            $(".js-trend-container_" + pointId).removeClass("dn");
            $("#trend-graph_" + pointId).html(response);
            $("#duration_" + pointId).closest(".ui-widget-content").dialog({
                width : 800,
                height : 500
            });
        }).always(function () {
            yukon.ui.unblock($(".js-trend-container_" + pointId));
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-hide-dropdown', function () {
                $('.dropdown-menu').hide();
            });
            
            /** User is deleting a RPH value */
            $(document).on('yukon:historical:readings:delete', function (ev) {
                var container = $(ev.target),
                    pointId = container.attr('data-pointId'),
                    value = container.attr('data-value'),
                    timestamp = container.attr('data-timestamp'),
                    valuesTable = $('.js-values-table-' + pointId + ":visible"),
                    valuesDialog = valuesTable.closest('.ui-dialog-content'),
                    btns = container.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                $.ajax({ 
                    url: yukon.url('/meter/historicalReadings/delete'),
                    data: {
                        pointId: pointId,
                        value: value,
                        timestamp: timestamp
                    },
                    method : 'POST'
                }).done(function (details) {
                    container.dialog('destroy');
                    valuesTable.html(details);
                    valuesDialog.scrollTop(0);
                });
            });

            $(document).on('yukon:historical:readings:editValue', function(ev) {
                var dialog = $(ev.target),
                    pointId = dialog.attr('data-point-id'),
                    valuesTable = $('.js-values-table-' + pointId + ":visible"),
                    valuesDialog = valuesTable.closest('.ui-dialog-content'),
                    form = dialog.find('#manual-entry-form'),
                    btns = dialog.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                form.ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        dialog.dialog('destroy');
                        valuesTable.html(result);
                        valuesDialog.scrollTop(0);
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            $(document).on('click', "button[id^='trend_']", function () {
                _loadTrend($(this).data('point-id'));
            });
            
            $(document).on('click', '.js-close-trend-btn', function (event) {
                $(this).closest(".ui-widget-content").dialog({
                    width : 500,
                    height : 400
                });
                $(this).closest("div[class^='js-trend-container_']").addClass("dn");
            });
            
            $(document).on('click', "button[id^='download_']", function () {
                yukon.historical.readings.setDownloadUrl($(this).data('point-id'));
            });
            
            $(document).on('change', "select[id^='duration_']", function () {
                var pointId = $(this).data('point-id'),
                    trendContainer = $(".js-trend-container_" + pointId);
                if (!trendContainer.hasClass("dn")) {
                    yukon.ui.block(trendContainer);
                    _loadTrend(pointId);
                }
                yukon.historical.readings.setDownloadUrl(pointId);
            });
            
            _initialized = true;
        },
        
        setDownloadUrl : function (pointId) {
            var duration = '#duration_' + pointId,
                url = $(duration + ' :selected').data('download-url');
            $('#download_' + pointId).attr('data-href', yukon.url(url));
        }

    };
    
    return mod;
})();

$(function () { yukon.historical.readings.init(); });