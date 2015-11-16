function initiateLogUpdate(url, updateSecs) {
    var getNewLines = function() {
        $.getJSON(url, function(data) {
            //build new rows
            var items = [];
            $.each(data, function(logString, quality) {
                items.push('<span class="' + quality + '">' + logString + '</span><br>');
            });
            
            //add new rows
            var logDiv = $('#logDiv');
            logDiv.append(items.join(''));
        });
        setTimeout(getNewLines, updateSecs * 1000);
    };
    getNewLines();
}