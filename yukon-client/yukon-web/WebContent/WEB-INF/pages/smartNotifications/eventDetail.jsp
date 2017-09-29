<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="smartNotifications" page="detail">

    <cti:url var="detailUrl" value="/notifications/events/${eventType.urlPath}"/>
    <c:if test="${!empty parameter}">
        <cti:url var="detailUrl" value="/notifications/events/${eventType.urlPath}/${parameter}"/>
    </c:if>
    <form:form action="${detailUrl}" method="GET" commandName="filter">
    <cti:csrfToken/>
    <div class="column-14-10 clearfix">
        <h3>Filters</h3>
        <div class="column one" style="border:1px solid #ccc;padding:5px;">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".type">
                    <i:inline key="${eventType.formatKey}"/>
                </tags:nameValue2>
                <c:if test="${!empty monitorName}">
                    <tags:nameValue2 nameKey=".monitor">
                        ${monitorName}
                    </tags:nameValue2>
                </c:if>
                <tags:nameValue2 nameKey=".dateRange">
                    <dt:dateTime path="startDate" value="${filter.startDate}"/>
                    <dt:dateTime path="endDate" value="${filter.endDate}"/>
<%--                     <dt:dateTime value="${filter.startDate}"/>
                    <dt:dateTime value="${filter.endDate}"/> --%>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="action-area">
                <cti:button classes="primary action" nameKey="filter" type="submit" busy="true"/>
            </div>
            
        </div>
    </div>
    </form:form>
    
    <div data-url="${detailUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <tr>
                <tags:sort column="${deviceName}" />
                <tags:sort column="${type}" />
                <tags:sort column="${status}" />
                <tags:sort column="${timestamp}" />        
            </tr>
            <tbody>
                <c:forEach var="event" items="${events.resultList}">
                    <tr>
                        <td>
    <%--                         <cti:paoDetailUrl yukonPao="${event}" newTab="true"> --%>
                                ${event.deviceName}
    <%--                         </cti:paoDetailUrl> --%>
                        </td>
                        <td>
    <%--                         <tags:paoType yukonPao="${event}"/> --%>
                            ${event.type}
                        </td>
                        <td>${event.status}</td>
                        <td><cti:formatDate value="${event.timestamp}" type="BOTH"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${events}" adjustPageCount="true" thousands="true"/>
    </div>
                

</cti:standardPage>