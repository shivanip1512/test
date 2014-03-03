/**
 * JavaScript for the dropdown menus in Yukon
 * 
 * Tags: dropdown.tag, ajaxDropdown.tag, and criteria.tag
 * 
 * @requires jQuery 1.8.3+
 */
jQuery(function() {
    
    /** Handle clicks on menu triggers */
    jQuery(document).on('click', '.dropdown-container', function(e) {
        
        jQuery('.dropdown-container').removeClass('menu-open');
        
        var target = jQuery(this),
            menu = target.find('.dropdown-menu');
        
        /** Get menu, do the body prepending at this point since the menu may
         * have been ajaxed in. */
        if (menu[0]) { // The menu hasn't been moved to the body yet
            target.data({'menu': menu});
            jQuery('body').prepend(menu); // prepend will move, not clone
        } else { // The menu has been opened once already
            menu = target.data('menu');
        }

        if (menu.is(':visible')) {
            jQuery('ul.dropdown-menu').hide();
            /*
             * We want to propagate the click event if it was on a link.
             * The target may have been an icon inside that link, 
             * so  we can't just check if the target has an href.
             */
            if ( jQuery(e.target).closest('[href]').length !== 0 ) {
                return true;
            }
            return false;
        }

        jQuery('ul.dropdown-menu').hide();
        if (target.closest('.dropdown-container').hasClass('ajax-menu')) {
            ajaxMenuOpen(target, e);
            return false;
        }
        positionDropdownMenu(menu, target);
        return false;
    });

    /**
     * Reposition when showing to ensure the menu follows the element even
     * if the page changed since the last time it was shown.
     */
    function positionDropdownMenu(menu, container) {
        
        var left = jQuery(window).scrollLeft(),
            offset = container.offset(),
            width = menu.width();
        
        container.addClass('menu-open');

        menu.removeAttr('style');
        menu.css({top: offset.top + container.height() + 2, right: (jQuery(window).width() - offset.left - container.width())});

        menu.toggle();

        if (left >= menu.offset().left) {
            // Our menu is spilling off the left side of the screen. 
            // Lets fix this by switching it to instead be positioned to the right
            menu.css({top: offset.top + container.height() + 2, left: offset.left});
        }
    }
    
    /** Retrieve a menu via ajax and open it. */
    function ajaxMenuOpen(target, e) {
        var menu = target.data('menu'),
            params;
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
            target.addClass('menu-open');
        } else {
            target.find('.icon-cog').removeClass('icon-cog').addClass('icon-spinner');
            params = {};
            target.closest('.dropdown-container').prev('.params').find('input').each(function() {
                params[jQuery(this).attr('name')] = jQuery(this).val();
            });
            jQuery.ajax({
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
            });
        }
        return false;
    }
    
    /** Close all menus if clicking somewhere off the menu */
    jQuery(document).click(function(e) {
        /* click was not inside the dropdown menu, hide it. */
        if (jQuery(e.target).closest('.dropdown-menu').length === 0) {
            jQuery('ul.dropdown-menu').hide();
            jQuery('.dropdown-container').removeClass('menu-open');
        }
    });
    
    /** Close all menus when esc key is hit */
    jQuery(document).keyup(function(e) {
        if (e.which == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
            jQuery('.dropdown-container').removeClass('menu-open');
        }
    });
    
    /** Show menu in tables on right click */
    jQuery(document).on('contextmenu', '.has-actions tr', function(event) {
        event.preventDefault();
        jQuery(this).find('.dropdown-container').trigger('click');
    });
    
    /** Handle option selections for criteria buttons */
    jQuery(document).on('click', '.criteria-menu .criteria-option', function(e) {
        
        var option = jQuery(e.target),
            menu = option.closest('.criteria-menu');
        
        updateCriteriaButton(menu);
        
        positionDropdownMenu(menu, menu.data('button').closest('.dropdown-container'));
        return false;
    });
    
    /** Update a criteria button's text */
    function updateCriteriaButton(menu) {
        var button = menu.prev(),
            allOptions = menu.find('.criteria-option input'),
            checkedOptions = allOptions.filter(':checked'),
            buttonText = ''; 
        
        if (allOptions.length === checkedOptions.length) {
            button.find('.criteria-value').text(button.data('allText'));
        } else if (checkedOptions.length === 0) {
            button.find('.criteria-value').text(button.data('noneText'));
        } else {
            checkedOptions.each(function (idx, item) {
                buttonText += jQuery(item.parentElement).text();
                if (idx != checkedOptions.length - 1) {
                    buttonText += ', '; 
                }
            });
            button.find('.criteria-value').text(buttonText);
        }
    }
    
    /** Update criteria buttons on page load */
    jQuery('.dropdown-container').each(function(idx, container) {
        var container = jQuery(container),
            menu = container.find('.dropdown-menu');
        
        if (menu.is('.criteria-menu')) {
            updateCriteriaButton(menu);
        }
    });
});