<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterProgramming">

    <div id="page-actions" class="dn">
        <cti:url var="summaryUrl" value="/amr/meterProgramming/summary"/>
        <cm:dropdownOption key=".summaryLink" icon="icon-application-view-columns" href="${summaryUrl}"/>
        <cti:url var="programOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/meterProgramming/inputs"/>
        <cm:dropdownOption key=".programOtherDevices" href="${programOthersUrl}" icon="icon-cog-add"/>
    </div>
    
    <c:if test="${!empty specialCases}">
        <tags:sectionContainer2 nameKey="specialCases" styleClass="PB10">
            <table class="compact-results-table row-highlighting">
                <thead>
                    <tr>
                        <th width="40%"><i:inline key=".type"/></th>
                        <th><i:inline key=".numberOfDevices"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="program" items="${specialCases}">
                        <tr>
                            <td>
                                <cti:url var="programUrl" value="/amr/meterProgramming/summary">
                                    <cti:param name="programs[0].guid" value="${program.programInfo.guid}"/>
                                    <cti:param name="programs[0].name" value="${program.programInfo.name}"/>
                                    <cti:param name="programs[0].source" value="${program.programInfo.source}"/>
                                </cti:url>
                                <a href="${programUrl}">${fn:escapeXml(program.programInfo.name)}</a>
                                <c:if test="${program.programInfo.source.isOldFirmware()}">
                                    <cti:icon icon="icon-help" data-popup="#firmware-help" classes="fn cp ML0 vam"/>
                                    <cti:msg2 var="helpTitle" key=".oldFirmware.helpTitle"/>
                                    <div id="firmware-help" class="dn" data-dialog data-cancel-omit="true" data-title="${helpTitle}"><cti:msg2 key=".oldFirmware.helpText"/></div>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${program.deviceTotal > 0}">
                                        <c:set var="deviceTotalUrl" value="${programUrl}&statuses=PROGRAMMED"/>
                                        <c:if test="${program.programInfo.source.isFailure()}">
                                            <c:set var="deviceTotalUrl" value="${programUrl}&statuses=FAILURE"/>
                                        </c:if>
                                        <a href="${deviceTotalUrl}">${program.deviceTotal}</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${program.deviceTotal}
                                    </c:otherwise>
                                </c:choose>
                           </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </tags:sectionContainer2>
    </c:if>
    
    <tags:sectionContainer2 nameKey="yukonPrograms">
        <cti:url var="programUrl" value="/amr/meterProgramming/sortedYukonPrograms"/>
        <div data-url="${programUrl}">
            <%@ include file="sortedYukonPrograms.jsp" %>
        </div>
    </tags:sectionContainer2>
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.meterProgramming.summary.js" />

</cti:standardPage>