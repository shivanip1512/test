<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <table width="70%">
        <tr>
            <c:if test="${deviceTotalCount != 0}">
                <tags:infrastructureWarningsCount deviceTotalCount="${deviceTotalCount}" 
                                                  deviceWarningsCount="${deviceWarningsCount}" 
                                                  deviceLabel="${deviceLabel}" 
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="${deviceType}"/>
            </c:if>
        </tr>
    </table>
    <%@ include file="infrastructureWarningsDetails.jsp" %>
</cti:msgScope>