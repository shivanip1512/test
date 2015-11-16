<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="routeUsage">

<script type="text/javascript">

$(function () {
    $('td.used').each( function (index, element) {

        $(element).on('mouseout', function (e) {
            var td = e.target;
            $(td).removeClass('highlighted');
            $(td).siblings().each(function (index, item) {
                $(item).removeClass('highlighted');
            });
        });

        $(element).on('mouseover', function (e) {
            var td = e.target,
                routeId;
            routeId = $(td).attr('routeId');
            $(td).addClass('highlighted');
            $(td).siblings().each(function (index, item) {
                if ($(item).attr('routeId') == routeId) {
                    $(item).addClass('highlighted');
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
    border: 1px solid #ccc;
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
        <tags:hideReveal title="${levelLabel} ${levelStatus.index + 1}">
            <table id="level${levelStatus.index + 1}" class="results-table routeTable">
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
    </c:forEach>
    
</cti:standardPage>