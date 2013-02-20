jQuery(document).ready(function() {
	jQuery(".f_showSelectedDevices, .f_showSelectedInventory").click(function(event) {
		var args = jQuery(event.currentTarget).attr("data-function-arguments");
		eval('args = ' + args);
		showSelectedDevices(this, args.id, args.url);
	});
});

function showSelectedDevices(imgEl, divId, url) {
    jQuery(imgEl).toggleClass("loading magnifier");
    jQuery.ajax({
        url: url,
        success: function(transport) {
        	jQuery("#" + divId).html(transport);
        	jQuery("#" + divId).dialog({width: "auto", minWidth: 500});
            jQuery(imgEl).toggleClass("loading magnifier");
        }
    });
}