mod = {
        
        /** Initialize this module. */
        init: function () {

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
        }
}

$(function () { mod.init(); });