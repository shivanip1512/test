<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="systemAdministration">
    <tags:boxContainer2 nameKey="adminPages" styleClass="mediumContainer">
        <c:if test="${displayLinkToEnergyCompanySetup}"><div>
            <cti:url var="ecHome" value="/spring/adminSetup/energyCompany/home"/>
            <a href="${ecHome}"><i:inline key=".energyCompanyAdministration"/></a>
        </div></c:if>
        <cti:checkRolesAndProperties value="ADMIN_MULTISPEAK_SETUP"><div>
            <cti:url var="multiSpeakSetup" value="/spring/multispeak/setup/home"/>
            <a href="${multiSpeakSetup}"><i:inline key=".multiSpeakSetup"/></a>
        </div></cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="ADMIN_SUPER_USER"><div>
            <cti:url var="lmUserAssign" value="/spring/adminSetup/userEditor/home"/>
            <a href="${lmUserAssign}"><i:inline key=".userGroupEditor"/></a>
        </div></cti:checkRolesAndProperties>
        <div>
            <cti:url var="substation" value="/spring/adminSetup/substations/routeMapping/view"/>
            <a href="${substation}"><i:inline key=".substations"/></a>
        </div>
        <cti:checkRolesAndProperties value="ADMIN_SUPER_USER"><div>
            <cti:url var="maintenance" value="/spring/adminSetup/maintenance/view"/>
            <a href="${maintenance}"><i:inline key=".maintenance"/></a>
        </div></cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
            <cti:checkGlobalRolesAndProperties value="SHOW_ONE_WAY_ENCRYPT">
                <div>
                	<cti:url var="security" value="/spring/adminSetup/security/view"/>
                	<a href="${security}"><i:inline key=".security"/></a>
             	</div>
             </cti:checkGlobalRolesAndProperties>   
         </cti:checkRolesAndProperties>
         <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
            <div>
                <cti:url var="configuration" value="/spring/adminSetup/config/view"/>
                <a href="${configuration}"><i:inline key=".configuration"/></a>
            </div>
         </cti:checkRolesAndProperties>
    </tags:boxContainer2>
</cti:standardPage>