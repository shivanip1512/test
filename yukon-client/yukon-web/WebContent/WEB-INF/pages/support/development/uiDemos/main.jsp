<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="uiDemos">
    <cti:includeScript link="/JavaScript/yukon.development.uiDemos.js"/>
    <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
    <cti:includeScript link="JQUERY_COOKIE" />

    <div class="column-6-18">
        <div class="column one">
            <tags:sectionContainer2 nameKey="response">
                <ul id="sections" class="side-tabs">
                    <li><a id="containers" href="#">Containers</a></li>
                    <li><a id="buttons" href="#">Buttons</a></li>
                    <li><a id="pickers" href="#">Pickers</a></li>
                    <li><a id="i18n" href="#">I18N</a></li>
                    <li><a id="i18nScopes" href="#">I18N Scopes</a></li>
                    <li><a id="dialogs" href="#">Dialogs</a></li>
                    <li><a id="jsTesting" href="#">JS Testing</a></li>
                    <li><a id="sprites" href="#">CSS Icons</a></li>
                    <li><a id="more" href="#">More</a></li>
                </ul>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <div id="rightColumn" class="clearfix">
                <%@ include file="containers.jsp"%>
            </div>
        </div>
    </div>




</cti:standardPage>