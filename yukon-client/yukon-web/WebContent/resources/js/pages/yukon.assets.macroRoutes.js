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
            
                picker.selectedItems.forEach(function (signalTransmitter) {
                    var templateRow = $('.template-row').clone();
                    templateRow.attr('data-id', signalTransmitter.paoId);
                    var children = templateRow.children();
                    templateRow.text(signalTransmitter.paoName + " - " + signalTransmitter.paoId).append(children);
                    templateRow.removeClass('dn template-row');
                    templateRow.appendTo(selectedContainer);
                    picker.disableItem(signalTransmitter.paoId);
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
            	debugger;
                var container = $('#js-assigned-signal-transmitter-container'),
                    selectedPoints = [],
                    macroRouteModel = new Object();
                
                macroRouteModel.routeList = [];
                
                container.find('.select-box-item').each(function (idx, item) {
                    if ($(item).is(':visible')) {
                        var routeObj = new Object();
                        routeObj.routeId = $(item).data('id');
                        macroRouteModel.routeList.push(routeObj);
//                        selectedPoints.push($(item).data('id'));
                    }
                });
                
//                $('#selectedPoints').val(selectedPoints.join(','));
                console.log(macroRouteModel);
                $('#js-macro-route-form').submit();
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { 
    yukon.assets.macroRoutes.init();
});