<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="chartId" required="true"%>
<%@ attribute name="title" required="true"%>
<%@ attribute name="jsonDataAndOptions" required="true" type="java.util.Map" description="json format should look like this: {datas: [{data: [[x,y, {tooltip: 'sometooltip'}], [x,y, {tooltip: 'sometooltip'}], etc], lineName: 'someLineName', color: 'hex/rgb color of this one line', phase: 'A/B/C for ivvc graphs'}], options: {nested objects of options}, type: 'line/bar/pie'}. See HighChartServiceImpl.java for more specific examples"%>

<div id="js-chart-container-${chartId}" class="js-highchart-graph-container ov"></div>

<script>
$(function() {    
    var jsonData = ${cti:jsonString(jsonDataAndOptions)};
    yukon.highChart.buildChart($("#js-chart-container-${chartId}"), jsonData, "${title}", "350", "550");
    
    function getCheckedPhases() {
        var phases = {};
        choiceContainer.find("input").each(function () {
            var phaseKey = $(this).attr("name");
            var is_checked = $(this).is(":checked");
            phases[phaseKey] = is_checked;
        });
        return phases;
    }

    var phaseMap = {};
    for (var i=0; i < yukon.flot.charts[chartId].data_with_meta.length; i++) {
        var line_phase = yukon.flot.charts[chartId].data_with_meta[i].phase;
        if (typeof line_phase !== 'undefined') {
            if (typeof phaseMap[line_phase] === 'undefined') {
                phaseMap[line_phase] = {};
            }
            phaseMap[line_phase] = {
                text: yukon.flot.charts[chartId].data_with_meta[i].lineName,
                color: yukon.flot.charts[chartId].data_with_meta[i].color,
            };
        }
    }
    $.each(phaseMap, function(key, val) {
        $('<style type="text/css">.graph_phases input.phase' + key + '{ outline-color: ' + val.color + '}</style>').appendTo('head');
        choiceContainer.append('<label><input type="checkbox" class="phase' + key + '" name="' + key + '" checked="checked" id="id' + key + '">' +
                               val.text + '</label>');
    });
    
    $(".graph_phases input").click(function () {
        //yukon.flot.charts[chartId].methods.plotGraph(chartId);
    });

});
</script>
