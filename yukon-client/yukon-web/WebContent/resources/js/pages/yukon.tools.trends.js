yukon.namespace('yukon.tools.trends');

/**
 * Module to manage the trends page, uses highstock library to display trends
 * 
 * @require JQUERY
 * @require highstock
 */
yukon.tools.trends = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 900000, // 15 minutes
    _updateTimeout = null,
    _trendChartContainer,
    _trendId,
    _labels,
    _rangeSelectorButtons,
    
    _initDatePickerInResetPeakPopup = function () {
        yukon.ui.initDateTimePickers().ancestorInit($(".js-reset-peak-popup"));
    },
    
    _updateChart = function(blockPage) {

        if (blockPage) {
            yukon.ui.blockPage();
        }
        $.ajax({
            url: yukon.url('/tools/trends/getZoom'),
            type: 'get'
        }).done(function (data) {
            var selectedZoomOption = data.prefZoom;
            $.getJSON(yukon.url('/tools/trends/' + _trendId + '/data'), function (trend) {
                var trendChartOptions = {
                        rangeSelector: {
                            inputEnabled: true,
                            rangeSelectorButtons : _rangeSelectorButtons,
                            selected: selectedZoomOption,
                            inputStyle : {
                                color: yg.colors.BLACK
                            }
                        },
                        chartWidth : null, //When null the width is calculated from the offset width of the containing element.
                        chartHeight : 675,
                        animateSeriesPloting: true
                    },
                    highChartOptions = {};
                
                if (_trendChartContainer.exists()) {
                    yukon.trends.buildChart(_trendChartContainer, trend, trendChartOptions, highChartOptions);
                }
            }).always(function () {
                if (blockPage) {
                    yukon.ui.unblockPage();
                }
            });
        }).fail(function (xhr, status) {
            $("#error-message").removeClass('dn');
        });
        
        
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }        
        var trendUpdater = $('#trend-updater .yes').is('.on');
        if (trendUpdater) {
            _updateTimeout = setTimeout(function () { _updateChart(false)}, _updateInterval);
        }
    },
    
    mod = {
        
        init: function () {
            
            if (_initialized) return;
            
            _trendChartContainer = $('[data-trend]'),
            _trendId = $(_trendChartContainer).data("trend"),
            _labels = JSON.parse(decodeURIComponent($('#label-json').html())),
            _rangeSelectorButtons = [{
                type: 'day',
                count: 1,
                text: _labels.day,
                value: 'DAY_1'
            }, {
                type: 'week',
                count: 1,
                text: _labels.week,
                value: 'WEEK_1'
            }, {
                type: 'month',
                count: 1,
                text: _labels.month,
                value: 'MONTH_1'
            }, {
                type: 'month',
                count: 3,
                text: _labels.threeMonths,
                value: 'MONTH_3'
            }, {
                type: 'month',
                count: 6,
                text: _labels.sixMonths,
                value: 'MONTH_6'
            }, {
                type: 'ytd',
                text: _labels.ytd,
                value: 'YTD'
            }, {
                type: 'year',
                count: 1,
                text: _labels.year,
                value: 'YEAR_1'
            }, {
                type: 'all',
                text: _labels.all,
                value: 'ALL'
            }];

            _updateChart(true);
            
            var trendList = $('.trend-list li.selected');
            if (trendList.length) {
                $('.trend-list').scrollTo(trendList);
            }
            $(document).on('click', '.js-print', function (ev) {
                var chart = _trendChartContainer.highcharts(),
                    width = chart.chartWidth,
                    height = chart.chartHeight;
                chart.print();
                setTimeout(function() {
                    chart.setSize(width, height, false);
                }, 100);
            });
            $(document).on('click', '.js-dl-png', function (ev) {
                var chart = _trendChartContainer.highcharts();
                chart.exportChart({type: 'image/png'});
            });
            $(document).on('click', '.js-dl-jpg', function (ev) {
                var chart = _trendChartContainer.highcharts();
                chart.exportChart({type: 'image/jpeg'});
            });
            $(document).on('click', '.js-dl-pdf', function (ev) {
                var chart = _trendChartContainer.highcharts();
                chart.exportChart({type: 'application/pdf'});
            });
            $(document).on('click', '.js-dl-csv', function (ev) {
                var chart = _trendChartContainer.highcharts(),
                    ex = chart.series[0].xAxis.getExtremes(),
                    max = ex.max ? ex.max : 0,
                    min = ex.min ? ex.min : 0;
                
                window.location = yukon.url('/tools/trends/' + _trendId + '/csv?' 
                + 'from=' + new Date(min).getTime() + '&to=' + new Date(max).getTime()); 
            });
            
            $(document).on("yukon:tools:trend:delete", function (event) {
                yukon.ui.blockPage();
                $("#js-delete-trend-form").submit();
            });
            
            $(document).on("yukon:tools:trend:resetPeakPopupLoaded", function (event) {
                _initDatePickerInResetPeakPopup();
            });
            
            $(document).on("yukon:tools:trend:resetPeak", function (event) {
                var form = $(event.target).find(".js-reset-peak-form");
                       $('.js-reset-peak-date').removeAttr("disabled");
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $(".js-reset-peak-popup").dialog('close');
                        $("#reset-peak-success-message").empty();
                        $("#reset-peak-error-message").empty();
                        $("#reset-peak-success-message").toggleClass("dn", !data.hasOwnProperty("successMessage"));
                        $("#reset-peak-error-message").toggleClass("dn", !data.hasOwnProperty("errorMessage"));
                        yukon.ui.removeAlerts();
                        if (data.hasOwnProperty("successMessage")) {
                            $("#reset-peak-success-message").addMessage({
                                message: data.successMessage,
                                messageClass: 'success'
                            });
                        }
                        if (data.hasOwnProperty("errorMessage")) {
                            $("#reset-peak-error-message").addMessage({
                                message: data.errorMessage,
                                messageClass: 'error'
                            });
                        }
                    },
                    error: function (xhr, status, error, $form) {
                        $(".js-reset-peak-popup").empty();
                        $(".js-reset-peak-popup").html(xhr.responseText);
                        _initDatePickerInResetPeakPopup();
                        $(".js-reset-peak-date").prop("disabled", !($(".js-reset-peak-duration").val() === $(".js-enum-val-selected-date").val()));
                    }
                });
            });

            $(document).on("change", ".js-reset-peak-duration", function () {
                var selectedDuration = $(this).val(),
                      datePicker = $(".js-reset-peak-date");
                if (selectedDuration === $(".js-enum-val-today").val()) {
                    datePicker.val($(".js-todays-date-val").val());
                } else if (selectedDuration === $(".js-enum-val-first-day-of-month").val()) {
                    datePicker.val($(".js-first-date-of-month-val").val());
                } else if (selectedDuration === $(".js-enum-val-first-day-of-year").val()) {
                    datePicker.val($(".js-first-date-of-year-val").val());
                }
                datePicker.prop("disabled", !(selectedDuration === $(".js-enum-val-selected-date").val()));
            });
            
            /** Pause/Resume updating on updater button clicks. */
            $('#trend-updater .button').on('click', function(ev) {
                var pause = $('#trend-updater .yes').is('.on'),
                    url = yukon.url('/tools/trends/setAutoUpdate/' + !pause);
                $.ajax({ type: 'post', url: url});
                $('#trend-updater .button').toggleClass('on');
                if (pause) {
                    clearTimeout(_updateTimeout);
                } else {
                    _updateChart(false);
                }
            });
            
            _initialized = true;

        }
    };
    
    return mod;
}());

$(function () { yukon.tools.trends.init(); });