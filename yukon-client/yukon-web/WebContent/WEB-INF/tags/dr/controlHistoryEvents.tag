<%@ attribute name="controlHistoryEventList" required="true" type="java.util.Set"%>
<%@ attribute name="showControlSummary" required="true" type="java.lang.Boolean"%>
<%@ attribute name="consumer" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:choose>
    <c:when test="${consumer}">
        <c:set var="widthPercent" value="33%"/>
        <c:set var="columns" value="3"/>
    </c:when>
    <c:otherwise>
        <c:set var="widthPercent" value="${widthPercent}"/>
        <c:set var="columns" value="4"/>
    </c:otherwise>
</c:choose>

<cti:msgScope paths=", .controlHistoryEvent, components.controlHistoryEvent">
  <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
  <table class="compactResultsTable">
    <c:choose>
      <c:when test="${controlHistoryEventListSize > 0}">
        <tr>
          <td colspan="${columns}" width="100%">
            <table style="border-collapse: collapse;" align="center" width="100%">
              <c:set var="totalDuration" value="${0}" />
              <tr>
                <th><i18n:inline key=".startDate" /></th>
                <th><i18n:inline key=".endDate" /></th>
                <c:if test="${not consumer}"><th><i18n:inline key=".gears" /></th></c:if>
                <th><i18n:inline key=".controlDuration" /></th>
              </tr>

              <tags:alternateRowReset/>
              <c:forEach var="event" items="${controlHistoryEventList}">
                <tr class="<tags:alternateRow odd='' even='altRow'/>">
                  <td width="${widthPercent}"><cti:formatDate value="${event.startDate}" type="BOTH"/></td>
                  <c:choose>
                    <c:when test="${event.controlling}">
                        <td width="${widthPercent}">----</td>
                    </c:when>
                    <c:otherwise>
                        <td class="nonwrapping" width="${widthPercent}"><cti:formatDate value="${event.endDate}" type="BOTH"/></td>
                    </c:otherwise>
                  </c:choose>
                  <c:if test="${not consumer}">
                    <td width="${widthPercent}">${event.gears}</td>
                  </c:if>
                  <td width="${widthPercent}">
                    <cti:formatDuration type="HM" startDate="${event.startDate}" endDate="${event.endDate}" />
                  </td>
                  <c:set var="totalDuration" value="${event.duration.millis + totalDuration}" />
                </tr>
              </c:forEach>
  
              <c:if test="${showControlSummary}">
                <tr>
                  <td width="${widthPercent}"></td>
                  <td width="${widthPercent}"></td>
                  <c:if test="${not consumer}"><td width="${widthPercent}"></td></c:if>
                  <td width="${widthPercent}" class="nonwrapping" style="font-weight: bold"><cti:formatDuration type="HM" value="${totalDuration}"/></td>
                </tr>
              </c:if>
            </table>	
          </td>
        </tr>
      </c:when>
      <c:otherwise>
        <tr class="${rowClass}">
          <td colspan="${columns}"><i18n:inline key=".noControlDuringPeriod" /></td>
        </tr>
      </c:otherwise>
    </c:choose>
  </table>
</cti:msgScope>