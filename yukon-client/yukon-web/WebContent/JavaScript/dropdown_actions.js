//JavaScript for the dropdownActions.tag && deviceDropdownActionsAjax.tag
jQuery(function() {
    jQuery(".dropdown-container").click(function(e) {
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

        target.addClass("menu-open");
        //reposition when showing to ensure the menu follows the element even
        //if the page changed since the last time it was shown
        var container_offset = target.offset();
        menu.css({top: container_offset.top + target.height() + 4, right: (jQuery(window).width() - container_offset.left - target.width())});

        jQuery("ul.dropdown-menu").hide();
        if (target.hasClass("ajax-menu")) {
            ajaxMenuOpen(target, e);
            return false;
        }
        menu.toggle();
        e.stopPropagation();
        return false;
    });
    
    function ajaxMenuOpen(target, e) {
        var menu = target.data('menu');
        if (typeof(target.data('menu_items')) !== 'undefined') {
            menu.toggle();
        } else {
            target.find(".cog").removeClass("cog").addClass("loading");
            var params = {};
            target.prev(".params").find("input").each(function() {
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
                    menu.toggle();
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