<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.results">
    <cti:msgScope paths="yukon.common.device.bulk.bulkHome">
    
        <c:if test="${empty resultsList}">
            <span class="empty-list"><i:inline key=".recentBulkOperations.noResults"/></span>
        </c:if>
        
        <%-- BULK UPDATE RESULTS --%>
        <c:if test="${not empty resultsList}">
        
            <cti:msg2 var="recentBulkOperationsHeaderTitle" key=".recentBulkOperations.header"/>
            <tags:boxContainer title="${recentBulkOperationsHeaderTitle}" id="recentBulkOperationsContainer" hideEnabled="false">
            
                <cti:msg2 var="performNewActionLinkTitle" key=".recentBulkOperations.performNewActionLinkTitle"/>
                <table class="compact-results-table">
                    <thead>
                        <tr>
                            <th><cti:msg2 key=".recentBulkOperations.operationType"/></th>
                            <th><cti:msg2 key=".recentBulkOperations.updateTime"/></th>
                            <th><cti:msg2 key=".recentBulkOperations.success"/></th>
                            <th><cti:msg2 key=".recentBulkOperations.processingException"/></th>
                            <th></th>
                            <th><cti:msg2 key=".recentBulkOperations.fields"/></th>
                            <c:if test="${hasBulkImportRP || hasBulkUpdateRP || hasMassChangeRP}">
                                <th><cti:msg2 key=".recentBulkOperations.detailHeader"/></th>
                            </c:if>
                            <th><cti:msg2 key=".recentBulkOperations.status"/></th>
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
                                        
                                        <form id="${successFormName}" method="post" action="<cti:url value="/bulk/collectionActions"/>">
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
                                                  <form id="${processingExceptionCollectionActionFormName}" method="post" action="<cti:url value="/bulk/collectionActions"/>">
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
                                        <a href="${resultDetailUrl}"><cti:msg2 key=".recentBulkOperations.detailLink"/></a>
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
      document.getElementById(id).submit();
    }
    function hideShowErrorList(id) {
      var elem = $('#' + id);
      if (elem.is(':visible')) {
        elem.hide();
      } else {
        elem.show();
      }
    }
    </script>
    </cti:msgScope>
</cti:standardPage>