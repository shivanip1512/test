<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="miscellaneousMethod">

<cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
    <cti:dataGridCell>
        <tags:sectionContainer title="ISOC">
        <form action="insertProgramTypes" method="post">
            <cti:button key="insertProgramTypes" type="submit"/>
        </form>
        </tags:sectionContainer>
    </cti:dataGridCell>
</cti:dataGrid>

</cti:standardPage>
