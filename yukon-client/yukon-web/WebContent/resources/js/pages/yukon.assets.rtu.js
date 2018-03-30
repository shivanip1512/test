yukon.namespace('yukon.assets.rtu');

/**
 * Module that handles the behavior on the RTU Details page
 * @module yukon.assets.rtu
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.rtu= (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _timeFormatter = yukon.timeFormatter,
    
    _initializeTimeSlider = function () {
        var timeLabel = $('.js-time-range-label'),
            timeSlider = $('.js-time-range-slider'),
            defaultStart = $("#start-time").val() /60,
            defaultEnd = $("#end-time").val() /60;
        
        //initialize time slider
        timeSlider.slider({
            max: 1440,
            min: 0,
            values: [defaultStart, defaultEnd],
            step: 15,
            range: true,
            slide: function (ev, ui) {
                var start = _timeFormatter.formatTime(ui.values[0], 0),
                    end = _timeFormatter.formatTime(ui.values[1], 0);
                    timeLabel.text(start + ' - ' + end);
                    $("#start-time").val(ui.values[0] * 60);
                    $("#end-time").val(ui.values[1] * 60);
            },
            change: function (ev, ui) {
                var start = _timeFormatter.formatTime(ui.values[0], 0),
                    end = _timeFormatter.formatTime(ui.values[1], 0);
                    timeLabel.text(start + ' - ' + end);
                    $("#start-time").val(ui.values[0] * 60);
                    $("#end-time").val(ui.values[1] * 60);
            }
        });
        var start = _timeFormatter.formatTime(defaultStart, 0),
            end = _timeFormatter.formatTime(defaultEnd, 0);
            timeLabel.text(start + ' - ' + end);
            timeSlider.find('.ui-slider-range').css({"background" : "#38c", "height" : "12px", "padding" : "0"});
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _initializeTimeSlider();
            
            /** User clicked on one of the show hide buttons on the Child Hierarchy tab */
            $(document).on('click', '.js-show-hide', function () {
                var paoId = $(this).attr('data-paoId'),
                    pointsDiv = $('.js-points-' + paoId),
                    childPointsDiv = pointsDiv.find('.js-child-points'),
                    borderedDiv = pointsDiv.find('.bordered-div'),
                    icon = $(this).find('i');
                //check if points are already visible
                if (borderedDiv.is(":visible")) {
                    childPointsDiv.addClass('dn');
                    icon.removeClass('icon-collapse');
                    icon.addClass('icon-expand');
                } else {
                    $.ajax({ url: yukon.url('/stars/rtu/child/' + paoId + '/points') })
                    .done(function (details) {
                        pointsDiv.html(details);
                        icon.removeClass('icon-expand');
                        icon.addClass('icon-collapse');
                    });
                }
            });
            
            /** User clicked on the All Points tab */
            $(document).on('click', '.js-all-points-tab', function () {
                var rtuId = $('#rtuId').val();
                $.ajax({ url: yukon.url('/stars/rtu/' + rtuId + '/allPoints') })
                .done(function (details) {
                    $('.js-all-points').html(details);
                });
            });
            
            /** User filtered the All Points tab */
            $(document).on('click', '.js-filter', function (ev) {
                var tableContainer = $('.js-all-points'),
                    rtuId = $('#rtuId').val(),
                    form = $('#rtuAllPoints');
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/stars/rtu/' + rtuId + '/allPoints?' + form.serialize()));
                    }
                });
            });
            
            $(document).on('click', '#cancel-btn', function (event) {
                window.history.back();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.rtu.init(); });