function showSelectedDevices(imgEl, divId, innerDivId, url) {
	jQuery(imgEl).addClass("loading");
    jQuery.ajax({
        url: url,
        success: function(transport) {
        	jQuery("#" + innerDivId).html(transport);
            jQuery("#" + divId).show();
            jQuery(imgEl).removeClass("loading");
        }
    });
}