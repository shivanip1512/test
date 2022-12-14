<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<input type="hidden" id="allAddressUsage" value="${loadGroup.addressUsage}"/>
<input type="hidden" class="js-page-mode" value="${mode}">
<tags:sectionContainer2 nameKey="geoAddress">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-addressUsage' nameKey=".addressUsage">
            <c:set var="items" value="${isViewMode ? geoAddressUsage : addressUsageList}"/>
            <tags:checkboxButtonGroup items="${items}" path="addressUsage" buttonGroupContainerCssClasses="addressUsage" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="geoAddressing">
    <tags:nameValueContainer2>
       <tags:nameValue2 nameKey=".serviceProvider">
            <tags:input path="serviceProvider" size="25" maxlength="60"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-geoRow" nameKey=".geo" rowClass="${displayGeo == true ? '' : 'dn'}">
            <tags:input id="js-geo" path="geo" size="25" maxlength="60"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-substationRow" nameKey=".substation" rowClass="${displaySubstation == true ? '' : 'dn'}">
            <tags:input id="js-substation" path="substation" size="25" maxlength="60"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-feederRow" nameKey=".feeder" rowClass="${displayFeeder == true ? '' : 'dn'}">
          <cti:displayForPageEditModes modes="CREATE,EDIT">
              <form:hidden id="feederValueString" path="feeder"/>
              <div id="js-feeder" class="button-group feederChk">
                  <c:forEach var="feederValue" items="${feederList}" varStatus="status">
                      <tags:check id="${feederValue}_chk" value="0" key="${feederValue}" classes="${status.first ? 'ML0' : ''}"/>
                   </c:forEach>
              </div>
          </cti:displayForPageEditModes>
          <cti:displayForPageEditModes modes="VIEW">
              <c:choose>
                  <c:when test="${empty loadGroup.feeder}">
                      <i:inline key="yukon.common.none" />
                  </c:when>
                  <c:otherwise>
                      ${loadGroup.feeder}
                  </c:otherwise>
              </c:choose>
          </cti:displayForPageEditModes>
      </tags:nameValue2>
      <tags:nameValue2 id="js-zipRow" nameKey=".zip" rowClass="${displayZip == true ? '' : 'dn'}">
          <tags:input id="js-zip" path="zip" size="25" maxlength="60"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-userRow" nameKey=".user" rowClass="${displayUser == true ? '' : 'dn'}">
          <tags:input id="js-user" path="user" size="25" maxlength="60"/>
      </tags:nameValue2>
      <tags:nameValue2 id="js-serialRow" nameKey=".serial" rowClass="${displaySerial == true ? '' : 'dn'}">
          <tags:input id="js-serial" path="serialNumber" size="25" maxlength="60"/>
      </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="loadAddress">
    <tags:nameValueContainer2>
       <tags:nameValue2 id='js-loadAddressUsage'  nameKey=".loadAddressUsage">
            <c:set var="items" value="${isViewMode ? loadAddressUsage : loadAddressUsageList}"/>
            <tags:checkboxButtonGroup items="${items}" path="addressUsage" buttonGroupContainerCssClasses="loadaddressing" />
       </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="loadAddressing">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".sendControlMessage" nameClass="PB10">
            <cti:displayForPageEditModes modes="CREATE,EDIT">
               <span id="js-sendControlMessageNo">
                  <i:inline key="yukon.common.no"/>
               </span>
               <span id="js-sendControlMessageYes" class="dn">
                  <i:inline key="yukon.common.yes"/>
               </span>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <c:choose>
                    <c:when test="${loadSelected}">
                        <i:inline key="yukon.common.yes"/>
                    </c:when>
                    <c:otherwise>
                        <i:inline key="yukon.common.no"/>
                    </c:otherwise>
                </c:choose>  
            </cti:displayForPageEditModes>
        </tags:nameValue2>
        <tags:nameValue2 id="js-loads" nameKey=".loads">
            <c:set var="items" value="${isViewMode ? loadGroup.relayUsage : loadsList}"/>
            <tags:checkboxButtonGroup items="${items}" path="relayUsage" />
        </tags:nameValue2>
        <tags:nameValue2 id="js-programRow" nameKey=".program" rowClass="${displayProgram == true ? '' : 'dn'}">
            <tags:input id="js-program" path="program" size="25" maxlength="60"/>
        </tags:nameValue2>
        <tags:nameValue2 id="js-splinterRow" nameKey=".splinter" rowClass="${displaySplinter == true ? '' : 'dn'}">
            <tags:input id="js-splinter" path="splinter" size="25" maxlength="60"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div id="addressing-popup" data-dialog class="dn" 
            data-title="<cti:msg2 key=".loadAddressingPopup.title"/>"
            data-event="yukon:dr:setup:loadGroup:addressing"
            data-ok-text="<cti:msg2 key="yukon.common.no"/>"
            data-cancel-text="<cti:msg2 key="yukon.common.yes"/>"
            data-width="500" data-height="250"><cti:msg2 key=".loadAddressingPopup.message"/></div>
            
</tags:sectionContainer2>