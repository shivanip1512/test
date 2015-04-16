yukon.namespace('yukon.da.regulator');

/**
 * Module that manages the regulator page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.regulator = (function () {

    'use strict';
    
    /** @type {number} - The regulator id. */
    var _id = null;
    
    /** {String} - The IANA timezone name. */
    var _tz = jstz.determine().name();
    
    var _templates = {
        successful: '<span class="label label-success">' + yg.text.successful + '</span>',
        failed: '<span class="label label-danger">' + yg.text.failed + '</span>'
    };
    
    /**
     * @type {Object.<string, string>}
     *
     * Object mapping PaoTypes to supported attributes. ex:
     * 
     *  { PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    var _paoTypeToAttributes = {};
    
    /**
     * Different regulator types (LTC, CL7) have different attributes.
     * this method sets up the attributes table to display only the supported attributes
     * and hides and disabled all the other inputs.
     */
    var _showHideMappings = function () {
        
        var paoType = $('#regulator-type').val(),
            mappings = _paoTypeToAttributes[paoType],
            table = $(document).find('.js-mappings-table');
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);
        
        mappings.forEach(function (mapping) {
            table.find('[data-mapping="' + mapping + '"]').show().find('input').prop('disabled', false);
        });
    };
    
    var _updateRecentEvents = function () {
        
        $.ajax({
            url: yukon.url('/capcontrol/regulators/' + _id + '/events'),
            data: { 'lastUpdate': $('#regulator-events-last-update').val() }
        }).done(function (data) {
            
            $('#regulator-events-last-update').val(data.timestamp);
            var templateRow = $('#regulator-events-template-row');
            var body = $('#regulator-events').find('tbody');
            
            var events = data.events;
            
            if (events.length) {
                $('.js-ivvc-events-empty').hide();
                $('.js-ivvc-events-holder').show();
            }
            
            events.reverse().forEach(function (event) {
                
                var row = $(templateRow.clone()).removeAttr('id');
                
                row.find('.js-event-icon').addClass(event.icon);
                row.find('.js-message').html(event.message);
                row.find('.js-user').text(event.user);
                
                var timeText = moment(event.timestamp).tz(_tz).format(yg.formats.date.full);
                row.find('.js-timestamp').text(timeText);
                
                body.prepend(row);
                row.flash();
            });

            body.find('tr:gt(20)').remove();
            
            body.find('tr:gt(20)').remove();
            
            setTimeout(_updateRecentEvents, yg.rp.updater_delay);
            
        });
    };
    
    var mod = {
        
        init : function () {
            
            _id = $('#regulator-id').val();
            
            _paoTypeToAttributes = yukon.fromJson('#pao-type-map');
            _showHideMappings();
            
            $('#regulator-type').on('change', function () {
                _showHideMappings();
            });
            
            $(document).on('yukon:da:regulator:delete', function () {
                $('#delete-regulator').submit();
            });
            
            $(document).on('yukon:da:regulator:automap', function (ev) {
                
                var dialog = $(ev.target);
                debug.log(dialog);
                
                yukon.ui.elementGlass.show('.js-auto-map-dialog');
                dialog.parent().find('.ui-dialog-buttonpane').find('.ui-button').prop('disabled', true);
                 
                $.ajax(yukon.url('/capcontrol/regulators/' + _id + '/automap'))
                .done(function (result) {
                    
                    debug.debug('Mapping Result', result);
                    
                    yukon.ui.elementGlass.hide('.js-auto-map-dialog');
                    dialog.parent().find('.ui-dialog-buttonpane').find('.ui-button').prop('disabled', false);
                    
                    var tbody = $('.js-auto-map-dialog .js-mappings');
                    var status = result.status.type;
                    var success = status === 'SUCCESSFUL' || status === 'PARTIALLY_SUCCESSFUL';
                    
                    $('.js-result-header').show();
                    $('.js-automap-results').show()
                    .find('.js-automap-result').text(result.status.text)
                    .toggleClass('label-info', status === 'INCOMPLETE')
                    .toggleClass('label-danger', status === 'FAILED' )
                    .toggleClass('label-warning', status === 'PARTIALLY_SUCCESSFUL')
                    .toggleClass('label-success', status === 'SUCCESSFUL');
                    
                    result.mappings.forEach(function (mapping) {
                        var td = tbody.find('[data-mapping="' + mapping.type + '"] .js-result');
                        if (mapping.success) {
                            td.html(_templates.successful);
                        } else {
                            td.html(_templates.failed).append($('<span>').text(' - ' + mapping.text));
                        }
                    });
                    
                    if (success) {
                        $.ajax(yukon.url('/capcontrol/regulators/' + _id + '/mapping-table'))
                        .done(function (table) {
                            $('.js-mappings-container').html(table);
                        });
                    }
                    
                });
            });
            
            /** Reset the dialog when closed */
            $(document).on('dialogclose', '.js-auto-map-dialog', function (ev) {
                
                var dialog = $(this);
                
                dialog.find('.js-automap-results').hide();
                $('.js-result-header').hide();
                dialog.find('.js-result').empty();
            });
            
            _updateRecentEvents();
        }
    };
    
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
