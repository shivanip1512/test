<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.pointMappingDecipher">

    <tags:sectionContainer title="Decipher Point Mapping">
        <p class="dialogFootnote">
          This tool determines if a point mapping exists in Yukon for a specific uom/modifier combination.  Enter the uom and the modifier, and, if applicable, the coincident value (Base Modifier) uom and modifier info.
          If the point was only found in rfnPointMapping.xml, the point name and Pao Type will be displayed, if the point was found in paoDefinition.xml point type, offset, multiplier, attributes will be displayed as well.
        </p>
        <form action="find" method="post">
            <cti:csrfToken />
            <tags:nameValueContainer2>
                     <tags:inputNameValue nameKey=".rfnTest.pointMappingDecipher.uom" path="dataToMatch.uom"/>
                     <tags:inputNameValue nameKey=".rfnTest.pointMappingDecipher.modifiers" path="dataToMatch.modifiers" size="100"/>
                      <!--    <td><label><input type="checkbox" id="coincident" name="coincident" toggleGroup="coincident">Coincident</label></td>-->
                     <tags:inputNameValue nameKey=".rfnTest.pointMappingDecipher.baseUom" path="dataToMatch.baseUom"/>
                     <tags:inputNameValue nameKey=".rfnTest.pointMappingDecipher.baseModifiers" path="dataToMatch.baseModifiers" size="100"/>
            </tags:nameValueContainer2>
            <div class="page-action-area">
                <cti:button nameKey="submit" type="submit" classes="js-blocker" />
            </div>
        </form>
        <br>
        <c:if test="${not empty mappings}">
            <table>
                <thead>
                    <tr>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.paoType"/></th>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.pointName"/></th>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.pointType"/></th>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.offset"/></th>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.multiplier"/></th>
                        <th><i:inline key=".rfnTest.pointMappingDecipher.attributes"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <c:forEach items="${mappings}" var="mapping">
                    <tbody>
                        <tr>
                            <td>${mapping.paoType}</td>
                            <td>${mapping.pointName}</td>
                            <td>${mapping.template.pointType}</td>
                            <td class="tar">${mapping.template.offset}</td>
                            <td class="tar">${mapping.template.multiplier}</td>
                            <td>${mapping.attributes}</td>
                        </tr>
                    </tbody>
                </c:forEach>
            </table>
        </c:if>
    </tags:sectionContainer>

</cti:standardPage>