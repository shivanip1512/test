yukon.namespace('yukon.da.regulator');

/**
 * Singleton that manages the regulator edit page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.regulator = (function () {

    var _showHideMappings = function (mappings) {
        var table = $(document).find('.js-mappings-table');
        var i, length;
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);
        for (i = 0, length = mappings.length; i < length; i += 1) {
            table.find('[data-mapping="' + mappings[i] + '"]').show().find('input').prop('disabled', false);
        }
    },

    _paoTypeMap = [],

    _getMappings = function () {
        var paoType = $('#regulator-type').val();
        return _paoTypeMap[paoType];
    };

    var mod = {

            init : function () {
                _paoTypeMap = $('[data-pao-type-map]').data('paoTypeMap');

                _showHideMappings(_getMappings());

                $('#regulator-type').on('change', function () {
                    _showHideMappings(_getMappings());
                });

                $(document).on('yukon:da:regulator:delete', function () {
                    $('#delete-regulator').submit();
                });
            }
    };
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
