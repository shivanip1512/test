<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="recentResultsTitle" key="yukon.common.device.groupMeterRead.resultDetail.recentResultsTitle" />
<cti:standardPage page="groupMeterRead.results" module="tools">

    <tags:boxContainer title="${recentResultsTitle}" id="groupMeterReadResultsContainer" hideEnabled="false">
    <c:if test="${empty resultWrappers}">
        <p><i:inline key="yukon.common.search.noResultsFound"/></p>
    </c:if>
    <c:if test="${not empty resultWrappers}">
        <table class="compact-results-table">
            <cti:msgScope paths=".tableHeader">
            <tr>
                <th><i:inline key=".attributes"/></th>
                <th><i:inline key=".devices"/></th>
                <th style="text-align:right;"><i:inline key=".successCount"/></th>
                <th style="text-align:right;"><i:inline key=".failureCount"/></th>
                <th style="text-align:right;"><i:inline key=".unsupportedCount"/></th>
                <th></th>
                <th><i:inline key=".detail"/></th>
                <th><i:inline key=".status"/></th>
            </tr>
            </cti:msgScope>
            <c:forEach var="resultWrapper" items="${resultWrappers}">
            
            	<c:set var="resultKey" value="${resultWrapper.key.result.key}"/>
            
                <cti:url var="resultDetailUrl" value="/group/groupMeterRead/resultDetail">
                    <cti:param name="resultKey" value="${resultKey}" />
                </cti:url>
            
                <tr>
                    <td>${resultWrapper.value}</td>
                    <td><cti:msg key="${resultWrapper.key.result.deviceCollection.description}"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/SUCCESS_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/FAILURE_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/UNSUPPORTED_COUNT"/></td>
                    <td><div style="width:20px;"></div></td>
                    <td><a href="${resultDetailUrl}">View</a></td>
                    <td>
                    	<div id="statusDiv_${resultKey}">
                    		<cti:classUpdater type="GROUP_METER_READ" identifier="${resultKey}/STATUS_CLASS">
	                        	<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/STATUS_TEXT"/>
	                        </cti:classUpdater>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    </tags:boxContainer>

</cti:standardPage>