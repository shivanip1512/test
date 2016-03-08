<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="yukon.web.modules.capcontrol">
    <input type="hidden" class="title" value="${title}">
    <div class="column-12-12">
        <div class="column one">
            <div class="stacked"><a href="<cti:url value="/capcontrol/areas/create"/>"><i:inline key=".type.AREA"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/areas/special/create"/>"><i:inline key=".type.SPECIAL_AREA"/></a></div>
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=4004"/>"><i:inline key=".type.SUBSTATION"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/buses/create"/>"><i:inline key=".type.SUBBUS"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/feeders/create"/>"><i:inline key=".type.FEEDER"/></a></div>
        </div>
        <div class="column two nogutter">
            <div class="stacked"><a href="<cti:url value="/editor/cbcWizBase.jsf?type=1050"/>"><i:inline key=".type.CAPBANK"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/cbc/create"/>"><i:inline key=".type.CBC"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/regulators/create"/>"><i:inline key=".regulator"/></a></div>
            <div class="stacked"><a href="<cti:url value="/capcontrol/strategies/create"/>"><i:inline key=".type.STRATEGY"/></a></div>
        </div>
    </div>
</cti:msgScope>