<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:operationSection sectionName="Client Launcher" sectionImageName="ClientLauncherLogo">
    <c:forEach items="${jnlpList}" var="jnlp">
        <tags:sectionLink>
            <a href="<c:url value="/jws/${jnlp.path}"/>">${jnlp.appTitle}</a>
        </tags:sectionLink>
    </c:forEach>
    <tags:sectionLink>
      <a href="http://www.java.com/getjava/">Install Java</a>
    </tags:sectionLink>
</tags:operationSection>
