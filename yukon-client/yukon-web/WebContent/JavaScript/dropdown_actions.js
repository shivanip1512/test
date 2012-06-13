//JavaScript for the dropdownActions.tag
jQuery(function() {
    jQuery(".dropdown-container").click(function(e) {
        var target = jQuery(this);
        var menu = target.find("ul.dropdown-menu");
        
        //register menu
        if (menu[0]) {
            target.data({'menu': menu});
            jQuery('body').prepend(menu);
            var container_offset = target.offset();
            menu.css({top: container_offset.top + target.height() + 4, right: (window.innerWidth - container_offset.left - target.width())});
        } else {
            menu = target.data('menu');
        }
        
        menu.toggle();
        e.stopPropagation();
    });
    jQuery(document.body).click(function() {
        jQuery('ul.dropdown-menu').hide();
    });
    jQuery(document).keyup(function(e) {
        if (e.keyCode == 27) { // esc
            jQuery('ul.dropdown-menu').hide();
        }
    });
    jQuery(".dropdown-container .arrow-down").mouseenter(function() {
        jQuery(this).find(".cog").addClass("cog_hovered");
    }).mouseleave(function() {
        jQuery(this).find(".cog").removeClass("cog_hovered");
    });
});