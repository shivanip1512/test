<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.commander">
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:nameValue2 nameKey=".currentRoute">
            ${fn:escapeXml(route.paoName)}
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".newRoute">
            <select name="newRoute" id="new-route">
                <c:forEach items="${routes}" var="newRoute">
                    <option value="${newRoute.liteID}">${fn:escapeXml(newRoute.paoName)}</option>
                </c:forEach>
            </select>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
</cti:msgScope>