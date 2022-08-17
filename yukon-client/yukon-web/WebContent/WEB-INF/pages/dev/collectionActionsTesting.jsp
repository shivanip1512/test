<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="collectionActionsTesting">

    ${description}
    
    <form action="clearCache">
        
        <div class="page-action-area stacked">
            <cti:button label="Clear Cache" type="Submit"/>
        </div>
    </form>

</cti:standardPage>