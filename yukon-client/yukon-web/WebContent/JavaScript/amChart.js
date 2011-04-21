// reload both the data and settings of the amChart
function amChart_reloadAll(chart_Id) {
    var graph = $(chart_Id);
    if (graph != null) {
        graph.reloadAll();
    }
}

var mostRecentPointTime = 0;
function checkGraphExpired(chart_Id) {
    //assumes data is of type Hash
    return function(data) {
        var newLargestTime = data.get('largestTime');
        
        if (mostRecentPointTime > 0 && newLargestTime > mostRecentPointTime) {
            amChart_reloadAll(chart_Id);
        }

        mostRecentPointTime = newLargestTime;
    }
}