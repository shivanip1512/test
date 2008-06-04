<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="defaultValue" required="true" type="java.lang.Boolean"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>


<label>
<input type="checkbox" id="${id}">
<jsp:doBody/>
</label>

<script type="text/javascript">
stickyCheckboxes_setup('${id}', ${defaultValue});
</script>
	