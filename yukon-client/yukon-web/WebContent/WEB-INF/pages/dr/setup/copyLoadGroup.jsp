<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.dr.setup.loadGroup">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/dr/setup/loadGroup/copy" />
    <form:form id="loadGroup-copy-form" action="${actionUrl}" method="post" modelAttribute="loadGroup">
        <cti:csrfToken />
        <form:hidden path="id" />
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name" />
            </tags:nameValue2>
            <tags:hidden path="type"/>
            <tags:hidden path="kWCapacity"/>
            <tags:hidden path="disableGroup"/>
            <tags:hidden path="disableControl"/>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroup.js" />