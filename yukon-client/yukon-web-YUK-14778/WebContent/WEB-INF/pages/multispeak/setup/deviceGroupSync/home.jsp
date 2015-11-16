<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="deviceGroupSync" tagdir="/WEB-INF/tags/deviceGroupSync" %>

<cti:standardPage module="adminSetup" page="deviceGroupSyncHome">

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>

    <cti:checkGlobalSetting setting="MSP_LM_MAPPING_SETUP">
        <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.modules.adminSetup.lmMappings.tab.title">
            <c:url value="/multispeak/setup/lmMappings/home" />
        </cti:linkTab>
    </cti:checkGlobalSetting>

    <cti:linkTab tabId="deviceGroupTab" selectorKey="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/deviceGroupSync/home" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    
    <script type="text/javascript">
    
        function toggleSyncNowControls() {
            return function(data) {
                if (data.value === 'true') {
                    $('#deviceGroupSyncTypeSelect').prop('disabled', true);
                    $('#startButton').prop('disabled', true);
                } else {
                    $('#deviceGroupSyncTypeSelect').prop('disabled', false);
                    $('#startButton').prop('disabled', false);
                }
            };
        }
    
    </script>
    
    <div class="column-12-12">
        <div class="column one">
          <cti:url var="submitUrl" value="/multispeak/setup/deviceGroupSync/start"/>
            <form id="startForm" action="${submitUrl}" method="post">
                <cti:csrfToken/>
                <select name="deviceGroupSyncType" id="deviceGroupSyncTypeSelect">
                    <c:forEach var="type" items="${deviceGroupSyncTypes}">
                        <option value="${type}"><cti:msg key="${type.formatKey}"/></option>
                    </c:forEach>
                </select>
                <cti:msg2 key=".startButton" var="label"/>
                <cti:button id="startButton" label="${label}" classes="primary action" type="submit"/>
            </form>
        </div>
        <div class="column two nogutter">
            <%-- last run --%>
            <table class="compact-results-table lastSync">
                <thead>
                    <tr><th colspan="2"><i:inline key=".lastSyncCompleted"/></th></tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="lastRunTimestampValue" items="${lastRunTimestampValues}">
                        <tr>
                            <td class="type"><cti:msg key="${lastRunTimestampValue.type}"/></td>
                            <td>
                                <deviceGroupSync:lastCompletedSyncLink lastRunTimestampValue="${lastRunTimestampValue}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <%-- instructions --%>
    <tags:sectionContainer2 nameKey="instructionsContainer">
        <cti:msg2 key=".instructions"/>
    </tags:sectionContainer2>
    
    <cti:dataUpdaterCallback function="toggleSyncNowControls()" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
    
</cti:standardPage>