<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.capcontrol.ivvc,modules.capcontrol.ivvc.zoneDetail">
    <tags:boxContainer2 nameKey="busView.selectedZoneInformation" arguments="${zoneDto.name}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".busView.zoneName">
                <cti:url var="url" value="/capcontrol/ivvc/zone/detail">
                    <cti:param name="zoneId" value="${zoneId}"/>
                </cti:url>
                <a href="${url}">${fn:escapeXml(zoneDto.name)}</a>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".details.table.zone">
                <i:inline key=".zone.${zoneDto.zoneType}"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".details.table.regulator">
                <table class="compact-results-table has-alerts has-actions">
                    <%@ include file="zoneRegulators.jsp" %>
                </table>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <!-- Zone Editor popup -->
        <cti:msg2 var="editorTitle" key=".zoneWizard.editor.title" />
        <cti:url var="editorUrl" value="/capcontrol/ivvc/wizard/zoneEditor">
            <cti:param name="zoneId" value="${zoneId}"/>
        </cti:url>
        <div id="zone-editor-info" data-editor-url="${editorUrl}" data-editor-title="${editorTitle}"></div>
        
        <div class="action-area">
            <cti:button classes="js-zone-editor" nameKey="edit" icon="icon-pencil"/>
        </div>
    </tags:boxContainer2>
    
    <tags:boxContainer2 nameKey="busView.selectedZoneEvents" arguments="${zoneDto.name}">
        <div id="selectedZoneEvents" class="js-events-timeline js-block-this clear" 
            data-zone-id="${zoneId}" style="height:40px"></div>
    </tags:boxContainer2>
</cti:msgScope>