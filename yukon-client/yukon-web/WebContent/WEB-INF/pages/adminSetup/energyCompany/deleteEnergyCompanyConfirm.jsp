<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="deleteEnergyCompanyConfirm">

    <cti:url value="delete" var="deleteEnergyCompanyUrl"/>

    <div class="listContainer">
        <div>
            <cti:msg2 key=".disclaimer" argument="${energyCompanyName}"/>
        </div>
        <br>
<%--    
        <i:inline key=".deleteConfirm" />
        <br><br>
--%>
        <form:form action="${deleteEnergyCompanyUrl}" id="deleteEnergyCompanyForm">
            <input type="hidden" name="ecId" value="${ecId}" />
            
<%--            <tags:csrfTokenRequest> --%>
                <cti:button nameKey="delete" name="delete" type="submit" styleClass="f_blocker"/>
                <cti:button nameKey="cancel" name="cancel" type="submit"/>
<%--            </tags:csrfTokenRequest> --%>
        </form:form>
    </div>

</cti:standardPage>