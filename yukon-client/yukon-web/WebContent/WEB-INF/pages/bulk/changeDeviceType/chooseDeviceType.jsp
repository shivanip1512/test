<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.changeDeviceTypeChoose">

    <tags:bulkActionContainer key="yukon.common.device.bulk.changeDeviceTypeChoose" deviceCollection="${deviceCollection}">
    
        <cti:url var="changeTypeUrl" value="/bulk/changeDeviceType/changeDeviceType"/>
        <form method="post" action="${changeTypeUrl}">
            <cti:csrfToken/>
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- AVAILABLE TYPES --%>
            <c:choose>
              <c:when test="${!empty deviceTypes}">
                <c:set var="disabled" value="false" />
                <select name="deviceTypes">
                  <c:forEach var="deviceType" items="${deviceTypes}">
                    <option value="${deviceType.value}">${deviceType.key}</option>
                  </c:forEach>
                </select>
              </c:when>
              <c:otherwise>
                <c:set var="disabled" value="true" />
                <span class="empty-list"><cti:msg key="yukon.web.modules.tools.bulk.changeDeviceTypeChoose.noDeviceTypes"/></span>
              </c:otherwise>
            </c:choose>
            <div class="page-action-area">
              <cti:button nameKey="change" type="submit" classes="primary action" disabled="${disabled}" />
            </div>
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>