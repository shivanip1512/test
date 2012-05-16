<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="url" value="/spring/capcontrol/command/commandOneLine"/>

<div>
    <table>
        <tr>
            <td align="center" style="color:#9FBBAC; font-weight: bold; font-size: 16;">Temp Move Menu</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr style="color: gray;"/>
                <a href="javascript:void(0);" align="center" style="color: white" 
                   onclick="submitOnelineCommand(11);">Return to Original Feeder</a>
				<p align="center" style="color: white; font-size: 10;">Requires a regenerate to take effect</p>
            </td>
        </tr>

    </table>
    <input type="hidden" id="cmdId" value="${cmdId}"/>
    <input type="hidden" id="url" value="${url}"/>
    <input type="hidden" id="paoId" value="${paoId}" />
	<input type="hidden" id="controlType" value="adf" />
</div>    
