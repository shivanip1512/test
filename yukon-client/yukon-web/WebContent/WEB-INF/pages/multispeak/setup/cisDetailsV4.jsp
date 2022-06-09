<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="multispeak.cisDetailsV4">


    <cti:tabs containerName="yukon:multispeak:cisDetailsV4:tab">

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
                <c:forEach var="address" items="${servLocAddressesList}" varStatus="addressStatus">
                    <h3><i:inline key=".alternateContactAddressInfo" />${addressStatus.index+1}</h3>
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

            <!-- Meter -->
            <cti:msg2 var="meterInfo" key=".meterInfo" />
            <tags:sectionContainer title="${meterInfo}">
            
             <!-- Electric Service Point Information -->
                <c:if test="${not empty electricServicePointsInfo}">
                <h3>
                        <i:inline key=".electricService" />
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
                
              <!-- Water Service Point Information -->
                <c:if test="${not empty waterServiceListInfo}">
                    <h3>
                        <i:inline key=".waterService" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="waterService" items="${waterServiceListInfo}">
                            <c:choose>
                                <c:when test="${empty waterService.label}">
                                    <td><h3>${waterService.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${waterService.label}">${waterService.value}</tags:nameValue>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tags:nameValueContainer>
                </c:if>
                
                <!-- Gas Service Point Information -->
                <c:if test="${not empty gasServiceListInfo}">
                    <h3>
                        <i:inline key=".gasService" />
                    </h3>
                    <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                        <c:forEach var="gasService" items="${gasServiceListInfo}">
                            <c:choose>
                                <c:when test="${empty gasService.label}">
                                    <td><h3>${gasService.value}</h3></td>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue name="${gasService.label}">${gasService.value}</tags:nameValue>
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
                        
                        <tags:nameValueContainer tableClass="name-value-table natural-width  stacked-md natural-width">
                            <c:forEach var="custAccountInfo" items="${custAccountInfo}" varStatus="electricStatus">
                             <c:choose>
                                 <c:when test="${empty custAccountInfo.label}">
                                     <td><h3>${custAccountInfo.value}</h3></td>
                                 </c:when>
                                 <c:otherwise>
                                     <tags:nameValue name="${custAccountInfo.label}">${custAccountInfo.value}</tags:nameValue>
                                 </c:otherwise>
                             </c:choose>
                            </c:forEach>
                        </tags:nameValueContainer> 

                        <cti:tabs containerName="yukon:multispeak:accountDetails:tab">
                            <!-- Account Receivables Tab -->
                            <cti:msg2 var="receivables" key=".tab.receivables" />
                            <cti:tab title="${receivables}">
                                <c:set var="acctReceivables" value="${custReceivableInfo[custAccountInfo[caStatus.index].value]}" />
                                
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