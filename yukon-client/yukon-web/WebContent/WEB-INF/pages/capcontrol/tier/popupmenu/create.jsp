<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="yukon.web.modules.capcontrol">
    <input type="hidden" class="title" value="${title}">
    <div class="column-12-12">
        <div class="column one">
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4002"/>"><i:inline key=".type.AREA"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4003"/>"><i:inline key=".type.SPECIAL_AREA"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4004"/>"><i:inline key=".type.SUBSTATION"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4000"/>"><i:inline key=".type.SUBBUS"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4001"/>"><i:inline key=".type.FEEDER"/></a></div>
        </div>
        <div class="column two nogutter">
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=1050"/>"><i:inline key=".type.CAPBANK"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=1051"/>"><i:inline key=".type.CBC"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4007"/>"><i:inline key=".regulator"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4005"/>"><i:inline key=".type.SCHEDULE"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4006"/>"><i:inline key=".type.STRATEGY"/></a></div>
        </div>
    </div>
</cti:msgScope>