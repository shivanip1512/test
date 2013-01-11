<%@ include file="defaultTagLibs.jspf" %>
<%@ include file="defaultIncludes.jspf" %>
<%@ include file="defaultAttributes.jspf" %>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
    <div class="graph_phases axis_label usn"></div>
</div>

<script>
jQuery(function() {
    var chartId = '${chartId}',
        jsonDataAndOptions = ${jsonDataAndOptions},
        choiceContainer = jQuery(".graph_phases");

    Yukon.Flot.addChart({
        chartId: chartId,
        type: jsonDataAndOptions.type,
        data: jsonDataAndOptions.datas,
        options: jsonDataAndOptions.options,
        methods: {
            getFilteredGraphData: function() {
                // only show checked phase lines
                var phases = getCheckedPhases();
                var data_with_meta = Yukon.Flot.charts[chartId].data_with_meta;
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
            var phaseKey = jQuery(this).attr("name");
            var is_checked = jQuery(this).is(":checked");
            phases[phaseKey] = is_checked;
        });
        return phases;
    }

    var phaseMap = {};
    for (var i=0; i < Yukon.Flot.charts[chartId].data_with_meta.length; i++) {
        var line_phase = Yukon.Flot.charts[chartId].data_with_meta[i].phase;
        if (typeof line_phase !== 'undefined') {
            if (typeof phaseMap[line_phase] === 'undefined') {
                phaseMap[line_phase] = {};
            }
            phaseMap[line_phase] = {
                text: Yukon.Flot.charts[chartId].data_with_meta[i].lineName,
                color: Yukon.Flot.charts[chartId].data_with_meta[i].color,
            };
        }
    }
    jQuery.each(phaseMap, function(key, val) {
        jQuery('<style type="text/css">.graph_phases input.phase' + key + '{ outline-color: ' + val.color + '}</style>').appendTo('head');
        choiceContainer.append('<label><input type="checkbox" class="phase' + key + '" name="' + key + '" checked="checked" id="id' + key + '">' +
                               val.text + '</label>');
    });
    
    jQuery(".graph_phases input").click(function () {
        Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
    });
    
    /* chart it!! */
    Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
});
</script>
