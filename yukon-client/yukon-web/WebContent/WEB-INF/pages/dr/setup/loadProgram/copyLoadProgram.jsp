<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.loadProgram">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/dr/setup/loadProgram/${loadProgramId}/copy" />
    <form:form id="loadProgram-copy-form" action="${actionUrl}" method="post" modelAttribute="programCopy">
        <cti:csrfToken />
        <input type="hidden" id="js-selected-copy-program" value="${selectedSwitchType}"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operationalState">
                <tags:selectWithItems items="${operationalStates}" id="operationalState" path="operationalState"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".constraint">
                <tags:selectWithItems items="${constraints}" id="constraint" path="constraint.constraintId"  itemLabel="name" itemValue="id"/>
            </tags:nameValue2>
            <cti:checkRolesAndProperties value="ALLOW_MEMBER_PROGRAMS">
                <tags:nameValue2 nameKey=".copyMemberControl">
                    <tags:switchButton path="copyMemberControl" />
                </tags:nameValue2>
            </cti:checkRolesAndProperties>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.program.js" />