yukon.namespace('yukon.map.location');

/**
 * Module for the Editing a device location
 * @module yukon.map.location
 * @requires JQUERY
 * @requires yukon
 */
yukon.map.location = (function () {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;

            $(document).on('click', '.js-save-coordinates', function() {
                var deviceId = $('.js-device-id').val(),
                latitude = $('.js-latitude-input').val(),
                longitude = $('.js-longitude-input').val();
                $.ajax({
                    url: yukon.url('/stars/mapNetwork/saveCoordinates?' + $.param({ deviceId: deviceId, latitude: latitude, longitude: longitude })),
                    success: function(results) {
                        if (results.error) {
                            $('.js-location-error').html(results.errorMessages);
                        } else {
                            window.location.reload();
                        }
                    }
                });
            });
            
            $(document).on('click', '.js-edit-coordinates', function() {
                $('.js-view-display').addClass('dn');
                $('.js-edit-display').removeClass('dn');
            });
            
            $(document).on('click', '.js-cancel', function() {
                window.location.reload();
            });
            
            _initialized = true;

        },
        
        changeCoordinatesPopup : function (e, _destProjection, src_projection) {
            var feature = e.features.getArray()[0];
            var coord = ol.proj.transform(feature.getGeometry().getCoordinates(), _destProjection, src_projection);
            var latitude = coord[1].toFixed(6);
            var longitude = coord[0].toFixed(6);
            var changeCoordinatesDialog = $('#change-coordinates-confirm');
            changeCoordinatesDialog.find('.js-latitude').html(latitude);
            changeCoordinatesDialog.find('.js-longitude').html(longitude);
            //confirmation
            changeCoordinatesDialog.dialog({
                'buttons': 
                    [{
                         text: yg.text.cancel, 
                         click: function() {
                             $(this).dialog('close');
                             window.location.reload();
                         }
                    }, 
                    {
                         text: yg.text.ok, 
                         click: function() {
                             $.ajax({
                                 url: yukon.url('/stars/mapNetwork/saveCoordinates?' + $.param({ deviceId: feature.get('pao').paoId, latitude: latitude, longitude: longitude })),
                                 success: function(results) {
                                     window.location.reload();
                                 }
                             });
                         },
                         'class': 'primary action'
                    }]
                 });
        },
    };
    
    return mod;
})();
 
$(function () { yukon.map.location.init(); });