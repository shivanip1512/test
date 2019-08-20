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
        if (triggerType === 'THRESHOLD') {
            $('.js-threshold-point').addClass('dn');
            $('.js-status').addClass('dn');
            $('.js-threshold').removeClass('dn');
            _setDefaultProjectionProperties();
            _enableDisablePeakTracking();
        } else if (triggerType === 'THRESHOLD_POINT') {
            $('.js-status').addClass('dn');
            $('.js-threshold').addClass('dn');
            $('.js-threshold-point').removeClass('dn');
            _enableDisablePeakTracking();
        } else {
            $('.js-threshold').addClass('dn');
            $('.js-threshold-point').addClass('dn');
            $('.js-status').removeClass('dn');
            var pointId = $("#trigger-point-id").val();
            $('#js-normal-state').toggleClass('dn', pointId == "");
            if (pointId != "") {
                _retrieveNormalStates();
            }
        }
    },
    
    _enableDisablePeakTracking = function() {
        var usePeakTracking = $('#js-use-peak-tracking').prop('checked');
        $('.js-peak-tracking').toggleClass('dn', !usePeakTracking);
    },
    
    _setDefaultProjectionProperties = function(){
        var noneSelected = $("#js-threshold-projection-type option:selected").val() === "NONE";
        $('.js-threshold-samples-row').toggleClass('dn', noneSelected);
        $('.js-threshold-ahead-row').toggleClass('dn', noneSelected);
        if (noneSelected) {
            var sampleValue = $('#js-threshold-samples').val();
            if (!sampleValue) {
                $("#js-threshold-samples").val(5);
            }
        }
    },
    
    _retrieveNormalStates = function() {
        var triggerType = $("#js-trigger-type option:selected").val(),
        container = $(".js-trigger-controls:visible"),
        pointId = $("#trigger-point-id").val();
        if (triggerType === 'STATUS') {
            $("#js-normal-state").removeClass("dn");
            $.ajax({
                url: yukon.url('/dr/setup/controlArea/getNormalState/' + pointId),
                type: 'get'
            }).done(function (data) {
                container.find("#js-status-normal-state").empty();
                data.normalStates.forEach(function (field){
                    var option = $('<option value=' + field.id + '>' + field.name + '</option>');
                    container.find("#js-status-normal-state").append(option);
                });
            });
        }
    },

    mod = {
        init : function() {
            
            if (_initialized) return;
            
            $(document).on('change', '#js-trigger-type', function() {
                _enableTriggerSection();
            });
            $(document).on('change', '#js-threshold-projection-type', function() {
                _setDefaultProjectionProperties()
            });
            
            $(document).on('change', '#js-use-peak-tracking', function() {
                $('.js-peak-tracking').toggleClass('dn', !this.checked);
                if (!this.checked) {
                    $('#point-peak-id').val(0);
                }
            });
            
            $(document).on('yukon:trigger:identification:complete', function() {
                _retrieveNormalStates();
            });
            
            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.dr.setup.controlArea.trigger.init();
});