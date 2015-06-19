<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.programList">

<div class="column-24">
    <div class="column one nogutter">
        <ol>
        <c:forEach var="entry" items="${programMap}">
            <li>${entry.key}&nbsp;
                <ul>
                    <c:forEach var="program" items="${entry.value}">
                        <li><a href="programDetail/${program.id}">${program.name}</a></li>
                    </c:forEach>
                </ul>
            </li>
        </c:forEach>
        </ol>
    </div>
    <cti:url value="/dr/cc/programCreate" var="createUrl"/>
    <div><cti:button nameKey="create" href="${createUrl}"/></div>
</div>

</cti:standardPage>