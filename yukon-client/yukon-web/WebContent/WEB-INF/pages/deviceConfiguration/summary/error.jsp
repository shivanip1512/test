<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:msgScope paths="modules.tools.configs.summary">

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".error">
            ${error.porter}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".description">
            ${error.description}  
        </tags:nameValue2>
        <c:if test="${!empty error.troubleshooting}">
            <tags:nameValue2 nameKey=".troubleshooting">
                ${error.troubleshooting}
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>

</cti:msgScope>