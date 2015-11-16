<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.adminSetup.substationToRouteMapping">
<cti:url var="submitUrl" value="/admin/substations/routemapping/multispeak/add"/>  
<form action="${submitUrl}" method="post">
    <cti:csrfToken/>

    <div style="text-align:right;">
    <a href="javascript:void(0);" onclick="mspSubstations_check(false);">Uncheck All</a> |
    <a href="javascript:void(0);" onclick="mspSubstations_check(true);">Check All</a>
    <br><br>
    </div>

    <div style="overflow:auto; height:300px;">
    <table class="compact-results-table" style="width:95%;">
    
        <tr>
            <th>Substation Name</th>
        </tr>
        
        <c:forEach var="mspSubstation" items="${mspSubstations}">
        
            <c:set var="show" value="${mspSubstation.show}"/>
            <c:set var="trClass" value=""/>
            <c:if test="${!show}">
                <c:set var="trClass" value="class='subtle'"/>
            </c:if>
        
            <tr ${trClass}>
                <td>
                    <c:choose>
                        <c:when test="${show}">
                            <label><input class="mspSubstationCheckbox" type="checkbox" checked name="substationName_${mspSubstation.name}" value="${mspSubstation.name}"> ${fn:escapeXml(mspSubstation.name)}</label>
                        </c:when>
                        <c:otherwise>
                            <input class="mspSubstationCheckbox" type="checkbox" disabled> ${fn:escapeXml(mspSubstation.name)}
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    
    </table>
    </div>
    
    <div class="page-action-area">
        <cti:button type="submit" nameKey="add"/>
    </div>

</form>
</cti:msgScope>