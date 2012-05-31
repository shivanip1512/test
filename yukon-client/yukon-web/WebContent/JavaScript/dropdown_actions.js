//JavaScript for the dropdownActions.tag
jQuery(function() {
    jQuery(".actions_link").click(function(e) {
        jQuery(this).next("ul.dropdown-menu").toggle();
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
    jQuery(".dropdown-container .cog").mouseenter(function() {
        jQuery(this).closest(".arrow-down").addClass("arrow-down_hovered");
    }).mouseleave(function() {
        jQuery(this).closest(".arrow-down").removeClass("arrow-down_hovered");
    });
    jQuery(".dropdown-container .arrow-down").mouseenter(function() {
        jQuery(this).find(".cog").addClass("cog_hovered");
    }).mouseleave(function() {
        jQuery(this).find(".cog").removeClass("cog_hovered");
    });
});