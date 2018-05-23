<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="tdc.display.${mode}">

    <div id="page-buttons" class="dn">
        <c:if test="${display.acknowledgable}">
            <tags:dynamicChoose updaterString="TDC/ALARM_DISPLAY/${display.displayId}" suffix="${display.displayId}">
                <tags:dynamicChooseOption optionId="MULT_ALARMS">
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" 
                        data-display-id="${display.displayId}" 
                        icon="icon-tick" 
                        classes="js-tdc-ack-alarms"/>
                </tags:dynamicChooseOption>
                <tags:dynamicChooseOption optionId="NO_ALARMS"> 
                    <cti:button nameKey="tdc.alarm.acknowledgeAll" 
                        data-display-id="${display.displayId}" 
                        icon="icon-tick" 
                        classes="js-tdc-ack-alarms dn"/>
                </tags:dynamicChooseOption>
            </tags:dynamicChoose>
        </c:if>
        <c:if test="${display.type != cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
            <cti:url var="download" value="/tools/data-viewer/${display.displayId}/download">
                <cti:param name="date" value="${backingBean.date}"/>
                <cti:param name="alarmFilter" value="${backingBean.alarmFilter}"/>
            </cti:url>               
            <cti:button nameKey="download" href="${download}" icon="icon-page-white-excel"/>
        </c:if>
    </div>
    <c:if test="${display.type == cti:constantValue('com.cannontech.common.tdc.model.DisplayType.CUSTOM_DISPLAYS')}">
    
        <div id="page-actions" class="dn">    
            <cti:url var="createUrl" value="/tools/data-viewer/create"/>
            <cm:dropdownOption key="components.button.create.label" href="${createUrl}" icon="icon-plus-green"/>
            <cti:url var="editUrl" value="/tools/data-viewer/${display.displayId}/edit"/>
            <cm:dropdownOption key="components.button.edit.label" href="${editUrl}" icon="icon-pencil"/>
            <cti:url var="copy" value="/tools/data-viewer/${display.displayId}/copy"/>
            <cti:msg2 key=".copy" var="copyTitle"/>
                <cm:dropdownOption key="yukon.web.modules.tools.tdc.copy" icon="icon-disk-multiple"
            data-display-id="${display.displayId}" data-copy-title="${copyTitle}" 
            classes="js-tdc-copy"/>
            <cm:dropdownOption id="deleteCustomDisplay_${display.displayId}" key=".display.DELETE" icon="icon-cross"
                data-display-id="${display.displayId}" data-ok-event="yukon:display:remove"/>
            <d:confirm on="#deleteCustomDisplay_${display.displayId}" nameKey="confirmDelete" argument="${display.name}"/>
            <li class="divider"/>
            <cti:url var="download" value="/tools/data-viewer/${display.displayId}/download"/>
            <cm:dropdownOption key=".download" href="${download}" icon="icon-page-white-excel"/>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${pageable}">   
            <%@ include file="javaSortedTable.jsp" %>
        </c:when>
        <c:otherwise>   
            <%@ include file="jsSortedTable.jsp" %>
        </c:otherwise>
    </c:choose>
    <tags:simplePopup id="tdc-popup" title=""/>
    <tags:simplePopup id="manual-entry-popup" title=""/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.tdc.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.points.js"/>
</cti:standardPage>