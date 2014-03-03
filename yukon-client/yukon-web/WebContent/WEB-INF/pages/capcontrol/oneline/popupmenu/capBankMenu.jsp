<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url var="url" value="/capcontrol/command/commandOneLine"/>

<div>
    <table width=250">
        <tr>
            <td/>
            <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">${fn:escapeXml(paoName)}</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td  colspan="3">
                <hr style="color: gray;"/>
            </td>
        </tr>
        <tr>
            <td align="center" colspan="3">
                <cti:button label="open" onclick="submitOnelineCommand(${open})"/>
                <cti:button label="close" onclick="submitOnelineCommand(${close})"/>
                <cti:button label="confirm" onclick="submitOnelineCommand(${confirm})"/>
            </td>
	   </tr>
        <tr>
            <td align="left">
                <a href="javascript:void(0)" onclick="openPopupWin(this, '${childCapMaintPaoId}');" style="color: white; margin-left: 0.1cm;">Maintenance</a>
            </td>
            <td/>
            <td align="right">
                <a href="javascript:void(0)" onclick="openPopupWin(this, '${childCapDBChangePaoId}');" style="color: white; margin-right: 0.1cm;">DB Change</a>
            </td>
        </tr>
    </table>
    
    <input type="hidden" id="url" name="url" value="${url}" />
    <input type="hidden" id="paoId" name="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" name="controlType" value="${controlType}" />
<div>
		
