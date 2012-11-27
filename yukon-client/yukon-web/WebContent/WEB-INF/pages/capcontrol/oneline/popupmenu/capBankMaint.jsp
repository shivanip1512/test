<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="url" value="/capcontrol/command/commandOneLine"/>

<div>
    <table>
        <tr>
            <td align="center" style="color:#9FBBAC; font-weight: bold; font-size: 16;">${paoName}</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td  colspan="2">
                <hr style="color: gray;"/>
            </td>
        </tr>	
        <tr>
            <td>
                <input type="button" value="Scan" onclick="submitOnelineCommand(${scanCmdId})" <c:if test="${scanDisabled}">disabled</c:if> />
            </td>
        </tr>
        <tr>
		    <td>
                <input type="button" value="Enable OV/UV" onclick="submitOnelineCommand(${enableOvUvCmdId})"/>
            </td>
        </tr>
        <tr>
            <td>
                <input type="button" value="Send TimeSync" onclick="submitOnelineCommand(${sendTimeSyncCmdId})"/>
            </td>
	   </tr>
	   <tr>
            <td>
                <input type="button" value="Sync CapBank State" onclick="submitOnelineCommand(${syncCapBankState})"/>
            </td>
	   </tr>
    </table>
    
    <input type="hidden" id="url" name="url" value="${url}" />
	<input type="hidden" id="paoId" name="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" name="controlType" value="${controlType}" />
</div>