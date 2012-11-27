<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="manualUrl" value="/capcontrol/command/manualStateChange" />
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
            <td colspan="2">
                <input id="resetOpcount" style="color: white;" type="checkbox" onclick="resetCapOpCount(this, '${resetOpcount}');"/>
                <span style="color: white;">Reset Op Count</span>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <select id="changeBankState" style="background-color=gray" onchange="submitOnelineManualCommand(jQuery('#changeBankState').val());">
                    <option value="" style="color: white;"></option>
                    <c:forEach var="state" items="${states}">
                        <option style="color:white;" value="${state.stateRawState}">${state.stateText}</option>
                    </c:forEach>
                </select>    
            </td>
        </tr>
    </table>
    
    <input type="hidden" id="url" value="${url}" />
    <input type="hidden" id="manualUrl" value="${manualUrl}"/>
    <input type="hidden" id="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" value="${controlType}" />
</div>