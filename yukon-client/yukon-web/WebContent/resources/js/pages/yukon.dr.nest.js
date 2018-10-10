yukon.namespace('yukon.dr.nest');

/**
 * Module that serves the nest details page
 * @module   yukon.dr.nest
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 */
yukon.dr.nest = (function () {

    mod = null;
    
    mod = {

        init: function () {
            $(document).on('click', '.nest-sync-button-group-toggle .button', function () {
                var scheduledSyncOn = $('.nest-sync-button-group-toggle .button.yes').hasClass('on');
                if (scheduledSyncOn) {
                    $('#nest-sync').val('true');
                    $('.nest-sync-time-slider').show('fade');
                } else {
                    $('#nest-sync').val('false');
                    $('.nest-sync-time-slider').hide('fade');
                }
            });
            
            if ('true' === $('#nest-sync').val()) {
                $('.nest-sync-button-group-toggle .yes').addClass('on');
                $('.nest-sync-button-group-toggle .no').removeClass('on');
                $('.nest-sync-time-slider').show();
            } else {
                $('.nest-sync-button-group-toggle .no').addClass('on');
                $('.nest-sync-button-group-toggle .yes').removeClass('on');
                $('.nest-sync-time-slider').hide();
            }
            $('#nest-sync-settings-row').removeClass('dn');
        }
    };
    return mod;
})();

$(function () { yukon.dr.nest.init(); });
