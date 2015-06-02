<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="validation.advanced" module="amr">
<cti:includeScript link="/JavaScript/yukon.ami.vee.advanced.js"/>

<div id="tags-accepted" class="user-message success dn">
    <i:inline key=".tagsAccepted"/>
</div>

<div id="tags-deleted" class="user-message success dn">
    <i:inline key=".tagsDeleted"/>
</div>

<div id="engine-reset" class="user-message success dn">
    <i:inline key=".engineReset"/> 
    <i:inline key=".engineRestart"/>
</div>
    
<div class="column-12-12">
    <div class="column one">
        <tags:sectionContainer2 nameKey="acceptAll">
            
            <p><i:inline key=".acceptAllText"/></p>
            
            <form id="accept-form">
                <cti:csrfToken/>
                <div class="stacked">
                    <ul class="simple-list">
                        <c:forEach items="${displayTypes}" var="displayType" varStatus="index">
                            <li>
                                <label class="notes">
                                    <input type="checkbox" class="js-display-type" name="${displayType.rphTag}">
                                    <cti:icon icon="${displayType.rphTag.iconClass}" classes="fn"/>
                                    <cti:msg2 key="${displayType.rphTag}"/>
                                    <cti:dataUpdaterValue type="VALIDATION_PROCESSING" 
                                        identifier="${displayType.rphTag}_VIOLATIONS" styleClass="badge"/>
                                </label>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <cti:button id="accept-all" nameKey="accept" busy="true"/>
            </form>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="deleteAll">
            
            <p><i:inline key=".deleteAllText"/></p>
            
            <form id="delete-form">
                <cti:csrfToken/>
                <div class="stacked">
                    <ul class="simple-list">
                        <c:forEach items="${displayTypes}" var="displayType" varStatus="index">
                            <li>
                                <label class="notes">
                                    <input type="checkbox" class="js-display-type" name="${displayType.rphTag}">
                                    <cti:icon icon="${displayType.rphTag.iconClass}" classes="fn"/>
                                    <cti:msg2 key="${displayType.rphTag}"/>
                                    <cti:dataUpdaterValue type="VALIDATION_PROCESSING" 
                                        identifier="${displayType.rphTag}_VIOLATIONS" styleClass="badge"/>
                                </label>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <cti:button id="delete-all" nameKey="delete" busy="true"/>
            </form>
        </tags:sectionContainer2>
    </div>
    
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="recalculateTags">
            
            <p><i:inline key=".recalculateTags1"/></p>
            <p><i:inline key=".recalculateTags2"/></p>
            
            <div class="clearfix stacked">
                <form id="reset-one-year-form">
                    <cti:csrfToken/>
                    <span class="fl"><i:inline key=".deleteTagsOneYear"/></span>
                    <cti:button id="reset-12-months" nameKey="reset" busy="true"/>
                </form>
            </div>
            
            <div class="clearfix">
                <form id="reset-all-form">
                    <cti:csrfToken/>
                    <span class="fl"><i:inline key=".deleteAllTags"/></span>
                    <cti:button id="reset-all" nameKey="reset" busy="true"/>
                </form>
            </div>
            
        </tags:sectionContainer2>
    </div>
    
</div>
</cti:standardPage>