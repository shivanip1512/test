<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<c:choose>
    <c:when test="${mode == 'CREATE'}">
        <cti:url var="url" value="/stars/gateways"/>
        <c:set var="method" value="post"/>
    </c:when>
    <c:otherwise>
        <cti:url var="url" value="/stars/gateways/${gateway.id}"/>
        <c:set var="method" value="put"/>
    </c:otherwise>
</c:choose>
<form action="${url}" method="${method}">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".name">
            <input type="text" value="" class="js-focus full-width">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".ipaddress">
            <input type="text" value="" class="full-width">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".username">
            <input type="text" value="">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".password">
            <input type="password" value="">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <h3><i:inline key=".detail.location.title"/>&nbsp;<em class="subtle"><i:inline key=".optional"/></em></h3>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".latitude">
            <input type="text" value="">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".longitude">
            <input type="text" value="">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
</form>

</cti:msgScope>