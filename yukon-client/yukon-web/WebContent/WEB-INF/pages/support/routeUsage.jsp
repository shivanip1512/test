<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
    <cti:standardMenu menuSelection="other|routeUsage"/>
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
        <cti:crumbLink url="/support/" title="Support" />
        <cti:crumbLink>Route Usage</cti:crumbLink>
    </cti:breadCrumbs>

<script type="text/javascript">

jQuery(function() {
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
        <tags:hideReveal title="Level ${levelStatus.index + 1}" showInitially="true">
            <table id="level${levelStatus.index + 1}" class="resultsTable routeTable">
                <thead>
                    <tr>
                        <th class="fixedColumn">Fixed Bit</th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">0</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">1</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">2</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">3</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">4</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">5</span></th>
                        <th class="variableColumn">Variable Bit: <span class="variableBit">6</span></th>
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
                        <th>Fixed Bit</th>
                        <th>Variable Bit: <span class="variableBit">0</span></th>
                        <th>Variable Bit: <span class="variableBit">1</span></th>
                        <th>Variable Bit: <span class="variableBit">2</span></th>
                        <th>Variable Bit: <span class="variableBit">3</span></th>
                        <th>Variable Bit: <span class="variableBit">4</span></th>
                        <th>Variable Bit: <span class="variableBit">5</span></th>
                        <th>Variable Bit: <span class="variableBit">6</span></th>
                    </tr>
                </tfoot>
                
            </table>
        </tags:hideReveal>
        <br>
    </c:forEach>
    
</cti:standardPage>