<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript" src="/JavaScript/simpleCookies.js"></script>
<script type="text/javascript" src="/JavaScript/javaWebStartLauncher.js"></script>

<tags:operationSection sectionName="Client Launcher" sectionImageName="ClientLauncherLogo">
    <c:forEach items="${jnlpList}" var="jnlp">
        <tags:sectionLink>
            <a href="javascript:jwsLaunch('<c:url value="/jws/${jnlp.path}"/>')">${jnlp.appTitle}</a>
        </tags:sectionLink>
    </c:forEach>
</tags:operationSection>

<tags:simplePopup title="Yukon Client Launcher" id="javaWebStartPopup" onClose="jwsClosePopup()">
<div id="javaWebStartWaiting" style="text-align:center;margin:15px">
<img src="/WebConfig/yukon/Icons/indicator_arrows.gif" alt="spinning arrows">
Searching for Java on your system...
</div>
<div id="javaWebStartNoJava" style="display:none;text-align:center;margin:15px">
It does not appear that Java is installed on your system.<br>
Please use the following link to install Java. <br>
If you think Java is already installed, you may also attempt to start the 
application.
</div>
<div id="javaWebStartLinks" style="text-align:center;padding: 15px;">
<a href="<c:url value="/spring/static/jre-1_5_0_11-windows-i586-p.exe"/>" onclick="jwsClosePopup()">Install Java</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="javascript:jwsRelaunchCurrent()">Relaunch Application</a>
</div>
</tags:simplePopup>
