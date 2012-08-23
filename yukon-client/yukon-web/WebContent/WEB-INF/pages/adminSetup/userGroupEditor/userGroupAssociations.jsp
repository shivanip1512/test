<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="userGroupEditor.${mode}">
    <tags:UserGroupAssociations yukonRoles="${yukonRoles}" />
</cti:standardPage>