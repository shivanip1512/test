<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="encryption">

    <script type="text/javascript">
    function cancel() {
        window.location = "view";
    }

    function submitForm() {
        Yukon.ui.blockPage();
        $("mainForm").submit();
    }
</script>

    <cti:msg2 var="boxTitle" key=".error.boxHeading" />
    <tags:boxContainer title="${boxTitle}" styleClass="mediumContainer" hideEnabled="false">
        <form:form id="mainForm" commandName="encryptedRoute" action="saveKey" method="POST">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".paoNameLbl">
                    <c:out value="${encryptedRoute.paoName}"></c:out>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".paoTypeLbl">
                    <c:out value="${encryptedRoute.type}"></c:out>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".CPSkeyLbl">
                    <tags:input path="value" size="50" />
                </tags:nameValue2>
                <form:input path="paobjectId" type="hidden" />
                <form:input path="type" type="hidden"/>
                <form:input path="paoName" type="hidden"/>
            </tags:nameValueContainer2>
        </form:form>
        </tags:boxContainer><br>
        <cti:button key="saveBtn" onclick="javascript:submitForm()" />
        <cti:button key="cancelBtn" onclick="javascript:cancel()" />
</cti:standardPage>
