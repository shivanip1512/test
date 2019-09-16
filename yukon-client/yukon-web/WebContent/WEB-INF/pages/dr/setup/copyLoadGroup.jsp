<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.dr.setup.loadGroup">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/dr/setup/loadGroup/${loadGroupId}/copy" />
    <form:form id="loadGroup-copy-form" action="${actionUrl}" method="post" modelAttribute="lmCopy">
        <cti:csrfToken />
        <input type="hidden" name="lmCopy" value="${selectedSwitchType}"> 
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name" maxlength="60" inputClass="pao-name-width"/>
            </tags:nameValue2>
            <c:if test="${not empty routes}">
                <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="liteID" 
                nameKey=".route" path="routeId" defaultItemLabel="${route.paoName}" defaultItemValue="${route.liteID}"/>
            </c:if>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroup.js" />