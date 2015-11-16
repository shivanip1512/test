<%@ tag body-content="empty" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<tags:sectionContainer2 nameKey="operationalAddress">
    <tags:input path="${path}addressingInfo.operationalAddress" size="15" maxlength="15"/>
</tags:sectionContainer2>