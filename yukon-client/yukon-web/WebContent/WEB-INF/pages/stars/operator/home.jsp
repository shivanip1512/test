<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="home">
    <cti:checkEnergyCompanyOperator showError="true" >
	   <tags:widget bean="operatorAccountSearchWidget"/>
	</cti:checkEnergyCompanyOperator>
</cti:standardPage>
