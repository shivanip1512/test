<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="databaseValidate">

    <c:choose>
        <c:when test="${not empty msgError}">
            <div class="error">${msgError}</div>
        </c:when>
        <c:otherwise>
            <script>
                $(function () {
                    $.ajax({
                        type: 'post',
                        url: yukon.url('/support/database/validate/results')
                    }).done(function (data, textStatus, jqXHR) {
                        $('#compareResults').html(jqXHR.responseText);
                    });
                });
            </script>
            
            <div id="compareResults">
                <c:if test="${displayOracleWarning}">
                    <div class="stacked"><i:inline key=".oracleWarning"/></div>
                </c:if>
                <i:inline key=".loading"/>&nbsp;<img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>" alt="waiting">
            </div>
    
        </c:otherwise>
    </c:choose>
</cti:standardPage>
