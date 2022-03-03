<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="cacheManagement">

    <tags:sectionContainer title="Collection Actions">
       <form action="clearCollectionActionCache">
            <div class="page-action-area stacked">
             Click on the Clear Cache button to clear all Collection Actions from cache.
                <cti:button label="Clear Cache" type="Submit" />
            </div>
        </form>
        <form action="terminateCollectionActions">
            <div class="page-action-area stacked">
            Terminate started Collection Actions.
                <cti:button label="Terminate" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
    <tags:sectionContainer title="Point Data Cache Correlation">
	    Compares point data in PointUpdateBackingService, AsyncDynamicDataSource and latest value in RPH, if there is a mismatch, generates CSV file with results. File location ${location}.
	    Notifies dispatch to log its values if mismatch is found. 
	    <form action="correlatePointData">
            <p>&nbsp;</p>
            <tags:deviceGroupPicker inputName="deviceSubGroups" multi="true" classes="fl" />
            <p>&nbsp;</p>
            <div class="page-action-area stacked">
                <cti:button label="Correlate Point Data" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
    <tags:sectionContainer title="Point Data Cache Correlation Schedule">
        <form action="scheduleCorrelationOfPointData">
            <p>Checks devices in a group every ${hours} hours, if mismatch found, sends an email. File location ${location}. <b>This task will only run if the email is present.</b></c:if></p>
            <tags:deviceGroupPicker inputName="deviceSubGroups"
                multi="true" inputValue="${groups}" classes="fl" />
            <p>&nbsp;</p>
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="Hours">
                    <input name="hours" type="text"
                        style="width: 45px;"
                        value="${hours}">
                </tags:nameValue>
            </tags:nameValueContainer>
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="Email">
                    <input name="email" type="text"
                        style="width: 200px;"
                        value="${email}">
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="page-action-area stacked">
                <cti:button label="Schedule" type="Submit" />
            </div>
        </form>
    </tags:sectionContainer>
</cti:standardPage>