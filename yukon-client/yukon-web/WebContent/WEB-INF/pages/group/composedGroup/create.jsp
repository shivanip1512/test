<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msg var="pageTitle" key="yukon.web.deviceGroups.composedGroup.pageTitle"/>
<cti:msg var="nameLabelText" key="yukon.web.deviceGroups.composedGroup.nameLabel"/>
<cti:msg var="headerRulesText" key="yukon.web.deviceGroups.composedGroup.header.rules"/>
<cti:msg var="headerRemoveText" key="yukon.web.deviceGroups.composedGroup.header.remove"/>
<cti:msg var="saveButtonText" key="yukon.web.deviceGroups.composedGroup.saveButton"/>
<cti:msg var="instructions" key="yukon.web.deviceGroups.composedGroup.instructions"/>
<cti:msg var="instructionsHeader" key="yukon.web.deviceGroups.composedGroup.instructions.header"/>
<cti:msg var="matchSentencePrefix" key="yukon.web.deviceGroups.composedGroup.matchSentence.prefix"/>
<cti:msg var="matchSentenceSuffix" key="yukon.web.deviceGroups.composedGroup.matchSentence.suffix"/>
<cti:msg var="ruleSentenceDeviceGroupPrefix" key="yukon.web.deviceGroups.composedGroup.ruleSentence.deviceGroup.prefix"/>
<cti:msg var="ruleSentenceDeviceGroupSuffix" key="yukon.web.deviceGroups.composedGroup.ruleSentence.deviceGroup.suffix"/>

<cti:url var="add" value="/WebConfig/yukon/Icons/add.png"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.png"/>
<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.png"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.png"/>

<cti:standardPage title="${pageTitle}" module="amr">
    <cti:msgScope paths="yukon.web.deviceGroups.composedGroup">
    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/dashboard" title="Operations Home" />
        <cti:crumbLink url="/meter/start" title="Metering" />
        <cti:url var="groupHomeUrl" value="/group/editor/home">
            <cti:param name="groupName" value="${groupName}"/>
        </cti:url>
        <cti:crumbLink url="${groupHomeUrl}" title="Device Groups" />
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">

        function setSelectedGroupName(spanElName, valueElName) {

            $(spanElName).innerHTML = $(valueElName).value;
        }
    
    </script>

    <h2>${pageTitle}</h2>
    <br>
    
    <c:if test="${not empty errorMsg}">
        <div class="error">${errorMsg}</div>
        <br>
    </c:if>
    
    <form id="buildForm" action="/group/composedGroup/build" method="post">
    
    <input type="hidden" name="groupName" value="${fn:escapeXml(groupName)}">
    <input type="hidden" name="firstLoad" value="false">
    
    <%-- NAME --%>
    <tags:nameValueContainer>
        <tags:nameValue name="${nameLabelText}" nameColumnWidth="60px">
            ${fn:escapeXml(groupName)}
        </tags:nameValue>
    </tags:nameValueContainer>
    <br>
            
    <%-- INSTRUCTIONS --%>
    <tags:boxContainer title="${instructionsHeader}" hideEnabled="false">
        ${instructions}
    </tags:boxContainer>
    
    <%-- MATCH --%>
    <h3>
    ${matchSentencePrefix} 
    <select name="compositionType">
        <c:forEach var="compositionType" items="${availableCompositionTypes}">
            <c:set var="selected" value=""/>
            <c:if test="${compositionType == selectedCompositionType}">
                <c:set var="selected" value="selected"/>
            </c:if>
            <option value="${compositionType}" ${selected}><cti:msg key="${compositionType.formatKey}"/></option>
        </c:forEach>
     </select>
     ${matchSentenceSuffix}
     </h3>
    <br>
    <%-- RULES TABLE --%>
    <table id="groupsTable" class="resultsTable">
        <thead>
            <tr>
                <th>${headerRulesText}</th>
                <th style="text-align:right;width:100px;">${headerRemoveText}</th>
            </tr>
        </thead>
        <%-- ADD RULE --%>
        <tfoot>
            <tr>
                <td colspan="2">
                    <cti:button nameKey="addAnotherDeviceGroup" type="submit" name="addRow" icon="icon-add"/>
                </td>
            </tr>
       </tfoot>
        <tbody>
            <c:forEach var="group" items="${groups}">
                <tr>
                    <td>
                        <span class="fl" style="margin-right: 10px;">
	                        <span>${ruleSentenceDeviceGroupPrefix}</span>
	                        <select name="notSelect_${group.order}">
	                            <option value="false">contained in</option>
	                            <option value="true" <c:if test="${group.negate}">selected</c:if>>not contained in</option>
	                        </select>
	                        <span>${ruleSentenceDeviceGroupSuffix}</span>
                        </span>
                        <tags:deviceGroupNameSelector fieldName="deviceGroupNameField_${group.order}" 
                                                      fieldValue="${group.groupFullName}" 
                                                      dataJson="${chooseGroupTreeJson}"/>
                    </td>
                    
                    <td>
                        <cti:button classes="fr" nameKey="remove" type="submit" name="removeRow${group.order}" renderMode="image" icon="icon-cross"/>
                    </td>
                </tr>
            </tbody>
        </c:forEach>
    </table>
    
    <%-- SAVE --%>
    <cti:button nameKey="save" type="submit"/>
    </form>
    </cti:msgScope>
</cti:standardPage>