<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage title="Group Processing" module="amr">

    <cti:standardMenu menuSelection="devicegroups|commander"/>

       	<cti:breadCrumbs>
    	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    	    <cti:crumbLink url="/spring/group/editor/home" title="Device Groups" />
    	    <cti:crumbLink title="Group Command Processing"/>
    	</cti:breadCrumbs>
        
        <cti:includeScript link="/JavaScript/commanderPrompter.js"/>

        <script type="text/javascript">
        	
        	function selectCommand() {
        		$('commandString').value = $F('commandSelect');
        	}
        	
        	Event.observe (window, 'load', selectCommand);
    
            function validateGroupIsSelected(btn, alertText) {
        
                if ($('groupName').value == '') {
                    alert(alertText);
                    return false;
                }
                
                $('submitGroupCommanderButton').disable();
                $('waitImg').show();
                $('groupCommanderForm').submit();
            }
        
        </script>
		
		<h2>Group Command Processing</h2>
    
    	<c:if test="${param.errorMsg != null}">
    		<div style="color: red;margin: 10px 0px;">Error: <spring:escapeBody htmlEscape="true">${param.errorMsg}</spring:escapeBody></div>
    		<c:set var="errorMsg" value="" scope="request"/>
    	</c:if>
	
    	<br>
    	<div style="width: 700px;">
        
            <form id="groupCommanderForm" action="<cti:url value="/spring/group/commander/executeGroupCommand" />" method="post">
        
            <%-- SELECT COMMAND --%>
            <div class="largeBoldLabel">Select Command:</div>
            
            <c:choose>
                <c:when test="${fn:length(commands) <= 0}">
                    No pre-defined commands authorized for this user.
                </c:when>
                
                <c:otherwise>
                    <select id="commandSelect" name="commandSelect" onChange="loadCommanderCommand(this, 'commandString');">
                        <c:forEach var="commandOption" items="${commands}">
                        
                            <c:if test="${commandOption.command == command}">
                                <c:set var="selected" value="selected" scope="page"></c:set>
                            </c:if>
                            <c:if test="${commandOption.command != command}">
                                <c:set var="selected" value="" scope="page"></c:set>
                            </c:if>
                        
                            <option value="${fn:escapeXml(commandOption.command)}" ${selected}> ${commandOption.label}</option>
                        </c:forEach>
                    </select>
                </c:otherwise>
            </c:choose>
            <br>
            <input style="margin-top: .25em;" type="text" id="commandString" name="commandString" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="40" />
            
            <%-- SELECT DEVICE GROUP TREE INPUT --%>
            <br><br>
            <div class="largeBoldLabel">Group:</div>
            
            <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" />
            <ext:nodeValueSelectingInlineTree   fieldId="groupName" 
                                                fieldName="groupName"
                                                nodeValueName="groupName" 
                                                multiSelect="false"
                                                id="selectGroupTree" 
                                                dataJson="${dataJson}" 
                                                width="500"
                                                height="400" treeAttributes="{'border':true}" />
                                                
            Email Address (optional): <input type="text" name="emailAddress" value="" size="40">
                                                
            
                  
                  
            <%-- EXECUTE BUTTON --%>
            <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
            <cti:url var="waitImgUrl" value="/WebConfig/yukon/Icons/indicator_arrows.gif" />
            
            <input type="button" id="submitGroupCommanderButton" value="Execute" onclick="return validateGroupIsSelected(this, '${cti:escapeJavaScript(noGroupSelectedAlertText)}');">
            <img id="waitImg" src="${waitImgUrl}" style="display:none;">
            
            <br><br>
            <span class="largeBoldLabel">Recent Results: </span> 
            <a href="/spring/group/commander/resultList">View</a>
    			 
    		</form>
    	</div>
  
  
  
	
</cti:standardPage>