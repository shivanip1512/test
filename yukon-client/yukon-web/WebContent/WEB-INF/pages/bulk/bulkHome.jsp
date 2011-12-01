<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="deviceselection" />
    
    <script type="text/javascript">
        function submitForm(id) {
            $(id).submit();
        }
        
        function hideShowErrorList(id) {
            $(id).toggle();
        }
    </script>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.bulkHome.header"/>
    <tags:boxContainer title="${headerTitle}" id="bulkHomeContainer" hideEnabled="false">
    
        
        <table cellspacing="10">
            
            <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
            
            <cti:checkProperty property="operator.DeviceActionsRole.BULK_IMPORT_OPERATION">
            <tr>
                <td>
                    <form id="bulkImportForm" method="get" action="/spring/bulk/import/upload">
                        <input type="button" id="massChangeTypeButton" value="Bulk Import" onclick="$('bulkImportForm').submit();" style="width:170px;"/>
                    </form>
                </td>
                <td>Import a file to create meters.</td>
            </tr>
            </cti:checkProperty>
            
            
            <cti:checkProperty property="operator.DeviceActionsRole.BULK_UPDATE_OPERATION">
            <tr>
                <td>
                    <form id="bulkUpdateForm" method="get" action="/spring/bulk/update/upload">
                        <input type="button" id="massChangeTypeButton" value="Bulk Update" onclick="$('bulkUpdateForm').submit();" style="width:170px;"/>
                    </form>
                </td>
                <td>Import a file to update existing meters.</td>
            </tr>
            </cti:checkProperty>
            
            </cti:checkRole>
            
            <tr>
                <td>
                    <form id="createCollectionForm" method="get" action="/spring/bulk/deviceSelection">
                        <input type="button" id="massChangeTypeButton" value="Create Collection" onclick="$('createCollectionForm').submit();" style="width:170px;"/>
                    </form>
                </td>
                <td>Create a collection of meters to perform actions upon.</td>
            </tr>

            <cti:checkProperty property="operator.DeviceActionsRole.FDR_TRANSLATION_MANAGER">
            <tr>
                <td>
                    <form id="manageFdrTranslationsForm" method="post"
                        action="/spring/bulk/fdrTranslationManager/home">
                        <input type="button" id="massChangeTypeButton"
                            value="Manage FDR Translations"
                            onclick="$('manageFdrTranslationsForm').submit();"
                            style="width: 170px;" />
                    </form>
                </td>
                <td>Import, export or delete FDR translations.</td>
            </tr>
            </cti:checkProperty>
        </table>
        
    </tags:boxContainer>
    
    
    <%-- BULK UPDATE RESULTS --%>
    <c:if test="${not empty resultsList}">
    
        <br>
        <cti:msg var="recentBulkOperationsHeaderTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.header"/>
        <tags:boxContainer title="${recentBulkOperationsHeaderTitle}" id="recentBulkOperationsContainer" hideEnabled="false">
        
            <cti:msg var="performNewActionLinkTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.performNewActionLinkTitle"/>
            <cti:url var="downloadImg" value="/WebConfig/yukon/Icons/download_file.gif"/>
            
            <div style="width:75%">
            <table class="compactResultsTable" style="vertial-align:bottom;">
    
                <tr>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.operationType"/></th>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.updateTime"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.success"/></th>
                    <th style="text-align:right;padding-left:10px;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.processingException"/></th>
                    <th></th>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.fields"/></th>
                    <c:if test="${hasBulkImportRP || hasBulkUpdateRP || hasMassChangeRP}">
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.detailHeader"/></th>
                    </c:if>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.status"/></th>
                </tr>
            
                <c:forEach var="displayableResult" items="${resultsList}" varStatus="resultStatus">
                
                    <c:set var="callbackResult" value="${displayableResult.result}" />
                    <c:set var="detailViewable" value="${displayableResult.detailViewable}" />
                    <c:set var="resultsId" value="${callbackResult.resultsId}" />
                    
                    <tr valign="bottom">
                    
                        <%-- TYPE --%>
                        <td>
                            ${callbackResult.backgroundProcessType.title}
                        </td>
                        
                        
                        <%-- START/STOP TIME --%>
                        <td>
                            <cti:formatDate value="${callbackResult.startTime}" type="BOTH"/> - <br>
                            <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/STOP_TIME"/>
                        </td>
                        
                        
                        
                        <%-- SUCCESS --%>
                        <td align="right">
                        
                            <c:choose>
                                
                                <%-- SUCCESS COUNT --%>
                                <c:when test="${not callbackResult.successDevicesSupported}">
                                    <div style="font-size:12px;padding-right:20px;">
                                        <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/>
                                    </div>                  
                                </c:when>
                                
                                <%-- SUCCESS COUNT/DEVICE COLLECTION --%>
                                <c:otherwise>
                                
                                    <c:set var="successFormName" value="processingExceptionForm${resultsId}"/>
                                    
                                    <a href="javascript:submitForm('${successFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/></a> 
                                    <tags:selectedDevicesPopup deviceCollection="${callbackResult.successDeviceCollection}" />
                                    
                                    <form id="${successFormName}" method="post" action="/spring/bulk/collectionActions">
                                        <cti:deviceCollection deviceCollection="${callbackResult.successDeviceCollection}" />
                                    </form>
                                    
                                </c:otherwise>
                                
                            </c:choose>
                            
                        </td>
                        
                        
                        
                        <%-- FAILURES --%>
                        <td align="right">
                        
                        	<table>
                        	<tr>
                        	
                        		<td>
                        			<c:choose>
                        			
                        			<%-- FAILURE COUNT --%>
                        			<c:when test="${not callbackResult.failureDevicesSupported}">
	                                	<td>
	                                	<div style="font-size:12px;padding-right:20px;">
	                                		<cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/>
										</div>
										</td>
	                                </c:when>
	                                
	                                <%-- FAILURE COUNT/DEVICE COLLECTION --%>
	                                <c:otherwise>
                                
	                                	<td>
	                                    <c:set var="processingExceptionCollectionActionFormName" value="processingExceptionCollectionActionForm${resultsId}"/>
	                                
	                                    <a href="javascript:submitForm('${processingExceptionCollectionActionFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/></a> 
	                                    
	                                    <tags:selectedDevicesPopup deviceCollection="${callbackResult.failureDeviceCollection}" />
	                                    
	                                    <form id="${processingExceptionCollectionActionFormName}" method="post" action="/spring/bulk/collectionActions">
	                                        <cti:deviceCollection deviceCollection="${callbackResult.failureDeviceCollection}" />
	                                    </form>
	                                    </td>
	                                    
	                                </c:otherwise>
	                            	
	                            	</c:choose>
                        		</td>
                        
                        		<%-- FAILURE FILE DOWNLOAD --%>
                            	<td>
                            		<c:if test="${callbackResult.failureFileSupported}">
                               			<tags:downloadBulkFailuresFile resultsId="${resultsId}" showText="false" />
                           			</c:if>
                                </td>

                           	</tr>
                           	</table>
                            
                        </td>
                        
                        
                        <%-- BULK FIELDS --%>
                        <td>
                            <div style="width:20px;"></div>
                        </td>
                        <td>
                        	<c:if test="${displayableResult.bulkFieldColumnHeaders != null}">
	                            <c:forEach var="field" items="${displayableResult.bulkFieldColumnHeaders}" varStatus="fieldStatus">
	                                ${field}<c:if test="${fieldStatus.count < fn:length(displayableResult.bulkFieldColumnHeaders)}">,</c:if>
	                            </c:forEach>
                            </c:if>
                        </td>
                        
                        <%-- DEATIL LINK --%>
                        <c:choose>
                            <c:when test="${detailViewable}">
                                <cti:url var="resultDetailUrl" value="/spring/bulk/${callbackResult.backgroundProcessType.pathValue}/${callbackResult.backgroundProcessType.pathValue}Results">
                                    <cti:param name="resultsId" value="${resultsId}" />
                                </cti:url>
                                <td>
                                    <a href="${resultDetailUrl}"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.detailLink"/></a>
                                </td>
                            </c:when>
                            <c:when test="${hasBulkImportRP || hasBulkUpdateRP || hasMassChangeRP}">
                                <td></td>
                            </c:when>
                            <c:otherwise>
                            </c:otherwise>
                            
                        </c:choose>
                        
                        <%-- COMPLETE? --%>
                        <td align="right">
                            <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/STATUS_TEXT"/>
                        </td>
                    </tr>
                
                </c:forEach>
            
            </table>
            </div>
        
        </tags:boxContainer>
        
    </c:if>
    
</cti:standardPage>