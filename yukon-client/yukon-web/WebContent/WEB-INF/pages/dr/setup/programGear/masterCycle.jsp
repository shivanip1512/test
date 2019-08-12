 <%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
 
 <cti:msgScope paths="yukon.web.modules.dr.setup.gear">
     <div class="column-12-12 clearfix">
         <div class="column one">
             <tags:sectionContainer2 nameKey="controlParameters">
                 <tags:nameValueContainer2>
                     <tags:nameValue2 nameKey=".controlPercent">
                         <tags:numeric path="fields.controlPercent" size="10" minValue="5" maxValue="100"/>
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".cyclePeriod"> 
                         <tags:numeric path="fields.cyclePeriodInMinutes" size="10" minValue="5" maxValue="945"/>
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".groupSelectionMethod">
                         <cti:displayForPageEditModes modes="EDIT,CREATE">
                             <tags:selectWithItems items="${groupSelectionMethod}" path="fields.groupSelectionMethod" />
                         </cti:displayForPageEditModes>
                         <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.groupSelectionMethod}"/>
                        </cti:displayForPageEditModes>
                     </tags:nameValue2>
                 </tags:nameValueContainer2>
             </tags:sectionContainer2>
             
             <tags:sectionContainer2 nameKey="optionalAttributes">
                 <tags:nameValueContainer2>
                     <tags:nameValue2 nameKey=".groupCapacityReduction">
                         <tags:numeric path="fields.capacityReduction" size="10" minValue="0" maxValue="100"/>
                     </tags:nameValue2>
                      <%@ include file="gearWhenToChange.jsp" %>
                 </tags:nameValueContainer2>
              </tags:sectionContainer2>
         </div>

         <div class="column two nogutter"> 
             <tags:sectionContainer2 nameKey="rampIn">
                 <c:set var="rampInFieldEnabled"
                    value="${programGear.fields.rampInPercent > 0 || programGear.fields.rampInIntervalInSeconds > 0}" />
                 <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampIn">
                        <tags:switchButton name="rampIn" toggleGroup="rampInWindow" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"
                            checked="${rampInFieldEnabled}" />
                     </tags:nameValue2>
                     <c:set var="rampInFieldClass" value="${rampInFieldEnabled ? '' : 'dn'}" />
                     <tags:nameValue2 nameKey=".rampInPercent" data-toggle-group="rampInWindow" rowClass="${rampInFieldClass}">
                         <tags:numeric path="fields.rampInPercent" size="10" minValue="0" maxValue="100"/>
                         <i:inline key="yukon.common.units.PERCENT" />
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".rampInInterval" data-toggle-group="rampInWindow" rowClass="${rampInFieldClass}">
                         <tags:numeric path="fields.rampInIntervalInSeconds" size="10" minValue="-99999" maxValue="99999"/>
                         <i:inline key=".sec" />
                     </tags:nameValue2>
                 </tags:nameValueContainer2>
              </tags:sectionContainer2>
 
              <tags:sectionContainer2 nameKey="stopControl">
                 <tags:nameValueContainer2>
                     <tags:nameValue2 nameKey=".howToStopControl">
                         <cti:displayForPageEditModes modes="EDIT,CREATE">
                             <tags:selectWithItems id="howToStopControl" items="${howToStopControl}" path="fields.howToStopControl"/>
                         </cti:displayForPageEditModes>
                         <cti:displayForPageEditModes modes="VIEW">
                         <input type=hidden id="howToStopControl" value="${programGear.fields.howToStopControl}">
                            <i:inline key="${programGear.fields.howToStopControl}"/>
                        </cti:displayForPageEditModes>
                     </tags:nameValue2>
                     <tags:nameValue2 id="js-stopOrder-row" nameKey=".stopOrder" rowClass="dn">
                         <cti:displayForPageEditModes modes="EDIT,CREATE">
                             <tags:selectWithItems items="${stopOrder}" path="fields.stopOrder" />
                         </cti:displayForPageEditModes>
                         <cti:displayForPageEditModes modes="VIEW">
                             <i:inline key="${programGear.fields.stopOrder}"/>
                         </cti:displayForPageEditModes>
                     </tags:nameValue2>
                     <tags:nameValue2 id="js-rampOutPercent-row" nameKey=".rampOutPercent" rowClass="dn">
                         <tags:numeric path="fields.rampOutPercent" size="10" minValue="0" maxValue="100"/> 
                         <i:inline key="yukon.common.units.PERCENT" />
                     </tags:nameValue2>
                      <tags:nameValue2 id="js-rampOutInterval-row" nameKey=".rampOutInterval" rowClass="dn">
                          <tags:numeric path="fields.rampOutIntervalInSeconds" size="10" minValue="-99999" maxValue="99999"/>
                          <i:inline key=".sec" /> 
                      </tags:nameValue2>
                 </tags:nameValueContainer2>
              </tags:sectionContainer2>
         </div>
     </div>
 </cti:msgScope>