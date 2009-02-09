<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <form id="filterForm" action="/spring/meter/search">
        <input type="hidden" name="Filter" value="true" >
        <div>
	        <c:forEach var="filter" items="${filterByList}">
	            <div style="width: 21em; text-align: right; float:left; margin-bottom: 5px;margin-right: 5px;">${filter.name}:&nbsp;<input style="width: 10em" type="text" id="${filter.name}" name="${filter.name}" value="${filter.filterValue}"></div>
	        </c:forEach>
        </div>
        <div style="clear:both"></div>
        
        <div style="text-align: right"><input type="submit" value="Search"></div>
    </form>
