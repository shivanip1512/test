$(function() {
    $(document).on('click', '.f-showSelectedDevices, .f-showSelectedInventory', function(event) {
        var args = $(event.currentTarget).attr("data-function-arguments");
        eval('args = ' + args);
        showSelectedDevices(this, args.id, args.url);
    });
});

function showSelectedDevices(imgEl, divId, url) {
      
    var icon = $(imgEl).find('.icon-magnifier'),
        popup = $('#' + divId);
      
    icon.toggleClass("icon-spinner icon-magnifier");
    popup.load(url, function() {
        popup.dialog({width: 450, height: 300});
        icon.toggleClass("icon-spinner icon-magnifier");
    });
}