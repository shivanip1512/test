<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:url var="copyUrl" value="/dr/setup/macroLoadGroup/${loadGroupId}/copy" />
<form:form id="js-copy-macro-load-group-form" action="${copyUrl}" method="post" modelAttribute="lmCopy">
    <cti:csrfToken />
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey="yukon.web.modules.dr.setup.macroLoadGroup.name">
            <tags:input path="name" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form:form>