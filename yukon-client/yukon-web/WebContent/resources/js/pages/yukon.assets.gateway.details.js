yukon.namespace('yukon.assets.gateway.details');
 
/**
 * Module to handle the gateway details page.
 * 
 * @module yukon.assets.gateway.details
 * @requires yukon
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires OPEN_LAYERS
 */
yukon.assets.gateway.details = (function () {
 
    var
    _initialized = false,
    
    _text,
    
    /** @type {Number} - The gateway pao id. */
    _gateway,
    
    _updateSequences = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/' + _gateway + '/sequences')
        }).done(function (table) {
            $('#gw-sequence-table').html(table);
        }).always(function () {
            setTimeout(_updateSequences, 4000);
        });
    },
    
    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/' + _gateway + '/data')
        }).done(function (gw) {
                        
            var comm = $('#gw-comm'), collection = $('#gw-data-collection'), 
                data = gw.data, radios = gw.data.radios, percent = data.collectionPercent.toFixed(2);
            
            comm.find('.js-gw-admin').text(data.admin);
            comm.find('.js-gw-super-admin').text(data.superAdmin);
            comm.find('.js-gw-conn-type').text(data.connType);
            comm.find('.js-gw-ip').text(data.ip);
            comm.find('.js-gw-port').text(_text['port'].replace('{0}', data.port));
            comm.find('.js-gw-nmip').text(data.nmip);
            comm.find('.js-gw-nmport').text(_text['port'].replace('{0}', data.nmport));
            comm.find('.js-gw-radios').empty();
            radios.forEach(function (item, idx, arr) {
                var radio = radios[idx],
                    timestamp = moment(radio.timestamp).tz(yg.timezone).format(yg.formats.date.full_hm),
                    div = $('<div>').addClass('stacked').attr('title', timestamp);
                div.append('<div>' + radio.type + '</div>');
                div.append('<div>' + radio.mac + '</div>');
                div.append('<div>' + radio.version + '</div>');
                $('.js-gw-radios').append(div);
            });
            comm.find('.js-gw-last-comm').text(data.lastCommText)
            .toggleClass('green', data.lastComm == 'SUCCESSFUL')
            .toggleClass('red', data.lastComm == 'FAILED')
            .toggleClass('orange', data.lastComm == 'MISSED')
            .toggleClass('subtle', data.lastComm == 'UNKNOWN');
            comm.find('.js-gw-last-comm-time').text(moment(data.lastCommTimestamp).tz(yg.timezone).format(yg.formats.date.full_hm));
            collection.find('.js-gw-data-completeness .progress-bar').css({ width: data.collectionPercent + '%' })
            .toggleClass('progress-bar-success', !data.collectionDanger && !data.collectionWarning)
            .toggleClass('progress-bar-warning', data.collectionWarning)
            .toggleClass('progress-bar-danger', data.collectionDanger);
            if (percent == 100) percent = 100;
            collection.find('.js-gw-data-completeness-percent').text(percent + '%');
            collection.find('.js-gw-schedule').text(data.schedule);
        }).always(function () {
            setTimeout(_update, 4000);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _text = yukon.fromJson('#gateway-text');
            _gateway = $('#gateway-id').data('id');
                        
            /** Delete this gateway. */
            $(document).on('yukon:assets:gateways:delete', function (ev) {
                $('#delete-gw-form').submit();
            });
            
            /** Save button clicked on location popup, save the location. */
            $(document).on('yukon:assets:gateway:location:save', function (ev) {
                
                var popup = $('#gateway-location-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
            
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-location-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
                
            });

            /** Save button clicked on schedule popup, save the schedule. */
            $(document).on('yukon:assets:gateway:schedule:save', function (ev) {
                
                var popup = $('#gateway-schedule-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
            
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-schedule-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
                
            });
            
            _update();
            _updateSequences();
            
            _initialized = true;
        },

    };
 
    return mod;
})();
 
$(function () { yukon.assets.gateway.details.init(); });