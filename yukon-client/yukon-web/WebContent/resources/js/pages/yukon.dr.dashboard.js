yukon.namespace('yukon.dr.dashboard');

/**
 * Module for the dr dashboard page
 * @module   yukon.dr.dashboard
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.dr.dashboard = (function() {
        
    var command_time = '#time',
        email_time = '#emailTime';
    
    var mod = {
            
        init: function() {
            
            $(document).on('click', '#broadcast-config .button-group-toggle .button', function() {
                
                if ($('#broadcast-config .button-group-toggle .yes').hasClass('on')) {
                    $('#rf-performance-email').val('true');
                    $('.js-notif-group').show('fade');
                    $('.js-email-schedule').show('fade');
                } else {
                    $('#rf-performance-email').val('false');
                    $('.js-notif-group').hide('fade');
                    $('.js-email-schedule').hide('fade');
                }
                
            });
            
            if ($('#rf-performance-email').val() === 'true') {
                $('#broadcast-config .button-group-toggle .yes').addClass('on');
                $('#broadcast-config .button-group-toggle .no').removeClass('on');
                $('.js-notif-group').show();
                $('.js-email-schedule').show();
            } else {
                $('#broadcast-config .button-group-toggle .no').addClass('on');
                $('#broadcast-config .button-group-toggle .yes').removeClass('on');
                $('.js-notif-group').hide();
                $('.js-email-schedule').hide();
            }
            
            $(document).on('yukon.dr.rf.config.load', function (ev) {
                // each time the configure button popup is loaded, reset the field values of the times
                // and reinit the sliders so they accurately reflect the current settings in the database
                $(command_time).val(_originalRfCommandTime);
                $(email_time).val(_originalRfEmailTime);
                yukon.ui.timeSlider.init();
            });
            
            /** Reset the season control hours. */
            $(document).on('yukon:dr:season-cntl-hrs-reset', function (ev) {
                
                $.ajax(yukon.url('/dr/season-control-hours/reset'))
                .done(function(status) {
                    if (status.success) {
                        yukon.ui.alertSuccess(status.message);
                    } else {
                        yukon.ui.alertError(status.message);
                    }
                    setTimeout(function() {
                        $('.user-message.success').fadeOut(200, function() { $(this).remove(); });
                    }, 5000);
                });
                
            });
            
            var _originalRfCommandTime = $(command_time).val(),
                _originalRfEmailTime = $(email_time).val();
        }
    };
    
    return mod;
}());

$(function() { yukon.dr.dashboard.init(); });