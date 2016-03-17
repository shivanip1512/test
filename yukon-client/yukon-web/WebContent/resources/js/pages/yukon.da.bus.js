yukon.namespace('yukon.da.bus');

/**
 * Module for the volt/var bus page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.bus = (function () {

    'use strict';
    var initialized = false,

    mod = {

        /** Initialize this module. */
        init: function () {

            if (initialized) return;

            /** User clicked save on stations assignment dialog. */
            $(document).on('yukon:vv:children:save', function (ev) {

                var container = $(ev.target),
                    parentId = container.data('parentId'),
                    children = [];

                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    children.push($(item).data('id'));
                });

                $.ajax({
                    url: yukon.url('/capcontrol/buses/' + parentId + '/feeders'),
                    method: 'post',
                    data: { children: children}
                }).done(function () {
                    window.location.reload();
                });

            });
            
            $(document).on('yukon:vv:schedule:save', function (ev) {

                var dialog = $('.js-edit-sched-popup');

                $('.js-edit-sched-popup form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });
            
            /** User confirmed intent to delete bus. */
            $(document).on('yukon:da:bus:delete', function () {
                $('#delete-bus').submit();
            });
            
            /** User clicked Enable Dual Bus toggle button; clear out selected alternate sub bus. */
            $(document).on('click', '.js-dual-bus', function () {
                
                var toggle = $(this),
                    enableDualBusRow = toggle.closest('tr'),
                    active = enableDualBusRow.find('.switch-btn-checkbox').prop('checked');

                if (!active)
                    yukon.pickers['altBusPicker'].removeEvent();

            });
            
            /** User clicked User Per Phase; clear out phase b and phase c points. */
            $(document).on('click', '.js-per-phase', function () {
                
                var toggle = $(this),
                    perPhaseRow = toggle.closest('tr'),
                    active = perPhaseRow.find('.switch-btn-checkbox').prop('checked');

                if (!active) {
                    yukon.pickers['phaseBPointPicker'].removeEvent();
                    yukon.pickers['phaseCPointPicker'].removeEvent();
                }

            });
        }
    };

    return mod;
})();

$(function () { yukon.da.bus.init(); });