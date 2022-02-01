yukon.namespace('yukon.admin.substations');

/**
 * Module that manages the substations and route mapping on the substations page under Admin menu.
 * @module yukon.admin.substations
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
yukon.admin.substations = (function () {

    assignRoute = function (event, ui) {
        var selectedItem = ui.item;
        var button = selectedItem.find('.js-add-route');
        assignAndUnassignRoute(button, true);
    },
    
    unAssignRoute = function (event, ui) {
        var selectedItem = ui.item;
        var button = selectedItem.find('.js-remove-route');
        assignAndUnassignRoute(button, true);
    },
    
    updateArrows = function (event, ui) {
        ui.item
        .closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
    },
    
    assignAndUnassignRoute = function (btn, skipAppend) {
        var remove = btn.is('.js-remove-route'),
        container = btn.closest('.select-box').find(remove ? '.select-box-available' : '.select-box-selected'),
        item = btn.closest('.select-box-item'),
        dataId = item.attr('data-id');

        // Move item to avaliable/assigned only if item was not dragged
        if (!skipAppend) {
            item.remove();
            item.appendTo(container);
        }
        
        //update buttons
        item.find('.js-remove-route, .js-add-route')
            .toggleClass('js-remove-route js-add-route')
            .find('.icon').toggleClass('icon-plus-green icon-cross');
        
        item.find('.icon-plus-green').html(yg.iconSvg.iconPlusGreen);
        item.find('.icon-cross').html(yg.iconSvg.iconCross);
        
        // Show/hide movers.
        item.find('.select-box-item-movers').toggle(!remove);
        
        // Tell yukon's ordered list handler to update the mover buttons.
        container.closest('.select-box')
                 .find('.js-with-movables')
                 .trigger('yukon:ordered-selection:added-removed');
    },
    
    _loadSubstationRouteMappingPage = function () {
        document.substationForm.action = yukon.url("/admin/substations/routeMapping/view");
        document.substationForm.submit();
    };
    
    var _initialized = false,
        removedRouteIds=[],
        mod = {
                /**
                 * Initializes the module, hooking up event handlers to components.
                 * Depends on localized text in the jsp, so only run after DOM is ready.
                 */
                init: function () {
                    /** Assign/unassign Route */
                    $(document).on('click', '.select-box .js-add-route, .select-box .js-remove-route', function () {
                        var btn = $(this);
                        assignAndUnassignRoute(btn, false);
                    });

                    $('#unassigned').sortable({
                        connectWith: "#assigned",
                        remove: function(event, ui) {
                            assignRoute(event, ui);
                        }
                    });

                    $('#assigned').sortable({
                        connectWith: "#unassigned",
                        stop: function(event, ui) {
                            updateArrows(event, ui);  
                        },
                        remove: function(event, ui) {
                            unAssignRoute(event, ui);
                        }
                    }).disableSelection();

                    $('#saveAllRoutes').click(function (ev) {
                        
                        $('#assigned > div.select-box-item').each(function() {
                            var routeId = $(this).attr("data-id");
                            var route = $('<option/>').attr('selected', 'selected')
                                                      .val(routeId);
                            $("#selectedRoutes").append(route);
                        });
                        
                        document.substationForm.action = yukon.url("/admin/substations/routeMapping/save");
                        document.substationForm.submit();
                        
                    });
                    
                    $(document).on('change', '#substation', function () {
                        _loadSubstationRouteMappingPage();
                    });
                    
                    $(document).on('click', '#cancel', function () {
                        _loadSubstationRouteMappingPage();
                    });
                    
                    if (_initialized) return;
                    _initialized = true;
                }
            };
        return mod;
}());

$(function () { yukon.admin.substations.init(); });