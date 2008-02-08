<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="background: white; border: 1px solid black;">
    <table>
        <tr>
            <td align="center" class="top">${paoName}</td>
            <td align="center" class='top' onclick='cClick()'><a href='javascript:void(0)'>X</a></td>
        </tr>
        <c:forEach var="cmdHolder" items="${list}">
            <tr>
                <td colspan="2">
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="cClick(); ${executeMethodName}('${paoId}','${cmdHolder.cmdId}', '${cmdHolder.commandName}');">${cmdHolder.displayName}</a>
                </td>
            </tr>    
        </c:forEach>
        <c:if test="${isCapBankSystemMenu}">
            <tr>
                <td colspan="2">
                    <a href="javascript:void(0);" 
                       class="optDeselect"
                       onmouseover="changeOptionStyle(this);"
                       onclick="cClick(); enableResetOpCountSpan('${paoId}');">${resetOpsCmdHolder.displayName}</a>
                </td>
            </tr> 
            <c:forEach var="state" items="${states}">
                <tr>
                    <td colspan="2">
                        <a href="javascript:void(0);" 
                           class="optDeselect"
                           onmouseover="changeOptionStyle(this);"
                           onclick="cClick(); ${executeMethodName}('${paoId}','${state.stateRawState}', '${state.stateText}', 'true');">${state.stateText}</a>
                         
                    </td>    
                </tr>
            </c:forEach>
        </c:if>
     </table>
</div>     