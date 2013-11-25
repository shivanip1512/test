<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    jQuery(function() {
        jQuery('#searchField_1').focus();
    });
</script>

    <form id="filterForm" action="/meter/search">
    <cti:csrfToken/>
        <tags:nameValueContainer>
	        <c:forEach var="filter" items="${filterByList}" varStatus="status">
	        	<cti:msg2 key="${filter.formatKey}" var="filterName"/>
                <tags:nameValue name="${filterName}" valueClass="full-width"><input class="full-width" type="text" id="searchField_${status.count}" name="${filter.name}" value="${filter.filterValue}"></tags:nameValue>
	        </c:forEach>
        </tags:nameValueContainer>
        
        <div class="action-area"><cti:button type="submit" classes="button" nameKey="search"/></div>
    </form>
