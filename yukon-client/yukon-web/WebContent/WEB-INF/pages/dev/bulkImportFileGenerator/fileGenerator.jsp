<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="bulkImportFileGenerator">

    <script>
    $(function() {
        $("input[type='text']").keypress(function () {
            $('.user-message').remove();
        });
    });
    </script>

    <form id="bulkImportFileGenerator" action="<cti:url value="/dev/bulkImportFileGenerator/fileGenerator"/>" method="post">
        <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue id="deviceGroups" name="Device Group" nameColumnWidth="250px">
                        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
                        <tags:deviceGroupNameSelector fieldName="deviceGroupName"
                            dataJson="${groupDataJson}" showSelectedDevicesIcon="false"/>
                </tags:nameValue>
                <tags:nameValue name="Append Text to Meter Name/Number" nameColumnWidth="250px">
                    <input type="text" name="appendedText">
                </tags:nameValue>
            </tags:nameValueContainer>
    </form>
    <div class="page-action-area">
        <cti:button type="submit" name="buttonAction" classes="primary action" label="Download" onclick="$('#bulkImportFileGenerator').submit();"/>
    </div>
</cti:standardPage>
