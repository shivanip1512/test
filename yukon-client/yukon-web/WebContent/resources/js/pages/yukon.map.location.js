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
            	var location = {
            			paoId : $('.js-device-id').val(),
            			latitude : $('.js-latitude-input').val(),
            			longitude : $('.js-longitude-input').val()
            		}
                $.ajax({
                	type: "POST",
                    url: yukon.url('/stars/mapNetwork/saveCoordinates'),
                    data: location,
                    success: function(results) {
                    	$('.js-latitude-input').removeClass("error");
                    	$('.js-longitude-input').removeClass("error");
                        if (results.error) {
                            yukon.ui.removeAlerts();
                            $('.js-location-error').html(results.errorMessages);
                            if(results.latError){
                                $('.js-latitude-input').addClass("error");
                            }
                            if(results.lonError){
                                $('.js-longitude-input').addClass("error");
                            }
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
            var feature = e.features.getArray()[0],
                coord = ol.proj.transform(feature.getGeometry().getCoordinates(), _destProjection, src_projection),
                latitude = coord[1].toFixed(6),
                longitude = coord[0].toFixed(6),
                changeCoordinatesDialog = $('#change-coordinates-confirm');
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