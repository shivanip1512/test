<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.readAttribute">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.readAttribute" deviceCollection="${deviceCollection}">
        <cti:url var="groupMeterReadUrl" value="/group/groupMeterRead/readCollection"/>
        <form action="${groupMeterReadUrl}" method="post">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <tags:attributeSelector 
                attributes="${allGroupedReadableAttributes}" 
                name="attribute" 
                selectedAttributes="${selectedAttributes}" 
                multipleSize="8" 
                groupItems="true"/>
            <div class="page-action-area">
                <cti:button nameKey="read" busy="true" classes="js-action-submit primary action M0"/>
            </div>
        </form>

    </tags:bulkActionContainer>
    
</cti:msgScope>