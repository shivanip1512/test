<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:url var="commentsUrl" value="/capcontrol/capcontrolcomments.jsp?paoID="/>

<div style="background: white; border: 1px solid black;">
    <table>
        <tr>
            <td align="center" class="top">${paoName}</td>
            <td align="center" class='top' onclick='cClick()'><a href='javascript:void(0)'>X</a></td>
        </tr>
        <c:forEach var="cmdHolder" items="${list}">
            <tr>
                <c:set var="isReasonRequired" value="${(cmdHolder.reasonRequired && allowAddComments)}"/>
                <td colspan="2">
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="cClick(); executeCommand('${paoId}','${cmdHolder.cmdId}', '${cmdHolder.commandName}', '${controlType}', '${isReasonRequired}');">${cmdHolder.commandName}</a>
                </td>
            </tr>    
        </c:forEach>
        <c:if test="${isCapBankSystemMenu}">
            <tr>
                <td colspan="2">
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="cClick(); showResetOpCountSpan('${paoId}');">${resetOpsCmdHolder.commandName}</a>
                </td>
            </tr> 
            <tr>
                <td colspan="2">
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="cClick(); showChangeOpStateMenu('${paoId}');">${changeOpStateCmdHolder.commandName}</a>
                </td>
            </tr> 
            <c:forEach var="state" items="${states}">
                <tr>
                    <td colspan="2">
                        <a href="javascript:void(0);" 
                           class="optDeselect"
                           onmouseover="changeOptionStyle(this);"
                           onclick="cClick(); executeCBCommand('${paoId}', '${state.stateText}', '${state.stateRawState}');">${state.stateText}</a>
                    </td>    
                </tr>
            </c:forEach>
        </c:if>
        <tr>
            <td colspan="2">
                <a href="javascript:void(0);"
                   class="optDeselect"
                   onmouseover="changeOptionStyle(this);"
                   onclick="cClick(); GB_show('CapControl Comments (${paoName})','${commentsUrl}${paoId}', 500, 800);">View Comments</a>
            </td>
        </tr>
     </table>
</div>     