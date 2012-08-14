<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:msgScope paths="modules.support.i18nDemo">

<p class="pageDescription">
This page shows some examples of how to use i18n message key scoping in Yukon.  This is the same
information one would get using i:inline and setting I18N_DESIGN_MODE to true in master.cfg.
</p>

<c:set var="msgScopeMsg" value="${scopePeeker.scope}"/>

<div class="scopeDemo">
<h1 class="i18nDemo"><cti:msg2 key=".scope.title" argument="${msgScopeMsg}"/></h1>
<div class="messageSample">
&lt;i:inline key=&quot;.testMessage&quot;/&gt; resolves to <span class="messageSample"><cti:msg2 key=".testMessage" debug="true" fallback="true"/></span>
</div>

<ul class="keyList">
<c:forEach items="${msg2TagDebugMap}" var="entry">
    <li>${entry.key}=${entry.value}</li>
</c:forEach>
</ul>
</div>


<debug:scopeDemo newPaths=".nested"/>
<debug:scopeDemo newPaths=".nested,"/>
<debug:scopeDemo newPaths=",.nested"/>
<debug:scopeDemo newPaths="startOver"/>
<debug:scopeDemo newPaths=".nested,startOver"/>
<debug:scopeDemo newPaths=".nested,startOver,"/>

<cti:msgScope paths=",duck,.duck,goose">
<debug:scopeDemo newPaths=".nested,StartOver,"/>
</cti:msgScope>

</cti:msgScope>
