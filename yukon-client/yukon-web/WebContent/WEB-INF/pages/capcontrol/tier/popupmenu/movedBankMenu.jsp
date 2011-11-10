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
    
    <div id="menuPopupBoxContainer" class="thinBorder">
        <div class="titledContainer boxContainer">
        
            <div class="titleBar boxContainer_titleBar">
                <div class="controls" onclick="$('menuPopup').hide()">
                    <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
                </div>
                <div class="title boxContainer_title">${title}</div>
            </div>
            
            <div class="content boxContainer_content">
                <ul class="capcontrolMenu">
                    <li class="menuOption f_blocker" onclick="window.location='${assignUrl}'">${assignLabel}</li>
                    <li class="menuOption f_blocker" onclick="window.location='${returnUrl}'">${returnLabel}</li>
                </ul>
            </div>
        </div>
    </div>
</cti:msgScope>