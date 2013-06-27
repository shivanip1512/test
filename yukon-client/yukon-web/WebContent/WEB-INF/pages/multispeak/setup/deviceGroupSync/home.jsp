<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="deviceGroupSync" tagdir="/WEB-INF/tags/deviceGroupSync" %>

<cti:standardPage module="adminSetup" page="deviceGroupSyncHome">

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <c:url var="tab_url" value="/multispeak/setup/home" />
    <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.interfaces.home.tab.title" />
    <c:set var="on_tab" value='${isDevicesPage}'/>
    <cti:linkTab tabId="deviceTab" selectorName="${tab_name}" tabHref="${tab_url}"/>

    <cti:checkGlobalSetting setting="MSP_LM_MAPPING_SETUP">
        <c:url var="tab_url" value="/multispeak/setup/lmMappings/home" />
        <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.lmMappings.tab.title" />
        <c:set var="on_tab" value='${isLoadMngtPage}'/>
        <cti:linkTab tabId="loadMgtTab" selectorName="${tab_name}" tabHref="${tab_url}"/>
    </cti:checkGlobalSetting>

    <c:url var="tab_url" value="/multispeak/setup/deviceGroupSync/home" />
    <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title" />
    <cti:linkTab tabId="deviceGroupTab" selectorName="${tab_name}" tabHref="${tab_url}" initiallySelected="${true}"/>
</cti:linkTabbedContainer>

	<cti:includeCss link="/WebConfig/yukon/styles/multispeak/deviceGroupSync.css"/>
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
	
	<script type="text/javascript">
	
		function toggleSyncNowControls() {
		  //assumes data is of type Hash
			return function(data) {
		        if (data.value === 'true') {
		            $('deviceGroupSyncTypeSelect').disable();
		            $('startButton').disabled = true;
		        } else {
		        	$('deviceGroupSyncTypeSelect').enable();
		            $('startButton').disabled = false;
			    }
		    };
		}
	
	</script>
	
	<tags:boxContainer2 nameKey="startContainer">
	
		<table class="homeLayout">
			<tr>
			
				<%-- sync now --%>
				<td class="syncNow">
					<br>
					<form id="startForm" action="/multispeak/setup/deviceGroupSync/start" method="post">
						<span id="syncNowContent" class="nonwrapping">
			    		<select name="deviceGroupSyncType" id="deviceGroupSyncTypeSelect">
			    			<c:forEach var="type" items="${deviceGroupSyncTypes}">
			    				<option value="${type}"><cti:msg key="${type.formatKey}"/></option>
			    			</c:forEach>
			    		</select>
			    		<button id="startButton" class="formSubmit">
			    			<i:inline key=".startButton"/>
			    		</button>
			    		</span>
			    	</form>
			    	
			    	<%-- last run --%>
			    	<br>
			    	<table class="compactResultsTable lastSync">
			    	
			    		<tr><th colspan="2"><i:inline key=".lastSyncCompleted"/></th></tr>
			    	
			    		<c:forEach var="lastRunTimestampValue" items="${lastRunTimestampValues}">
			    			<tr>
				    			<td class="type"><cti:msg key="${lastRunTimestampValue.type}"/></td>
				    			<td>
				    				<deviceGroupSync:lastCompletedSyncLink lastRunTimestampValue="${lastRunTimestampValue}"/>
				    			</td>
				    		</tr>
			    		</c:forEach>
			    	</table>
				</td>
				
				<%-- instructions --%>
				<td class="instructions">
					<tags:formElementContainer nameKey="instructionsContainer">
						<cti:msg2 key=".instructions"/>
					</tags:formElementContainer>
				</td>
			
			</tr>
		</table>
	
	</tags:boxContainer2>
	
	<cti:dataUpdaterCallback function="toggleSyncNowControls()" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
	
</cti:standardPage>