jQuery(document).ready(function() {
	jQuery(".f-showSelectedDevices, .f-showSelectedInventory").click(function(event) {
		var args = jQuery(event.currentTarget).attr("data-function-arguments");
		eval('args = ' + args);
		showSelectedDevices(this, args.id, args.url);
	});
});

function showSelectedDevices(imgEl, divId, url) {
	var icon = jQuery(imgEl).find('.icon-magnifier');
    icon.toggleClass("icon-loading icon-magnifier");
    jQuery.ajax({
        url: url,
        success: function(transport) {
        	jQuery(document.getElementById(divId)).html(transport);
        	jQuery(document.getElementById(divId)).dialog({width: "auto", height: 500});
            icon.toggleClass("icon-loading icon-magnifier");
        }
    });
}