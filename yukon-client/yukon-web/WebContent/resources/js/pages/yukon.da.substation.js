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
            
            /** User clicked volt reduction toggle button; show hide point picker. */
            $(document).on('change', '.js-volt-reduct', function () {
                
                var toggle = $(this),
                    row = toggle.closest('tr'),
                    picker,
                    btn = row.find('.js-picker-btn'),
                    active = row.find('.switch-btn-checkbox').prop('checked');
                
                btn.toggleClass('dn', !active);
                picker = yukon.pickers[btn.data('pickerId')];

                if (!active) {
                    picker.clearSelected();
                    picker.clearEntireSelection();
                } else {
                    picker.show();
                }
            });
        }
    };

    return mod;
})();

$(function () { yukon.da.substation.init(); });