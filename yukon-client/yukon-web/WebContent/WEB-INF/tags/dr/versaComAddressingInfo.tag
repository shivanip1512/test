<%@ tag body-content="empty" %>

<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<tags:sectionContainer2 nameKey="versaComAddresses">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".utility">
            <tags:input path="${path}addressingInfo.utility" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".section">
            <tags:input path="${path}addressingInfo.section" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".class">
            <dr:bitSetter path="${path}addressingInfo.classAddressBits"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".division">
            <dr:bitSetter path="${path}addressingInfo.divisionBits"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>