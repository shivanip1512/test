<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.widgets.disconnectMeterWidget">
<tags:nameValueContainer2>
    <c:if test="${is410Supported}">
        <tags:nameValue2 nameKey=".stateGroupHeader"  argument="${stateGroups[0]}">
            <i:inline key=".confirmedDisconnectMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
            <i:inline key=".connectedMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
            <i:inline key=".unconfirmedDisconnectMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[3]}">
            <i:inline key=".connectArmedMsg"/>
        </tags:nameValue2>
    </c:if>
    <c:if test="${is310Supported}">
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
            <i:inline key=".openMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
            <i:inline key=".closedMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
            <i:inline key=".unknownMsg"/>
        </tags:nameValue2>
    </c:if>
    <c:if test="${is213Supported}">
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
            <i:inline key=".openMsg"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
            <i:inline key=".closedMsg"/>
        </tags:nameValue2>
    </c:if>
</tags:nameValueContainer2>
<p><i:inline key=".disconnectNote"/></p>
</cti:msgScope>