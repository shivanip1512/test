<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="commentsUrl" value="/capcontrol/capcontrolcomments.jsp?paoID="/>
<c:url var="url" value="/spring/capcontrol/commandexecutor?action=executeCommandOneLineTag" />
<c:set var="maxCommentLength" value="35"/>

<script language="JavaScript" type="text/javascript" src="/JavaScript/cconelinepopup.js"></script>

<div style="background: white; border: 1px solid black;">
    <table width="295px">
        <tr>
            <td align="center" class="top">${paoName}</td>
            <td align="center" class='top' onclick='cClick()'><a href='javascript:void(0)'>X</a></td>
        </tr>
        <tr>
            <td colspan="2">
                <div>
                    <span >Operational State</span>
                    <select id="operationalStateValue" size="1">
                    <c:forEach items="${allowedOperationStates}" var="currentState">
                        <option value="${currentState}" <c:if test="${operationalState == currentState}">selected</c:if>>${currentState}</option>
                    </c:forEach>
                    </select>
                    <input type="hidden" id="operationalStateValue_orig" value="${operationalState}"/>
                </div>
                <div id="operationalStateReasonId" style="margin-top: 0.2cm;">
                    <span >Reason :</span>
                    <textarea id="operationalStateReason" style="width: 98%;" rows="2">${operationalStateReason}</textarea>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <select onchange="$('operationalStateReason').value = this.options[this.selectedIndex].value;">
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
            <td>
                <input type="button" name="execute" value="Execute" onclick="submitChangeOpStateMenu();" />
           </td>
        </tr>
    </table>
    <input type="hidden" id="url" name="url" value="${url}" />
    <input type="hidden" id="paoId" name="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" name="controlType" value="${controlType}" />
</div>
