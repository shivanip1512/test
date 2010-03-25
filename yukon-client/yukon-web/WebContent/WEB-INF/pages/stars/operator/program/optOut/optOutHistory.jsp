<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="operator" page="optOut">
    
    <tags:boxContainer2 key="optOutHistory">
        <dr:optOutHistory previousOptOutList="${previousOptOutList}" />
    </tags:boxContainer2>

</cti:standardPage>