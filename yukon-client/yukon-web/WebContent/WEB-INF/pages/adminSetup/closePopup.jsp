<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    $('${popupId}').hide();
    <%-- reload without rerequesting every image/css file/etc. --%>
    <c:if test="${empty newLocation}">
        window.location = window.location;
    </c:if>
    <c:if test="${!empty newLocation}">
        window.location = '${newLocation}';
    </c:if>
</script>
