<%@ include file="defaultTagLibs.jspf" %>
<%@ include file="defaultAttributes.jspf" %>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
    <div class="graph_phases axis_label usn"></div>
</div>

<script>
$(function() {
    var chartId = '${chartId}',
        jsonDataAndOptions = ${cti:jsonString(jsonDataAndOptions)},
        choiceContainer = $(".graph_phases");

    yukon.flot.addChart({
        chartId: chartId,
        type: jsonDataAndOptions.type,
        data: jsonDataAndOptions.datas,
        options: jsonDataAndOptions.options,
        methods: {
            getFilteredGraphData: function() {
                // only show checked phase lines
                var phases = getCheckedPhases();
                var data_with_meta = yukon.flot.charts[chartId].data_with_meta;
                var data = [];
                for (var i=0; i < data_with_meta.length; i++) {
                    if (typeof data_with_meta[i].phase === 'undefined'
                            || phases[data_with_meta[i].phase]) {
                        data.push(data_with_meta[i]);
                    }
                }
                return data;
            }
        }
    });
    
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
        yukon.flot.charts[chartId].methods.plotGraph(chartId);
    });
    
    /* chart it!! */
    yukon.flot.charts[chartId].methods.plotGraph(chartId);
});
</script>
