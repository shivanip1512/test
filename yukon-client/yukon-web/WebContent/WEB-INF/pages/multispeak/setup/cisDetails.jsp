<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="multispeak.cisDetails">


    <cti:tabs containerName="yukon:multispeak:cisDetails:tab">

        <!-- Basic Info Tab -->
        <cti:msg2 var="general" key=".tab.basicInfo" />
        <cti:tab title="${general}">
            <cti:msg2 var="custInfo" key=".customerInfo" />
            <tags:sectionContainer title="${custInfo}">
                <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                    <c:forEach var="x" items="${custBasicsInfo}">
                        <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
                    </c:forEach>
                </tags:nameValueContainer>
            </tags:sectionContainer>
            <cti:msg2 var="customerContactInfo" key=".customerContactInfo" />
            <c:forEach var="x" items="${custBasicContactInfo}" varStatus="status">
                <cti:msg2 var="customerContactInfo" key=".customerContactInfo" />
                <tags:sectionContainer title="${customerContactInfo}">
                    <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                        <tags:nameValue2 nameKey=".phoneNumbers">${x.phoneNumbers}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddresses">${x.emailAddresses}</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <c:forEach var="address" items="${x.addresses}" varStatus="addressStatus">
                        <h3>
                            <i:inline key=".alternateContactAddressInfo" />${addressStatus.index+1}</h3>
                        <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                            <tags:nameValue2 nameKey=".address1Label">${address.locationAddress1}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".address2Label">${address.locationAddress2}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".cityLabel">${address.cityName}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".stateLabel">${address.stateCode}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".zipLabel">${address.zipCode}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".countyLabel">${address.county}</tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:forEach>
                </tags:sectionContainer>
            </c:forEach>

            <!-- Service Location -->
            <cti:msg2 var="serviceLocationInfo" key=".serviceLocationInfo" />
            <tags:sectionContainer title="${serviceLocationInfo}">
                <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                    <c:forEach var="x" items="${servLocBasicsInfo}">
                        <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
                    </c:forEach>
                </tags:nameValueContainer>
            </tags:sectionContainer>

            <!-- Meter -->
            <cti:msg2 var="meterInfo" key=".meterInfo" />
            <tags:sectionContainer title="${meterInfo}">
                <c:if test="${not empty electricServicePointsInfo}">
                    <h3>
                        <i:inline key=".electricServicePointInfo" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="x" items="${electricServicePointsInfo}" varStatus="electricStatus">
                            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
                        </c:forEach>
                    </tags:nameValueContainer>
                </c:if>
            </tags:sectionContainer>

        </cti:tab>

        <!-- Accounts Tab -->
        <cti:msg2 var="accounts" key=".tab.accounts" />
        <cti:tab title="${accounts}">

            <c:if test="${empty custAccountInfo}">
                <span class="empty-list"><i:inline key=".noResults" /></span>
            </c:if>

            <c:if test="${not empty custAccountInfo}">

                <c:forEach var="custAccount" items="${custAccountInfo}" varStatus="caStatus" step="15">
                    <cti:msg2 var="accountNumberHeaderTitle" key=".accountNumber" />
                    <tags:boxContainer title="${accountNumberHeaderTitle}${custAccountInfo[caStatus.index].value}"
                        id="accountNumberHeaderTitle">


                        <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                            <tags:nameValue2 nameKey=".accountStatus">${custAccountInfo[caStatus.index+1].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".billingCycle">${custAccountInfo[caStatus.index+2].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".budgetCycle">${custAccountInfo[caStatus.index+3].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".paymentDueDate">${custAccountInfo[caStatus.index+4].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".lastPaymentDate">${custAccountInfo[caStatus.index+5].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".lastPaymentAmount">${custAccountInfo[caStatus.index+6].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".billDate">${custAccountInfo[caStatus.index+7].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".shutOffDate">${custAccountInfo[caStatus.index+8].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".prePay">${custAccountInfo[caStatus.index+9].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".billingTerm">${custAccountInfo[caStatus.index+10].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".calculatedCurrentBillAmt">${custAccountInfo[caStatus.index+11].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".calculatedCurrentBillDate">${custAccountInfo[caStatus.index+12].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".lastBillAmt">${custAccountInfo[caStatus.index+13].value}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".calculatedUsedYesterday">${custAccountInfo[caStatus.index+14].value}</tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:boxContainer>
                </c:forEach>
            </c:if>

        </cti:tab>

        <!-- Customer Hazards Tab -->
        <cti:msg2 var="customerHazards" key=".tab.customerHazards" />
        <cti:tab title="${customerHazards}">
            <c:if test="${empty custHazardInfo}">
                <span class="empty-list"><i:inline key=".noResults" /></span>
            </c:if>

            <c:if test="${not empty custHazardInfo}">
                <table id="custHazardInfoTable" class="compact-results-table dashed row-highlighting">
                    <thead>
                        <tr>

                            <th><i:inline key=".customerHazardType" /></th>
                            <th><i:inline key=".customerHazardSubType" /></th>
                            <th><i:inline key=".hazardText" /></th>

                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>

                        <c:forEach var="custHazard" items="${custHazardInfo}" step="3" varStatus="i">
                            <tr>
                                <td>${custHazardInfo[i.index].value}</td>
                                <td>${custHazardInfo[i.index+1].value}</td>
                                <td>${custHazardInfo[i.index+2].value}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </c:if>
        </cti:tab>

        <!-- Alternate Contacts Tab -->
        <cti:msg2 var="alternateContacts" key=".tab.alternateContacts" />
        <cti:tab title="${alternateContacts}">
            <c:forEach var="x" items="${custAlternateContactInfo}" varStatus="status">
                <cti:msg2 var="alternateContactInfo" key=".alternateContactInfo" />
                <tags:sectionContainer title="${alternateContactInfo}${status.index+1}">
                    <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                        <tags:nameValue2 nameKey=".lastName">${x.lastName}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName">${x.firstName}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".middleName">${x.middleName}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".phoneNumbers">${x.phoneNumbers}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddresses">${x.emailAddresses}</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <c:forEach var="address" items="${x.addresses}" varStatus="addressStatus">
                        <h3>
                            <i:inline key=".alternateContactAddressInfo" />${addressStatus.index+1}</h3>

                        <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                            <tags:nameValue2 nameKey=".address1Label">${address.locationAddress1}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".address2Label">${address.locationAddress2}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".cityLabel">${address.cityName}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".stateLabel">${address.stateCode}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".zipLabel">${address.zipCode}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".countyLabel">${address.county}</tags:nameValue2>
                        </tags:nameValueContainer2>

                    </c:forEach>

                </tags:sectionContainer>
            </c:forEach>
        </cti:tab>
    </cti:tabs>

</cti:standardPage>