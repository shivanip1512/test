<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="allVersaAddressUsage" value="${loadGroup.addressUsage}"/>
<input type="hidden" class="js-page-mode" value="${mode}">
<tags:sectionContainer2 nameKey="addressUsage">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-versaAddressUsage' nameKey=".usage">
            <c:set var="items" value="${mode == 'VIEW' ? addressUsages : addressUsageList}"/>
            <tags:checkboxButtonGroup items="${items}" path="addressUsage" buttonGroupContainerCssClasses="verAddressUsage"/>
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
                    <c:choose>
                        <c:when test="${empty loadGroup.classAddress}">
                            <i:inline key="yukon.common.none"/>
                        </c:when>
                        <c:otherwise>
                            ${loadGroup.classAddress}
                        </c:otherwise>
                    </c:choose>
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
                <c:choose>
                        <c:when test="${empty loadGroup.divisionAddress}">
                            <i:inline key="yukon.common.none"/>
                        </c:when>
                        <c:otherwise>
                            ${loadGroup.divisionAddress}
                        </c:otherwise>
                    </c:choose>
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
            <c:set var="items" value="${mode == 'VIEW' ? relayUsages : relayUsageList}"/>
            <tags:checkboxButtonGroup items="${items}" path="relayUsage"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>