<%@ tag body-content="empty" %>

<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<tags:sectionContainer2 nameKey="xcomAddress">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".serviceProvider">
            <tags:input path="${path}addressingInfo.serviceProvider" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".geo">
            <tags:input path="${path}addressingInfo.geo" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".substation">
            <tags:input path="${path}addressingInfo.substation" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".feeder">
            <dr:bitSetter path="${path}addressingInfo.feederBits"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".zip">
            <tags:input path="${path}addressingInfo.zip" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".userAddress">
            <tags:input path="${path}addressingInfo.userAddress" size="6" maxlength="15"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>