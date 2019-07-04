<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${!displayGeo}">
    <c:set var="geoRowClass" value="dn"/>
</c:if>
<c:if test="${!displaySubstation}">
    <c:set var="substationRowClass" value="dn"/>
</c:if>
<c:if test="${!displayFeeder}">
    <c:set var="feederRowClass" value="dn"/>
</c:if>
<c:if test="${!displayZip}">
    <c:set var="zipRowClass" value="dn"/>
</c:if>
<c:if test="${!displayUser}">
    <c:set var="userRowClass" value="dn"/>
</c:if>
<c:if test="${!displaySerial}">
    <c:set var="serialRowClass" value="dn"/>
</c:if>
<c:if test="${!displayProgram}">
    <c:set var="programRowClass" value="dn"/>
</c:if>
<c:if test="${!displaySplinter}">
    <c:set var="splinterRowClass" value="dn"/>
</c:if>
<input type="hidden" id="allAddressUsage" value="${loadGroup.addressUsage}"/>
<tags:sectionContainer2 nameKey="geoAddress">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-addressUsage' nameKey=".addressUsage">
             <div class="button-group stacked">
                 <c:forEach var="addressUsageValue" items="${addressUsageList}">
                     <cti:displayForPageEditModes modes="CREATE,EDIT">
                           <tags:check id="${addressUsageValue}_chk" path="addressUsage" value="${addressUsageValue}" key="${addressUsageValue}"/>
                     </cti:displayForPageEditModes>
                 </c:forEach>
                 <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${not empty geoAddressUsage}">
                         <c:forEach var="geoAddress" items="${geoAddressUsage}">
                           <i:inline key="${geoAddress}"/>
                           <i:inline key="yukon.common.comma"/>
                         </c:forEach>
                     </c:if>
                 </cti:displayForPageEditModes>
             </div>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="geoAddressing">
    <tags:nameValueContainer2>
       <tags:nameValue2 nameKey=".serviceProvider">
            <tags:input path="serviceProvider" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-geoRow" nameKey=".geo" rowClass="${geoRowClass}">
            <tags:input id="js-geo" path="geo" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-substationRow" nameKey=".substation" rowClass="${substationRowClass}">
            <tags:input id="js-substation" path="substation" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-feederRow" nameKey=".feeder" rowClass="${feederRowClass}">
          <div id="js-feeder" class="button-group stacked">
            <form:hidden id="feederValueString" path="feeder"/>
                <c:forEach var="feederValue" items="${feederList}">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:check id="${feederValue}_chk" value="0" key="${feederValue}"/>
                     </cti:displayForPageEditModes>
                </c:forEach>
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${not empty loadGroup.feeder}">${loadGroup.feeder}</c:if>
                </cti:displayForPageEditModes>
         </div>
       </tags:nameValue2>
       <tags:nameValue2 id="js-zipRow" nameKey=".zip" rowClass="${zipRowClass}">
            <tags:input id="js-zip" path="zip" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-userRow" nameKey=".user" rowClass="${userRowClass}">
            <tags:input id="js-user" path="user" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-serialRow" nameKey=".serial" rowClass="${serialRowClass}">
            <tags:input id="js-serial" path="serialNumber" size="25" maxlength="60" autofocus="autofocus"/>
      </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="loadAddress">
    <tags:nameValueContainer2>
       <tags:nameValue2 id='js-loadAddressUsage'  nameKey=".loadAddressUsage">
             <div class="button-group stacked">
                 <c:forEach var="loadAddressUsageValue" items="${loadAddressUsageList}">
                     <cti:displayForPageEditModes modes="CREATE,EDIT">
                         <tags:check id="${loadAddressUsageValue}_chk" path="addressUsage"  value="${loadAddressUsageValue}" key="${loadAddressUsageValue}"/>
                     </cti:displayForPageEditModes>
                 </c:forEach>
                 <cti:displayForPageEditModes modes="VIEW">
                     <c:if test="${not empty loadAddressUsage}">
                         <c:forEach var="loadAddress" items="${loadAddressUsage}">
                             <i:inline key="${loadAddress}"/>
                             <i:inline key="yukon.common.comma"/>
                        </c:forEach>
                     </c:if>
                 </cti:displayForPageEditModes>
             </div>
       </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="loadAddressing">
    <tags:nameValueContainer2>
        <tags:nameValue2 id="js-programRow" nameKey=".program" rowClass="${programRowClass}">
            <tags:input id="js-program" path="program" size="25" maxlength="60" autofocus="autofocus"/>
        </tags:nameValue2>
        <tags:nameValue2 id="js-splinterRow" nameKey=".splinter" rowClass="${splinterRowClass}">
            <tags:input id="js-splinter" path="splinter" size="25" maxlength="60" autofocus="autofocus"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".sendControlMessage">
           <span id="js-sendControlMessageNo">
              <i:inline key="yukon.common.no"/>
           </span>
           <span id="js-sendControlMessageYes" class="dn">
              <i:inline key="yukon.common.yes"/>
           </span> 
        </tags:nameValue2>
        <tags:nameValue2 id="js-loads" nameKey=".loads">
             <div class="button-group stacked">
                 <c:forEach var="loadValue" items="${loadsList}">
                     <cti:displayForPageEditModes modes="CREATE,EDIT">
                         <tags:check id="${loadValue}_chk" path="relayUsage" value="${loadValue}" key="${loadValue}"/>
                     </cti:displayForPageEditModes>
                 </c:forEach>
                 <cti:displayForPageEditModes modes="VIEW">
                     <c:if test="${not empty loadGroup.relayUsage}">
                         <c:forEach var="load" items="${loadGroup.relayUsage}">
                           <i:inline key="${load}"/>
                           <i:inline key="yukon.common.comma"/>
                         </c:forEach>
                     </c:if>
                 </cti:displayForPageEditModes>
             </div>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>