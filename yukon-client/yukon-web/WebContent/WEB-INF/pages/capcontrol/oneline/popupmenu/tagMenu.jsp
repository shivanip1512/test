<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="url" value="/spring/capcontrol/commandexecutor?action=executeCommandOneLineTag" />
<cti:url var="commentsUrl" value="/spring/capcontrol/comments/paoCommentsForOneline">
    <cti:param name="paoId" value="${paoId}"/>
</cti:url>
<c:set var="maxCommentLength" value="35"/>

<script language="JavaScript" type="text/javascript" src="/JavaScript/cconelinepopup.js"></script>

<div>
    <table width="295px">
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
                
                    <div style="color: white;">
                        <input type="checkbox" id="disableCheckBox" onclick="toggleReason(this, 'disableReasonId');" <c:if test="${isDisabled}">checked</c:if> />
                        <input type="hidden" id="disableCheckBox_orig" value="${isDisabled}"/>
                        Disable
                    </div>
                    <div id="disableReasonId" <c:if test="${!isDisabled}">style="display:none;"</c:if>>
                        <span style="color: gray; margin-right: 0.2cm;">Reason :</span>
                        <textarea id="disableReason" style='width: 225px; vertical-align: bottom;' rows="2" disabled>${disableReason}</textarea> 
                    </div>
                    <br>
                <cti:checkProperty property="CBCSettingsRole.CBC_ALLOW_OVUV">
                    <div style="color: white;"> 
                        <input type="checkbox" id="disableOVUVCheckBox" onclick="toggleReason(this, 'disableOVUVReasonId');" <c:if test="${isDisabledOVUV}">checked</c:if> />
                        <input type="hidden" id="disableOVUVCheckBox_orig" value="${isDisabledOVUV}"/>
                         Disable OV/UV
                    </div>     
                    <div id="disableOVUVReasonId" <c:if test="${!isDisabledOVUV}">style="display:none;"</c:if>>
                        <span style="color: gray; margin-right: 0.2cm;">Reason :</span>
                        <textarea id="disableOVUVReason" style='width: 225px; vertical-align: bottom;' rows="2" disabled>${disableOVUVReason}</textarea>
                    </div>
                </cti:checkProperty>
                
                <c:if test="${isCapBank}">
                    <br>
                    <div>
                        <span style="color: white; margin-right: 0.1cm;">Operational State</span>
                        <select id="operationalStateValue" size="1" onchange="toggleReason(this, 'operationalStateReasonId');">
                        <c:forEach items="${allowedOperationStates}" var="currentState">
                            <option value="${currentState}" <c:if test="${operationalState == currentState}">selected</c:if>>${currentState}</option>
                        </c:forEach>
                        </select>
                        <input type="hidden" name="operationalStateValue_orig" value="${operationalState}"/>
                    </div>
                    <div id="operationalStateReasonId" style="margin-top: 0.2cm;">
                        <span style="color: gray; margin-right: 0.2cm;">Reason :</span>
                        <textarea id="operationalStateReason" style='width: 225px; vertical-align: bottom;' rows="2" disabled>${operationalStateReason}</textarea>
                    </div>
                    <c:if test="${isIgnoreFlag}">
	                    <br>
	                    <div>
	                    	<span style="color: red; margin-right: 0.1cm;">Command Refused: </span>
	                    	<span style="color: red; margin-left: 0.1cm;">${refusedReason}</span>
	                    </div>
	                    <br>
                    </c:if>
                </c:if>
                    
                <a href="javascript:void(0);" onclick="GB_show('Comments','${commentsUrl}', 380, 710);" style="color: white; font-weight: bold;" >Comments</a>
                <br>
                <div style="color: white;">
                    <c:set var="count" value="1" />
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
                        <div id="commentField_${count}">${count}) ${formattedComment}</div>
                        <c:set var="count" value="${count + 1}" />
                    </c:forEach>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <input type="button" name="execute" value="Execute" onclick="submitTagMenu(${isCapBank});" />
           </td>
        </tr>
    </table>
    
    <input type="hidden" id="url" name="url" value="${url}" />
    <input type="hidden" id="paoId" name="paoId" value="${paoId}" />
    <input type="hidden" id="controlType" name="controlType" value="${controlType}" />
</div>
