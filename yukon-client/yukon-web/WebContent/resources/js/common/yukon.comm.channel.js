yukon.namespace('yukon.comm.channel');

/** 
 * Module that handles the behavior of physical port for the comm channel
 * @module yukon.comm.channel.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.comm.channel = (function () {

    mod = {
        /**
         * Shows user entered physical port field based on the drop down value selected
         */
        togglePhysicalPort: function() {
            if ($('.js-physical-port').is(":visible")) {
                var selectedPort = $('.js-physical-port option:selected').val(),
                    otherEnumValue = $('#otherPhysicalPortEnumValue').val(),
                    isOtherSelected = selectedPort === otherEnumValue;
                $('.js-user-physical-port-value').toggleClass('dn', !isOtherSelected);
                var physicalPortError = $("#physicalPortErrors").val();
                if (physicalPortError) {
                    var physicalPortRow = $('.js-physical-port-row'),
                        portError = physicalPortRow.find("span[id='physicalPort.errors']");
                    portError.toggleClass('dn', !isOtherSelected);
                }
            }
        },

        /**
         * Provides physical port error formatting
         */
        formatPhysicalPortErrors: function() {
            var physicalPort = $('.js-physical-port');
            if (physicalPort.exists()) {
                var physicalPortError = $("#physicalPortErrors").val();
                if (physicalPortError) {
                    var physicalPortRow = $('.js-physical-port-row'),
                        firstPortError = physicalPortRow.find("span[id='physicalPort.errors']").first();
                    //remove second validation error and line break
                    firstPortError.prev('br').remove();
                    firstPortError.remove();
                    physicalPort.removeClass("error");
                    physicalPortRow.find("span[id='physicalPort.errors']").css({'margin-left':'80px'});
                }
            }
        },

        /**
         * Provides initial physical port other selection
         */
        loadPhysicalPort: function() {
            var physicalPort = $('.js-physical-port');
            if (physicalPort.exists()) {
                var isOtherSelected = $('#isOtherSelected').val(),
                    otherEnumValue = $('#otherPhysicalPortEnumValue').val(),
                    userEnteredPhysicalPort = $('.js-user-physical-port-value');
                if (isOtherSelected) {
                    physicalPort.val(otherEnumValue);
                    userEnteredPhysicalPort.toggleClass('dn', !isOtherSelected);
                }
                var physicalPortError = $("#physicalPortErrors").val();
                if (!physicalPortError && !isOtherSelected) {
                    userEnteredPhysicalPort.val("");
                }
            }
        }
    };

    return mod;
}());
