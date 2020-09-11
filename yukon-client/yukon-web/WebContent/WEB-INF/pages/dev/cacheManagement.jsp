<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="cacheManagement">

    <tags:sectionContainer title="Collection Actions">
    Click on the Clear Cache button to clear all Collection Actions from cache.
       <form action="clearCollectionActionCache">
            <div class="page-action-area stacked">
                <cti:button label="Clear Cache" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
    <tags:sectionContainer title="Cache Correlation">
	    Compares point data in PointUpdateBackingService, AsyncDynamicDataSource and latest value in RPH, if there is a mismatch, generates CSV file with results.
	    Notifies dispatch to log its values if mismatch is found. 
	    <form action="correlatePointData">
            <p>&nbsp;</p>
            <tags:deviceGroupPicker inputName="deviceSubGroups"
                multi="true" inputValue="${groups}" classes="fl" />
            <p>&nbsp;</p>
            <div class="page-action-area stacked">
                <cti:button label="Correlate Point Data" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
    <tags:sectionContainer title="Cache Correlation Schedule">
        <form action="scheduleCorrelationOfPointData">
            <p>&nbsp;</p>
            <p>Runs on the group selected every X hours, if mismatch found sends an email.</p>
            <tags:deviceGroupPicker inputName="deviceSubGroups"
                multi="true" inputValue="${groups}" classes="fl" />
            <input type="text" name="Hours" size="35" value="A">
            <input type="text" name="Email" size="35" value="B">
            <div class="page-action-area stacked">
                <cti:button label="Schedule" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
</cti:standardPage>