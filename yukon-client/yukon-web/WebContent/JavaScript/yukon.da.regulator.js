yukon.namespace('yukon.da.regulator');

/**
 * Module that manages the regulator edit page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.regulator = (function () {

    /**
     * Different regulator types (LTC, CL7) have different attributes.
     * this method sets up the attributes table to display only the supported attributes
     * and hides and disabled all the other inputs.
     */
    var _showHideMappings = function (mappings) {

        var paoType = $('#regulator-type').val(),
            mappings = _paoTypeMap[paoType],
            table = $(document).find('.js-mappings-table');
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);

        for (var i = 0, length = mappings.length; i < length; i += 1) {
            table.find('[data-mapping="' + mappings[i] + '"]').show().find('input').prop('disabled', false);
        }
    },

    /**
     * Object mapping PaoTypes to supported attributes. ex:
     * 
     *  { PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    _paoTypeMap = {};

    var mod = {

            init : function () {
                _paoTypeMap = $('[data-pao-type-map]').data('paoTypeMap');

                _showHideMappings();

                $('#regulator-type').on('change', function () {
                    _showHideMappings();
                });

                $(document).on('yukon:da:regulator:delete', function () {
                    $('#delete-regulator').submit();
                });
            }
    };
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
