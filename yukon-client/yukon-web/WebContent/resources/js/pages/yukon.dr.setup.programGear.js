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
        var whenToChange = $("#whenToChange").val();
        $('#js-changePriority-row').toggleClass('dn', whenToChange != 'Priority');
        $('#js-changeDurationInMinutes-row').toggleClass('dn', whenToChange != 'Duration');
        $('#js-triggerNumber-row').toggleClass('dn', whenToChange != 'TriggerOffset');
        $('#js-triggerOffset-row').toggleClass('dn', whenToChange != 'TriggerOffset');
    },

    _howToStopControl= function() {
        var howToStopControl = $("#howToStopControl").val();
        $('#js-stopOrder-row').toggleClass('dn', (howToStopControl != 'RampOutTimeIn' && howToStopControl != 'RampOutRestore'));
        $('#js-rampOutPercent-row').toggleClass('dn', (howToStopControl != 'RampOutTimeIn' && howToStopControl != 'RampOutRestore'));
        $('#js-rampOutInterval-row').toggleClass('dn', (howToStopControl != 'RampOutTimeIn' && howToStopControl != 'RampOutRestore'));
    },

    _handleRampIn= function() {
        $('#js-rampInPercent-row').toggleClass('dn', $("#rampInPercent").val() == 0 &&  $("#rampInIntervalInSeconds").val() == 0);
        $('#js-rampInInterval-row').toggleClass('dn', $("#rampInPercent").val() == 0 &&  $("#rampInIntervalInSeconds").val() == 0);
    },

    _refreshShedType= function() {
        var refreshShedType = $("#refreshShedType").val();
        $('#js-fixedShedTime-row').toggleClass('dn', refreshShedType != 'FixedShedTime');
        $('#js-maxShedTime-row').toggleClass('dn', refreshShedType != 'DynamicShedTime');
    },

    _handleRampInField = function() {
        $('#js-rampInPercent-row').toggleClass('dn', !($('#rampIn').is(':checked')));
        $('#js-rampInInterval-row').toggleClass('dn', !($('#rampIn').is(':checked')));
    },
    
    _initCss = function () {
        $("#js-program-gear-form").find(".timeOffsetWrap").css({"margin-left" : "-5px"});
        $("#js-program-gear-form").find(".js-delta-value").css({"margin-left" : "-5px"});
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

            $(document).on('change', '#whenToChange', function(event) {
                _whenToChange();
            });

            $(document).on('change', '#howToStopControl', function (event) {
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
                _handleRampIn();
                _initCss();
            });

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function() {
    yukon.dr.setup.programGear.init();
});