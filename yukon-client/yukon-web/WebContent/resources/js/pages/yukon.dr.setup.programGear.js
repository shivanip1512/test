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
    
    _whenToChange = function() {
        var whenToChangeElement = null,
            whenToChange = "";
        if ($(".js-when-to-change").is(":visible")) {
           whenToChangeElement = $(".js-when-to-change:visible");
           whenToChange = $(".js-when-to-change:visible option:selected").val();
        } else {
           whenToChangeElement = $("#whenToChange");
           whenToChange = $("#whenToChange").val();
        }
        whenToChangeElement.closest(".name-value-table").find('#js-changePriority-row').toggleClass('dn', whenToChange != 'Priority');
        whenToChangeElement.closest(".name-value-table").find('#js-changeDurationInMinutes-row').toggleClass('dn', whenToChange != 'Duration');
        whenToChangeElement.closest(".name-value-table").find('#js-triggerNumber-row').toggleClass('dn', whenToChange != 'TriggerOffset');
        whenToChangeElement.closest(".name-value-table").find('#js-triggerOffset-row').toggleClass('dn', whenToChange != 'TriggerOffset');
    },

    _howToStopControl= function() {
        var value = "",
            element = null;
        if ($(".js-how-to-stop-control").is(":visible")) {
            element = $(".js-how-to-stop-control:visible");
            value = $(".js-how-to-stop-control:visible option:selected").val();
        } else {
            element = $("#howToStopControl");
            value = $("#howToStopControl").val();
        }
        element.closest(".name-value-table").find('#js-stopOrder-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
        element.closest(".name-value-table").find('#js-rampOutPercent-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
        element.closest(".name-value-table").find('#js-rampOutInterval-row').toggleClass('dn', (value != 'RampOutTimeIn' && value != 'RampOutRestore'));
    },

    _refreshShedType= function() {
        var refreshShedType = $("#refreshShedType").val();
        $('#js-fixedShedTime-row').toggleClass('dn', refreshShedType != 'FixedShedTime');
        $('#js-maxShedTime-row').toggleClass('dn', refreshShedType != 'DynamicShedTime');
    },

    _initCss = function () {
        $("#js-program-gear-form").find(".timeOffsetWrap").css({"margin-left" : "-5px"});
        var selectedGearType = $("#controlMethod option:selected").val();
        $(".js-help-btn-span").toggleClass("dn", selectedGearType != 'ThermostatRamping' && selectedGearType != 'SimpleThermostatRamping');
        yukon.ui.initDateTimePickers().ancestorInit('.js-simple-thermostat-ramping-gear');
    },

    mod = {
        /** Initialize this module. */
        init : function() {
            if (_initialized)
                return;

            $(document).on('click', '#js-cancel-btn', function(event) {
                window.history.back();
            });

            $(document).on('change', '.js-when-to-change', function(event) {
                _whenToChange();
            });

            $(document).on('change', '.js-how-to-stop-control', function (event) {
                _howToStopControl();
            });

            $(document).on('change', '#refreshShedType', function (event) {
                _refreshShedType();
            });

            $(document).on('click', '#rampIn', function (event) {
                _handleRampInField();
            });

            $(document).on('click', '.js-help-btn', function () {
                var selectedGearType = $("#controlMethod option:selected").val();
                $(".js-simple-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'SimpleThermostatRamping');
                $(".js-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'ThermostatRamping');
            });

            $(document).on("yukon:dr:setup:gear:viewMode", function (event){
                _whenToChange();
                _howToStopControl();
                _refreshShedType();
                _initCss();
            });
            
            $(document).on("yukon:dr:setup:program:gearRendered", function (event) {
                _whenToChange();
                _howToStopControl();
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
                debugger;
                var container = $(this).closest(".js-sep-temperature-ctrl-prms");
                if ($(this).val() == 'HEAT') {
                    container.find(".js-temperature-mode-td").text($(".js-heating-offset-lbl").val() + ":");
                } else {
                    container.find(".js-temperature-mode-td").text($(".js-cooling-offset-lbl").val() + ":");
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