<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="routeUsage">

<script type="text/javascript">

Event.observe(window, 'load', function() {
    $$('td.used').each( function (element) {
        
        Event.observe(element, 'mouseout', function(e) {
            var td = Event.element(e);
            td.removeClassName('highlighted');
            td.siblings().each(function (item) {
                item.removeClassName('highlighted');
            });
        });
        
        Event.observe(element, 'mouseover', function(e) {
            
            var td = Event.element(e);
            var routeId = td.readAttribute('routeId');
            td.addClassName('highlighted');
            td.siblings().each(function (item) {
                if (item.readAttribute('routeId') == routeId) {
                    item.addClassName('highlighted');
                }
            });
            
        });
    });
});
</script>

<style>
table.routeTable {
	width: 100%;
}

table.routeTable td {
	text-align: center;
    font-size: 11px;
}

table.routeTable tr:hover td.available {
    background-color: lemonchiffon;
}

table.routeTable th {
    white-space: nowrap;
    text-align: center;
}

th.fixedBit, span.variableBit {
    color: black !important;
    font-weight: bold;
}

td.available {
    background-color: #FFFFFF;
}

td.used {
    background-color: #D8F0FF;
}

td.highlighted {
    background-color: #B8FFB8 !important;
}

th.fixedColumn {
    width: 4%;
}

th.variableColumn {
    width: 13%;
}
</style>
    
    <c:forEach var="level" items="${routeTables}" varStatus="levelStatus">
        <cti:msg2 var="levelLabel" key=".level" argument="${levelStatus.index + 1}"/>
        <tags:hideReveal title="${levelLabel}" showInitially="true">
            <table id="level${levelStatus.index + 1}" class="resultsTable routeTable">
                <thead>
                    <tr>
                        <th class="fixedColumn"><i:inline key=".fixedBit"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="0"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="1"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="2"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="3"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="4"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="5"/></th>
                        <th class="variableColumn"><i:inline key=".variableBit" arguments="6"/></th>
                    </tr>
                </thead>
                
                <tbody>
                    <c:forEach var="fixed" items="${level}" varStatus="fixedStatus">
                        <tr>
                            <th class="fixedBit">${fixedStatus.index}</th>
                            
                            <c:set var="skip" value="0"/>
                            <c:forEach var="variable" items="${fixed}" varStatus="variableStatus">
                                
                                <c:choose>
                                    <c:when test="${skip == 0}">
                                        <c:choose>
                            
                                            <c:when test="${variable > -1}">
                                                
                                                <c:set var="doneLooping" value="false"/>
                                                <c:forEach begin="${variableStatus.index}" end="6">
                                                    <c:if test="${!doneLooping}">
                                                        <c:choose>
                                                            <c:when test="${fixed[variableStatus.index + skip + 1] == variable}">
                                                                <c:set var="skip" value="${skip + 1}"/>
                                                            </c:when>
                                                            <c:otherwise><c:set var="doneLooping" value="true"/></c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </c:forEach>
                                                
                                                <c:set var="tdClass" value="used"/>
                                                <c:set var="deviceId" value=" DeviceId: ${variable}"/>
                                                <c:set var="routeId" value="routeId='${variable}'"/>
                                            </c:when>
                            
                                            <c:otherwise>
                                                <c:set var="skip" value="0"/>
                                                <c:set var="tdClass" value="available"/>
                                                <c:set var="deviceId" value=""/>
                                                <c:set var="routeId" value=""/>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <c:set var="colspan" value="${skip + 1}"/>
                                        <td class="${tdClass}" title="Fixed: ${fixedStatus.index} Variable: ${variableStatus.index}<c:if test="${skip > 0}"> - ${variableStatus.index + skip}</c:if>${deviceId}" ${routeId} colspan="${colspan}">
                                            <c:if test="${variable > -1}">
                                                <cti:deviceName deviceId="${variable}" var="deviceName"/>
                                                ${deviceName}
                                            </c:if>
                                        </td>
                                    </c:when>
                                    
                                    <c:otherwise>
                                        <c:set var="skip" value="${skip - 1}"/>
                                    </c:otherwise>
                                </c:choose>
                                
                            </c:forEach>
                            
                        </tr>
                    </c:forEach>
                    
                </tbody>
                
                <tfoot>
                    <tr>
                        <th><i:inline key=".fixedBit"/></th>
                        <th><i:inline key=".variableBit" arguments="0"/></th>
                        <th><i:inline key=".variableBit" arguments="1"/></th>
                        <th><i:inline key=".variableBit" arguments="2"/></th>
                        <th><i:inline key=".variableBit" arguments="3"/></th>
                        <th><i:inline key=".variableBit" arguments="4"/></th>
                        <th><i:inline key=".variableBit" arguments="5"/></th>
                        <th><i:inline key=".variableBit" arguments="6"/></th>
                    </tr>
                </tfoot>
                
            </table>
        </tags:hideReveal>
        <br>
    </c:forEach>
    
</cti:standardPage>