<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <tags:dynamicChoose updaterString="DR_PROGRAM/${paoId}/SHOW_ACTION" suffix="${paoId}">
        <dr:programListAction program="${pao}" state="unknown"/>
        <dr:programListAction program="${pao}" state="runningEnabled"/>
        <dr:programListAction program="${pao}" state="scheduledEnabled"/>
        <dr:programListAction program="${pao}" state="enabled"/>
        <dr:programListAction program="${pao}" state="runningDisabled"/>
        <dr:programListAction program="${pao}" state="scheduledDisabled"/>
        <dr:programListAction program="${pao}" state="disabled"/>
    </tags:dynamicChoose>
</cti:checkPaoAuthorization>                    
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
    <span class="subtleGray" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
    </span>
    <span class="subtleGray" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
