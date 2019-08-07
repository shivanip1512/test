yukon.namespace('yukon.dr.setup.controlArea.trigger');

/**
 * Module that handles the behavior of triggers on control area page.
 * @module yukon.dr.setup.controlarea.trigger
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.controlArea.trigger = (function() {

    'use strict';

    var _initialized = false,

    _enableTriggerSection = function() {
        if ($("#js-trigger-type option:selected").val() === "THRESHOLD_POINT") {
            $(".js-threshold-point-section").removeClass("dn");
            $(".js-threshold-section").addClass("dn");
            $(".js-status-section").addClass("dn");
        }
        if ($("#js-trigger-type option:selected").val() === "THRESHOLD") {
            $(".js-threshold-point-section").addClass("dn");
            $(".js-threshold-section").removeClass("dn");
            $(".js-status-section").addClass("dn");
            _setDefaultProjectionProperties();
        }
        if ($("#js-trigger-type option:selected").val() === "STATUS") {
            $(".js-threshold-point-section").addClass("dn");
            $(".js-threshold-section").addClass("dn");
            $(".js-status-section").removeClass("dn");
        }
    },
    _setDefaultProjectionProperties = function(){
        if ($("#js-threshold-projection-type option:selected").val() === "NONE") {
            $("#js-threshold-samples").prop('disabled', true);
            $("#js-threshold-samples").val(5);
            $("#js-threshold-ahead").prop('disabled', true);
        } else {
            $("#js-threshold-samples").prop('disabled', false);
            $("#js-threshold-ahead").prop('disabled', false);
        }
    },

    mod = {
        init : function() {
            if (_initialized)
                return;
            $(document).on('change', '#js-trigger-type', function() {
                _enableTriggerSection();
            });
            $(document).on('change', '#js-threshold-projection-type', function() {
                _setDefaultProjectionProperties()
            });
            $(document).on('click', '#js-use-peak-tracking-threshold-point .button', function() {
                var usePeakTracking = $(this).hasClass('use-Prak-Tracking');
                if (usePeakTracking) {
                    $("#picker-thresholdPointPeakTracking-btn").prop('disabled', false);
                } else {
                    $("#point-peak-tracking").val("");
                    $("#picker-thresholdPointPeakTracking-btn").prop('disabled', true);
                }
            });
            $(document).on('click', '#js-use-peak-tracking-threshold .button', function() {
                var usePeakTracking = $(this).hasClass('use-Prak-Tracking');
                if (usePeakTracking) {
                    $("#picker-thresholdPeakTracking-btn").prop('disabled', false);
                } else {
                    $("#point-peak-tracking").val("");
                    $("#picker-thresholdPeakTracking-btn").prop('disabled', true);
                }
            });
            $(document).on("yukon:dr:setup:controlArea:renderTriggerFields", function (event){
                $("js-add-triggers").html('');
            });
            
            $(document).on('yukon:trigger:identification:complete', function() {
                var pointId = $("#point-trigger-identification").val();
                $.ajax({
                    url: yukon.url('/dr/setup/controlArea/getNormalState/' + pointId),
                    type: 'get'
                }).done(function (data) {
                    data.normalStates.forEach(function (field){
                        var option = $('<option value=' + field.id + '>' + field.name + '</option>');
                        $('#js-ststus-normal-state').append(option);
                    });
                });
            });
            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.dr.setup.controlArea.trigger.init();
});