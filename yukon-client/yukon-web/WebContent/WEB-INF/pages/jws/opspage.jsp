<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript" src="/JavaScript/javaWebStartLauncher.js"></script>

<c:if test="${jnlpListSize > 0}">
	<tags:operationSection sectionName="Client Launcher" sectionImageName="ClientLauncherLogo">
	    <c:forEach items="${jnlpList}" var="jnlp">
	        <tags:sectionLink>
	            <a href="javascript:jwsLaunch('<cti:url value="/jws/${jnlp.path}"/>')">${jnlp.title}</a>
	        </tags:sectionLink>
	    </c:forEach>
	</tags:operationSection>
</c:if>

<tags:simplePopup title="Yukon Client Launcher" id="javaWebStartPopup" onClose="jwsClosePopup()">
<div style="text-align:center;padding: 15px;">
<button onclick="jwsRelaunchCurrent()">Launch Application</button>
</div>
<div style="text-align:center;margin:10px">
Java must be installed on this computer for the application to launch.<br>
Try to <c:choose>
    <c:when test='${jreInstaller != null}'>
        <a href="<cti:url value="/static/JRE/${jreInstaller}"/>" onclick="jwsClosePopup()">install Java</a> 
    </c:when>
    <c:otherwise>
        <a href="http://www.java.com/getjava/" onclick="jwsClosePopup()">install Java</a>
    </c:otherwise>
</c:choose> or contact your administrator if the application will not launch.<br>

</div>
</tags:simplePopup>
