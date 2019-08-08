<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".ramp">
                        <cti:msg2 var="rampInLbl" key=".rampIn"/>
                        <cti:msg2 var="rampOutBtnLbl" key=".rampOut"/>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <div class="button-group">
                                <tags:check path="fields.rampIn" label="${rampInLbl}" classes="ML0"/>
                                <tags:check path="fields.rampOut" label="${rampOutBtnLbl}"/>
                            </div>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:choose>
                                <c:when test="${programGear.fields.rampIn && programGear.fields.rampOut}">
                                    ${rampInLbl},&nbsp;${rampOutBtnLbl}
                                </c:when>
                                <c:when test="${programGear.fields.rampIn}">
                                    ${rampInLbl}
                                </c:when>
                                <c:when test="${programGear.fields.rampOut}">
                                    ${rampOutBtnLbl}
                                </c:when>
                            </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".mode">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="tempreatureMode" items="${tempreatureModes}" varStatus="status">
                                <c:choose>
                                    <c:when test="${status.index == 0}">
                                        <c:set var="css" value="left yes ML0"/>
                                    </c:when>
                                    <c:when test="${status.index == fn:length(units)-1}">
                                        <c:set var="css" value="right yes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="css" value="middle yes"/>
                                    </c:otherwise>
                                </c:choose>
                                <tags:radio path="fields.mode" value="${tempreatureMode}" classes="${css}" key=".${tempreatureMode}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.mode}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".unit">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="unit" items="${units}" varStatus="status">
                                <c:choose>
                                    <c:when test="${status.index == 0}">
                                        <c:set var="css" value="left yes ML0"/>
                                    </c:when>
                                    <c:when test="${status.index == fn:length(units)-1}">
                                        <c:set var="css" value="right yes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="css" value="middle yes"/>
                                    </c:otherwise>
                                </c:choose>
                                <tags:radio path="fields.celsiusOrFahrenheit" value="${unit}" classes="${css}" key=".${unit}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.celsiusOrFahrenheit}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".heatingOffset" nameClass="vam">
                        <table>
                            <tr>
                                <td><tags:numeric path="fields.offset" size="5"/></td>
                                <td class="vam"><cti:msg2 key=".degreeFahrenheit"/></td>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".criticality" nameClass="vam">
                        <tags:numeric path="fields.criticality" size="5"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter"> 
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".groupCapacityReductionPercentage">
                        <tags:input path="fields.capacityReduction"/>
                    </tags:nameValue2>
                    <c:if test="${not empty selectedGearType}">
                        <%@ include file="gearWhenToChange.jsp" %>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".howToStopControl" nameClass="vam">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <tags:selectWithItems items="${howtoStopControlFields}" path="fields.howToStopControl"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.howToStopControl}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>