<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
    
<cti:standardPage module="amr" page="cre.list">

    <c:set var="singleJob" value="false"/>
    <c:if test="${backingBean.jobId > 0}">
        <c:set var="singleJob" value="true"/>
    </c:if>
    <c:if test="${singleJob}">
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:nameValue2 nameKey=".filter.type">${singleJobType}</tags:nameValue2>
        </tags:nameValueContainer2>
    </c:if>
    <cti:msg var="filterSectionText" key="yukon.web.modules.tools.schedules.all.filter.section"/>
    <%-- FILTER POPUP --%>
    <tags:simplePopup id="filterPopup" title="${filterSectionText}" on="#filterButton">
        <cti:url var="listUrl" value="/common/commandRequestExecutionResults/list"/>
        <form name="filterForm" id="filterForm" action="${listUrl}" method="get">
            <input type="hidden" name="jobId" value="${backingBean.jobId}">
            
            <tags:nameValueContainer2>            
                <tags:nameValue2 nameKey=".filter.dateFrom">
                    <dt:date name="fromDate" value="${backingBean.fromDate}" />
                </tags:nameValue2>             
                <tags:nameValue2 nameKey=".filter.dateTo">
                    <dt:date name="toDate" value="${backingBean.toDate}" />
                </tags:nameValue2>
                
                <c:if test="${!singleJob}">
                    <tags:nameValue2 nameKey=".filter.type">
                        <select name="typeFilter">
                            <option value="ANY" <c:if test="${backingBean.typeFilter eq 'ANY'}">selected</c:if>><cti:msg2 key=".filter.typeAny"/></option>
                            <c:forEach var="commandRequestExecutionType" items="${executionTypes}">
                                <option value="${commandRequestExecutionType}" title="${commandRequestExecutionType.description}" <c:if test="${(not (backingBean.typeFilter eq 'ANY')) && backingBean.typeFilter eq commandRequestExecutionType}">selected</c:if>>${commandRequestExecutionType.shortName}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue2>     
                </c:if>
            </tags:nameValueContainer2>
            
            <div class="action-area">
                <cti:button nameKey="filter" busy="true" type="submit" classes="primary action"/>
                <cti:url value="/common/commandRequestExecutionResults/list" var="clearUrl"/>
                <cti:button nameKey="clear" href="${clearUrl}" busy="true"/>
            </div>
        </form>
    </tags:simplePopup>
    
    
    <%-- RESULTS TABLE --%>
    <div id="page-buttons" class="dn"><cti:button nameKey="filter" id="filterButton" icon="icon-filter"/></div>
    <cti:url var="pageUrl" value="/common/commandRequestExecutionResults/page">
        <cti:param name="jobId" value="${backingBean.jobId}" />
        <cti:param name="typeFilter" value="${backingBean.typeFilter}" />
        <cti:param name="fromDateMillies" value="${backingBean.fromDate.getMillis()}" />
        <cti:param name="toDateMillies" value="${backingBean.toDate.getMillis()}" />
    </cti:url>
    <div data-url="${pageUrl}">
        <jsp:include page="table.jsp"/>
    </div>

</cti:standardPage>