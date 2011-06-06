<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.capcontrol.ivvc.voltProfileGraph">

<settings>
    <font><cti:msg2 key=".font"/></font>
    <text_size><cti:msg2 key=".textSize"/></text_size>
    <text_color><cti:msg2 key=".textColor"/></text_color>
    <decimals_separator>.</decimals_separator>
    <redraw>true</redraw>
    <rescale_on_hide>true</rescale_on_hide>
    <connect>true</connect>
    
    <background>
        <color><cti:msg2 key=".background.color"/></color>
        <alpha><cti:msg2 key=".background.alpha"/></alpha>
        <border_color><cti:msg2 key=".background.borderColor"/></border_color>
        <border_alpha><cti:msg2 key=".background.borderAlpha"/></border_alpha>
        <file><cti:msg2 key=".background.file"/></file>
    </background>
    
    <plot_area>
        <color><cti:msg2 key=".plotArea.color"/></color>
        <alpha><cti:msg2 key=".plotArea.alpha"/></alpha>
        <border_color><cti:msg2 key=".plotArea.borderColor"/></border_color>
        <border_alpha><cti:msg2 key=".plotArea.borderAlpha"/></border_alpha>
        <margins>
            <left>50</left>
            <top>35</top>
            <right>15</right>
            <bottom>80</bottom>
        </margins>
    </plot_area>
    
    <values>
        <x>
            <color><cti:msg2 key=".values.x.color"/></color>
            <text_size><cti:msg2 key=".values.x.textSize"/></text_size>
        </x>
        <y_left>
            <cti:msg2 var="yLeftMin" key=".values.yLeft.min"/>
            <cti:msg2 var="yLeftMax" key=".values.yLeft.max"/>
            <min>${graphSettings.YLowerBound + yLeftMin}</min>
            <max>${graphSettings.YUpperBound + yLeftMax}</max>
            <color><cti:msg2 key=".values.yLeft.color"/></color>
            <text_size><cti:msg2 key=".values.yLeft.textSize"/></text_size>
            <unit><cti:msg2 key=".values.yLeft.unit"/></unit>
            <unit_position><cti:msg2 key=".values.yLeft.unitPosition"/></unit_position>
        </y_left>
    </values>

    <axes>
        <x>
            <color><cti:msg2 key=".axis.x.color"/></color>
            <alpha><cti:msg2 key=".axis.x.alpha"/></alpha>
            <width><cti:msg2 key=".axis.x.width"/></width>
            <tick_length><cti:msg2 key=".axis.x.tickLength"/></tick_length>
        </x>
        <y_left>
            <type><cti:msg2 key=".axis.yLeft.type"/></type>
            <color><cti:msg2 key=".axis.yLeft.color"/></color>
            <alpha><cti:msg2 key=".axis.yLeft.alpha"/></alpha>
            <width><cti:msg2 key=".axis.yLeft.width"/></width>
            <tick_length><cti:msg2 key=".axis.yLeft.tickLength"/></tick_length>
        </y_left>
    </axes>
    
    <indicator>
        <enabled><cti:msg2 key=".indicator.enabled"/></enabled>
        <zoomable><cti:msg2 key=".indicator.zoomable"/></zoomable>
        <color><cti:msg2 key=".indicator.color"/></color>
        <line_alpha><cti:msg2 key=".indicator.lineAlpha"/></line_alpha>
        <selection_color><cti:msg2 key=".indicator.selectionColor"/></selection_color>
        <selection_alpha><cti:msg2 key=".indicator.selectionAlpha"/></selection_alpha>
        <x_balloon_enabled><cti:msg2 key=".indicator.xBalloonEnabled"/></x_balloon_enabled>
        <x_balloon_text_color><cti:msg2 key=".indicator.xBalloonTextColor"/></x_balloon_text_color>
    </indicator>

    <balloon>
        <only_one><cti:msg2 key=".balloon.onlyOne"/></only_one>
        <on_off><cti:msg2 key=".balloon.onOff"/></on_off>
        <color><cti:msg2 key=".balloon.color"/></color>
        <alpha><cti:msg2 key=".balloon.alpha"/></alpha>
        <text_color><cti:msg2 key=".balloon.textColor"/></text_color>
        <text_size><cti:msg2 key=".balloon.textSize"/></text_size>
        <corner_radius><cti:msg2 key=".balloon.cornerRadius"/></corner_radius>
        <border_width><cti:msg2 key=".balloon.borderWidth"/></border_width>
        <border_alpha><cti:msg2 key=".balloon.borderAlpha"/></border_alpha>
        <border_color><cti:msg2 key=".balloon.borderColor"/></border_color>
    </balloon>

    <legend>
        <x></x>
        <y>90%</y>
        <color><cti:msg2 key=".legend.color"/></color>
        <alpha><cti:msg2 key=".legend.alpha"/></alpha>
        <border_color><cti:msg2 key=".legend.borderColor"/></border_color>
        <border_alpha><cti:msg2 key=".legend.borderAlpha"/></border_alpha>
        <text_color><cti:msg2 key=".legend.textColor"/></text_color>
        <text_color_hover><cti:msg2 key=".legend.textColorHover"/></text_color_hover>
        <text_size><cti:msg2 key=".legend.textSize"/></text_size>
        <graph_on_off><cti:msg2 key=".legend.graphOnOff"/></graph_on_off>
        <key>
            <size><cti:msg2 key=".legend.key.size"/></size>
            <border_color><cti:msg2 key=".legend.key.borderColor"/></border_color>
            <key_mark_color><cti:msg2 key=".legend.key.keyMarkColor"/></key_mark_color>
        </key>
        <values>
            <text><cti:msg2 key=".legend.values.text"/></text>
        </values>
    </legend>
    
    <strings>
        <no_data><cti:msg2 key=".strings.noData"/></no_data>
    </strings>

    <labels>
        <label>
            <x></x>
            <y>2%</y>
            <align>center</align>
            <text_color><cti:msg2 key=".labels.graphTitle.textColor"/></text_color>
            <text_size><cti:msg2 key=".labels.graphTitle.textSize"/></text_size>
            <text>
                <![CDATA[<b>${graphSettings.graphTitle}</b>]]>
            </text>
        </label>
        <label>
            <x></x>
            <y>50%</y>
            <rotate>90</rotate>
            <align>left</align>
            <text_color><cti:msg2 key=".labels.yAxis.textColor"/></text_color>
            <text_size><cti:msg2 key=".labels.yAxis.textSize"/></text_size>
            <text>
                <![CDATA[<b>${graphSettings.YAxisLabel}</b>]]>
            </text>        
        </label>
        <label>
            <x></x>
            <y>81%</y>
            <align>center</align>
            <text_color><cti:msg2 key=".labels.xAxis.textColor"/></text_color>
            <text_size><cti:msg2 key=".labels.xAxis.textSize"/></text_size>
            <text>
                <![CDATA[<b>${graphSettings.XAxisLabel}</b>]]>
            </text>
        </label>
    </labels>

    <guides>
        <max_min>false</max_min>
        <guide>
            <start_value>300</start_value>
            <end_value>${graphSettings.YUpperBound}</end_value>
            <width><cti:msg2 key=".guide.width"/></width>
            <fill_color><cti:msg2 key=".guide.fillColor"/></fill_color>
            <fill_alpha><cti:msg2 key=".guide.fillAlpha"/></fill_alpha>
            <alpha><cti:msg2 key=".guide.alpha"/></alpha>
            <color><cti:msg2 key=".guide.color"/></color>
            <dashed><cti:msg2 key=".guide.dashed"/></dashed>
            <dash_length><cti:msg2 key=".guide.dashLength"/></dash_length>
        </guide>
        <guide>
            <start_value>${graphSettings.YLowerBound}</start_value>
            <end_value>0</end_value>
            <width><cti:msg2 key=".guide.width"/></width>
            <fill_color><cti:msg2 key=".guide.fillColor"/></fill_color>
            <fill_alpha><cti:msg2 key=".guide.fillAlpha"/></fill_alpha>
            <alpha><cti:msg2 key=".guide.alpha"/></alpha>
            <color><cti:msg2 key=".guide.color"/></color>
            <dashed><cti:msg2 key=".guide.dashed"/></dashed>
            <dash_length><cti:msg2 key=".guide.dashLength"/></dash_length>
        </guide>
    </guides>

    <graphs>
        <c:forEach var="line" items="${graph.lines}">
            <graph gid="${line.id}">
                <title>${line.lineName}</title>
                <balloon_text>{description}</balloon_text>
                <bullet>${line.settings.bullet}</bullet>
                <bullet_size><cti:msg2 key=".graphs.bulletSize"/></bullet_size>
                <bullet_alpha><cti:msg2 key=".graphs.bulletAlpha"/></bullet_alpha>
                <color>${line.settings.color}</color>
                <line_alpha><cti:msg2 key=".graphs.lineAlpha"/></line_alpha>
                <line_width><cti:msg2 key=".graphs.lineWidth"/></line_width>
                <vertical_lines>${line.settings.verticalLines}</vertical_lines>
                <data_labels>${line.settings.dataLabel}</data_labels>
                <data_labels_text_color><cti:msg2 key=".graphs.dataLabelsTextColor"/></data_labels_text_color>
                <data_labels_text_size><cti:msg2 key=".graphs.dataLabelsTextSize"/></data_labels_text_size>
                <min_max>${line.settings.minMax}</min_max>
                <selected>${line.settings.selected}</selected>
                <visible_in_legend>${line.settings.visibleInLegend}</visible_in_legend>
            </graph>
        </c:forEach>
    </graphs>

    <data>
        <chart>
            <series>
                <c:if test="${empty graph.lines}">
                    <value xid="0">0.0</value>
                    <value xid="1">1.0</value>
                    <value xid="2">2.0</value>
                    <value xid="3">3.0</value>
                    <value xid="4">4.0</value>
                </c:if>
                <c:forEach var="seriesValue" items="${graph.seriesValues}" varStatus="status">
                    <value xid="${status.index}">${seriesValue}</value>
                </c:forEach>
            </series>
            <graphs>
                <c:if test="${empty graph.lines}">
                    <graph visible_in_legend="false"></graph>
                </c:if>
                <c:forEach var="line" items="${graph.lines}">
                    <graph gid="${line.id}">
                        <c:forEach var="point" items="${line.points}">
                            <c:choose>
                                <c:when test="${point.description != null}">
                                    <value xid="${point.seriesId}" description="${point.description}"><c:if test="${point.y != null}">${point.y}</c:if></value>
                                </c:when>
                                <c:otherwise>
                                    <value xid="${point.seriesId}"><c:if test="${point.y != null}">${point.y}</c:if></value>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </graph>
                </c:forEach>
            </graphs>
        </chart>
    </data>

</settings>

</cti:msgScope>