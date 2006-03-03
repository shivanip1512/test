<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Switch Search" module="esub">
<form action="/servlet/DisplaySearchServlet">
	<input type="text" name="key">
	<input type="hidden" name="searchurl" value="/esub/jsp/switch_search.jsp">
	<input type="submit" value="Search">
</form>	
</cti:standardPage>
