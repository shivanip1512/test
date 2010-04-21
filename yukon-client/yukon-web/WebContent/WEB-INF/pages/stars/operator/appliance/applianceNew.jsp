<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>
<%@ taglib tagdir="/WEB-INF/tags/dr/forms" prefix="drForms" %>

<cti:standardPage module="operator" page="appliance.new">
<tags:setFormEditMode mode="${mode}"/>

    <table>
        <tr>
            <td rowspan="3" valign="top">
                <tags:sectionContainer2 key="applianceInfo">
                    <form id="cancelForm" action="/spring/stars/operator/appliances/applianceList" method="get">
                        <input type="hidden" name="accountId" value="${accountId}">
                    </form>

                    <drForms:starsApplianceForm formId="createForm" formUrl="/spring/stars/operator/appliances/applianceCreate" />

                    <br>
                    <tags:slowInput2 myFormId="createForm" key="create" width="80px"/>
                    <tags:reset myFormId="createForm" />
                    <tags:slowInput2 myFormId="cancelForm" key="cancel" width="80px"/>
                        
                </tags:sectionContainer2>
            </td>
        </tr>
 	</table>
</cti:standardPage>