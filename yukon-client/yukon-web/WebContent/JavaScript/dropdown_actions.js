//JavaScript for the dropdownActions.tag && deviceDropdownActionsAjax.tag
jQuery(function() {
    jQuery(document).on("click", ".dropdown-container", function(e) {
        jQuery(".dropdown-container").removeClass("menu-open");
        var target = jQuery(this);
        var menu = target.find("ul.dropdown-menu");
        
        //register menu
        if (menu[0]) {
            target.data({'menu': menu});
            jQuery('body').prepend(menu);
        } else {
            menu = target.data('menu');
        }

        if (menu.is(":visible")) {
            jQuery("ul.dropdown-menu").hide();
            e.stopPropagation();
            return false;
        }

        jQuery("ul.dropdown-menu").hide();
        if (target.closest(".f_dropdown_outer_container").hasClass("ajax-menu")) {
            ajaxMenuOpen(target, e);
            return false;
        }
        positionDropdownMenu(menu, target);
        e.stopPropagation();
        return false;
    });
    
    function positionDropdownMenu(menu, dropdown_container) {
        dropdown_container.addClass("menu-open");

        var container_offset = dropdown_container.offset();
        //reposition when showing to ensure the menu follows the element even
        //if the page changed since the last time it was shown
        menu.removeAttr("style");
        menu.css({top: container_offset.top + dropdown_container.height() + 4, right: (jQuery(window).width() - container_offset.left - dropdown_container.width())});

        menu.toggle();

        var left = jQuery(window).scrollLeft();
        var menu_offset = menu.offset();
        var width = menu.width();
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
        var menu = target.data('menu');
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
        } else {
            target.find(".cog").removeClass("cog").addClass("loading");
            var params = {};
            target.closest(".f_dropdown_outer_container").prev(".params").find("input").each(function() {
                params[jQuery(this).attr("name")] = jQuery(this).val();
            });
            jQuery.ajax({
                url: '/contextualMenu/list',
                type: 'GET',
                data: params,
                success: function(data) {
                    var items = [];
                    for (var i=0; i<data.length; i++) {
                        var list_item;
                        if (data[i].divider === true) {
                            list_item = '<li class="divider"></li>';
                        } else {
                            list_item = "<li><a href='" + data[i].url + "'>" + data[i].text + "</a></li>";
                        }
                        items.push(list_item);
                    }
                    target.data({'menu_items': true});
                    menu.append(items.join(''));
                    target.find(".loading").removeClass("loading").addClass("cog");
                    positionDropdownMenu(menu, target);
                }
            });
        }
        e.stopPropagation();
        return false;
    }
    
    //close on document click
    jQuery(document).click(function() {
        jQuery('ul.dropdown-menu').hide();
        jQuery(".dropdown-container").removeClass("menu-open");
    });
    
    //close on escape key
    jQuery(document).keyup(function(e) {
        if (e.which == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
            jQuery(".dropdown-container").removeClass("menu-open");
        }
    });
});