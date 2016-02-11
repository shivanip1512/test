yukon.namespace('yukon.da.feeder');

/**
 * Module for the volt/var feeder page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.feeder = (function () {
    
    assignCapBank = function (event, ui) {
        var selectedItem = ui.item;
        var button = selectedItem.find('.js-add-capbank');
        assignAndUnassignCapBanks(button, true);
    },
    
    unAssignCapBank = function (event, ui) {
        var selectedItem = ui.item;
        var button = selectedItem.find('.js-remove-capbank');
        assignAndUnassignCapBanks(button, true);
    },
    
    updateArrows = function (event, ui) {
        ui.item
        .closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
    },
    
    assignAndUnassignCapBanks = function (btn, skipAppend) {
        var remove = btn.is('.js-remove-capbank'),
        container = btn.closest('.select-box').find(remove ? '.select-box-available' : '.select-box-selected'),
        tripContainer = btn.closest('.select-box').find('.select-box-trip-order'),
        closeContainer = btn.closest('.select-box').find('.select-box-close-order'),
        item = btn.closest('.select-box-item'),
        dataId = item.attr('data-id'),
        tripItem = item.clone(),
        closeItem = item.clone();

        // Move item to unassigned/assigned only if item was not dragged
        if (!skipAppend) {
            item.remove();
            item.appendTo(container);
        }
        
        //update buttons
        item
        .find('.js-remove-capbank, .js-add-capbank').toggleClass('js-remove-capbank js-add-capbank')
        .find('.icon').toggleClass('icon-plus-green icon-cross');
        
        // Insert or remove item from trip order and close order
        if (remove) {
            tripContainer.find('.select-box-item[data-id=' + dataId + ']').remove();
            closeContainer.find('.select-box-item[data-id=' + dataId + ']').remove();
        } else {
            tripItem
            .css('padding-left', '10px')
            .appendTo(tripContainer)
            .find('.js-add-capbank').remove();
            
            closeItem
            .css('padding-left', '10px')
            .appendTo(closeContainer)
            .find('.js-add-capbank').remove()
        }
        
        // Show/hide movers.
        item.find('.select-box-item-movers').toggle(!remove);
        tripItem.find('.select-box-item-movers').toggle(!remove);
        closeItem.find('.select-box-item-movers').toggle(!remove);
        
        // Tell yukon's ordered list handler to update the mover buttons.
        container.closest('.select-box')
        .find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
    },


    'use strict';
    var initialized = false,

    mod = {

        /** Initialize this module. */
        init: function () {

            if (initialized) return;

            /** Assign/unassign item */
            $(document).on('click', '.select-box .js-add-capbank, .select-box .js-remove-capbank', function () {
                var btn = $(this);
                assignAndUnassignCapBanks(btn, false);
            });

            /** User clicked save on stations assignment dialog. */
            $(document).on('yukon:vv:children:save', function (ev) {

                var container = $(ev.target),
                    parentId = container.data('parentId'),
                    children = [],
                    tripOrder = [],
                    closeOrder = [];

                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    children.push($(item).data('id'));
                });
                container.find('.select-box-trip-order .select-box-item').each(function (idx, item) {
                    tripOrder.push($(item).data('id'));
                });
                container.find('.select-box-close-order .select-box-item').each(function (idx, item) {
                    closeOrder.push($(item).data('id'));
                });

                $.ajax({
                    url: yukon.url('/capcontrol/feeders/' + parentId + '/capbanks'),
                    method: 'post',
                    data: { children: children, tripOrder: tripOrder, closeOrder: closeOrder}
                }).done(function () {
                    window.location.reload();
                });

            });
            
            /** User confirmed intent to delete feeder. */
            $(document).on('yukon:da:feeder:delete', function () {
                $('#delete-feeder').submit();
            });
            

            // TODO Move these someplace more common or programatically loaded
            /** User changed the season schedule, load season for schedule. */
            $(document).on('change', '.js-season-schedule-select', function () {

                var scheduleId = $(this).val();

                $.ajax({
                    url: yukon.url('/capcontrol/strategy-assignment/schedule/' + scheduleId + '/seasons')
                }).done(function (seasons) {
                    $('.js-seasons-table tbody').html(seasons);
                });
            });

            /** User changed the holiday schedule, hide holiday strat if 'No Holiday' is chosen. */
            $(document).on('change', '.js-holiday-schedule-select', function () {
                var scheduleId = $(this).val();
                $('.js-holiday-strat').toggleClass('dn', +scheduleId === -1);
            });

            // TODO Move these someplace more common or programatically loaded
            /** User clicked save on edit info dialog. */
            $(document).on('yukon:vv:strategy-assignment:save', function (ev) {

                var dialog = $('.js-edit-strat-popup');

                $('.js-edit-strat-popup form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });

        }
    };

    return mod;
})();

$(function () { yukon.da.feeder.init(); });