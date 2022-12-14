<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest.deviceArchive">

    <tags:sectionContainer title="Device Archive Request Test">
        <form:form id="deviceArchiveForm" action="sendDeviceArchiveRequest" modelAttribute="deviceArchiveParameters" method="POST">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom"/> to <form:input path="serialTo"/>
                </tags:nameValue>
                <tags:nameValue name="Device Type">
                    <select id="deviceType">
                        <option value="js-rf-da">RF DA</option>
                        <option value="js-rfn-lcr">Rfn LCR</option>
                        <option value="js-meter">Meter</option>
                        <option value="js-relay">Relay</option>
                        <option value="js-rfn-1200">RFN-1200</option>
                    </select>
                </tags:nameValue>
                <input type="hidden" id="manufacturer" name="manufacturer"/>
                <input type="hidden" id="model" name="model"/>
                <!-- RF DA Manufacturer and Model -->
                <tags:nameValue name="Model" nameClass="js-rfn-1200 dn" valueClass="js-rfn-1200 dn">
                    <select id="js-rfn-mm-1200">
                        <c:forEach var="port" items="${rfn1200}">
                             <option data-model="${port.model}" data-manufacturer="${port.manufacturer}" value="${port}"><cti:msg2 key="${port.type}" /> (${port.manufacturer} ${port.model})</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Manufacturer" nameClass="js-rf-da" valueClass="js-rf-da">
                    <select id="js-rf-da-manufacturer">
                        <option value="CPS">CPS</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Model" nameClass="js-rf-da" valueClass="js-rf-da">
                    <select id="js-rf-da-model">
                        <c:forEach var="daModel" items="${rfDaModels}">
                            <option value="${daModel}">${daModel}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <!-- RFN LCR Manufacturer and Model -->
                <tags:nameValue name="Manufacturer" nameClass="js-rfn-lcr dn" valueClass="js-rfn-lcr dn">
                    <select id="js-rfn-lcr-manufacturer">
                        <option value="CPS">CPS</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Model" nameClass="js-rfn-lcr dn" valueClass="js-rfn-lcr dn">
                    <select id="js-rfn-lcr-model">
                        <c:forEach var="lcrMm" items="${rfnLcrModels}">
                            <option value="${lcrMm.model}">${lcrMm.model}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <!-- Relay Manufacturer and Model -->
                <tags:nameValue name="Manufacturer" nameClass="js-relay dn" valueClass="js-relay dn">
                    <select id="js-relay-manufacturer">
                        <option value="EATON">EATON</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Model" nameClass="js-relay dn" valueClass="js-relay dn">
                    <select id="js-relay-model">
                        <option value="RFRelay">RFRelay</option>
                    </select>
                </tags:nameValue>
                <!-- Meter Manufacturer and Model -->
                <tags:nameValue name="Manufacturer and Model" nameClass="js-meter dn" valueClass="js-meter dn">
                    <select id="js-meter-manufacturer-model">
                        <c:forEach var="group" items="${meterModels}">
                            <optgroup label="${group.key}">
                            <c:forEach var="mm" items="${group.value}">
                                <option data-model="${mm.model}" data-manufacturer="${mm.manufacturer}" value="${mm}"><cti:msg2 key="${mm.type}" /> (${mm.manufacturer} ${mm.model})</option>
                            </c:forEach>
                            </optgroup>
                        </c:forEach>
                    </select>
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="page-action-area">
                <cti:button nameKey="send" classes="js-send-device-archive-request js-blocker"/>
            </div>
        </form:form>
    </tags:sectionContainer>
    
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.deviceArchiveRequestSimulator.js"/>

</cti:standardPage>