<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="usageAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>
<%@ attribute name="peakAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<cti:msg key="${headerKey}" var="headerName" />

<c:if test="${not empty usageAttribute || not empty peakAttribute}">

    <tags:sectionContainer title="${headerName}">
        <tags:nameValueContainer2>
            <c:if test="${not empty usageAttribute}" >
                <tags:nameValue2 label="${usageAttribute}" >
                    <tags:attributeValue device="${meter}" attribute="${usageAttribute}" />
                </tags:nameValue2>
                <tags:nameValueGap2 gapHeight="6px" />
            </c:if>
            <c:if test="${not empty peakAttribute}" >
                <tags:nameValue2 label="${peakAttribute}" >
                    <tags:attributeValue device="${meter}" attribute="${peakAttribute}" />
                </tags:nameValue2>
                <tags:nameValueGap2 gapHeight="6px" />
            </c:if>
        </tags:nameValueContainer2>
    </tags:sectionContainer>

</c:if>