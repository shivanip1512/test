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
    var twoWayTypes = [];

    var updatePaoTypeFields = function () {
        var paoType = $('#pao-type').val();
        var isTwoWay = twoWayTypes.indexOf(paoType) !== -1;

        $('.js-two-way').toggleClass('dn', !isTwoWay);
        $('.js-one-way').toggleClass('dn', isTwoWay);
        
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
                var url = yukon.url('/deviceConfiguration/' + configId);

                var dnpFields = $('.js-dnp-fields');
                yukon.ui.block(dnpFields, 200);
                $.get(url)
                .done(function (data) {
                    if(data.deviceConfiguration.dnpCategory != null) {
                        data.deviceConfiguration.dnpCategory.deviceConfigurationItems.forEach(function (field) {
                            var fieldName = field.fieldName;
                            var value = field.value;
                            if(fieldName == 'timeOffset'){
                            	value = data.timeOffsetValue;
                            }
                            dnpFields.find('.js-dnp-' + fieldName).text(value);
                        });
                    }
                }).always(function () {
                    yukon.ui.unblock(dnpFields);
                });
            });
        }
    };

    return mod;
})();

function getKeyValue(key){
	
 	var res = '';
	 $.ajax({
        url: yukon.url('/capcontrol/cbc/getKeyValue/' + key),
        async: false
    }).done(function (data) {
       alert(data);
       res = data;
    });

	 return res;
	/*  var url = yukon.url('/capcontrol/cbc/getKeyValue/' + key);
	  var res = "";
	  $.get(url).done(function (data) {
		  alert(data);
		  return data;
		  res = data;
	  });*/
}
$(function () { yukon.da.cbc.init(); });