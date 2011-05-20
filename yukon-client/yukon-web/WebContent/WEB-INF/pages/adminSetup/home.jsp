<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="systemAdministration">
    <tags:boxContainer2 nameKey="adminPages" styleClass="mediumContainer">
        <div><c:if test="${isEcOperator}">
            <cti:url var="ecHome" value="/spring/adminSetup/energyCompany/home"/>
            <a href="${ecHome}">Energy Company Administration</a><br>
        </c:if></div>
        <div><cti:checkRolesAndProperties value="ADMIN_MULTISPEAK_SETUP">
            <cti:url var="multiSpeakSetup" value="/spring/multispeak/setup/home"/>
            <a href="${multiSpeakSetup}">MultiSpeak Setup</a>
        </cti:checkRolesAndProperties></div>
        <div><cti:checkRolesAndProperties value="ADMIN_SUPER_USER,ADMIN_LM_USER_ASSIGN">
            <cti:url var="lmUserAssign" value="/spring/adminSetup/userGroupEditor/home"/>
            <a href="${lmUserAssign}">User/Group Editor</a><br>
        </cti:checkRolesAndProperties></div>
        <div>
            <cti:url var="substation" value="/spring/adminSetup/substations/routeMapping/view"/>
            <a href="${substation}">Substations</a>
        </div>
    </tags:boxContainer2>
</cti:standardPage>