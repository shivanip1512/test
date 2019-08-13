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
        var triggerType = $("#js-trigger-type option:selected").val();
        $('.js-threshold-point-section').toggleClass('dn', triggerType !== 'THRESHOLD_POINT');
        $('.js-threshold-section').toggleClass('dn', triggerType !== 'THRESHOLD');
        $('.js-status-section').toggleClass('dn', triggerType !== 'STATUS');
        if(triggerType === 'THRESHOLD'){
            _setDefaultProjectionProperties();
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
            $(document).on('change', '#js-use-peak-tracking-threshold-point', function() {
                if (this.checked) {
                    $("#picker-thresholdPointPeakTracking-btn").prop('disabled', false);
                } else {
                    $("#point-peak-tracking").val("");
                    $("#picker-thresholdPointPeakTracking-btn").prop('disabled', true);
                }
            });
            $(document).on('change', '#js-use-peak-tracking-threshold', function() {
                if (this.checked) {
                    $("#picker-thresholdPeakTracking-btn").prop('disabled', false);
                } else {
                    $("#point-peak-tracking").val("");
                    $("#picker-thresholdPeakTracking-btn").prop('disabled', true);
                }
            });
            $(document).on("yukon:dr:setup:controlArea:renderTriggerFields", function (event){
                var peakPointId = $("#js-peak-point-id").val();
                if(!peakPointId){
                    $("#picker-thresholdPeakTracking-btn").prop('disabled', true);
                    $("#picker-thresholdPointPeakTracking-btn").prop('disabled', true);
                }
            });
            
            $(document).on('yukon:trigger:identification:complete', function() {
                var container = $(".js-trigger-controls:visible"),
                    pointId = $("#point-trigger-identification-status").val(),
                    isViewMode = container.find(".js-trigger-type-view-mode").is(":visible");
                $("#js-normal-state").removeClass("dn");
                $.ajax({
                    url: yukon.url('/dr/setup/controlArea/getNormalState/' + pointId),
                    type: 'get'
                }).done(function (data) {
                    if(!isViewMode){
                        container.find("#js-status-normal-state-create").empty();
                        data.normalStates.forEach(function (field){
                            var option = $('<option value=' + field.id + '>' + field.name + '</option>');
                            container.find("#js-status-normal-state-create").append(option);
                        });
                    } else {
                        container.find("#js-status-normal-state-edit").empty();
                        data.normalStates.forEach(function (field){
                            var option = $('<option value=' + field.id + '>' + field.name + '</option>');
                            container.find("#js-status-normal-state-edit").append(option);
                        });
                    }
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