/**
 * JavaScript for the dropdown.tag, ajaxDropdown.tag, and criteria.tag
 */
jQuery(function() {
    jQuery(document).on("click", ".dropdown-container", function(e) {
        var target,
            menu;
        jQuery(".dropdown-container").removeClass("menu-open");
        target = jQuery(this);
        menu = target.find("ul.dropdown-menu");
        
        //register menu
        if (menu[0]) {
            target.data({'menu': menu});
            jQuery('body').prepend(menu);
            menu.data('button', target.find('.criteria-button'));
        } else {
            menu = target.data('menu');
        }

        if (menu.is(":visible")) {
            jQuery("ul.dropdown-menu").hide();
            e.stopPropagation();
            return false;
        }

        jQuery("ul.dropdown-menu").hide();
        if (target.closest(".f-dropdown_outer_container").hasClass("ajax-menu")) {
            ajaxMenuOpen(target, e);
            return false;
        }
        positionDropdownMenu(menu, target);
        e.stopPropagation();
        return false;
    });
    
    function positionDropdownMenu(menu, dropdown_container) {
        var container_offset,
            left,
            menu_offset,
            width;
        dropdown_container.addClass("menu-open");

        container_offset = dropdown_container.offset();
        //reposition when showing to ensure the menu follows the element even
        //if the page changed since the last time it was shown
        menu.removeAttr("style");
        menu.css({top: container_offset.top + dropdown_container.height() + 4, right: (jQuery(window).width() - container_offset.left - dropdown_container.width())});

        menu.toggle();

        left = jQuery(window).scrollLeft();
        menu_offset = menu.offset();
        width = menu.width();
        if (left >= menu_offset.left) {
            // our menu is spilling off the left side of the screen. Lets fix this by switching it to instead be positioned to the right
            menu.css({top: container_offset.top + dropdown_container.height() + 4, left: container_offset.left});
            menu.removeClass("menu-left");
            menu.addClass("menu-right");
        } else {
            menu.removeClass("menu-right");
            menu.addClass("menu-left");
        }
    }
    
    function ajaxMenuOpen(target, e) {
        var menu = target.data('menu'),
            params;
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
        } else {
            target.find(".icon-cog").removeClass("icon-cog").addClass("icon-loading");
            params = {};
            target.closest(".f-dropdown_outer_container").prev(".params").find("input").each(function() {
                params[jQuery(this).attr("name")] = jQuery(this).val();
            });
            jQuery.ajax({
                url: '/contextualMenu/list',
                type: 'GET',
                data: params,
                success: function(data) {
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
                    target.find(".icon-loading").removeClass("icon-loading").addClass("icon-cog");
                    positionDropdownMenu(menu, target);
                }
            });
        }
        e.stopPropagation();
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
        var button = menu.data('button');
        var allOptions = menu.find('.criteria-option input');
        var checkedOptions = allOptions.filter(':checked');
        var text = option.closest('label').text();
        var buttonText = ''; 

        if (option.is(':checked')) {
            if (allOptions.length === checkedOptions.length) {
                button.find('.criteria-value').text(button.data('allText'));
            } else {
                checkedOptions.each(function (idx, item) {
                    buttonText += jQuery(item.parentElement).text();
                    if (idx != checkedOptions.length - 1) {
                        buttonText += ', '; 
                    }
                });
                button.find('.criteria-value').text(buttonText);
            }
        } else {
            if (checkedOptions.length === 0) {
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
        positionDropdownMenu(menu, button.closest('.dropdown-container'));
    });
    
    /* close on escape key */
    jQuery(document).keyup(function(e) {
        if (e.which == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
            jQuery(".dropdown-container").removeClass("menu-open");
        }
    });
});