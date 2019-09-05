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
    
    <cti:url var="programUrl" value="/amr/meterProgramming/home"/>
    <div data-url="${filterUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${program}"/>
                    <tags:sort column="${numberOfDevices}"/>
                    <tags:sort column="${numberInProgress}"/>
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="program" items="${programs}">
                    <tr>
                        <td>${fn:escapeXml(program.programInfo.name)}</td>
                        <td>${program.deviceTotal}</td>
                        <td>${program.inProgressTotal}</td>
                        <td>
                            <c:if test="${program.displayDelete()}">
                                <cm:dropdown icon="icon-cog">
                                    <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-delete-program" 
                                        data-program-guid="${program.programInfo.guid}"/>
                                </cm:dropdown>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty programs}">
                     <tr><td colspan="4">
                        <span class="empty-list"><i:inline key=".noProgramsFound"/></span>
                    </td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.meterProgramming.summary.js" />

</cti:standardPage>