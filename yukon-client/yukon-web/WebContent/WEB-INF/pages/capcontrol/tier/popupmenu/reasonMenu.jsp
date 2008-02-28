<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="maxCommentLength" value="40"/>

<div style="background: white; border: 1px solid black;">
    <table>
        <tr>
            <td align="center" class="top">${paoName}</td>
            <td align="center" class='top' onclick='cClick()'><a href='javascript:void(0)'>X</a></td>
        </tr>
        <tr>
            <td colspan="2">Enter a comment for this operation.</td>
        </tr>
        <tr>
            <td colspan="2">
                <textarea id="textAreaElement" style="width: 98%;"></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <select onchange="$('textAreaElement').value = this.options[this.selectedIndex].value;">
                    <option>&lt;Choose a previously entered comment&gt;</option>
                    <c:forEach var="comment" items="${comments}">
                        <c:choose>
                            <c:when test="${fn:length(comment) > maxCommentLength}">
                                <c:set var="subString" value="${fn:substring(comment, 0, (maxCommentLength - 3))}"/>
                                <c:set var="formattedComment" value="${subString}..."/>   
                            </c:when>
                            <c:otherwise>
                                <c:set var="formattedComment" value="${comment}"/>
                            </c:otherwise>
                        </c:choose>
                        <option value="${formattedComment}">${formattedComment}</option>
                    </c:forEach>
                </select>        
            </td>
        </tr>
        <tr>
            <td align="right" colspan="2">
                <input type="button" value="Submit" onclick="cClick(); executeCommandController('${paoId}', '${cmdId}', '${commandName}', '${controlType}', $('textAreaElement').value);"></input>
            </td>
        </tr>
    </table>
</div>