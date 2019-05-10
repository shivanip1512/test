<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.operator.rtuDetail">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/stars/rtu/copy" />
    <form:form id="rtu-copy-form" action="${actionUrl}" method="post" modelAttribute="rtu">
        <cti:csrfToken />
        <form:hidden path="id" />
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".deviceName">
                <tags:input path="name" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".masterAddress">
                   <tags:input path="deviceAddress.masterAddress" />
             </tags:nameValue2>
             <tags:nameValue2 nameKey=".slaveAddress">
                   <tags:input path="deviceAddress.slaveAddress" />
             </tags:nameValue2>
            <tags:nameValue2 nameKey=".copy.copyPoints">
                <tags:switchButton path="copyPointFlag" offNameKey=".no.label"
                    onNameKey=".yes.label" disabled="${!isPointsAvailable}" 
                    checked="${rtu.copyPointFlag}" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.assets.rtu.js" />