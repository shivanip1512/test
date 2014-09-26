<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.readAttribute">

    <c:if test="${not empty errorMsg}">
        <div class="error stacked">${errorMsg}</div>
    </c:if>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.readAttribute" deviceCollection="${deviceCollection}">
        <cti:url var="groupMeterReadUrl" value="/group/groupMeterRead/readCollection"/>
        <form id="groupMeterReadForm" action="${groupMeterReadUrl}" method="post">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <tags:attributeSelector 
                attributes="${allGroupedReadableAttributes}" 
                name="attribute" 
                selectedAttributes="${selectedAttributes}" 
                multipleSize="8" 
                groupItems="true"/>
            <div class="page-action-area">
                <cti:button nameKey="read" type="submit" busy="true" classes="primary action M0"/>
            </div>
        </form>

    </tags:bulkActionContainer>
    
</cti:standardPage>