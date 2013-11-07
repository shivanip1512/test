<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<tags:sectionContainer2 nameKey="expressComAddresses">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".serviceProvider">
            <tags:input path="addressingInfo.serviceProvider" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".geo">
            <tags:input path="addressingInfo.geo" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".substation">
            <tags:input path="addressingInfo.substation" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".feeder">
            <dr:bitSetter path="addressingInfo.feederBits"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".zip">
            <tags:input path="addressingInfo.zip" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".userAddress">
            <tags:input path="addressingInfo.userAddress" size="6" maxlength="15"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
