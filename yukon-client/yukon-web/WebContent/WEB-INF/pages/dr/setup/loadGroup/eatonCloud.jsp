<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:sectionContainer2 nameKey="loadAddressing">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".loads">
            <c:set var="items" value="${isViewMode ? loadGroup.relayUsage : loadsList}"/>
            <tags:checkboxButtonGroup items="${items}" path="relayUsage" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
