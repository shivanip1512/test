<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="amr" page="phaseDetect.clearPhaseData">
    
    <cti:url var="clearUrl" value="/amr/phaseDetect/clear"/>
    <form action="${clearUrl}" method="post">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="clearData" styleClass="stacked">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".substation">${fn:escapeXml(substationName)}</tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <div id="clearDiv">
            <c:if test="${not empty param.errorReason}">
                <span class="error"><b><i:inline key=".errorSending" arguments="${param.errorReason}"/></b></span>
            </c:if>
        </div>
        <div class="page-action-area">
            <cti:msg2 var="clearPhaseData" key=".clearPhaseData"/>
            <cti:button id="clear" type="submit" label="${clearPhaseData}" busy="true" classes="primary action"/>
            <cti:button nameKey="cancelTest" name="cancel" type="submit"/>
        </div>
    </form>
</cti:standardPage>