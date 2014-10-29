<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.groupList">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-24">
    <h3>Group Setup</h3>
    <div class="column one nogutter">
        <div>
            <ul>
            <c:forEach var="group" items="${groups}">
                <li><a href="groupDetail/${group.id}">${group.name}</a></li>
            </c:forEach>
            </ul>
        </div>
    </div>
    <cti:url value="/dr/cc/groupCreate" var="createUrl"/>
    <div><cti:button nameKey="create" href="${createUrl}"/></div>
</div>
</cti:msgScope>
</cti:standardPage>