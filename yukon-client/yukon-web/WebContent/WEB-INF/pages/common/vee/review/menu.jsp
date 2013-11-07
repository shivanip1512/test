<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="helperMenu" module="monitors">
<cti:standardMenu menuSelection="validation|helper"/>

<cti:breadCrumbs>
    <cti:crumbLink url="/dashboard" title="Operations Home"  />
    <cti:crumbLink title="Advanced" />
</cti:breadCrumbs>

<c:if test="${param.tagsAccepted}">
<div class="flashMessage">
Matching RawPointHistory rows have been marked as Accepted.
</div>
</c:if>
	
<c:if test="${param.tagsDeleted}">
<div class="flashMessage">
Matching RawPointHistory rows have been deleted.
</div>
</c:if>
	
<c:if test="${param.validationEngineReset}">
<div class="flashMessage">
The Validation Engine has been reset. 
<br><br>
Please restart the Yukon Service Manager to resume processing.
</div>
</c:if>
	
<div class="column-12-12">
    <div class="column one">
            <tags:boxContainer title="Accept All" styleClass="widget-container">
                <p class="warning">
                This will insert the OK tag for the any RawPointHistory value that has the identical
                set of tags as are checked. This has the same effect as the Accept selection
                on the Review page.
                </p>
                <form method="post" action="acceptAllTaggedRows">
                    <c:forEach var="tag" items="${rphTags}">
                        <label>
                            <input type="checkbox" name="selectedTags" value="${tag}">
                            <cti:logo key="${tag.logoKey}"/>
                            <cti:msg key="${tag}"/>
                        </label><br>
                    </c:forEach>
                            
                    <input type="submit" value="Accept">
                </form>
            </tags:boxContainer>	
            <tags:boxContainer title="Delete All" styleClass="widget-container">
                <p class="warning">
                This will delete any RawPointHistory value that has the identical set of tags
                as are selected tags (but has not already been marked as Accepted). This has the same effect as the Delete selection
                on the Review page. <b>Archived usage readings will be removed
                completely from the database. This cannot be undone! </b>
                </p>
                <form method="post" action="deleteAllTaggedRows">
                    <c:forEach var="tag" items="${rphTags}">
                        <label>
                            <input type="checkbox" name="selectedTags" value="${tag}">
                            <cti:logo key="${tag.logoKey}"/>
                            <cti:msg key="${tag}"/>
                        </label><br>
                    </c:forEach>
                            
                    <input type="submit" value="Delete">
                </form>
            </tags:boxContainer>	
    </div>
    <div class="column two nogutter">
            <tags:boxContainer title="Recalculate Tags" styleClass="widget-container">
                <p class="warning">
                The following options will reset the validation engine so that it reprocesses
                all of the RawPointHistory rows from the beginning or from 12 months ago.
                </p>
                <p class="warning">
                Before selecting one of these options, shut down the Yukon Service Manager service.
                After pressing the Delete button, the service may be restarted.
                </p>
                <form method="post" action="resetValidationEngineOneYear">
                    Delete previous 12 months of tags: <input type="submit" value="Reset">
                </form>
                <form method="post" action="resetValidationEngine">
                    Delete all tags: <input type="submit" value="Reset">
                </form>
            </tags:boxContainer>	
    </div>
</div>
</cti:standardPage>