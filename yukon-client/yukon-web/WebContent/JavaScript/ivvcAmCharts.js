// reload both the data and settings of the amChart
function amChart_reloadAll(chart_Id) {
    var chart = $(chart_Id);
    if (chart != null) {
        chart.reloadAll();
    }
}

var can_update_chart = {};
function amChartInited(chart_Id) {
	can_update_chart[chart_Id] = true;
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
        if (graphTitle != null && graphTitle == hiddenGraphTitle && graphIndex != index) {
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
        
        if (mostRecentPointTime === 0) mostRecentPointTime = newLargestTime;
        if (can_update_chart[chart_Id] && mostRecentPointTime > 0 && newLargestTime > mostRecentPointTime) {
            amChart_reloadAll(chart_Id);
            mostRecentPointTime = newLargestTime;
        }
    };
}

var chart_timer = {};
jQuery(function() {
    jQuery(".ivvcGraphContainer").mouseenter(function() {
        jQuery('#hideMsg').hide();
        jQuery("#updatesPaused").show();
        can_update_chart[getChartId()] = false;
    }).mouseout(function() {
        var chart_Id = getChartId();
        clearInterval(chart_timer[chart_Id]);
        jQuery("#updatesPaused").hide();
        jQuery('#hideMsg span').text(getChartPauseSeconds());
        jQuery('#hideMsg').show();
        var sec = parseInt(jQuery('#hideMsg span').text()) || 0;
        chart_timer[chart_Id] = setInterval(function() {
            jQuery('#hideMsg span').text(--sec);
            if (sec == 0) {
                timeoutEnd();
            }
        }, 1000);
    });

    jQuery("#hideMsg a").click(function() {
        timeoutEnd();
    });
});

function timeoutEnd() {
    jQuery('#hideMsg').fadeOut('fast');
    var chart_Id = getChartId();
    can_update_chart[chart_Id] = true;
    clearInterval(chart_timer[chart_Id]);
}

function getChartId() {
    return jQuery("#ivvcChartIdValue").val();
}

function getChartPauseSeconds() {
    return jQuery("#ivvcChartPauseValue").val();
}
