yukon.namespace('yukon.da.cbc');

/**
 * Module for the volt/var cbc page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.cbc = (function () {

    'use strict';

    var mod = {

        /** Initialize this module. */
        init: function () {

            var twoWayTypes = yukon.fromJson('#two-way-types');

            $('#pao-type').on('change', function () {
                var paoType = $(this).val();
                var twoWay = twoWayTypes.indexOf(paoType) !== -1;

                $('.js-two-way').toggleClass('dn', !twoWay);
                $('.js-one-way').toggleClass('dn', twoWay);
            });

            $(document).on('yukon:da:cbc:delete', function () {
                $('#delete-cbc').submit();
            });
            $(document).on('yukon:da:cbc:copy', function () {
                $('#copy-cbc').find('form').submit();
            });

            $('#dnp-config').on('change', function () {
                var configId = $(this).val();
                var url = yukon.url('/deviceConfiguration/' + configId);

                var dnpFields = $('.js-dnp-fields');
                yukon.ui.block(dnpFields, 200);
                $.get(url)
                .done(function (data) {
                    data.dnpCategory.deviceConfigurationItems.forEach(function (field) {
                        var fieldName = field.fieldName;
                        var value = field.value;
                        dnpFields.find('.js-dnp-' + fieldName).text(value);
                    });
                }).always(function () {
                    yukon.ui.unblock(dnpFields);
                });
            });
        }
    };

    return mod;
})();

$(function () { yukon.da.cbc.init(); });