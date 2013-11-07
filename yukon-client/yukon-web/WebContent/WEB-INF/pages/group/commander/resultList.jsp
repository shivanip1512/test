<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="groupCommand.results" module="tools">

    <tags:boxContainer title="Recent Group Command Processing Results" id="commandProcessingContainer" hideEnabled="false">
        
    <c:if test="${empty resultList}">
        <p><i:inline key="yukon.common.search.noResultsFound"/></p>
    </c:if>
    <c:if test="${not empty resultList}">
        <table class="compact-results-table">
            <cti:msgScope paths=".tableHeader">
            <tr>
                <th><i:inline key=".command"/></th>
                <th><i:inline key=".devices"/></th>
                <th style="text-align:right;"><i:inline key=".successCount"/></th>
                <th style="text-align:right;"><i:inline key=".failureCount"/></th>
                <th></th>
                <th><i:inline key=".detail"/></th>
                <th><i:inline key=".status"/></th>
            </tr>
            </cti:msgScope>

            <c:forEach items="${resultList}" var="result">
            
                <cti:url var="resultDetailUrl" value="/group/commander/resultDetail">
                    <cti:param name="resultKey" value="${result.key}" />
                </cti:url>
            
                <tr>
                    <td>${result.command}</td>
                    <td><cti:msg key="${result.deviceCollection.description}"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></td>
                    <td><div style="width:20px;"></div></td>
                    <td><a href="${resultDetailUrl}">View</a></td>
                    <td>
                    	<div id="statusDiv_${result.key}">
                    		<cti:classUpdater type="COMMANDER" identifier="${result.key}/STATUS_CLASS">
	                        	<cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/STATUS_TEXT"/>
	                        </cti:classUpdater>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    </tags:boxContainer>
    
</cti:standardPage>