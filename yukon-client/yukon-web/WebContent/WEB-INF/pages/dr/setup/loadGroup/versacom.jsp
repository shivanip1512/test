<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="allVersaAddressUsage" value="${loadGroup.addressUsage}"/>
<input type="hidden" class="js-page-mode" value="${mode}">
<tags:sectionContainer2 nameKey="addressUsage">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-versaAddressUsage' nameKey=".usage">
             <cti:displayForPageEditModes modes="CREATE,EDIT">
                 <div class="button-group stacked verAddressUsage">
                     <c:forEach var="addressUsageValue" items="${addressUsageList}">
                         <tags:check id="${addressUsageValue}_chk" path="addressUsage" value="${addressUsageValue}"
                                    key="${addressUsageValue}"/>
                     </c:forEach>
                 </div>
             </cti:displayForPageEditModes>
             <cti:displayForPageEditModes modes="VIEW">
                 <c:if test="${not empty addressUsages}">
                     <c:forEach var="addressUsage" items="${addressUsages}" varStatus="status">
                         <c:if test="${!status.first}">
                             <i:inline key="yukon.common.comma"/>&nbsp;
                         </c:if>
                         <i:inline key="${addressUsage}"/>
                     </c:forEach>
                 </c:if>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="address">
    <tags:nameValueContainer2>
        <tags:nameValue2 id="js-utilityAddress-row" nameKey=".utilityAddress" rowClass="${showUtilityAddress == true ? '' : 'dn'}">
            <tags:input id="js-utilityAddress" path="utilityAddress" size="15" maxlength="60"/>
        </tags:nameValue2>
        <tags:nameValue2 id="js-sectionAddress-row" nameKey=".sectionAddress" rowClass="${showSectionAddress == true ? '' : 'dn'}">
            <tags:input id="js-sectionAddress" path="sectionAddress" size="15" maxlength="60"/>
        </tags:nameValue2>
        <tags:nameValue2 id="js-classAddress-row" nameKey=".classAddress" rowClass="${showClassAddress == true ? '' : 'dn'}">
            <form:hidden id="classAddressString" path="classAddress"/>
            <div id="js-classAddress" class="button-group stacked classAddress">             
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <c:forEach var="classAddressValue" items="${classAddressValues}" varStatus="status">
                        <tags:check id="${classAddressValue}_chk" value="0" key="${classAddressValue}" classes="${status.first ? 'ML0' : ''}"/>
                    </c:forEach>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${not empty loadGroup.classAddress}">${loadGroup.classAddress}</c:if>
                </cti:displayForPageEditModes>
            </div>
        </tags:nameValue2>
        <tags:nameValue2 id="js-divisionAddress-row" nameKey=".divisionAddress" rowClass="${showDivisionAddress == true ? '' : 'dn'}">
            <form:hidden id="divisionAddressString" path="divisionAddress"/>
            <div id="js-divisionAddress" class="button-group stacked divisionAddress">
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <c:forEach var="divisionAddressValue" items="${divisionAddressValues}" varStatus="status">
                        <tags:check id="${divisionAddressValue}_chk" value="0" key="${divisionAddressValue}" classes="${status.first ? 'ML0' : ''}"/>
                    </c:forEach>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${not empty loadGroup.divisionAddress}">${loadGroup.divisionAddress}</c:if>
                </cti:displayForPageEditModes>
            </div>
        </tags:nameValue2>
        <tags:nameValue2 id="js-serialAddress-row" nameKey=".serialAddress" rowClass="${showSerialAddress == true ? '' : 'dn'}">
            <tags:input id="js-serialAddress" path="serialAddress" size="15" maxlength="60"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="relayUsage">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-relayUsage-row' nameKey=".relayUsage">
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <div class="button-group stacked">
                    <c:forEach var="relayUsageValue" items="${relayUsageList}">
                        <tags:check id="${relayUsageValue}_chk" path="relayUsage" value="${relayUsageValue}" key="${relayUsageValue}"/>
                    </c:forEach>
                </div>
             </cti:displayForPageEditModes>
             <cti:displayForPageEditModes modes="VIEW">
                 <c:choose>
                    <c:when test="${not empty relayUsages}">
                        <c:forEach var="relayUsage" items="${relayUsages}" varStatus="status">
                            <c:if test="${!status.first}">
                                <i:inline key="yukon.common.comma"/>&nbsp;
                            </c:if>
                            <i:inline key="${relayUsage}"/>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <i:inline key="yukon.common.none"/>
                    </c:otherwise>
                </c:choose>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>