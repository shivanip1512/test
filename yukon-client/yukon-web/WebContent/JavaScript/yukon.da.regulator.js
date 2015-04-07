yukon.namespace('yukon.da.regulator');

/**
 * Module that manages the regulator page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.regulator = (function () {
    
    _templates = {
        successful: '<span class="label label-success">' + yg.text.successful + '</span>',
        failed: '<span class="label label-danger">' + yg.text.failed + '</span>'
    };
    
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
        
        mappings.forEach(function (mapping, idx) {
            table.find('[data-mapping="' + mapping + '"]').show().find('input').prop('disabled', false);
        });
    },
    
    /**
     * @type {Object.<string, string>}
     *
     * Object mapping PaoTypes to supported attributes. ex:
     * 
     *  { PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    _paoTypeToAttributes = {},
    
    _updateRecentEvents = function () {
        
        $.ajax({
            url: yukon.url('/capcontrol/regulators/' + $('#regulator-id').val() + '/events'),
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
            
            events.reverse().forEach(function (event, idx) {
                
                var row = $(templateRow.clone()).removeAttr('id');
                
                row.find('.js-event-icon').addClass(event.icon);
                row.find('.js-message').html(event.message);
                row.find('.js-user').text(event.user);
                row.find('.js-timestamp').text(event.timestamp);
                
                body.prepend(row);
                row.flash();
            });
            
            body.find('tr:gt(20)').remove();
            
            setTimeout(_updateRecentEvents, 4000);
            
        });
    };
    
    var mod = {
        
        init : function () {
            
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
                 
                $.ajax(yukon.url('/capcontrol/regulators/' + $('#regulator-id').val()) + '/automap')
                .done(function (result) {
                    
                    debug.debug('Mapping Result', result);
                    
                    yukon.ui.elementGlass.hide('.js-auto-map-dialog');
                    dialog.parent().find('.ui-dialog-buttonpane').find('.ui-button').prop('disabled', false);
                    
                    var tbody = $('.js-auto-map-dialog .js-mappings');
                    var status = result.status.type;
                    var success = result.status.type == 'SUCCESSFUL';
                    
                    $('.js-result-header').show();
                    $('.js-automap-results').show()
                    .find('.js-automap-result').text(result.status.text)
                    .toggleClass('label-danger', status === 'FAILED' )
                    .toggleClass('label-warning', status === 'PARTIALLY_SUCCESSFUL')
                    .toggleClass('label-info', status === 'INCOMPLETE')
                    .toggleClass('label-success', success);
                    
                    result.mappings.forEach(function (mapping) {
                        var td = tbody.find('[data-mapping="' + mapping.type + '"] .js-result');
                        if (mapping.success) {
                            td.html(_templates.successful);
                        } else {
                            td.html(_templates.failed).append(' - ' + mapping.text);
                        }
                    });
                    
                    if (success) {
                        
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
