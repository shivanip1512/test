<%@ attribute name="locale" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>


<cti:includeScript link="/JavaScript/timeFormatter.js"/>

<script type="text/javascript">
    <c:choose>
        <c:when test="${locale eq 'fr_CA'}">
            // French time formatter
            var timeFormatter = new FrenchTimeFormatter();
        </c:when>
        <c:otherwise>
            // Default time formatter
            var timeFormatter = new TimeFormatter();
        </c:otherwise>
    </c:choose>
</script>