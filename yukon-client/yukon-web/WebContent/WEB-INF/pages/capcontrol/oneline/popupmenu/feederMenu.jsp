<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="manualUrl" value="/spring/capcontrol/commandexecutor?action=executeManualStateChange" />
<c:url var="url" value="/spring/capcontrol/commandexecutor?action=executeCommandOneLine" />

<div>
    <table>
        <tr>
            <td align="right" style="color:#9FBBAC; font-weight: bold; font-size: 16;">${paoName}</td>
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
            <td>
                <input type="button" value="Reset Opcount" onclick="submitOnelineCommand(${resetOpCount});"/>
            </td>
            <td/>
        </tr>
        <tr>
            <td style="color: gray;">Fdr-Level CBC Commands:</td>
            <td>
                <select id="selectId" style="background-color=gray;">
				    <option value="" style="color: white"></option>
				    <option value="${openAllFdr}" style="color:white;">Open All CapBanks</option>
				    <option value="${closeAllFdr}" style="color:white;">Close All CapBanks</option>
                    <cti:checkProperty property="CBCSettingsRole.CBC_ALLOW_OVUV">
				        <option  value="${enableOvUvFdr}" style="color:white;">Enable OV/UV</option>
				        <option  value="${disableOvUvFdr}" style="color:white;">Disable OV/UV</option>
                    </cti:checkProperty>    
				    <option  value="${sendAll2WayFdr}" style="color:white;">Scan All 2way CBCs</option>
				    <option  value="${sendTimeSyncFdr}" style="color:white;">Send All TimeSync</option>
                </select>
            </td>
        </tr>
    	<tr>
            <td>
                <input type=button value="Execute" onclick="submitOnelineCommand($('selectId').options[$(selectId).selectedIndex].value);"/>
                <input type="button" value="Cancel" onclick="closePopupWindow();"/>
            </td>
            <td/>
	   </tr>
    </table>
    
    <input type="hidden" id="url" value="${url}" />
    <input type="hidden" id="manualUrl" value="${manualUrl}"/>
    <input type="hidden" id="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" value="${controlType}" />
</div>