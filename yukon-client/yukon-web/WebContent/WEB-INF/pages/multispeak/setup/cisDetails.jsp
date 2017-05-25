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
                    <c:forEach var="cust" items="${custBasicsInfo}">
                        <tags:nameValue name="${cust.label}">${cust.value}</tags:nameValue>
                    </c:forEach>
                </tags:nameValueContainer>
            </tags:sectionContainer>
            <cti:msg2 var="customerContactInfo" key=".customerContactInfo" />
            <c:forEach var="custBasicContact" items="${custBasicContactInfo}" varStatus="status">
                <cti:msg2 var="customerContactInfo" key=".customerContactInfo" />
                <tags:sectionContainer title="${customerContactInfo}">
                    <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:if test="${not empty custBasicContact.phoneNumbers}">
                            <tags:nameValue2 nameKey=".phoneNumbers">${cti:join(custBasicContact.phoneNumbers,", ")}</tags:nameValue2>
                        </c:if>
                        <c:if test="${not empty custBasicContact.emailAddresses}">
                            <tags:nameValue2 nameKey=".emailAddresses">${cti:join(custBasicContact.emailAddresses,", ")}</tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                    <c:forEach var="address" items="${custBasicContact.addresses}" varStatus="addressStatus">
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
                    <c:forEach var="serviceInfo" items="${servLocBasicsInfo}">
                        <tags:nameValue name="${serviceInfo.label}">${serviceInfo.value}</tags:nameValue>
                    </c:forEach>
                </tags:nameValueContainer>
            </tags:sectionContainer>

            <!-- Meter -->
            <cti:msg2 var="meterInfo" key=".meterInfo" />
            <tags:sectionContainer title="${meterInfo}">
                <!-- Electric Service Point Information -->
                <c:if test="${not empty electricServicePointsInfo}">
                    <h3>
                        <i:inline key=".electricServicePointInfo" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="electricServicePoint" items="${electricServicePointsInfo}"
                            varStatus="electricStatus">
                            <c:choose>
                                <c:when test="${empty electricServicePoint.label}">
                                    <td><h3>${electricServicePoint.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${electricServicePoint.label}">${electricServicePoint.value}</tags:nameValue>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tags:nameValueContainer>
                </c:if>

                <!-- Gas Service Point Information -->
                <c:if test="${not empty gasServicePointsInfo}">
                    <h3>
                        <i:inline key=".gasServicePointsInfo" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="gasServicePoint" items="${gasServicePointsInfo}">
                            <c:choose>
                                <c:when test="${empty gasServicePoint.label}">
                                    <td><h3>${gasServicePoint.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${gasServicePoint.label}">${gasServicePoint.value}</tags:nameValue>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tags:nameValueContainer>
                </c:if>
                <!-- Propane Service Point Information -->
                <c:if test="${not empty propaneServicePointsInfo}">
                    <h3>
                        <i:inline key=".propaneServicePointsInfo" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="propaneServicePoint" items="${propaneServicePointsInfo}">
                            <c:choose>
                                <c:when test="${empty propaneServicePoint.label}">
                                    <td><h3>${propaneServicePoint.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${propaneServicePoint.label}">${propaneServicePoint.value}</tags:nameValue>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tags:nameValueContainer>
                </c:if>
                <!-- Water Service Point Information -->
                <c:if test="${not empty waterServicePointsInfo}">
                    <h3>
                        <i:inline key=".waterServicePointsInfo" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="waterServicePoint" items="${waterServicePointsInfo}">
                            <c:choose>
                                <c:when test="${empty waterServicePoint.label}">
                                    <td><h3>${waterServicePoint.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${waterServicePoint.label}">${waterServicePoint.value}</tags:nameValue>
                                </c:otherwise>
                            </c:choose>
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
                        <cti:tabs containerName="yukon:multispeak:accountDetails:tab">
                            <!-- Account Receivables Tab -->
                            <cti:msg2 var="receivables" key=".tab.receivables" />
                            <cti:tab title="${receivables}">
                                <c:set var="acctReceivables"
                                    value="${custReceivableInfo[custAccountInfo[caStatus.index].value]}" />
                                <c:if test="${empty acctReceivables}">
                                    <span class="empty-list"><i:inline key=".noReceivables" /></span>
                                </c:if>

                                <c:if test="${not empty acctReceivables}">
                                    <table id="receivablesTable" class="compact-results-table dashed row-highlighting">
                                        <thead>
                                            <tr>
                                                <th>${acctReceivables[0].label}</th>
                                                <th>${acctReceivables[1].label}</th>
                                                <th>${acctReceivables[2].label}</th>
                                                <th>${acctReceivables[3].label}</th>
                                                <th>${acctReceivables[4].label}</th>
                                            </tr>
                                        </thead>
                                        <tfoot></tfoot>
                                        <tbody>
                                            <c:forEach var="acctReceivable" items="${acctReceivables}" varStatus="i"
                                                step="5">
                                                <tr>
                                                    <td>${acctReceivables[i.index].value}</td>
                                                    <td>${acctReceivables[i.index+1].value}</td>
                                                    <td>${acctReceivables[i.index+2].value}</td>
                                                    <td>${acctReceivables[i.index+3].value}</td>
                                                    <td>${acctReceivables[i.index+4].value}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                            </cti:tab>

                            <!-- Account Priorities Tab -->
                            <cti:msg2 var="priorities" key=".tab.priorities" />
                            <cti:tab title="${priorities}">
                                <c:set var="acctPriorities"
                                    value="${custPriorityInfo[custAccountInfo[caStatus.index].value]}" />
                                <c:if test="${empty acctPriorities}">
                                    <span class="empty-list"><i:inline key=".noPriorities" /></span>
                                </c:if>

                                <c:if test="${not empty acctPriorities}">
                                    <table id="receivablesTable" class="compact-results-table dashed row-highlighting">
                                        <thead>
                                            <tr>
                                                <th>${acctPriorities[0].label}</th>
                                                <th>${acctPriorities[1].label}</th>
                                                <th>${acctPriorities[2].label}</th>
                                            </tr>
                                        </thead>
                                        <tfoot></tfoot>
                                        <tbody>
                                            <c:forEach items="${acctPriorities}" varStatus="i" step="3">
                                                <tr>
                                                    <td>${acctPriorities[i.index].value}</td>
                                                    <td>${acctPriorities[i.index+1].value}</td>
                                                    <td>${acctPriorities[i.index+2].value}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                            </cti:tab>
                        </cti:tabs>

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
            <c:forEach var="custAlternateContact" items="${custAlternateContactInfo}" varStatus="status">
                <cti:msg2 var="alternateContactInfo" key=".alternateContactInfo" />
                <tags:sectionContainer title="${alternateContactInfo}${status.index+1}">
                    <tags:nameValueContainer2 tableClass="name-value-table natural-width  stacked-md natural-width">
                        <tags:nameValue2 nameKey=".lastName">${custAlternateContact.lastName}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName">${custAlternateContact.firstName}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".middleName">${custAlternateContact.middleName}</tags:nameValue2>
                        <c:if test="${not empty custAlternateContact.phoneNumbers}">
                            <tags:nameValue2 nameKey=".phoneNumbers">${cti:join(custAlternateContact.phoneNumbers,", ")}</tags:nameValue2>
                        </c:if>
                        <c:if test="${not empty custAlternateContact.emailAddresses}">
                            <tags:nameValue2 nameKey=".emailAddresses">${cti:join(custAlternateContact.emailAddresses,", ")}</tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                    <c:forEach var="address" items="${custAlternateContact.addresses}" varStatus="addressStatus">
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