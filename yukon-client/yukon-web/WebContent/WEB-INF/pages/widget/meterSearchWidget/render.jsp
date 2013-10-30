<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    jQuery(function() {
	  $('searchField_1').focus();
	});
</script>

    <form id="filterForm" action="/meter/search">
        <tags:nameValueContainer tableClass="nonwrapping">
	        <c:forEach var="filter" items="${filterByList}" varStatus="status">
	        	<cti:msg2 key="${filter.formatKey}" var="filterName"/>
                <tags:nameValue name="${filterName}" valueClass="full_width"><input class="full_width" type="text" id="searchField_${status.count}" name="${filter.name}" value="${filter.filterValue}"></tags:nameValue>
	        </c:forEach>
        </tags:nameValueContainer>
        
        <div class="actionArea"><cti:button type="submit" classes="button" nameKey="search"/></div>
    </form>
