<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<%@ attribute name="type" required="true" 
    type="com.cannontech.common.inventory.HardwareConfigType" description="The configuration type." %>
    
<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<cti:msgScope paths=",yukon.dr.config">

<div class="column-12-12 clearfix">
    <div class="column one">
        <c:choose>
            <c:when test="${type == 'EXPRESSCOM'}">
                <dr:expressComAddressingInfo path="${path}"/>
            </c:when>
            <c:when test="${type == 'VERSACOM'}">
                <dr:versaComAddressingInfo path="${path}"/>
            </c:when>
            <c:when test="${type == 'SA205'}">
                <dr:sa205AddressingInfo path="${path}"/>
            </c:when>
            <c:when test="${type == 'SA305'}">
                <dr:sa305AddressingInfo path="${path}"/>
            </c:when>
            <c:when test="${type == 'SA_SIMPLE'}">
                <dr:saSimpleAddressingInfo path="${path}"/>
            </c:when>
        </c:choose>
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="relays">
            <table class="compact-results-table dashed with-form-controls">
                <thead>
                    <tr>
                        <th><cti:msg2 key=".relay"/></th>
                        <th><cti:msg2 key=".coldLoadPickup"/></th>
                        <c:if test="${type.hasTamperDetect}">
                            <th><cti:msg2 key=".tamperDetect"/></th>
                        </c:if>
                        <c:if test="${type.hasProgramSplinter}">
                            <th><cti:msg2 key=".program"/></th>
                            <th><cti:msg2 key=".splinter"/></th>
                        </c:if>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="relay" begin="1" end="${type.numRelays}">
                    <tr>
                        <td>${relay}</td>
                        <td>
                            <tags:input path="${path}coldLoadPickup[${relay - 1}]" size="4" maxlength="10"/>
                            <i:inline key=".secs"/>
                        </td>
                        <c:if test="${type.hasTamperDetect}">
                            <td>
                                <c:if test="${relay <= 2}">
                                    <tags:input path="${path}tamperDetect[${relay - 1}]" size="4" maxlength="10"/>
                                </c:if>
                                <c:if test="${relay > 2}">&nbsp;</c:if>
                            </td>
                        </c:if>
                        <c:if test="${type.hasProgramSplinter}">
                            <td><tags:input path="${path}addressingInfo.program[${relay - 1}]" size="4" maxlength="10"/></td>
                            <td><tags:input path="${path}addressingInfo.splinter[${relay - 1}]" size="4" maxlength="10"/></td>
                        </c:if>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </tags:sectionContainer2>
    </div>
</div>

</cti:msgScope>