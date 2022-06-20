<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.operator.signalTransmitter">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/stars/device/signalTransmitter/${signalTransmitterId}/copy" />
    <form:form id="js-signal-transmitter-copy-form" action="${actionUrl}" method="post" modelAttribute="terminalCopy">
        <cti:csrfToken />
        <input type="hidden" name="lmCopy" value="${selectedSignalTransmitterType}"> 
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name" maxlength="60" inputClass="w300"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".copy.copyPoints">
                <tags:switchButton path="copyPoints" 
                                   offNameKey=".no.label"
                                   onNameKey=".yes.label"  
                                   checked="${terminalCopy.copyPoints}" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.assets.signalTransmitter.js" />