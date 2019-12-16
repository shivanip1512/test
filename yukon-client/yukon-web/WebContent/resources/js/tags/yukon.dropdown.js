/**
 * JavaScript for the dropdown menus in Yukon.
 * @requires JQUERY
 */
$(function() {
    
    /** Handle clicks on menu triggers */
    $(document).on('click', '.dropdown-trigger', function(ev) {
        var trigger = $(this),
        menu = trigger.find('.dropdown-menu');
        
        $('.dropdown-trigger').removeClass('menu-open');
        
        /** Get menu, do the body prepending at this point since the menu may
         * have been ajaxed in. */
        if (menu[0]) { // The menu hasn't been moved to the body yet
            trigger.data({'menu': menu});
            //check if in full screen mode
            if ((document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement) && $(this).closest('div.ol-viewport').length === 1) {
                $(this).closest('div.ol-viewport').prepend(menu); // prepend will move, not clone
            } else {
                $('body').prepend(menu); // prepend will move, not clone
            }
            
            menu.data('trigger', trigger);
        } else { // The menu has been opened once already
            menu = trigger.data('menu');
        }

        if (menu.is(':visible')) {
            $('ul.dropdown-menu').hide();
            /*
             * We want to propagate the click event if it was on a link.
             * The trigger may have been an icon inside that link, 
             * so  we can't just check if the trigger has an href.
             */
            if ( $(ev.target).closest('[href]').length !== 0 ) {
                return true;
            }
            return false;
        }

        $('ul.dropdown-menu').hide();
        if (trigger.closest('.dropdown-trigger').hasClass('ajax-menu')) {
            ajaxMenuOpen(trigger, ev);
            return false;
        }
        positionDropdownMenu(menu, trigger);
        return false;
    });

    /** Reposition when showing to ensure the menu follows the element even
     *  if the page changed since the last time it was shown.
     *  @param {Object} menu - Menu item to be positioned.
     *  @param {Object} container - element containing the dropdown menu
     */
    function positionDropdownMenu(menu, container) {
        
        var left = $(window).scrollLeft(),
            offset = container.offset(),
            width = menu.width();
        
        container.addClass('menu-open');

        menu.removeAttr('style');
        if(menu.closest('div.ol-viewport').length === 1) {
            var mapOffset = menu.closest('div.ol-viewport').offset().top;
            menu.css({top: offset.top - mapOffset + container.height() + 2, right: ($(window).width() - offset.left - container.width())});
        } else {
            menu.css({top: offset.top + container.height() + 2, right: ($(window).width() - offset.left - container.width())});
        }
        

        menu.toggle();

        if (left >= menu.offset().left) {
            // Our menu is spilling off the left side of the screen. 
            // Lets fix this by switching it to instead be positioned to the right
            menu.css({top: offset.top + container.height() + 2, left: offset.left});
        }
    }
    
    /** Retrieve a menu via ajax and open it. 
     *  @param {Object} target - element containing the drodown menu    
     *  @param {Object} event - jquery event object. 
     * */
    function ajaxMenuOpen(target, e) {
        var menu = target.data('menu'),
            params;
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
            target.addClass('menu-open');
        } else {
            target.find('.icon-cog').removeClass('icon-cog').addClass('icon-spinner');
            params = {};
            target.closest('.dropdown-trigger').prev('.params').find('input').each(function() {
                params[$(this).attr('name')] = $(this).val();
            });
            $.ajax({
                url: yukon.url('/contextualMenu/list'),
                type: 'GET',
                data: params
            }).done( function(data) {
                var items = [],
                    i,
                    list_item;
                for (i=0; i<data.length; i++) {
                    if (data[i].divider === true) {
                        list_item = '<li class="divider"></li>';
                    } else {
                        list_item = '<li><a href="' + data[i].url + '">' + data[i].text + '</a></li>';
                    }
                    items.push(list_item);
                }
                target.data({'menu_items': true});
                menu.append(items.join(''));
                target.find('.icon-spinner').removeClass('icon-spinner').addClass('icon-cog');
                positionDropdownMenu(menu, target);
            }).fail(function(data){
                target.find('.icon-cog').removeClass('icon-cog').removeClass('icon-spinner');
                yukon.ui.alertError(yg.text.ajaxError);
            });
        }
        return false;
    }
    
    /** Close all menus on click except when clicking a criteria menu option */
    $(document).click(function(e) {
        if ($(e.target).closest('.criteria-menu').length === 0) {
            /* click was not inside a criteria menu, hide all menus. */
            $('ul.dropdown-menu').hide();
            $('.dropdown-trigger').removeClass('menu-open');
        }
    });
    
    /** Close all menus when esc key is hit */
    $(document).keyup(function(e) {
        if (e.which == 27) { // esc
            $('ul.dropdown-menu').hide();
            $('.dropdown-trigger').removeClass('menu-open');
        }
    });
    
    /** Show menu in tables on right click */
    $(document).on('contextmenu', '.has-actions tr', function(event) {
        var clickedElement = event.target,
            parentElement = clickedElement.parentElement;
        //only display the cog menu if user has not clicked on a link
        if (clickedElement.nodeName.toLowerCase() != "a" && parentElement.nodeName.toLowerCase() != "a") {
            event.preventDefault();
            var trigger = $(this).find('.dropdown-trigger');
            var menu = trigger.find('.dropdown-menu');
            
            if (menu.length == 0)
                menu = trigger.data('menu');

            trigger.trigger('click');
            
            menu.removeAttr('style');
            menu.css({top:event.pageY, left:event.pageX});
            menu.toggle();
        }
        
    });
    
    /** Handle option selections for criteria buttons */
    $(document).on('change', '.criteria-menu .criteria-option', function(e) {
        
        var menu = $(this).closest('.criteria-menu'),
            checkbox = $(this).find(':checkbox'),
            targetElem = $(e.target);
        
        if (!targetElem.is(':checkbox')) {
            checkbox.prop('checked', !checkbox.prop('checked'));
        }
        updateCriteriaButton(menu);
        positionDropdownMenu(menu, menu.data('trigger'));
        
        // propagate click event.
        return true;
    });
    
    /** Update a criteria button's text
     *  @param {Object} menu - menu option.
     */
    function updateCriteriaButton(menu) {
        
        var trigger = $(menu.data('trigger')),
            allOptions = menu.find('.criteria-option input'),
            checkedOptions = allOptions.filter(':checked'),
            buttonText = '',
            button;
        
        if (!trigger.length) {
            // The menu hasn't been opened yet
            button =  menu.closest('.dropdown-trigger').find('.criteria-button');
        } else {
            // The menu has already been opened once
            button = trigger.find('.criteria-button');
        }
        
        if (allOptions.length === checkedOptions.length) {
            button.find('.criteria-value').text(button.data('allText'));
        } else if (checkedOptions.length === 0) {
            button.find('.criteria-value').text(button.data('noneText'));
        } else {
            checkedOptions.each(function (idx, item) {
                buttonText += $(item.parentElement).text();
                if (idx != checkedOptions.length - 1) {
                    buttonText += ', '; 
                }
            });
            button.find('.criteria-value').text(buttonText);
        }
    }
    
    /** Update criteria buttons on page load */
    $('.dropdown-trigger').each(function(idx, container) {
        var container = $(container),
            menu = container.find('.dropdown-menu');
        
        if (menu.is('.criteria-menu')) {
            updateCriteriaButton(menu);
        }
    });
});