<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.bulk.archivedValueExporter">

<form:form modelAttribute="attribute" id="attribute-form" cssClass="js-no-submit-on-enter">
    <cti:csrfToken/>
    <tags:nameValueContainer2>
        <tags:selectNameValue id="chooseAttrib" nameKey=".attribute" path="attribute" items="${groupedAttributes}" itemLabel="message" itemValue="key" groupItems="true"/>
        <tags:selectNameValue items="${dataSelection}" nameKey=".dataSelection" path="dataSelection"/>
        <tags:inputNameValue nameKey=".daysPrevious" path="daysPrevious" maxlength="3" size="3"/>
    </tags:nameValueContainer2>
</form:form>
<style>
#format-popup {
    overflow: visible !important;
}
</style>
<script>
    $("#chooseAttrib").chosen({width: "280px"});
</script>
</cti:msgScope>