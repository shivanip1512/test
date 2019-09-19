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

    _enableTriggerSection = function (element) {
        var triggerType = $("#js-trigger-type option:selected").val(),
            container = element.closest("#js-controlArea-trigger-form"),
            uniqueIdentifier = container.find(".js-unique-identifier").val();
        
        if (triggerType === 'THRESHOLD') {
            container.find('.js-threshold-point').addClass('dn');
            container.find('.js-status').addClass('dn');
            container.find('.js-threshold').removeClass('dn');
            _setDefaultProjectionProperties(element);
            _enableDisablePeakTracking(container);
        } else if (triggerType === 'THRESHOLD_POINT') {
            container.find('.js-status').addClass('dn');
            container.find('.js-threshold').addClass('dn');
            container.find('.js-threshold-point').removeClass('dn');
            _enableDisablePeakTracking(container);
        } else {
            container.find('.js-threshold').addClass('dn');
            container.find('.js-threshold-point').addClass('dn');
            container.find('.js-status').removeClass('dn');
            var pointId = container.find("#trigger-point-id-" + uniqueIdentifier).val();
            element.closest("#js-controlArea-trigger-form").find('#js-normal-state').toggleClass('dn', pointId == "");
            if (pointId != "") {
                _retrieveNormalStates();
            }
        }
    },
    
    _enableDisablePeakTracking = function (container) {
        var usePeakTracking = container.find("input[id^='js-use-peak-tracking']").prop('checked');
        container.find('.js-peak-tracking').toggleClass('dn', !usePeakTracking);
    },
    
    _setDefaultProjectionProperties = function (element) {
        var container = element.closest("#js-controlArea-trigger-form"),
            noneSelected = container.find("#js-threshold-projection-type option:selected").val() === "NONE";
        
        container.find('.js-threshold-samples-row').toggleClass('dn', noneSelected);
        container.find('.js-threshold-ahead-row').toggleClass('dn', noneSelected);
        if (noneSelected) {
            var sampleValue = container.find('#js-threshold-samples').val();
            if (!sampleValue) {
                container.find("#js-threshold-samples").val(5);
            }
        }
    },
    
    _retrieveNormalStates = function() {
        var container = $(".js-trigger-controls:visible"),
            triggerType = container.find("#js-trigger-type option:selected").exists() 
                ? container.find("#js-trigger-type option:selected").val() : container.find(".js-trigger-type-val").val(),
            uniqueIdentifier = container.find(".js-unique-identifier").val(),
            pointId = container.find("#trigger-point-id-" + uniqueIdentifier).val();
        if (triggerType === 'STATUS') {
            container.find("#js-normal-state").removeClass("dn");
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
                _enableTriggerSection($(this));
            });
            $(document).on('change', '#js-threshold-projection-type', function() {
                _setDefaultProjectionProperties($(this));
            });
            
            $(document).on('change', 'input[id^="js-use-peak-tracking"]', function() {
                var uniqueIdentifier = $(this).closest("#js-controlArea-trigger-form").find(".js-unique-identifier").val();
                $('.js-peak-tracking').toggleClass('dn', !this.checked);
                if (!this.checked) {
                    $('#peak-point-id-' + uniqueIdentifier).val(0);
                }
            });
            
            $(document).on('yukon:trigger:identification:complete', function (event) {
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