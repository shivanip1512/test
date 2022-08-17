yukon.namespace('yukon.dashboards.edit');

/**
 * Module for the dashboard edit page.
 * @module yukon.dashboards.edit
 * @requires JQUERY
 * @requires yukon
 */
yukon.dashboards.edit = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {         
            
        updateArrows : function (event, ui) {
            ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
        },
        
        updateLeftRightArrows : function () {
            $('.js-move-left, .js-move-right').prop('disabled', false);
            $('#column1-widgets').find('.js-move-left').prop('disabled', true);
            $('#column2-widgets').find('.js-move-right').prop('disabled', true);
        },
        
        updateEachInput : function (elem, idx, path) {
            $(":input", elem).each(function (inputIndex, input) {
                var name;
                input = $(input);
                
                if (input.is('[name]')) {
                    name = input.attr('name');
                    if (name.indexOf('column') > -1) {
                        //if name contains dashboard remove it - these were newly added widgets
                        name = name.replace('dashboard.', '');
                        var widget = name.split('.')[0];
                        input.attr('name', name.replace(widget, path + '[' + idx + ']'));
                    }

                }
            });
        },
        
        updateIndicies : function () {
            $("#column1-widgets > .select-box-item").each(function (idx, elem) {
                mod.updateEachInput(elem, idx, 'column1Widgets');
            });
            $("#column2-widgets > .select-box-item").each(function (idx, elem) {
                mod.updateEachInput(elem, idx, 'column2Widgets');
            });
        },
    
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            mod.updateLeftRightArrows();
            
            $('#column1-widgets').sortable({
                connectWith: "#column2-widgets",
                stop: function(event, ui) {
                    mod.updateArrows(event, ui);  
                    mod.updateLeftRightArrows();
                    mod.updateIndicies();
                }
            });
            
            $('#column2-widgets').sortable({
                connectWith: "#column1-widgets",        
                stop: function(event, ui) {
                    mod.updateArrows(event, ui);  
                    mod.updateLeftRightArrows();
                    mod.updateIndicies();
                }
            });
            
            $(document).on('click', '.js-with-movables .js-move-left, .js-with-movables .js-move-right', function () {
                
                var btn = $(this),
                    left = btn.is('.js-move-left'),
                    container = btn.closest('.js-with-movables'),
                    dataContainer = container.data('itemSelector'),
                    item = btn.closest(dataContainer);
                
                // Move item left or right
                if (left) {
                    item.appendTo($('#column1-widgets'));
                } else {
                    item.appendTo($('#column2-widgets'));
                }
                
                // Fix buttons.
                $('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                mod.updateLeftRightArrows();
                mod.updateIndicies();

            });
            
            $(document).on('click', '.js-with-movables .js-move-up, .js-with-movables .js-move-down', function () {
                mod.updateIndicies();
            });

                
            
            $(document).on('click', '.js-with-movables .js-remove', function () {
                
                var btn = $(this),
                container = btn.closest('.js-with-movables'),
                dataContainer = container.data('itemSelector'),
                item = btn.closest(dataContainer);
                
                item.remove();
                
                // Fix buttons.
                $('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                mod.updateLeftRightArrows();
                mod.updateIndicies();

            });
            
            $(document).on('click', '.js-show-category', function () {
                var category = $(this).data('category');
                $('.js-search').val("");
                $('.js-no-widgets-found').toggleClass('dn', true);
                $('.js-category-widgets').toggleClass('dn', true);
                $('.js-' + category).toggleClass('dn', false);
                $('.js-show-category').css("font-weight", "normal");
                $('.js-show-all').css("font-weight", "normal");
                $(this).css("font-weight", "bold");
            });
            
            $(document).on('click', '.js-show-all', function () {
                $('.js-search').val("");
                $('.js-no-widgets-found').toggleClass('dn', true);
                $('.js-category-widgets').toggleClass('dn', false);
                $('.js-show-category').css("font-weight", "normal");
                $(this).css("font-weight", "bold");
            });
            
            $(document).on('keyup', '.js-search', function () {
                $('.js-no-widgets-found').toggleClass('dn', true);
                var widgetFound = false;
                var searchValue = $(this).val().toLowerCase();
                $('.js-category-widgets').toggleClass('dn', true);
                $('.js-name, .js-description').each( function() {
                    var widgetValue = $(this).text().toLowerCase();
                    if (widgetValue.indexOf(searchValue) != -1) {
                        $(this).closest('.js-category-widgets').toggleClass('dn', false);
                        widgetFound = true;
                    }
                });
                if (!widgetFound) {
                    $('.js-no-widgets-found').toggleClass('dn', false);
                }
            });
            
            $(document).on('click', '.js-widget-add', function () {
                var dashboardId = $('#dashboardId').val();
                var widgetType = $(this).data('type');
                $.ajax(yukon.url('/dashboards/' + dashboardId + '/addWidget/' + widgetType)).done(function (data) {
                    $('#column1-widgets').prepend(data);
                    // Fix buttons.
                    $('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                    mod.updateLeftRightArrows();
                    mod.updateIndicies();
                    $('[name="column1Widgets[0].type"]').val(widgetType);
                    $('[name="column1Widgets[0].id"]').val(0);
                    $('.js-widget-added-msg-' + widgetType).show().fadeOut(5000);
                });
            });

                        
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.dashboards.edit.init(); });