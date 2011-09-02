<%@ attribute name="newPaths" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="${newPaths}">
<c:set var="msgScopeMsg" value="${scopePeeker.scope}"/>
</cti:msgScope>

<div class="scopeDemo">

<h1 class="i18nDemo"><cti:msg2 key=".scope.title" argument="${msgScopeMsg}"/></h1>

<cti:msgScope paths="${newPaths}">

<div class="messageSample">
&lt;i:inline key=&quot;.testMessage&quot;/&gt; resolves to <span class="messageSample"><cti:msg2 key=".testMessage" debug="true" fallback="true"/></span>
</div>

<h2 class="keyList">Keys Searched:</h2>
<ul class="keyList">
<c:forEach items="${msg2TagDebugMap}" var="entry">
    <li>${entry.key}=${entry.value}</li>
</c:forEach>
</ul>
</cti:msgScope>

</div>
