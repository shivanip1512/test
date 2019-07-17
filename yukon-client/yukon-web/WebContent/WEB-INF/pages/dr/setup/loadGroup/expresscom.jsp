<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<input type="hidden" id="allAddressUsage" value="${loadGroup.addressUsage}"/>
<input type="hidden" class="js-page-mode" value="${mode}">
<tags:sectionContainer2 nameKey="geoAddress">
    <tags:nameValueContainer2>
        <tags:nameValue2 id='js-addressUsage' nameKey=".addressUsage">
             <cti:displayForPageEditModes modes="CREATE,EDIT">
                <div class="button-group stacked">
                    <c:forEach var="addressUsageValue" items="${addressUsageList}">
                        <tags:check id="${addressUsageValue}_chk" path="addressUsage" value="${addressUsageValue}"
                                    key="${addressUsageValue}"/>
                    </c:forEach>
                </div>
             </cti:displayForPageEditModes>
             <cti:displayForPageEditModes modes="VIEW">
                 <c:if test="${not empty geoAddressUsage}">
                     <c:set var="geoCounter" value="0"/>
                     <c:forEach var="geoAddress" items="${geoAddressUsage}">
                         <c:if test="${geoCounter != 0}">
                             <i:inline key="yukon.common.comma"/>
                         </c:if>
                         <i:inline key="${geoAddress}"/>
                         <c:set var="geoCounter" value="${geoCounter + 1}"/>
                     </c:forEach>
                 </c:if>
            </cti:displayForPageEditModes>
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
              <div id="js-feeder" class="button-group">
                  <c:forEach var="feederValue" items="${feederList}" varStatus="status">
                      <tags:check id="${feederValue}_chk" value="0" key="${feederValue}" classes="${status.first ? 'ML0' : ''}"/>
                   </c:forEach>
              </div>
          </cti:displayForPageEditModes>
          <cti:displayForPageEditModes modes="VIEW">
             <c:if test="${not empty loadGroup.feeder}">${loadGroup.feeder}</c:if>
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
           <cti:displayForPageEditModes modes="CREATE,EDIT">
               <div class="button-group stacked">
                   <c:forEach var="loadAddressUsageValue" items="${loadAddressUsageList}">
                       <tags:check id="${loadAddressUsageValue}_chk" path="addressUsage"  value="${loadAddressUsageValue}" key="${loadAddressUsageValue}"/>
                   </c:forEach>
               </div>
           </cti:displayForPageEditModes>
           <cti:displayForPageEditModes modes="VIEW">
               <c:if test="${not empty loadAddressUsage}">
                   <c:set var="loadCounter" value="0"/>
                   <c:forEach var="loadAddress" items="${loadAddressUsage}">
                       <c:if test="${loadCounter != 0}">
                           <i:inline key="yukon.common.comma"/>
                       </c:if>
                       <i:inline key="${loadAddress}"/>
                       <c:set var="loadCounter" value="${loadCounter + 1}"/>
                    </c:forEach>
                </c:if>
            </cti:displayForPageEditModes>
       </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="loadAddressing">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".sendControlMessage">
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
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <div class="button-group stacked">
                    <c:forEach var="loadValue" items="${loadsList}">
                        <tags:check id="${loadValue}_chk" path="relayUsage" value="${loadValue}" key="${loadValue}" classes="${status.first ? 'ML0' : ''}"/>
                    </c:forEach>
                </div>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <c:if test="${not empty loadGroup.relayUsage}">
                    <c:set var="loadNumberCounter" value="0"/>
                    <c:forEach var="load" items="${loadGroup.relayUsage}">
                        <c:if test="${loadNumberCounter != 0}">
                            <i:inline key="yukon.common.comma"/>
                        </c:if>
                        <i:inline key="${load}"/>
                        <c:set var="loadNumberCounter" value="${loadNumberCounter + 1}"/>
                    </c:forEach>
                 </c:if>
            </cti:displayForPageEditModes>
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