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
        togglePhysicalPort: function(dialog) {
            if (dialog.find('.js-physical-port').is(":visible")) {
                var selectedPort = dialog.find('.js-physical-port option:selected').val(),
                    otherEnumValue = dialog.find('#otherPhysicalPortEnumValue').val(),
                    isOtherSelected = selectedPort === otherEnumValue;
                dialog.find('.js-user-physical-port-value').toggleClass('dn', !isOtherSelected);
                var physicalPortError = dialog.find("#physicalPortErrors").val();
                if (physicalPortError) {
                    var physicalPortRow = dialog.find('.js-physical-port-row'),
                        portError = physicalPortRow.find("span[id='physicalPort.errors']");
                    portError.toggleClass('dn', !isOtherSelected);
                }
            }
        },

        /**
         * Provides physical port error formatting
         */
        formatPhysicalPortErrors: function(dialog) {
            var physicalPort = dialog.find('.js-physical-port');
            if (physicalPort.exists()) {
                var physicalPortError = dialog.find("#physicalPortErrors").val();
                if (physicalPortError) {
                    var physicalPortRow = dialog.find('.js-physical-port-row'),
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
        loadPhysicalPort: function(dialog) {
            var physicalPort = dialog.find('.js-physical-port');
            if (physicalPort.exists()) {
                var isOtherSelected = dialog.find('#isOtherSelected').val(),
                    otherEnumValue = dialog.find('#otherPhysicalPortEnumValue').val(),
                    userEnteredPhysicalPort = dialog.find('.js-user-physical-port-value');
                if (isOtherSelected) {
                    physicalPort.val(otherEnumValue);
                    userEnteredPhysicalPort.toggleClass('dn', !isOtherSelected);
                }
                var physicalPortError = dialog.find("#physicalPortErrors").val();
                if (!physicalPortError && !isOtherSelected) {
                    userEnteredPhysicalPort.val("");
                }
            }
        }
    };

    return mod;
}());
