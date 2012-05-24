<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">

    <cti:url var="assignUrl" value="/spring/capcontrol/command/returnBank">
        <cti:param name="bankId" value="${paoId}"/>
        <cti:param name="assignHere" value="true"/>
    </cti:url>
    <cti:url var="returnUrl" value="/spring/capcontrol/command/returnBank">
        <cti:param name="bankId" value="${paoId}"/>
        <cti:param name="assignHere" value="false"/>
    </cti:url>

	<div id="menuPopupBoxContainer">
		<input type="hidden" id="dialogTitle" value="${title}">
		<div class="content boxContainer_content">
			<ul class="capcontrolMenu">
				<li class="menuOption f_blocker"
					onclick="window.location='${assignUrl}'"><a href="javascript:void(0);">${assignLabel}</a></li>
				<li class="menuOption f_blocker"
					onclick="window.location='${returnUrl}'"><a href="javascript:void(0);">${returnLabel}</a></li>
			</ul>
		</div>
	</div>
</cti:msgScope>