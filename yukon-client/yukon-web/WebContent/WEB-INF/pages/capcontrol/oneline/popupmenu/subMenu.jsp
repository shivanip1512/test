<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="url" value="/spring/capcontrol/command/commandOneLine"/>

<div>
    <table>
        <tr>
            <td align="center" style="color:#9FBBAC; font-weight: bold; font-size: 16;">${paoName}</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr style="color: gray;"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="button" value="Reset Opcount" onclick="submitOnelineCommand(${resetOpCount});"/>
                <input type ="button" value="Confirm All" onclick="submitOnelineCommand(${confirmSub});"/>
            </td>
        </tr>
    	<tr>
	       	<td colspan="2">
                <span style="color: gray;">Sub-Level CBC Commands:</span>
                <select id="subLevelCommand" style="background-color=gray;">
    				<option value="" style="color: white"></option>
	       			<option value="${openAllSub}" style="color:white;">Open All CapBanks</option>
			     	<option value="${closeAllSub}" style="color:white;">Close All CapBanks</option>
				    <cti:checkProperty property="CBCSettingsRole.CBC_ALLOW_OVUV">
				        <option  value="${enableOvUvSub}" style="color:white;">Enable OV/UV</option>
				        <option  value="${disableOvUvSub}" style="color:white;">Disable OV/UV</option>
				    </cti:checkProperty>
                    <option  value="${sendAll2WaySub}" style="color:white;">Scan All 2way CBCs</option>
				    <option  value="${sendTimeSyncSub}" style="color:white;">Send All TimeSync</option>
    				<option  value="${syncCapBankStatesSub}" style="color:white;">Sync CapBank States</option>
    				<c:choose>
    					<c:when test="${!isV}">
                            <option  value="${verifyAll}" style="color:white;">Verify All</option>
						    <option  value="${verifyFQ}" style="color:white;">Verify Failed and Questionable</option>
						    <option  value="${verifyFailed}" style="color:white;">Verify Failed</option>
						    <option  value="${verifyQuestion}" style="color:white;">Verify Questionable</option>
						    <option  value="${verifyStandalone}" style="color:white;">Verify Standalone</option>
					    </c:when>
    					<c:otherwise>
	       					<option  value="${verifyStop}" style="color:white;">Verify Stop</option>
	       					<option  value="${verifyEmergencyStop}" style="color:white;">Verify Emergency Stop</option>
			     		</c:otherwise>
                    </c:choose>
                </select>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="button" value="Execute" onclick="submitOnelineCommand(jQuery('#subLevelCommand').val());"/>
                <input type="button" value="Cancel" onclick="closePopupWindow();"/>
                <br>
            </td>
        </tr>
    </table>
    
    <input type="hidden" id="url" value="${url}" />
    <input type="hidden" id="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" value="${controlType}" />    
</div>