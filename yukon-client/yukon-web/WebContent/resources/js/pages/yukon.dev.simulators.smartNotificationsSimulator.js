yukon.namespace('yukon.dev.smartNotificationsSimulator');

/**
 * Module handling smart notifications simulator settings
 * @module yukon.dev.simulators.smartNotificationsSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.smartNotificationsSimulator = (function() {
    var _initialized = false,
    
    _showHideControls = function() {
      var eventType = $('.js-event-type-dropdown').val(),
          allTypes = $('.js-all-types').is(":checked"),
          ddmType = $('#ddmType').val(),
          assetImportType = $('#assetImportType').val();
      $('.js-event-type').toggleClass('dn', allTypes);
      $('.js-monitor').toggleClass('dn', allTypes || eventType != ddmType);
      $('.js-asset-import').toggleClass('dn', allTypes || eventType != assetImportType);
      $('.js-monitor-dropdown').attr('disabled', allTypes || eventType != ddmType);
      $('.js-asset-import-dropdown').attr('disabled', allTypes || eventType != assetImportType);
    },

    mod = {
        init : function() {
            if (_initialized) return;
            
            _showHideControls();
            
            $(document).on('click', '.js-create-events', function () {
                var waitTime = $('#waitTime').val(),
                    eventsPerMessage = $('#eventsPerMessage').val(),
                    numberOfMessages = $('#numberOfMessages').val();
                window.location.href = yukon.url('/dev/createEvents?waitTime=' + waitTime + "&eventsPerMessage=" + eventsPerMessage + "&numberOfMessages=" + numberOfMessages);
            });
            
            $(document).on('change', '.js-event-type, .js-all-types', function () {
                _showHideControls();
            });
            
            //set the value for the Daily Digest hour dropdown that was received from YukonSimulatorSettings
            var hourSelected = $(".js-hour-selected").data("hourSelected");
            $("#selectHour option").each(function() {
                if ($(this).val() == hourSelected) {
                    $(this).attr("selected", "selected");
                    return false;
                }
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.smartNotificationsSimulator.init(); });