<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="paoDefinitionTesting">

    <form:form action="reloadPaoDefinition">
        <cti:csrfToken/>
        
        <cti:button type="submit" label="Reload Pao Definition"/>
        
        <cti:url var="reloadOverrideUrl" value="reloadOverrideFile"/>
        <cti:button href="${reloadOverrideUrl}" label="Reload And Override Pao Definition"/>
        
    </form:form>
</cti:standardPage>