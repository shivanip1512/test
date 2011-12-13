function initiateLogUpdate(url, updateSecs) {
    var getNewLines = function() {
        jQuery.getJSON(url, function(data) {
            var items = [];
            jQuery.each(data, function(logString, quality) {
                items.push('<span class="' + quality + '">' + logString + '</span><br>');
            });
            var currentHtml = jQuery('#logDiv').html();
            jQuery('#logDiv').html(currentHtml + items.join(''));
        });
        setTimeout(getNewLines, updateSecs * 1000);
    };
    getNewLines();
}

