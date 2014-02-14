<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    
<!--     <div class="column-12-12"> -->
<!--         <div class="column one"> -->
<%--             <tags:nameValueContainer2> --%>
<%--                 <tags:nameValue2 nameKey=".neverReported1">${neverReported1}</tags:nameValue2> --%>
<%--                 <tags:nameValue2 nameKey=".neverReported2">${neverReported2}</tags:nameValue2> --%>
<%--                 <tags:nameValue2 ></tags:nameValue2> --%>
<%--             </tags:nameValueContainer2> --%>
<!--         </div> -->
<!--         <div class="column two nogutter"> -->
<!--         </div> -->
<!--     </div> -->
    
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key="yukon.common.deviceName"/></th>
                <th><i:inline key="yukon.common.deviceType"/></th>
                <th><i:inline key=".unknownType"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <td><cti:deviceName deviceId="${row.pao.paoIdentifier.paoId}"/></td>
                    <td>${row.pao.paoIdentifier.paoType.paoTypeName}</td>
                    <td>${row.unknownType}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${result}" baseUrl="/dr/rf/details/unknown/${test}" adjustPageCount="true"/>
    <div class="action-area">
        <cti:button nameKey="download" icon="icon-page-white-excel"/>
        <cti:button nameKey="actionUnknown" icon="icon-cog-go"/>
    </div>
</cti:msgScope>