<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="deviceGroupSync" tagdir="/WEB-INF/tags/deviceGroupSync" %>

<cti:standardPage module="multispeak" page="deviceGroupSyncHome">

	<cti:includeCss link="/WebConfig/yukon/styles/multispeak/deviceGroupSync.css"/>
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
	
	<script type="text/javascript">
	
		function toggleSyncNowControls() {
		  //assumes data is of type Hash
			return function(data) {
		        if (data.get('value') == 'true') {
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
					<form id="startForm" action="/spring/multispeak/setup/deviceGroupSync/start" method="post">
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
						<cti:msg2 key=".instructions" htmlEscape="false"/>
					</tags:formElementContainer>
				</td>
			
			</tr>
		</table>
	
	</tags:boxContainer2>
	
	<cti:dataUpdaterCallback function="toggleSyncNowControls()" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
	
</cti:standardPage>