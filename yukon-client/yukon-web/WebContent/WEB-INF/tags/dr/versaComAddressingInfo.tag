<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<tags:sectionContainer2 nameKey="versaComAddresses">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".utility">
            <tags:input path="addressingInfo.utility" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".section">
            <tags:input path="addressingInfo.section" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".class">
            <dr:bitSetter path="addressingInfo.classAddressBits"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".division">
            <dr:bitSetter path="addressingInfo.divisionBits"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
