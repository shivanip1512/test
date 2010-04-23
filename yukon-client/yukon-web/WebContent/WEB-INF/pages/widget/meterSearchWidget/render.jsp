<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
	Event.observe(window, 'load', function() {
	  $('searchField_1').focus();
	});
</script>


    <form id="filterForm" action="/spring/meter/search">
        <input type="hidden" name="Filter" value="true" >
        <tags:nameValueContainer altRowOn="false">
	        <c:forEach var="filter" items="${filterByList}" varStatus="status">
            <tags:nameValue name="${filter.name}"><input size="45" type="text" id="searchField_${status.count}" name="${filter.name}" value="${filter.filterValue}"></tags:nameValue>
	        </c:forEach>
        </tags:nameValueContainer>
        
        <div style="text-align: right"><input type="submit" value="Search" class="formSubmit"></div>
    </form>
