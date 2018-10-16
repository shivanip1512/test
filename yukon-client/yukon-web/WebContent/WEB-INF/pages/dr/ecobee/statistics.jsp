<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope  paths="modules.dr.home, modules.dr">
    <tags:sectionContainer2 nameKey="ecobee">
        <div class="stacked">
            <tags:nameValueContainer2 naturalWidth="false">
                <tags:nameValue2 nameKey=".ecobee.issues">
                    <c:set var="deviceClass" value="${deviceIssues > 0 ? 'label-danger' : 'label-default'}"/>
                    <c:set var="groupClass" value="${groupIssues > 0 ? 'label-danger' : 'label-default'}"/>
                    <span class="label ${deviceClass}">${deviceIssues}</span>&nbsp;
                    <i:inline key=".ecobee.devices"/>&nbsp;
                    <span class="label ${groupClass}">${groupIssues}</span>&nbsp;
                    <i:inline key=".ecobee.groups"/>&nbsp;
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="action-area">
            <a href="<cti:url value="/dr/ecobee"/>"><i:inline key=".details"/></a>
            <cti:button nameKey="configure" data-popup="#ecobee-config" icon="icon-cog-edit"/>
            <div data-dialog
                data-form
                id="ecobee-config"
                data-width="500"
                data-title="<cti:msg2 key=".ecobee.configure.title"/>"
                data-load-event="yukon_dr_ecobee_config_load"
                class="dn">
                <form:form action="ecobee/settings" method="POST" modelAttribute="ecobeeSettings">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                        <tags:nameValue2 nameKey=".ecobee.configure.errorChecking" rowId="ecobee-error-checking-toggle" valueClass="full-width">
                            <tags:hidden path="checkErrors" id="ecobee-check-errors"/>
                            <div class="button-group button-group-toggle">
                                <cti:button nameKey="on" classes="on yes M0"/>
                                <cti:button nameKey="off" classes="no M0"/>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ecobee.configure.dailyErrorCheck" rowId="ecobee-error-check-schedule" valueClass="full-width">
                            <tags:timeSlider startPath="checkErrorsTime" maxValue="1425" displayTimeToLeft="true"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ecobee.configure.dataCollection" rowId="ecobee-data-collection-toggle" valueClass="full-width">
                            <tags:hidden path="dataCollection" id="ecobee-data-collection"/>
                            <div class="button-group button-group-toggle">
                                <cti:button nameKey="on" classes="on yes M0"/>
                                <cti:button nameKey="off" classes="no M0"/>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ecobee.configure.dailyDataCollection" rowId="ecobee-data-collection-schedule" valueClass="full-width">
                            <tags:timeSlider startPath="dataCollectionTime" maxValue="1425" displayTimeToLeft="true"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form:form>
            </div>
        </div>
    </tags:sectionContainer2>
</cti:msgScope>

<cti:includeScript link="/resources/js/pages/yukon.dr.ecobee.js"/>