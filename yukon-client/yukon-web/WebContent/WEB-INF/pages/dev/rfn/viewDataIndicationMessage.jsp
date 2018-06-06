<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dev" page="rfnTest">
    <tags:sectionContainer title="RFN Data Indication Message Test">
        <form:form action="sendDataIndicationMessage" method="post" commandName="meterReading">
        <cti:csrfToken/>
            <tags:nameValueContainer tableClass="natural-width">
                
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom" size="5" />
                    to 
                    <form:input path="serialTo" size="5" cssClass="optional"/>
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer and Model">
                    <form:select path="manufacturerModel">
                    <c:forEach var="group" items="${rfnTypeGroups}">
                        <optgroup label="${group.key}">
                        <c:forEach var="mm" items="${group.value}">
                            <form:option value="${mm}"><cti:msg2 key="${mm.type}" /> (${mm.manufacturer} ${mm.model})</form:option>
                        </c:forEach>
                        </optgroup>
                    </c:forEach>
                    </form:select>
                </tags:nameValue>

                <tags:nameValue name="Manufacturer override">
                    <form:input path="manufacturerOverride" size="7" cssClass="optional"/>
                </tags:nameValue>

                <tags:nameValue name="Model override">
                    <form:input path="modelOverride" size="7" cssClass="optional"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="action-area"><cti:button nameKey="sendMessage" type="submit" classes="js-blocker"/></div>
        </form:form>
    </tags:sectionContainer>
</cti:standardPage>