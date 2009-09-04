<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:url var="commentsUrl" value="/spring/capcontrol/comments/paoComments?paoId="/>

<div style="background: white;">
    <input id="menuPaoName" type="hidden" value="${paoName}"/>
    <table>
        <c:forEach var="cmdHolder" items="${list}">
            <tr>
                <c:set var="isReasonRequired" value="${(cmdHolder.reasonRequired && allowAddComments)}"/>
                <td>
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="closeTierPopup(); executeCommand('${paoId}','${cmdHolder.cmdId}', '${cmdHolder.commandName}', '${controlType}', '${isReasonRequired}', <cti:getProperty property='CBCSettingsRole.CONTROL_WARNING'/>, event);" >${cmdHolder.commandName}</a>
                </td>
            </tr>    
        </c:forEach>
        <c:if test="${isCapBankSystemMenu}">
            <tr>
                <td>
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="closeTierPopup(); showResetOpCountSpan('${paoId}');">${resetOpsCmdHolder.commandName}</a>
                </td>
            </tr> 
            <tr>
                <td>
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="closeTierPopup(); showChangeOpStateMenu('${paoId}', event);">${changeOpStateCmdHolder.commandName}</a>
                </td>
            </tr> 
            <c:forEach var="state" items="${states}">
                <tr>
                    <td>
                        <a href="javascript:void(0);" 
                           class="optDeselect"
                           onmouseover="changeOptionStyle(this);"
                           onclick="closeTierPopup(); executeCBCommand('${paoId}', '${state.stateText}', '${state.stateRawState}');">${state.stateText}</a>
                    </td>    
                </tr>
            </c:forEach>
        </c:if>
        <tr>
            <td>
                <a href="javascript:void(0);"
                   class="optDeselect"
                   onmouseover="changeOptionStyle(this);"
                   onclick="closeTierPopup(); GB_show('CapControl Comments (${paoName})','${commentsUrl}${paoId}', 500, 800);">View Comments</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="javascript:void(0);"
                   class="optDeselect"
                   onmouseover="changeOptionStyle(this);"
                   onclick="closeTierPopup(); showRecentCmdsForSingle('/spring/capcontrol/search/recentControls', ${paoId});">View Recent Cmds</a>
            </td>
        </tr>
     </table>
</div>     