jQuery(document).ready(function() {
	jQuery(".f-showSelectedDevices, .f-showSelectedInventory").click(function(event) {
		var args = jQuery(event.currentTarget).attr("data-function-arguments");
		eval('args = ' + args);
		showSelectedDevices(this, args.id, args.url);
	});
});

function showSelectedDevices(imgEl, divId, url) {
	  
    var icon = jQuery(imgEl).find('.icon-magnifier'),
	      popup = jQuery('#' + divId);
	  
	  icon.toggleClass("icon-spinner icon-magnifier");
    popup.load(url, function() {
        popup.dialog({width: "auto", height: 300, title: popup.attr('original-title')});
        icon.toggleClass("icon-spinner icon-magnifier");
    });
}