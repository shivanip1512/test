/**
 * JavaScript for the dropdown.tag, ajaxDropdown.tag, and criteria.tag
 */
jQuery(function() {
    jQuery(document).on("click", ".dropdown-container", function(e) {
        var target,
            menu;
        jQuery(".dropdown-container").removeClass("menu-open");
        target = jQuery(this);

        menu = target.find('.dropdown-menu').eq(0);

        if (menu.is(":visible")) {
            jQuery("ul.dropdown-menu").hide();
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

        jQuery("ul.dropdown-menu").hide();
        if (target.closest(".dropdown-container").hasClass("ajax-menu")) {
            ajaxMenuOpen(target, e);
            return false;
        }
        positionDropdownMenu(menu, target);
        return false;
    });

    function positionDropdownMenu(menu, dropdown_container) {
        var left,
            menu_offset,
            width;
        dropdown_container.addClass("menu-open");

        //reposition when showing to ensure the menu follows the element even
        //if the page changed since the last time it was shown
        menu.removeAttr("style");
        var men_opts = {top: dropdown_container.height() + 7, left: dropdown_container.width() - menu.width() + 6};
        menu.css(men_opts);

        menu.toggle();

        left = jQuery(window).scrollLeft();
        menu_offset = menu.offset();
        width = menu.width();
        if (left >= menu_offset.left) {
            // our menu is spilling off the left side of the screen. Lets fix this by switching it to instead be positioned to the right
            menu.css({top: dropdown_container.height() + 7, left: 0});
            menu.removeClass("menu-left");
            menu.addClass("menu-right");
        } else {
            menu.removeClass("menu-right");
            menu.addClass("menu-left");
        }
    }
    
    function ajaxMenuOpen(target, e) {
        var menu = target.find('.dropdown-menu').eq(0),
            params;
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
            target.addClass("menu-open");
        } else {
            target.find(".icon-cog").removeClass("icon-cog").addClass("icon-spinner");
            params = {};
            target.closest(".dropdown-container").prev(".params").find("input").each(function() {
                params[jQuery(this).attr("name")] = jQuery(this).val();
            });
            jQuery.ajax({
                url: '/contextualMenu/list',
                type: 'GET',
                data: params,
            }).done( function(data) {
                var items = [],
                i,
                list_item;
            for (i=0; i<data.length; i++) {
                if (data[i].divider === true) {
                    list_item = '<li class="divider"></li>';
                } else {
                    list_item = "<li><a href='" + data[i].url + "'>" + data[i].text + "</a></li>";
                }
                items.push(list_item);
            }
            target.data({'menu_items': true});
            menu.append(items.join(''));
            target.find(".icon-spinner").removeClass("icon-spinner").addClass("icon-cog");
            positionDropdownMenu(menu, target);
            });
        }
        return false;
    }
    
    /* close on document click */
    jQuery(document).click(function(e) {
        /* click was not inside the dropdown menu, hide it. */
        if (jQuery(e.target).closest('.dropdown-menu').length === 0) {
            jQuery('ul.dropdown-menu').hide();
            jQuery(".dropdown-container").removeClass("menu-open");
        }
    });
    
    /* update text for criteria buttons */
    /* listening on the inner checkbox input since labels cause two click events */
    jQuery(document).on("click", ".criteria-menu .criteria-option input", function(e) {
        
        var option = jQuery(e.target);
        var menu = option.closest('.criteria-menu');
        
        updateCriteriaButton(menu);
        
        positionDropdownMenu(menu, menu.data('button').closest('.dropdown-container'));
    });
    
    function updateCriteriaButton(menu) {
        var button = menu.data('button');
        var allOptions = menu.find('.criteria-option input');
        var checkedOptions = allOptions.filter(':checked');
        var buttonText = ''; 
        
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
    
    /* close on escape key */
    jQuery(document).keyup(function(e) {
        if (e.which == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
            jQuery(".dropdown-container").removeClass("menu-open");
        }
    });
    
    jQuery('.dropdown-container').each(function(idx, container) {
        container = jQuery(container);
        var menu = container.find('.dropdown-menu');

        if (menu.is('.criteria-menu')) {
            updateCriteriaButton(menu);
        }
    });
});