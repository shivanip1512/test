yukon.namespace('yukon.ui.movables');

/**
 * Module to manage the buttons in ordered lists of things.
 * 
 * Containers are expected to have a class of 'js-with-movables'.
 * Items in the list are expected to have buttons with classes of 'js-move-up' and 'js-move-down'.
 * This module handles the moving of items when these buttons are clicked.
 * When items are added or removed from the container, the container should fire the
 * 'yukon:ordered-selection:added-removed' event.  This module will fix the button's
 * disable states on that event.
 * 
 * @module yukon.ui.movables
 * @requires JQUERY
 * @requires yukon
 */
yukon.ui.movables = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** Fix the move up/down buttons so the top row's move up and 
     *  bottom row's move down buttons are disabled. */
    _fixMovers = function (items) {
        items.find('.js-move-up, .js-move-down').prop('disabled', false);
        items.eq(0).find('.js-move-up').prop('disabled', true);
        items.eq(items.length - 1).find('.js-move-down').prop('disabled', true);
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /**
             * Move elements up or down in a container.
             * Movable elements are expected to contain a button to move up and one to move down.
             * This handler will move the element up or down and adjust any button disable states
             * entire list.
             * 
             * The container is expected to have a '[data-item-selector]' attribute whose value
             * will be used the find all the movable items in the container. 
             */
            $(document).on('click', '.js-with-movables .js-move-up, .js-with-movables .js-move-down', function () {
                
                var btn = $(this),
                    up = btn.is('.js-move-up'),
                    container = btn.closest('.js-with-movables'),
                    itemSelector = container.data('itemSelector'),
                    item = btn.closest(itemSelector),
                    neighbor = up ? item.prev() : item.next();
                
                // Move item up or down.
                if (up) {
                    item.insertBefore(neighbor);
                } else {
                    item.insertAfter(neighbor);
                }
                
                // Fix buttons.
                _fixMovers(container.find(itemSelector));
                
            });
            
            /**
             * Adjust elements in a container with movers when items are added/removed from the list.
             * 
             * The container is expected to have a '[data-item-selector]' attribute whose value
             * will be used the find all the movable items in the container. 
             */
            $(document).on('yukon:ordered-selection:added-removed', '.js-with-movables', function () {
                
                var container = $(this),
                    itemSelector = container.data('itemSelector');
                
                // Fix buttons.
                _fixMovers(container.find(itemSelector));
                
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.movables.init(); });