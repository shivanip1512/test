<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>



<cti:getCsrfToken/>
  <c:choose>
    <c:when test="${mode eq 'TOKEN'}">
      <input type="hidden" name="${token}" value="1"><jsp:doBody/>
    </c:when>
    <c:when test="${mode eq 'PASSWORD'}">
      <div class="csrfPasswordPrompt"><i:inline key="yukon.web.components.tokenRequest.passwordPrompt"/>: 
        <input autocomplete="false" type="password" name="${token}" value="">
        <jsp:doBody/>
      </div>
    </c:when>
<c:otherwise><jsp:doBody/></c:otherwise>
</c:choose>