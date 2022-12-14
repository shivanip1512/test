yukon.namespace('yukon.da.substation');

/**
 * Module for the volt/var substation page.
 * @module yukon.da.substation
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.substation = (function () {

    'use strict';
    var initialized = false,

    mod = {

        /** Initialize this module. */
        init: function () {

            if (initialized) return;

            /** User clicked save on substation bus assignment dialog. */
            $(document).on('yukon:vv:children:save', function (ev) {

                var container = $(ev.target),
                    parentId = container.data('parentId'),
                    children = [];

                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    children.push($(item).data('id'));
                });

                $.ajax({
                    url: yukon.url('/capcontrol/substations/' + parentId + '/buses'),
                    method: 'post',
                    data: { children: children}
                }).done(function () {
                    window.location.reload();
                });

            });
            
            /** User confirmed intent to delete substation. */
            $(document).on('yukon:da:substation:delete', function () {
                $('#delete-substation').submit();
            });
        }
    };

    return mod;
})();

$(function () { yukon.da.substation.init(); });