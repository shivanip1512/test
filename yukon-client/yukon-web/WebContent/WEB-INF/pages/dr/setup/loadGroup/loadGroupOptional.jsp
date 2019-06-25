<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<tags:sectionContainer2 nameKey="optional">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".kWCapacity">
                    <tags:input path="kWCapacity"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".disableGroup">
                    <tags:switchButton path="disableGroup" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".disableControl">
                    <tags:switchButton path="disableControl" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
</tags:sectionContainer2>