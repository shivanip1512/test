<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="deviceGroups.create.composed">

    <c:if test="${not empty errorMsg}">
        <div class="stacked error">${errorMsg}</div>
    </c:if>
    
    <form id="buildForm" action="<cti:url value="/group/composedGroup/build"/>" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="groupName" value="${fn:escapeXml(groupName)}">
        <input type="hidden" name="firstLoad" value="false">
        
        <%-- NAME --%>
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:nameValue2 nameKey=".nameLabel">${fn:escapeXml(groupName)}</tags:nameValue2>
        </tags:nameValueContainer2>
                
        <%-- INSTRUCTIONS --%>
        <tags:sectionContainer2 nameKey="instructions">
            <i:inline key=".instructions"/>
        </tags:sectionContainer2>
        
        <%-- MATCH --%>
        <h3>
        <i:inline key=".matchSentence.prefix"/> 
        <select name="compositionType">
            <c:forEach var="compositionType" items="${availableCompositionTypes}">
                <c:set var="selected" value=""/>
                <c:if test="${compositionType == selectedCompositionType}">
                    <c:set var="selected" value="selected"/>
                </c:if>
                <option value="${compositionType}" ${selected}><cti:msg key="${compositionType.formatKey}"/></option>
            </c:forEach>
         </select>
         <i:inline key=".matchSentence.suffix"/>
         </h3>
         <hr>
        <%-- RULES TABLE --%>
        <div class="separated-sections">
            <c:forEach var="group" items="${groups}">
                <div class="clearfix section">
                    <span><i:inline key=".ruleSentence.deviceGroup.prefix"/></span>
                    <select name="notSelect_${group.order}">
                        <option value="false">contained in</option>
                        <option value="true" <c:if test="${group.negate}">selected</c:if>>not contained in</option>
                    </select>
                    <span><i:inline key=".ruleSentence.deviceGroup.suffix"/></span>
                    <tags:deviceGroupNameSelector fieldName="deviceGroupNameField_${group.order}" 
                                                  fieldValue="${group.groupFullName}" 
                                                  dataJson="${chooseGroupTreeJson}"/>
                    <cti:button classes="fr" nameKey="remove" type="submit" name="removeRow${group.order}" renderMode="buttonImage" icon="icon-cross"/>
                </div>
            </c:forEach>
        </div>
        <div class="action-area"><cti:button nameKey="addAnotherDeviceGroup" type="submit" name="addRow" icon="icon-add"/></div>
    
        <%-- SAVE --%>
        <div class="page-action-area">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
        </div>
    </form>
</cti:standardPage>