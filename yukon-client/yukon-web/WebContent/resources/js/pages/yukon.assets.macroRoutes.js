yukon.namespace('yukon.assets.macroRoutes');
 

/** 
 * Module that handles the behavior on the macro routes page.
 * @module yukon.assets.macroRoutes
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.macroRoutes = (function () {
 
    'use strict';

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:macroRoute:delete", function () {
                yukon.ui.blockPage();
                $('#delete-macroRoute-form').submit();
            });
            
            $(document).on("click", ".js-add", function () {
                var picker = yukon.pickers['js-signal-transmitter-picker'],
                    selectedContainer = $('#js-assigned-signal-transmitter-container');
            
                picker.selectedItems.forEach(function (route) {
                    var templateRow = $('.template-row').clone();
                    templateRow.attr('data-id', route.paoId);
                    var children = templateRow.children();
                    templateRow.text(route.paoName).append(children);
                    templateRow.removeClass('dn template-row');
                    templateRow.appendTo(selectedContainer);
                    picker.disableItem(route.paoId);
                });
                selectedContainer.closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
            });

            $(document).on('click', '.js-remove', function () {
                var picker = yukon.pickers['js-signal-transmitter-picker'],
                    selectedId = $(this).parent().attr('data-id');
                $(this).parent().remove();
                $('#js-assigned-signal-transmitter-container').closest('.select-box')
                .find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
            });
            
            $(document).on('click', '#js-save-macro-routes', function (ev) {
                var container = $('#js-assigned-signal-transmitter-container'),
                    routeList = [];
                
                container.find('.select-box-item').each(function (idx, item) {
                    if ($(item).is(':visible')) {
                        var routeObj = new Object();
                        routeObj.routeId = $(item).data('id');
                        routeObj.routeName = $(item).text();
                        routeList.push(routeObj);
                    }
                });
                $('input[name="routeListJsonString"]').val(JSON.stringify(routeList));
                $('#js-macro-route-form').submit();
            });
            
            if ($("#js-unassigned-signal-transmitter-container").exists()) {
                yukon.pickers['js-signal-transmitter-picker'].show();
            }

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { 
    yukon.assets.macroRoutes.init();
});