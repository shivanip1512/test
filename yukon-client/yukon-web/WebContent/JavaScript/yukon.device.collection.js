jQuery(function() {
    jQuery(document).on('click', '.f-showSelectedDevices, .f-showSelectedInventory', function(event) {
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
        popup.dialog({width: 450, height: 300});
        icon.toggleClass("icon-spinner icon-magnifier");
    });
}