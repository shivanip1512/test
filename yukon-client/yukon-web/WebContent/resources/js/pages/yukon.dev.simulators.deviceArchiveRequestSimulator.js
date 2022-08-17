yukon.namespace("yukon.dev.simulators.deviceArchiveRequestSimulator");

/**
 * Module handling Device Archive Request Simulator
 * @module yukon.dev.simulators.deviceArchiveRequestSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.deviceArchiveRequestSimulator = ( function() {
       
    'use strict';

    var 
    _initialized = false,
    
    mod = {
        init: function() {
            
            if (_initialized) return;
            
            $(document).on('change', '#deviceType', function () {
                //change all to display none
                $('.js-rf-da').addClass('dn');
                $('.js-rfn-lcr').addClass('dn');
                $('.js-meter').addClass('dn');
                var selectedValue = $(this).val(),
                    selectedClasses = $('.' + selectedValue).removeClass('dn');
            });
            
            $(document).on('click', '.js-send-device-archive-request', function () {
                var deviceType = $('#deviceType').val();
                //set model and manufacturer
                if (deviceType == 'js-meter') {
                    var manufacturerModelField = $('#js-meter-manufacturer-model').find(':selected'),
                        manufacturer = manufacturerModelField.data("manufacturer"),
                        model = manufacturerModelField.data("model");
                } else {
                    var model = $('#' + deviceType + '-model').val(),
                        manufacturer = $('#' + deviceType + '-manufacturer').val();
                }
                $('#manufacturer').val(manufacturer);
                $('#model').val(model);
                $('#deviceArchiveForm').submit();
            });

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dev.simulators.deviceArchiveRequestSimulator.init(); });