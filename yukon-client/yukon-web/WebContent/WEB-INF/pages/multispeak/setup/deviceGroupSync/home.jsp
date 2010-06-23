<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="multispeak" page="deviceGroupSyncHome">

	<cti:includeCss link="/WebConfig/yukon/styles/multispeak/deviceGroupSync.css"/>
	
	<tags:boxContainer2 nameKey="startContainer">
	
		<table class="homeLayout">
			<tr>
			
				<%-- sync now --%>
				<td class="syncNow">
					<br>
					<form id="startForm" action="/spring/multispeak/setup/deviceGroupSync/start" method="post">
						<span id="syncNowContent" class="nonwrapping">
			    		<select name="deviceGroupSyncType">
			    			<option value=""><i:inline key=".selectSyncType"/></option>
			    			<c:forEach var="type" items="${deviceGroupSyncTypes}">
			    				<option value="${type}"><cti:msg key="${type.formatKey}"/></option>
			    			</c:forEach>
			    		</select>
			    		<tags:slowInput2 key="startButton" formId="startForm"/>
			    		</span>
			    	</form>
			    	
			    	<%-- last run --%>
			    	<br>
			    	<table class="compactResultsTable">
			    	
			    		<tr><th colspan="2"><i:inline key=".lastSyncCompleted"/></th></tr>
			    	
			    		<c:forEach var="entry" items="${lastSyncInstants}">
			    			<tr>
				    			<td><cti:msg key="${entry.key}"/></td>
				    			<td>
				    				<c:choose>
				    					<c:when test="${empty entry.value}">
				    						<cti:msg2 key="defaults.na"/>
				    					</c:when>
					    				<c:otherwise>
						    				<cti:formatDate value="${entry.value}" type="FULL"/>
					    				</c:otherwise>
					    			</c:choose>
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
	
</cti:standardPage>