<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:standardPage title="Strategies" module="capcontrol">
    
    <script type="text/javascript" language="JavaScript">
    var firstRun = true;
    function removeStrategy(strategyId, strategyName) {

        if( confirm("Are you sure you want to delete " + strategyName + "?") ) {
            var url = "/spring/capcontrol/strategy/deleteStrategy";
    
            hideErrors();
            
            new Ajax.Request(url, {'parameters': {'strategyId': strategyId}, 
                onComplete: function(transport, json) {
                    if (!json.success) {
                        $('errorElement').innerHTML = json.resultText;
                        $('errorElement').show();
                    } else {
                        deleteStrategyFromPage(strategyId);
                    }
                
                } });
        }
    }

    function hideErrors() {
        
        if(!firstRun) { 
            $('errorElement').hide();
        }
        firstRun = false;
    }

    function deleteStrategyFromPage(strategyId) {
        var line = document.getElementById('s_' + strategyId);
        var table = document.getElementById('tableBody');

        table.removeChild(line);
    }
    </script>
    
    <cti:standardMenu menuSelection="view|strategies" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
        <cti:crumbLink title="Strategies"/>
    </cti:breadCrumbs>
        <div id="errorElement" style="text-align: center; color: red;font-weight: bold;"></div>
    <table class="resultsTable" id="strategyTable" width="90%" border="0"
        cellspacing="0" cellpadding="3" align="center">
        <thead>
            <tr id="header">
                <th>Strategy Name</th>
                <th>Control Method</th>
                <th>Control Algorithm</th>
                <th>Start/Stop</th>
                <th>Interval</th>
                <th>Confirm Time</th>
                <th>Pass/Fail(%)</th>
                <th>Peak Settings</th>
                <th>Off Peak Settings</th>
            </tr>
        </thead>
        <tbody id="tableBody">
            <c:forEach var="item" items="${strategies}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>"
                    id="s_${item.strategyID}">
                    <td nowrap="nowrap">
                        <c:choose>
                            <c:when test="${hasEditingRole}">
                                <a href="/editor/cbcBase.jsf?type=5&itemid=${item.strategyID}" class="imgLink">
                                    <img class="rAlign editImg" src="/WebConfig/yukon/Icons/pencil.gif" />
                                </a>
                                <a href="javascript:removeStrategy(${item.strategyID}, '${item.strategyName}');" class="imgLink">
                                    <img class="rAlign editImg pointer" src="/WebConfig/yukon/Icons/delete.gif">
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="/editor/cbcBase.jsf?type=3&itemid=${item.strategyID}" class="imgLink">
                                    <img class="rAlign editImg" src="/WebConfig/yukon/Icons/information.gif" />
                                </a>
                            </c:otherwise>
                        </c:choose>
                        
                        <c:out value="${item.strategyName}" />
                    </td>
                    <td><c:out value="${item.controlMethod}" /></td>
                    <td><c:out value="${item.controlUnits}" /></td>
                    <td>
                        <cti:formatDuration value="${item.peakStartTime * 1000}" type="HM_SHORT"/>
                        <c:out value="/"/>
                        <cti:formatDuration value="${item.peakStopTime * 1000}" type="HM_SHORT"/>
                    </td>
                    <td><c:out value="${item.controlIntervalString}"/></td>
                    <td><c:out value="${item.minResponseTimeString}"/></td>
                    <td><c:out value="${item.passFailPercentString}"/></td>
                    <td><c:out value="${item.peakSettingsString}"/></td>
                    <td><c:out value="${item.offPeakSettingsString}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</cti:standardPage>