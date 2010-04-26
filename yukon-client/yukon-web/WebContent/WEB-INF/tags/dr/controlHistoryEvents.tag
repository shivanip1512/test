<%@ attribute name="controlHistoryEventList" required="true" type="java.util.List"%>
<%@ attribute name="showControlSummary" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:msgScope paths=".controlHistoryEvent, components.controlHistoryEvent">
  <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
  <table class="compactResultsTable">
    <c:choose>
      <c:when test="${controlHistoryEventListSize > 0}">
        <tr>
          <td colspan="3" width="100%">
            <table style="border-collapse: collapse;" align="center" width="100%">
              <c:set var="totalDuration" value="${0}" />
              <tr>
                <th><i18n:inline key=".startDate" /></th>
                <th><i18n:inline key=".endDate" /></th>
                <th><i18n:inline key=".controlDuration" /></th>
              </tr>

              <c:forEach var="event" items="${controlHistoryEventList}">
                <tr class="<tags:alternateRow odd='' even='altRow'/>">
                  <td class="nowrapping" width="33%"><cti:formatDate  value="${event.startDate}" type="BOTH"/></td>
                  <td class="nowrapping" width="33%"><cti:formatDate  value="${event.endDate}" type="BOTH"/></td>
                  <td class="nowrapping" width="33%"><cti:formatDuration type="HM" startDate="${event.startDate}" endDate="${event.endDate}" /></td>
                  <c:set var="totalDuration" value="${event.duration + totalDuration}" />
                </tr>
              </c:forEach>
  
              <c:if test="${showControlSummary}">
                <tr>
                  <td width="33%"></td>
                  <td width="33%"></td>
                  <td width="33%" class="nowrapping" style="font-weight: bold"><cti:formatDuration type="HM" value="${totalDuration *1000}"/></td>
                </tr>
              </c:if>
            </table>	
          </td>
        </tr>
      </c:when>
      <c:otherwise>
        <tr class="${rowClass}">
          <td class="nowrapping" colspan="3"><i18n:inline key=".noControlDuringPeriod" /></td>
        </tr>
      </c:otherwise>
    </c:choose>
  </table>
</cti:msgScope>