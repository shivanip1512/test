<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dr" page="cc.groupList">

<div class="column-24">
    <div class="column one nogutter">
        <div>
            <ul>
            <c:forEach var="group" items="${groups}">
                <cti:url var="groupDetailUrl" value="groupDetail/${group.id}"/>
                <li><a href="${groupDetailUrl}">${fn:escapeXml(group.name)}</a></li>
            </c:forEach>
            </ul>
        </div>
    </div>
    <cti:url var="createUrl" value="/dr/cc/groupCreate"/>
    <div><cti:button nameKey="create" href="${createUrl}"/></div>
</div>
</cti:standardPage>