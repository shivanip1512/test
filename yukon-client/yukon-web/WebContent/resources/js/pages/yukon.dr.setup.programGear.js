yukon.namespace('yukon.dr.setup.programGear');

/**
 * Module that handles the behavior on the setup program Gear page.
 * 
 * @module yukon.dr.setup.programGear
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.programGear = (function() {

    'use strict';

    var _initialized = false, 

    _whenToChange = function (event) {
        var whenToChangeElement = null,
            whenToChange = "";
        if ($(".js-when-to-change").is(":visible")) {
            whenToChangeElement = $(".js-when-to-change:visible");
            whenToChange = $(".js-when-to-change:visible option:selected").val();
        } else {
            var dialog = $(event.target),
                gearForm = dialog.find('#js-program-gear-form');
            whenToChangeElement = gearForm.find('#whenToChange');
            whenToChange = whenToChangeElement.val();
        }
        whenToChangeElement.closest(".name-value-table").find('#js-changePriority-row').toggleClass('dn', whenToChange != 'Priority');
        whenToChangeElement.closest(".name-value-table").find('#js-changeDurationInMinutes-row').toggleClass('dn', whenToChange != 'Duration');
        whenToChangeElement.closest(".name-value-table").find('#js-triggerNumber-row').toggleClass('dn', whenToChange != 'TriggerOffset');
        whenToChangeElement.closest(".name-value-table").find('#js-triggerOffset-row').toggleClass('dn', whenToChange != 'TriggerOffset');
    },

    _howToStopControl = function (event) {
        var value = "",
            element = null;
        if ($(".js-how-to-stop-control").is(":visible")) {
            element = $(".js-how-to-stop-control:visible");
            value = $(".js-how-to-stop-control:visible option:selected").val();
        } else {
            var dialog = $(event.target),
                gearForm = dialog.find('#js-program-gear-form');
            element = gearForm.find('#howToStopControl');
            value = element.val();
        }
        element.closest(".name-value-table").find('#js-stopOrder-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
        element.closest(".name-value-table").find('#js-rampOutPercent-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
        element.closest(".name-value-table").find('#js-rampOutInterval-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
    },

    _refreshShedType= function() {
        var refreshShedType = $("#refreshShedType").val();
        if(refreshShedType === 'FixedShedTime') {
            $("#js-shedTime-row").find(".name").text($("#fixedShedTime").val());
        } else {
            $("#js-shedTime-row").find(".name").text($("#maxShedTime").val());
        }
    },

    _initCss = function () {
        $("#js-program-gear-form").find(".timeOffsetWrap").css({"margin-left" : "0px"});
        var selectedGearType = $("#controlMethod option:selected").val();
        $(".js-help-btn-span").toggleClass("dn", selectedGearType != 'ThermostatRamping' && selectedGearType != 'SimpleThermostatRamping');
        yukon.ui.initDateTimePickers().ancestorInit('.js-simple-thermostat-ramping-gear');
    },

    mod = {
        /** Initialize this module. */
        init : function() {
            if (_initialized)
                return;

            $(document).on('change', '.js-when-to-change', function (event) {
                _whenToChange(event);
            });

            $(document).on('change', '.js-how-to-stop-control', function (event) {
                _howToStopControl(event);
            });

            $(document).on('change', '#refreshShedType', function (event) {
                _refreshShedType();
            });

            $(document).on('click', '.js-help-btn', function () {
                var selectedGearType = $("#controlMethod option:selected").val();
                $(".js-simple-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'SimpleThermostatRamping');
                $(".js-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'ThermostatRamping');
            });

            $(document).on("yukon:dr:setup:gear:viewMode", function (event) {
                _whenToChange(event);
                _howToStopControl(event);
                _refreshShedType();
                _initCss();
            });

            $(document).on('change', '.js-setpoint-input', function (event) {
                var container = $(this).closest(".js-thermostat-ramping-ctrl-prms");
                if ($(this).val() == 'DELTA') {
                    container.find(".js-value-b").text($(".js-deltaB-lbl").val() + ":");
                    container.find(".js-value-d").text($(".js-deltaD-lbl").val() + ":");
                    container.find(".js-value-f").text($(".js-deltaF-lbl").val() + ":");
                } else {
                    container.find(".js-value-b").text($(".js-absB-lbl").val() + ":");
                    container.find(".js-value-d").text($(".js-absD-lbl").val() + ":");
                    container.find(".js-value-f").text($(".js-absF-lbl").val() + ":");
                }
            });
            
            $(document).on('change', '.js-temperature-mode', function (event) {
                var container = $(this).closest(".js-sep-temperature-ctrl-prms");
                if ($(this).val() == 'HEAT') {
                    container.find(".js-temperature-mode-td").text($(".js-heating-offset-lbl").val() + ":");
                } else {
                    container.find(".js-temperature-mode-td").text($(".js-cooling-offset-lbl").val() + ":");
                }
            });

            $(document).on('change', '.js-setpoint-mode', function (event) {
               var container = $(this).closest(".js-hw-temperature-ctrl-prms");
               if ($(this).val() == 'HEAT') {
                   container.find(".js-setpoint-mode-td").text($(".js-preheat-offset-lbl").val() + ":");
               } else {
                   container.find(".js-setpoint-mode-td").text($(".js-precool-offset-lbl").val() + ":");
               }
            });

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function() {
    yukon.dr.setup.programGear.init();
});