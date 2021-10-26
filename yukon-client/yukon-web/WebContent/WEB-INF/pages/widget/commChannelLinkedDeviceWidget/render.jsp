<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,yukon.web.modules.operator.commChannelLinkedDeviceWidget">
    <c:choose>
        <c:when test="${not empty searchResult.resultList}">
          <cti:url var="linkedDeviceUrl" value="/widget/commChannelLinkedDeviceWidget/render">
              <cti:param name="deviceId" value="${deviceId}"/>
          </cti:url>
          <div data-url="${linkedDeviceUrl}">
              <table class="compact-results-table row-highlighting results-table">
                  <thead>
                      <tr>
                          <tags:sort column="${name}" />
                          <tags:sort column="${type}" />
                          <tags:sort column="${status}" />
                      </tr>
                  </thead>
                  <tbody>
                     <c:forEach var="device" items="${searchResult.resultList}">
                         <c:set var="cssClass" value="error" />
                         <cti:msg2 var="deviceStatus" key="yukon.common.disabled"/>
                         <c:if test="${device.enable}">
                              <c:set var="cssClass" value="success" />
                              <cti:msg2 var="deviceStatus" key="yukon.common.enabled"/>
                         </c:if>
                         <tr>
                            <td>
                              <cti:paoDetailUrl paoId="${device.deviceId}">${fn:escapeXml(device.deviceName)}</cti:paoDetailUrl>
                            </td>
                            <td><i:inline key="${device.deviceType}"/></td>
                             
                            <td class="${cssClass}">${deviceStatus}</td>
                         </tr>
                     </c:forEach>
                  </tbody>
              </table>
              <tags:pagingResultsControls result="${searchResult}" adjustPageCount="true"/>
          </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:otherwise>
    </c:choose>
</cti:msgScope>