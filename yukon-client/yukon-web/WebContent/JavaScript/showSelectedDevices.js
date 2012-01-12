jQuery(document).ready(function() {
	jQuery(".f_showSelectedDevices, .f_showSelectedInventory").click(function(event) {
		var args = jQuery(event.currentTarget).attr("data-function-arguments");
		eval('args = ' + args);
		showSelectedDevices(this, args.id, args.id + "InnerDiv", args.url);
	});
});

function showSelectedDevices(imgEl, divId, innerDivId, url) {
	jQuery(imgEl).addClass("loading");
    jQuery.ajax({
        url: url,
        success: function(transport) {
        	jQuery("#" + innerDivId).html(transport);
        	adjustDialogSizeAndPosition(divId);
            jQuery("#" + divId).show();
            jQuery(imgEl).removeClass("loading");
        }
    });
}