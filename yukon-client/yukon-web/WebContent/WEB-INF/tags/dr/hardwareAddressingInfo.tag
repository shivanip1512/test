<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<script type="text/javascript">
function rateChanged() {
    // There is Java code doing this also in SA305.java.
	var newRate = $('rate').value;
	var rateMember = '';
	var rateFamily = '';
	if (!isNaN(newRate) && newRate > 0) {
	    rateMember = newRate % 16;
	    rateFamily = Math.floor(newRate / 16);
	}
	$('rateMember').innerHTML = rateMember;
	$('rateFamily').innerHTML = rateFamily;
}
</script>

<c:choose>
    <c:when test="${hardware.hardwareType.hardwareConfigType == 'EXPRESSCOM'}">
        <tags:formElementContainer nameKey="expressComAddresses">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".serviceProvider">
                    <tags:input path="serviceProvider" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".geo">
                    <tags:input path="geo" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".substation">
                    <tags:input path="substation" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".feeder">
                    <dr:bitSetter path="feederBits"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".zip">
                    <tags:input path="zip" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".userAddress">
                    <tags:input path="userAddress" size="6" maxlength="15"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:formElementContainer>
    </c:when>
    <c:when test="${hardware.hardwareType.hardwareConfigType == 'VERSACOM'}">
        <tags:formElementContainer nameKey="versaComAddresses">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".utility">
                    <tags:input path="utility" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".section">
                    <tags:input path="section" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".class">
                    <dr:bitSetter path="classAddressBits"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".division">
                    <dr:bitSetter path="divisionBits"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:formElementContainer>
    </c:when>
    <c:when test="${hardware.hardwareType.hardwareConfigType == 'SA205'}">
        <tags:formElementContainer nameKey="slotAddresses">
            <table>
                <c:forEach var="slotNum" begin="0" end="5">
                    <tr>
                        <td><tags:input path="slots[${slotNum}]" size="15" maxlength="15"/></td>
                    </tr>
                </c:forEach>
            </table>
        </tags:formElementContainer>
    </c:when>
    <c:when test="${hardware.hardwareType.hardwareConfigType == 'SA305'}">
        <tags:formElementContainer nameKey="sa305Addresses">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".utility">
                    <tags:input path="utility" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".group">
                    <tags:input path="group" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".division">
                    <tags:input path="division" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".substation">
                    <tags:input path="substation" size="6" maxlength="15"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".rate">
                    <tags:input id="rate" path="rate" size="6" maxlength="15"
                        onkeyup="rateChanged()" onblur="rateChanged()"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".rateMember">
                    <span id="rateMember">${rateMember}</span>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".rateFamily">
                    <span id="rateFamily">${rateFamily}</span>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:formElementContainer>
        <script type="text/javascript">rateChanged();</script>
    </c:when>
    <c:when test="${hardware.hardwareType.hardwareConfigType == 'SA_SIMPLE'}">
        <tags:formElementContainer nameKey="operationalAddress">
            <tags:input path="operationalAddress" size="15" maxlength="15"/>
        </tags:formElementContainer>
    </c:when>
</c:choose>

<br>
<tags:formElementContainer nameKey="relays">
<table class="compactResultsTable">
    <tr>
        <td>
        <table>
            <tr>
                <th><cti:msg2 key=".relay"/></th>
                <th><cti:msg2 key=".coldLoadPickup"/></th>
                <c:if test="${hardware.hardwareType.hasTamperDetect}">
                    <th><cti:msg2 key=".tamperDetect"/>Tamper Detect</th>
                </c:if>
                <c:if test="${hardware.hardwareType.hasProgramSplinter}">
                    <th><cti:msg2 key=".program"/></th>
                    <th><cti:msg2 key=".splinter"/></th>
                </c:if>
            </tr>

            <c:forEach var="relayNumber" begin="1"
                end="${hardware.hardwareType.numRelays}">
                <tr align="center">
                    <td>${relayNumber}</td>
                    <td><tags:input path="coldLoadPickup[${relayNumber-1}]"
                        size="4" maxlength="10"/></td>
                    <c:if test="${hardware.hardwareType.hasTamperDetect}">
                        <td>
                            <c:if test="${relayNumber <= 2}">
                                <tags:input path="tamperDetect[${relayNumber-1}]"
                                    size="4" maxlength="10"/>
                            </c:if>
                            <c:if test="${relayNumber > 2}">&nbsp;</c:if>
                        </td>
                    </c:if>
                    <c:if test="${hardware.hardwareType.hasProgramSplinter}">
                        <td><tags:input path="program[${relayNumber-1}]"
                            size="4" maxlength="10"/></td>
                        <td><tags:input path="splinter[${relayNumber-1}]"
                            size="4" maxlength="10"/></td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        </td>
    </tr>
</table>
</tags:formElementContainer>
