<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="pqrReport">
    
    <div class="stacked-md">
        <tags:selectedInventory inventoryCollection="${inventoryCollection}"/>
    </div>
    
    <cti:url var="url" value="/stars/operator/inventory/pqrReport/export"/>
    
    <form action="${url}">
        <cti:csrfToken/>
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        
        <dt:dateRange startValue="${yesterday}" endValue="${now}" startName="reportStart" endName="reportEnd" maxDate="${now}"/>
        
        <div class="page-action-area">
            <cti:button type="submit" nameKey="downloadButton" classes="primary action"/>
        </div>
    </form>
    
</cti:standardPage>