<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="tools" page="dataStreaming.discrepancies">

    <p class="notes">
        <i:inline key=".instructions" />
    </p>

    <cti:url var="dataUrl" value="/tools/dataStreaming/discrepancies" />
    <div data-url="${dataUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${device}" />
                    <tags:sort column="${expectedAttributes}" />
                    <tags:sort column="${actualAttributes}" />
                    <tags:sort column="${status}" />
                    <tags:sort column="${lastCommunicated}" />
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${discrepancies.hitCount > 0}">
                        <c:forEach var="discrepancy" items="${discrepancies.resultList}">
                            <cti:url var="detailUrl" value="/meter/home">
                                <cti:param name="deviceId" value="${discrepancy.deviceId}" />
                            </cti:url>
                            <tr>
                                <td><a href="${detailUrl}">${fn:escapeXml(discrepancy.paoName)}</a></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${discrepancy.expected.attributes.size() > 0}">
                                            <c:forEach var="expectedAttribute"
                                                items="${discrepancy.expected.attributes}">
                                                    ${expectedAttribute.attribute.description} - ${expectedAttribute.interval }
                                                <c:choose>
                                                    <c:when test="${expectedAttribute.interval > 1}">
                                                        <i:inline key=".minutes" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key=".minute" />
                                                    </c:otherwise>
                                                </c:choose>
                                                <br />
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key="yukon.common.none"/>
                                        </c:otherwise>
                                     </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${discrepancy.actual.attributes.size() > 0}">
                                            <c:forEach var="actualAttribute"
                                                items="${discrepancy.actual.attributes}">
                                                    ${actualAttribute.attribute.description} - ${actualAttribute.interval }
                                                <c:choose>
                                                    <c:when test="${actualAttribute.interval > 1}">
                                                        <i:inline key=".minutes" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key=".minute" />
                                                    </c:otherwise>
                                                </c:choose>
                                                <br />
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key="yukon.common.none"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="wsnw"><i:inline key=".${discrepancy.status}" />
                                <c:if test="${discrepancy.status == 'CONFIGURATION_ERROR'}">
                                    <cti:icon icon="icon-information" classes="fn cp" data-popup="#config-error-${discrepancy.deviceId}" data-popup-toggle=""/>
                                    <cti:msg2 var="configErrorTitle" key=".configurationErrors" />
                                    <div class="dn" id="config-error-${discrepancy.deviceId}" title="${configErrorTitle}">
                                        <table class="compact-results-table">
                                            <thead>
                                                <tr>
                                                    <th><i:inline key=".attribute"/></th>
                                                    <th><i:inline key=".status"/></th>
                                                </tr>
                                            </thead>
                                            <c:forEach var="actualAttribute" items="${discrepancy.actual.attributes}">
                                                <c:if test="${actualAttribute.status != 'OK'}">
                                                    <tr>
                                                        <td>${actualAttribute.attribute.description}</td>
                                                        <td><i:inline key=".configError.${actualAttribute.status}"/></td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                        </table>
                                    </div>
                                 </c:if>
                                </td>
                                <c:set var="disabled" value="false" />
                                <td class="wsnw dif">
                                    <cti:formatDate value="${discrepancy.lastCommunicated}" type="DATEHM_12" />&nbsp;&nbsp;
                                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                                        <cm:dropdown icon="icon-cog" triggerClasses="fr">
                                            <cti:url var="resendUrl" value="/tools/dataStreaming/discrepancies/${discrepancy.deviceId}/resend" />
                                            <cm:dropdownOption id="resendConfiguration_${discrepancy.deviceId}" key=".resend" icon="icon-control-repeat-blue"
                                                data-device-id="${discrepancy.deviceId}" data-ok-event="yukon:tools:dataStreaming:resend" />
                                            <d:confirm on="#resendConfiguration_${discrepancy.deviceId}" nameKey="resendConfirmation" argument="${discrepancy.paoName}" />
                                            <cm:dropdownOption id="acceptConfiguration_${discrepancy.deviceId}" key=".accept" icon="icon-accept"
                                                data-device-id="${discrepancy.deviceId}" data-ok-event="yukon:tools:dataStreaming:accept" />
                                            <d:confirm on="#acceptConfiguration_${discrepancy.deviceId}" nameKey="acceptConfirmation" argument="${discrepancy.paoName}" />
                                            <c:if test="${discrepancy.displayRemove()}">
                                                <cm:dropdownOption id="removeConfiguration_${discrepancy.deviceId}" key=".remove" icon="icon-cross"
                                                    data-device-id="${discrepancy.deviceId}" data-ok-event="yukon:tools:dataStreaming:remove" />
                                                <d:confirm on="#removeConfiguration_${discrepancy.deviceId}" nameKey="removeConfirmation" argument="${discrepancy.paoName}" />
                                            </c:if>
                                        </cm:dropdown>
                                    </cti:checkRolesAndProperties>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td>
                                <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${discrepancies}" adjustPageCount="true" hundreds="true" />
    </div>

    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">

        <div class="page-action-area">

            <c:set var="disableButtons" value="${discrepancies.resultList.size() == 0}" />

            <cti:url var="resendAllUrl" value="/tools/dataStreaming/discrepancies/resendAll" />
            <form:form id="resendAll" action="${resendAllUrl}" method="POST">
                <cti:csrfToken />
                <input type="hidden" id="deviceIds" name="deviceIds" value="${deviceIds}" />
                <cti:button id="resendAllConfigurations" nameKey="resendAll"
                    disabled="${disableButtons}" data-ok-event="yukon:tools:dataStreaming:resendAll" />
                <d:confirm on="#resendAllConfigurations" nameKey="resendAllConfirmation" />
            </form:form>

            <cti:url var="acceptAllUrl" value="/tools/dataStreaming/discrepancies/acceptAll" />
            <form:form id="acceptAll" action="${acceptAllUrl}" method="POST">
                <cti:csrfToken />
                <input type="hidden" id="deviceIds" name="deviceIds" value="${deviceIds}" />
                <cti:button id="acceptAllConfigurations" nameKey="acceptAll"
                    disabled="${disableButtons}" data-ok-event="yukon:tools:dataStreaming:acceptAll" />
                <d:confirm on="#acceptAllConfigurations" nameKey="acceptAllConfirmation" />
            </form:form>

        </div>

    </cti:checkRolesAndProperties>

    <cti:includeScript link="/resources/js/pages/yukon.tools.dataStreaming.js" />

</cti:standardPage>