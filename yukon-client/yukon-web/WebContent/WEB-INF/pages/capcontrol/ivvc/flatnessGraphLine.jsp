<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<settings>
    <font>Arial</font>
    <connect>1</connect>
    <decimals_separator>.</decimals_separator>
    <redraw>true</redraw>
  
    <values>
        <x>
            <frequency>1</frequency>
        </x>
        <y_left>
            <min>${graphSettings.YLowerBound}</min>
            <max>${graphSettings.YUpperBound+5}</max>
        </y_left>
    </values>

    <axes>
        <x>
            <color>#e6e6e6</color>
            <width>1</width>
        </x>
        <y_left>
            <color>#e6e6e6</color>
            <width>1</width>
        </y_left>
    </axes>

    <guides>
        <max_min>false</max_min>
        <guide>
            <start_value>300</start_value>
            <end_value>130</end_value>
            <width>1</width>
            <fill_alpha>10</fill_alpha>
            <fill_color>#000000</fill_color>
        </guide>
        <guide>
            <start_value>115</start_value>
            <end_value>0</end_value>
            <width>1</width>
            <fill_alpha>10</fill_alpha>
            <fill_color>#000000</fill_color>
        </guide>
    </guides>

    <labels>
        <label>
            <x>0</x>
            <y>2%</y>
            <align>center</align>  
            <text>
                <![CDATA[<b>${graphSettings.graphTitle}</b>]]>
            </text>
        </label>
        <label>
            <x>0</x>
            <y>50%</y>
            <rotate>90</rotate>
            <align>left</align>  
            <text>
                <![CDATA[<b>${graphSettings.YAxisLabel}</b>]]>
            </text>        
        </label>
        <label>
            <x>0</x>
            <y>81%</y>
            <align>center</align>  
            <text>
                <![CDATA[<b>${graphSettings.XAxisLabel}</b>]]>
            </text>
        </label>
    </labels>
    
    <balloon>
        <color>#FFCC00</color>
        <alpha>70</alpha>
        <text_color>#000000</text_color>
    </balloon>
    
    <legend>
        <y>90%</y>
    </legend>

    <graphs>
        <c:forEach var="line" items="${graph.lines}">
            <graph gid="${line.id}">
                <balloon_text><![CDATA[{description}]]></balloon_text>
            </graph>
        </c:forEach>
    </graphs>

<data>
    <chart>
        <series>
            <c:forEach var="seriesValue" items="${graph.seriesValues}" varStatus="status">
                <value xid="${status.index}">${seriesValue}</value>
            </c:forEach>
        </series>
        <graphs>
            <c:forEach var="line" items="${graph.lines}">
                <c:if test="${line.boundary == false}">
                    <graph gid="${line.id}"
                           bullet="${line.settings.bullet}"
                           fill_alpha="${line.settings.fillAlpha}"
                           visible_in_legend="${line.settings.visibleInLegend}"
                           title="${line.lineName}"
                           bullet_size="10">
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
                </c:if>
            </c:forEach>
        </graphs>
    </chart>
</data>

</settings>