<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <div style="width:30%" class="PT10 PB10 clearfix">
        <c:if test="${deviceTotalCount != 0}">
            <tags:infrastructureWarningsCount deviceTotalCount="${deviceTotalCount}" 
                                              deviceWarningsCount="${deviceWarningsCount}" 
                                              deviceLabel="${deviceLabel}" 
                                              fromDetailPage="${fromDetailPage}"
                                              deviceType="${deviceType}"/>
        </c:if>
    </div>
    <%@ include file="infrastructureWarningsDetails.jsp" %>
</cti:msgScope>