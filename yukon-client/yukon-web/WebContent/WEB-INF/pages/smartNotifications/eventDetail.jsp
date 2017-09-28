<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="smartNotifications" page="detail">

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
                    <dt:dateTime value="${filter.startDate}"/>
                    <dt:dateTime value="${filter.endDate}"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="action-area">
                <cti:button classes="primary action" nameKey="filter" type="submit" busy="true"/>
            </div>
            
        </div>
    </div>
    
    <table class="compact-results-table has-actions row-highlighting">
        <tr>
            <th>Device</th>
            <th>Status</th>
            <th>Timestamp</th>
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
        </tr>
        <tbody>
            <c:forEach var="event" items="${events.resultList}">
                <tr>
                    <td>
<%--                         <cti:paoDetailUrl yukonPao="${event}" newTab="true"> --%>
                            ${event.deviceName}
<%--                         </cti:paoDetailUrl> --%>
                    </td>
                    <td>${event.status}</td>
                    <td>${event.timestamp}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
        
                

</cti:standardPage>