yukon.namespace('yukon.da.cbc');

/**
 * Module for the volt/var cbc page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.cbc = (function () {

    'use strict';

    /**
     * @type Array.string
     *
     * Filled out in yukon.da.cbc.init().
     * Array of strings of paoTypes that represent 2 way cbcs
     */
    var twoWayTypes = [],
        logicalTypes = [];

    var updatePaoTypeFields = function () {
        var paoType = $('#pao-type').val(),
            isTwoWay = twoWayTypes.indexOf(paoType) !== -1,
            isLogical = logicalTypes.indexOf(paoType) !== -1;

        $('.js-two-way').toggleClass('dn', !isTwoWay);
        $('.js-one-way').toggleClass('dn', isTwoWay || isLogical);
        $('.js-logical').toggleClass('dn', !isLogical);
        
        updateCommPortFields();
    };

    /**
     * @type Array.number
     *
     * Filled out in yukon.da.cbc.init().
     * Array of ids of comm ports that use tcp fields
     */
    var tcpCommPorts = [];
    var updateCommPortFields = function () {
        var commPort = +$('#comm-port').val();
        var isTcp = tcpCommPorts.indexOf(commPort) !== -1;
        $('[data-tcp-port]').toggleClass('dn', !isTcp);
    };

    var mod = {

        /** Initialize this module. */
        init: function () {

            twoWayTypes = yukon.fromJson('#two-way-types');
            logicalTypes = yukon.fromJson('#logical-types');
            tcpCommPorts = yukon.fromJson('#tcp-comm-ports');

            $(document).on('yukon:da:cbc:delete', function () {
                $('#delete-cbc').submit();
            });
            $(document).on('yukon:da:cbc:copy', function () {
                $('#copy-cbc').find('form').submit();
            });

            $('#pao-type').on('change', updatePaoTypeFields);
            updatePaoTypeFields();

            $('#comm-port').on('change', updateCommPortFields);
            updateCommPortFields();

            $('#dnp-config').on('change', function () {
                var configId = $(this).val();
                if (configId) {
                    var url = yukon.url('/deviceConfiguration/' + configId);

                    var dnpFields = $('.js-dnp-fields');
                    var heartbeatFields = $('.js-heartbeat-fields');
                    yukon.ui.block(dnpFields, 200);
                    yukon.ui.block(heartbeatFields, 200);
                    $.get(url)
                    .done(function (data) {
                        if(data.deviceConfiguration.dnpCategory != null) {
                            data.deviceConfiguration.dnpCategory.deviceConfigurationItems.forEach(function (field) {
                                var fieldName = field.fieldName;
                                var value = field.value;
                                if(fieldName == 'timeOffset') {
                                    value = data.timeOffsetValue;
                                }
                                dnpFields.find('.js-dnp-' + fieldName).text(value);
                            });
                        }
                        if(data.deviceConfiguration.heartbeatCategory != null) {
                            $('.js-heartbeat-field').removeClass('dn');
                            data.deviceConfiguration.heartbeatCategory.deviceConfigurationItems.forEach(function (field) {
                                var fieldName = field.fieldName;
                                var value = field.value;
                                if (fieldName == 'cbcHeartbeatMode') {
                                    value = data.heartbeatModeValue;
                                    $('.js-heartbeatMode-field').toggleClass('dn', field.value == 'DISABLED');
                                }
                                heartbeatFields.find('.js-heartbeat-' + fieldName).text(value);
                            });
                        }
                    }).always(function () {
                        yukon.ui.unblock(dnpFields);
                        yukon.ui.unblock(heartbeatFields);
                    });
                } else {
                    $('.js-heartbeat-field').addClass('dn');
                }

            });
        }
    };

    return mod;
})();

$(function () { yukon.da.cbc.init(); });