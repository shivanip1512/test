//JavaScript for the dropdownActions.tag
jQuery(function() {
    jQuery(".dropdown-container").click(function(e) {
        var target = jQuery(this);
        var menu = target.find("ul.dropdown-menu");
        
        //register menu
        if (menu[0]) {
            target.data({'menu': menu});
            jQuery('body').prepend(menu);
        } else {
            menu = target.data('menu');
        }
        
        //reposition when showing to ensure the menu follows the element even
        //if the page changed since the last time it was shown
        var container_offset = target.offset();
        menu.css({top: container_offset.top + target.height() + 4, right: (jQuery(window).width() - container_offset.left - target.width())});
        menu.toggle();
        e.stopPropagation();
        return false;
    });
    
    //close on document click
    jQuery(document).click(function() {
        jQuery('ul.dropdown-menu').hide();
    });
    
    //close on escape key
    jQuery(document).keyup(function(e) {
        if (e.keyCode == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
        }
    });
});