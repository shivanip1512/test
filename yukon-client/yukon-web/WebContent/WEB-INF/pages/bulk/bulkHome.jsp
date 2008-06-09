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
        
            <tr>
                <td>
                    <form id="bulkImportForm" method="post" action="/spring/bulk/import/upload">
                        <input type="button" id="massChangeTypeButton" value="Bulk Import" onclick="$('bulkImportForm').submit();" style="width:140px;"/>
                    </form>
                </td>
                <td>Import a file to create meters.</td>
            </tr>
            
            <tr>
                <td>
                    <form id="bulkUpdateForm" method="post" action="/spring/bulk/update/upload">
                        <input type="button" id="massChangeTypeButton" value="Bulk Update" onclick="$('bulkUpdateForm').submit();" style="width:140px;"/>
                    </form>
                </td>
                <td>Import a file to update existing meters.</td>
            </tr>
            
            <tr>
                <td>
                    <form id="createCollectionForm" method="post" action="/spring/bulk/deviceSelection">
                        <input type="button" id="massChangeTypeButton" value="Create Collection" onclick="$('createCollectionForm').submit();" style="width:140px;"/>
                    </form>
                </td>
                <td>Create a collection of meters to perform actions upon.</td>
            </tr>
            
        </table>
        
    </tags:boxContainer>
    
    
    <%-- BULK UPDATE RESULTS --%>
    <c:if test="${not empty bulkUpdateOperationResultsList}">
    
        <br>
        <cti:msg var="recentBulkOperationsHeaderTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.header"/>
        <tags:boxContainer title="${recentBulkOperationsHeaderTitle}" id="recentBulkOperationsContainer" hideEnabled="false">
        
            <cti:msg var="performNewActionLinkTitle" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.performNewActionLinkTitle"/>
        
            <div style="width:75%">
            <table class="compactResultsTable" style="vertial-align:bottom;">
    
                <tr>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.operationType"/></th>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.updateTime"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.success"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.processingException"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.mappingException"/></th>
                    <th></th>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.fields"/></th>
                    <th><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.detailHeader"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.status"/></th>
                </tr>
            
                <c:forEach var="b" items="${bulkUpdateOperationResultsList}" varStatus="resultStatus">
                
                    <tr valign="bottom">
                    
                        <%-- TYPE --%>
                        <td>
                            ${b.bulkOperationType.title}
                        </td>
                        
                        
                        <%-- START/STOP TIME --%>
                        <td>
                            <cti:formatDate value="${b.startTime}" type="BOTH"/> - <br>
                            <cti:dataUpdaterValue type="BULKRESULT" identifier="${b.resultsId}/STOP_TIME"/>
                        </td>
                        
                        
                        
                        <%-- SUCCESS --%>
                        <td align="right">
                            <c:set var="successFormName" value="processingExceptionForm${b.resultsId}"/>
                            
                            <a href="javascript:submitForm('${successFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="BULKRESULT" identifier="${b.resultsId}/SUCCESS_COUNT"/></a> 
                            <tags:selectedDevicesPopup deviceCollection="${b.successDeviceCollection}" />
                            
                            <form id="${successFormName}" method="post" action="/spring/bulk/collectionActions">
                                <cti:deviceCollection deviceCollection="${b.successDeviceCollection}" />
                            </form>
                        </td>
                        
                        
                        
                        <%-- PROCESSING EXCEPTION --%>
                        <td align="right">
                            <c:set var="processingExceptionFormName" value="processingExceptionForm${b.resultsId}"/>
                            
                            <a href="javascript:submitForm('${processingExceptionFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="BULKRESULT" identifier="${b.resultsId}/PROCESSING_EXCEPTION_COUNT"/></a> 
                            <tags:selectedDevicesPopup deviceCollection="${b.processingExceptionDeviceCollection}" />
                            
                            <form id="${processingExceptionFormName}" method="post" action="/spring/bulk/collectionActions">
                                <cti:deviceCollection deviceCollection="${b.processingExceptionDeviceCollection}" />
                            </form>
                        </td>
                        
                        <%-- MAPPING EXCEPTION --%>
                        <td align="right">
                            <cti:dataUpdaterValue type="BULKRESULT" identifier="${b.resultsId}/MAPPING_EXCEPTION_COUNT"/>
                        </td>
                        
                        
                        
                        <%-- FIELDS --%>
                        <td>
                            <div style="width:20px;"></div>
                        </td>
                        <td>
                            <c:forEach var="field" items="${b.bulkFieldColumnHeaders}" varStatus="fieldStatus">
                                ${field}<c:if test="${fieldStatus.count < fn:length(b.bulkFieldColumnHeaders)}">,</c:if>
                            </c:forEach>
                        </td>
                        
                        <%-- DEATIL LINK --%>
                        <c:url var="resultDetailUrl" value="/spring/bulk/${b.bulkOperationType.name}/${b.bulkOperationType.name}Results">
                            <c:param name="resultsId" value="${b.resultsId}" />
                        </c:url>
                
                        <td>
                            <a href="${resultDetailUrl}"><cti:msg key="yukon.common.device.bulk.bulkHome.recentBulkOperations.detailLink"/></a>
                        </td>
                        
                        <%-- COMPLETE? --%>
                        <td align="right">
                            <cti:dataUpdaterValue type="BULKRESULT" identifier="${b.resultsId}/IS_COMPLETE"/>
                        </td>
                    </tr>
                
                </c:forEach>
            
            </table>
            </div>
        
        </tags:boxContainer>
        
    </c:if>
    
</cti:standardPage>