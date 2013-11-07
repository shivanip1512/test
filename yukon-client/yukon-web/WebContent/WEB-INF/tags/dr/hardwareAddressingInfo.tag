<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<c:set var="hardwareConfigType" value="${hardware.hardwareType.hardwareConfigType}"/>
<c:choose>
    <c:when test="${hardwareConfigType == 'EXPRESSCOM'}">
        <dr:expressComAddressingInfo/>
    </c:when>
    <c:when test="${hardwareConfigType == 'VERSACOM'}">
        <dr:versaComAddressingInfo/>
    </c:when>
    <c:when test="${hardwareConfigType == 'SA205'}">
        <dr:sa205AddressingInfo/>
    </c:when>
    <c:when test="${hardwareConfigType == 'SA305'}">
        <dr:sa305AddressingInfo/>
    </c:when>
    <c:when test="${hardwareConfigType == 'SA_SIMPLE'}">
        <dr:saSimpleAddressingInfo/>
    </c:when>
</c:choose>

<br>
<tags:sectionContainer2 nameKey="relays">
<table class="compact-results-table">
    <tr>
        <td>
        <table>
            <tr>
                <th><cti:msg2 key=".relay"/></th>
                <th><cti:msg2 key=".coldLoadPickup"/></th>
                <c:if test="${hardware.hardwareType.hasTamperDetect}">
                    <th><cti:msg2 key=".tamperDetect"/>Tamper Detect</th>
                </c:if>
                <c:if test="${hardware.hardwareType.hasProgramSplinter}">
                    <th><cti:msg2 key=".program"/></th>
                    <th><cti:msg2 key=".splinter"/></th>
                </c:if>
            </tr>

            <c:forEach var="relayNumber" begin="1"
                end="${hardware.hardwareType.numRelays}">
                <tr align="center">
                    <td>${relayNumber}</td>
                    <td><tags:input path="coldLoadPickup[${relayNumber-1}]"
                        size="4" maxlength="10"/></td>
                    <c:if test="${hardware.hardwareType.hasTamperDetect}">
                        <td>
                            <c:if test="${relayNumber <= 2}">
                                <tags:input path="tamperDetect[${relayNumber-1}]"
                                    size="4" maxlength="10"/>
                            </c:if>
                            <c:if test="${relayNumber > 2}">&nbsp;</c:if>
                        </td>
                    </c:if>
                    <c:if test="${hardware.hardwareType.hasProgramSplinter}">
                        <td><tags:input path="addressingInfo.program[${relayNumber-1}]"
                            size="4" maxlength="10"/></td>
                        <td><tags:input path="addressingInfo.splinter[${relayNumber-1}]"
                            size="4" maxlength="10"/></td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        </td>
    </tr>
</table>
</tags:sectionContainer2>
