<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings">

<script>
$(document).ready(function(){
$('#download').click(function (ev) {
    var url = $('#duration :selected').val();
    $('#download').attr('data-href', yukon.url(url));
});
});
</script>

<input class="js-popup-title" type="hidden" value="${fn:escapeXml(title)}"> 

<div class="form-control clearfix">
    <c:if test="${points.size() > 0}">
        <span class="detail">${resultLimit}</span>
    </c:if>
    <div class="fr">
    <tags:nameValueContainer2>
       <tags:nameValue2 nameKey=".downloadPeriod">
           <select id="duration">
               <c:forEach var="duration" items="${duration}">
                   <option value="${duration.value}">${duration.key}</option>
               </c:forEach>
           </select>
        
           <cti:button id='download' classes="fr left" renderMode="buttonImage" icon="icon-page-white-excel"/>
       </tags:nameValue2>
    </tags:nameValueContainer2>
    </div>
</div>

<c:choose>
    <c:when test="${empty points}">
        <div class="empty-list"><i:inline key=".notFound"/></div>
    </c:when>
    <c:otherwise>
        <cti:url value="/meter/historicalReadings/values" var="viewUrl">
            <cti:param name="pointId" value="${pointId}"/>
        </cti:url>
        <div data-url="${viewUrl}"><%@ include file="values.jsp" %></div>
    </c:otherwise>
</c:choose>
</cti:msgScope>