<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="bulk.home">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.bulkHome.header"/>
    <tags:sectionContainer title="${headerTitle}" id="bulkHomeContainer" hideEnabled="false">
        <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
            <cti:checkRolesAndProperties value="BULK_IMPORT_OPERATION">
                <div class="stacked clear">
                    <a href="/bulk/import/upload" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.bulkImportButton"/></a>
                    <i:inline key="yukon.common.device.bulk.bulkHome.bulkImportButton.description"/>
                </div>
            </cti:checkRolesAndProperties>
            
            <cti:checkRolesAndProperties value="BULK_UPDATE_OPERATION">
                <div class="stacked clear">
                    <a href="/bulk/update/upload" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.bulkUpdateButton"/></a>
                    <i:inline key="yukon.common.device.bulk.bulkHome.bulkUpdateButton.description"/>
                </div>
            </cti:checkRolesAndProperties>
            
            <cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">
                <div class="stacked clear">
                    <a href="/bulk/pointImport/upload" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.bulkPointImportButton"/></a>
                    <i:inline key="yukon.common.device.bulk.bulkHome.bulkPointImportButton.description"/>
                </div>
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>
            
        <cti:checkRolesAndProperties value="FDR_TRANSLATION_MANAGER">
            <div class="stacked clear">
                <a href="/bulk/fdrTranslationManager/home" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.manageFDRTranslationsButton"/></a>
                <i:inline key="yukon.common.device.bulk.bulkHome.manageFDRTranslationsButton.description"/>
            </div>
        </cti:checkRolesAndProperties>
        
        <cti:checkRolesAndProperties value="IMPORTER_ENABLED">
            <div class="stacked clear">
                <a href="/amr/bulkimporter/home" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.legacyImporter"/></a>
                <i:inline key="yukon.common.device.bulk.bulkHome.legacyImporter.description"/>
            </div>
        </cti:checkRolesAndProperties>
        
        <cti:checkRolesAndProperties value="CAP_CONTROL_IMPORTER">
            <div class="stacked clear">
                <a href="/capcontrol/import/view" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.voltVarImporter"/></a>
                <i:inline key="yukon.common.device.bulk.bulkHome.voltVarImporter.description"/>
            </div>
        </cti:checkRolesAndProperties>
        
        <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT">
            <div class="stacked clear">
                <a href="/stars/operator/account/accountImport" class="described"><i:inline key="yukon.common.device.bulk.bulkHome.assetsImporter"/></a>
                <i:inline key="yukon.common.device.bulk.bulkHome.assetsImporter.description"/>
            </div>
        </cti:checkRolesAndProperties>
    </tags:sectionContainer>
    
    <%-- BULK UPDATE RESULTS --%>
    <c:if test="${not empty resultsList}">
    
        <cti:msg var="recentBulkOperationsHeaderTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.header"/>
        <tags:boxContainer title="${recentBulkOperationsHeaderTitle}" id="recentBulkOperationsContainer" hideEnabled="false">
        
            <cti:msg var="performNewActionLinkTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.performNewActionLinkTitle"/>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.operationType"/></th>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.updateTime"/></th>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.success"/></th>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.processingException"/></th>
                        <th></th>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.fields"/></th>
                        <c:if test="${hasBulkImportRP || hasBulkUpdateRP || hasMassChangeRP}">
                            <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.detailHeader"/></th>
                        </c:if>
                        <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.status"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
            
                <c:forEach var="displayableResult" items="${resultsList}" varStatus="resultStatus">
                
                    <c:set var="callbackResult" value="${displayableResult.result}" />
                    <c:set var="detailViewable" value="${displayableResult.detailViewable}" />
                    <c:set var="resultsId" value="${callbackResult.resultsId}" />
                    
                    <tr>
                        <%-- TYPE --%>
                        <td>${callbackResult.backgroundProcessType.title}</td>
                        
                        <%-- START/STOP TIME --%>
                        <td>
                            <cti:formatDate value="${callbackResult.startTime}" type="BOTH"/> - <br>
                            <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/STOP_TIME"/>
                        </td>
                        
                        <%-- SUCCESS --%>
                        <td>
                            <c:choose>
                                <%-- SUCCESS COUNT --%>
                                <c:when test="${not callbackResult.successDevicesSupported}">
                                    <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/>
                                </c:when>
                                
                                <%-- SUCCESS COUNT/DEVICE COLLECTION --%>
                                <c:otherwise>
                                    <c:set var="successFormName" value="processingExceptionForm${resultsId}"/>
                                    <a href="javascript:submitForm('${successFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/></a> 
                                    <tags:selectedDevicesPopup deviceCollection="${callbackResult.successDeviceCollection}" />
                                    
                                    <form id="${successFormName}" method="post" action="/bulk/collectionActions">
                                    <cti:csrfToken/>
                                        <cti:deviceCollection deviceCollection="${callbackResult.successDeviceCollection}" />
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        
                        <%-- FAILURES --%>
                        <td>
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
      	                                    <form id="${processingExceptionCollectionActionFormName}" method="post" action="/bulk/collectionActions">
                                            <cti:csrfToken/>
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
                            <div></div>
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
                                <cti:url var="resultDetailUrl" value="/bulk/${callbackResult.backgroundProcessType.pathValue}/${callbackResult.backgroundProcessType.pathValue}Results">
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
                        <td>
                            <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/STATUS_TEXT"/>
                        </td>
                    </tr>
                
                </c:forEach>
                </tbody>
            </table>
        
        </tags:boxContainer>
        
    </c:if>
<script type="text/javascript">
function submitForm(id) {
  document.getElementById().submit();
}
function hideShowErrorList(id) {
  var elem = jQuery('#' + id);
  if (elem.is(':visible')) {
    elem.hide();
  } else {
    elem.show();
  }
}
</script>
</cti:standardPage>