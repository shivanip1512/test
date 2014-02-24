<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="siteMap">

    <div class="column-8-8-8 tiles">
        <c:forEach items="${pageMap}" var="category" varStatus="index">
            
            <c:if test="${index.index %3 == 0}"><c:set var="column" value="column one" /></c:if>
            <c:if test="${index.index %3 == 1}"><c:set var="column" value="column two" /></c:if>
            <c:if test="${index.index %3 == 2}"><c:set var="column" value="column three nogutter" /></c:if>
    
            <cti:msg2 key="${category.key}" var="title"/>
            <tags:sectionContainer title="${title}" styleClass="${column}">
                <ul class="simple-list">
                <c:forEach var="wrapper" items="${category.value}">
                    <li>
                    <c:if test="${wrapper.enabled}">
                        <a href="<cti:url value="${wrapper.page.link}"/>"><i:inline key="${wrapper.page}"/></a>
                    </c:if>
                    <c:if test="${not wrapper.enabled}">
                        <cti:msg2 key=".noPermissions" argument="${wrapper.requiredPermissions}" var="perms"/>
                        <span title="${perms}" class="disabled"><i:inline key="${wrapper.page}"/></span>
                    </c:if>
                    </li>
                </c:forEach>
                </ul>
            </tags:sectionContainer>
        </c:forEach>
    </div>
</cti:standardPage>