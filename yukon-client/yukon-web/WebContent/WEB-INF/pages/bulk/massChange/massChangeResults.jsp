<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.massChangeResults">
    <tags:sectionContainer title="${headerTitle}" id="massChangeResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="massChange" callbackResult="${callbackResult}" />
    </tags:sectionContainer>
</cti:standardPage>