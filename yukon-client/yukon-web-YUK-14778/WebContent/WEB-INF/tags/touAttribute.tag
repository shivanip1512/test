<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="usageAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>
<%@ attribute name="peakAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty usageAttribute || not empty peakAttribute}">
    <tags:nameValueContainer2 tableClass="stacked">
        <c:if test="${not empty usageAttribute}">
            <tags:nameValue2 label="${usageAttribute}">
                <tags:attributeValue pao="${meter}" attribute="${usageAttribute}"/>
            </tags:nameValue2>
        </c:if>
        <c:if test="${not empty peakAttribute}">
            <tags:nameValue2 label="${peakAttribute}">
                <tags:attributeValue pao="${meter}" attribute="${peakAttribute}"/>
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>
</c:if>