<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.tools.tdc">
    <cti:flashScopeMessages/>
    <cti:url var="saveUrl" value="/tools/data-viewer/copySend"/>
    <form:form id="tdc-copy-form" cssClass="js-no-submit-on-enter" modelAttribute="backingBean" action="${saveUrl}">
        <cti:csrfToken/>
        <form:hidden path="displayId" />
        <tags:nameValueContainer2>
            <tags:inputNameValue nameKey=".copy.name" path="displayName" maxlength="30"/>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary js-tdc-copy-send" />
            <cti:button nameKey="close" onclick="$('#tdc-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>