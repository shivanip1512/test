<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="newPaths" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="${newPaths}">
    <c:set var="msgScopeMsg" value="${scopePeeker.scope}"/>
</cti:msgScope>

<tags:sectionContainer2 nameKey="scope" arguments="${msgScopeMsg}">

    <cti:msgScope paths="${newPaths}">
        <pre class="code prettyprint">&lt;i:inline key=&quot;.testMessage&quot;/&gt;</pre>
        <div>resolves to <span class="green"><cti:msg2 key=".testMessage" debug="true" fallback="true"/></span></div>
        
        <pre class="code"><strong>Keys Searched:</strong><ul><c:forEach items="${msg2TagDebugMap}" var="entry"><li>${entry.key}=${entry.value}</li></c:forEach></ul></pre>
    </cti:msgScope>
</tags:sectionContainer2>