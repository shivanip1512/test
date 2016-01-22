yukon.namespace('yukon.da.feeder');

/**
 * Module for the volt/var feeder page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.feeder = (function () {

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
                    url: yukon.url('/capcontrol/feeders/' + parentId + '/capbanks'),
                    method: 'post',
                    data: { children: children}
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