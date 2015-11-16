<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="operator" page="optOut.history">
    
    <tags:sectionContainer2 nameKey="optOutHistory">
        <dr:optOutHistory previousOptOutList="${previousOptOutList}" />
    </tags:sectionContainer2>

</cti:standardPage>