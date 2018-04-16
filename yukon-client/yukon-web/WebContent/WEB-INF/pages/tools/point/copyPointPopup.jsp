<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<cti:msgScope paths="modules.tools.point, yukon.common.device.selection">
    <cti:flashScopeMessages/>
    
    <cti:url var="copyPointUrl" value="/tools/points/copy-point"/>
    <form:form id="copy-point-form" action="${copyPointUrl}" method="post" commandName="copyPointModel">
        <cti:csrfToken/>
        <form:hidden path="pointBase.point.pointID"/>
        <form:hidden path="pointBase.point.pointType" id="copy-point-pointType"/>

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="pointBase.point.pointName"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".device">
                <form:select path="pointBase.point.paoID" id="copy-point-paos">
                    <c:forEach var="pao" items="${paos}">
                        <form:option value="${pao.paoIdentifier.paoId}">${fn:escapeXml(pao.paoName)}</form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".physicalOffset" rowClass="filter-section">
                <tags:switchButton path="pointBase.point.physicalOffset" offClasses="M0"
                                   toggleGroup="copy-point-physicalOffset" toggleAction="hide" 
                                   offNameKey=".physicalOffset.pseudo" id="copy-point-physicalOffset-toggle"/>

                <%-- The physical offset value within the current device or parent this point belongs to --%>
                <tags:input path="pointBase.point.pointOffset" size="6" toggleGroup="copy-point-physicalOffset"
                            id="copy-point-physicalOffset-txt" displayValidationToRight="true"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button nameKey="copy" classes="primary" id="copy-point-btn"/>
            <cti:button nameKey="cancel" id="close-copy-point-popup" />
        </div>
    </form:form>
</cti:msgScope>

<cti:includeScript link="/resources/js/pages/yukon.tools.point.js"/>