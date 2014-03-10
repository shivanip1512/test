<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="helperMenu" module="amr">

<c:if test="${param.tagsAccepted}">
    <div class="flashMessage">
        <i:inline key=".tagsAccepted"/>
    </div>
</c:if>
    
<c:if test="${param.tagsDeleted}">
    <div class="flashMessage">
        <i:inline key=".tagsDeleted"/>
    </div>
</c:if>
    
<c:if test="${param.validationEngineReset}">
    <div class="flashMessage">
        <i:inline key=".engineReset"/> 
        <i:inline key=".engineRestart"/>
    </div>
</c:if>
    
<div class="column-12-12">
    <div class="column one">
            <tags:sectionContainer2 nameKey="acceptAll">
                <p>
                    <i:inline key=".acceptAllText"/>
                </p>
                <form method="post" action="acceptAllTaggedRows">
                    <cti:csrfToken/>
                    <c:forEach var="tag" items="${rphTags}">
                        <label>
                            <input type="checkbox" name="selectedTags" value="${tag}">
                            <cti:logo key="${tag.logoKey}"/>
                            <cti:msg key="${tag}"/>
                        </label><br>
                    </c:forEach>
                            
                    <cti:button type="submit" value="Accept" nameKey="accept"/>
                </form>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="deleteAll">
                <p>
                    <i:inline key=".deleteAllText"/>
                </p>
                <form method="post" action="deleteAllTaggedRows">
                    <cti:csrfToken/>
                    <c:forEach var="tag" items="${rphTags}">
                        <label>
                            <input type="checkbox" name="selectedTags" value="${tag}">
                            <cti:logo key="${tag.logoKey}"/>
                            <cti:msg key="${tag}"/>
                        </label><br>
                    </c:forEach>
                            
                    <cti:button type="submit" value="Delete" nameKey="delete"/>
                </form>
            </tags:sectionContainer2>    
    </div>
    <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="recalculateTags">
                <p>
                    <i:inline key="yukon.web.modules.amr.helperMenu.recalculateTags1"/>
                </p>
                <p>
                    <i:inline key=".recalculateTags2"/>
                </p>
                <div class="clearfix stacked">
                    <form method="post" action="resetValidationEngineOneYear">
                        <cti:csrfToken/>
                        <span class="fl"><i:inline key=".deleteTagsOneYear"/></span>
                        <cti:button type="submit" nameKey="reset"/>
                    </form>
                </div>
                <div class="clearfix">
                    <form method="post" action="resetValidationEngine">
                        <cti:csrfToken/>
                        <span class="fl"><i:inline key=".deleteAllTags"/></span>
                        <cti:button type="submit" nameKey="reset"/>
                    </form>
                </div>
            </tags:sectionContainer2>    
    </div>
</div>
</cti:standardPage>