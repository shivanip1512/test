// reload both the data and settings of the amChart
function amChart_reloadAll(chart_Id) {
    var chart = $(chart_Id);
    if (chart != null) {
        chart.reloadAll();
    }
}


// FIND OUT WHICH GRAPH WAS HIDDEN ///////////////////////////////////////////////////////
// amGraphHide(chart_id, index, title)
// This function is called when the viewer hides the graph by clicking on the checkbox in
// the legend. Index is the sequential number of a graph in your settings, counting from 0
var hidingGraphs = false; //to stop recursive calls
function amGraphHide(chart_Id, index, title) {
    $(chart_Id).value = index;
    if (hidingGraphs == false) {
        hidingGraphs = true;
        hideAllGraphsWithSameTitle(chart_Id, index, title);
        hidingGraphs = false;
    }
}

function hideAllGraphsWithSameTitle(chart_Id, index, hiddenGraphTitle) {
    var chart = $(chart_Id);
    chart.getParam('graphs.graph.length');
    var graphsLength = chart.value;
    for (var graphIndex = 0; graphIndex < graphsLength; graphIndex++) {
        chart.getParam('graphs.graph['+ graphIndex + '].title');
        var graphTitle = chart.value;
        if (graphTitle == hiddenGraphTitle && graphIndex != index) {
            chart.hideGraph(graphIndex);
        }
    }
}

// FIND OUT WHICH GRAPH WAS SHOWN ///////////////////////////////////////////////////////
// amGraphShow(chart_id, index, title)
// This function is called when the viewer shows the graph by clicking on the checkbox in
// the legend. Index is the sequential number of a graph in your settings, counting from 0
var showingGraphs = false; //to stop recursive calls
function amGraphShow(chart_Id, index, title) {
    $(chart_Id).value = index;
    if (showingGraphs == false) {
        showingGraphs = true;
        showAllGraphsWithSameTitle(chart_Id, index, title);
        showingGraphs = false;
    }
}

function showAllGraphsWithSameTitle(chart_Id, index, shownGraphTitle) {
    var chart = $(chart_Id);
    chart.getParam('graphs.graph.length');
    var graphsLength = chart.value;
    for (var graphIndex = 0; graphIndex < graphsLength; graphIndex++) {
        chart.getParam('graphs.graph['+ graphIndex + '].title');
        var graphTitle = chart.value;
        if (graphTitle == shownGraphTitle && graphIndex != index) {
            chart.showGraph(graphIndex);
        }
    }
}

function amReturnParam(chart_Id, param) {
    $(chart_Id).value = unescape(param);
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