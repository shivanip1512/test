<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="databaseValidate">

<c:choose>
    <c:when test="${not empty msgError}">
        ${msgError}
    </c:when>
    <c:otherwise>
        <script>
            jQuery(function() {
                new Ajax.Updater('compareResults', '/support/database/validate/results');
            });
        </script>
        
        <div id="compareResults">
            <c:if test="${displayOracleWarning}">
                <i:inline key=".oracleWarning"/>
            </c:if>
            <br>
            <i:inline key=".loading"/> <img id="slowInputProcessImg${uniqueId}" src="<c:url value="/WebConfig/yukon/Icons/spinner.gif"/>" alt="waiting" >
        </div>

    </c:otherwise>
</c:choose>
</cti:standardPage>
