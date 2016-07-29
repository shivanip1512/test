<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="tools" page="dataStreaming.summary">

    <style>
.inline {
    display: inline-flex;
}
</style>

    <cti:url var="action" value="/tools/dataStreaming/summary" />
    <form:form commandName="searchFilters" action="${action}" method="POST">
        <cti:csrfToken />

        <tags:sectionContainer2 nameKey="filterSection">

            <div class="column-10-10 clearfix">
                <div class="column one">

                    <tags:nameValueContainer2>

                        <tags:nameValue2 nameKey=".filter.gateway">
                            <select multiple name="gatewaysSelect" path="selectedGatewayIds">
                                <option value="-1" selected>Any</option>
                                <c:forEach var="gateway" items="${gateways}">
                                    <option value="${gateway.id}">${gateway.name}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".filter.gatewayLoading">
                            <tags:input path="minLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />
                            <i:inline key="yukon.common.to" />
                            <tags:input path="maxLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".filter.configuration">
                            <tags:selectWithItems path="selectedConfiguration"
                                items="${existingConfigs}" itemValue="id" itemLabel="name"
                                defaultItemValue="0" defaultItemLabel="Any" />

                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".filter.attributes" valueClass="inline">
                            <select multiple name="attributesSelect" path="selectedAttributes">
                                <option value="-1" selected>Any</option>
                                <c:forEach var="attribute" items="${attributes}">
                                    <option value="${attribute.key}">${attribute.description}</option>
                                </c:forEach>
                            </select>
                            <span style="margin-left: 40px;"> <i:inline
                                    key=".filter.interval" />:&nbsp;&nbsp;&nbsp;&nbsp; <tags:selectWithItems
                                    path="selectedInterval" items="${intervals}"
                                    defaultItemValue="0" defaultItemLabel="Any" />
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>

            <div class="action-area">
                <cti:button nameKey="search" classes="primary action" type="submit" />
                <cti:button nameKey="showAll" />
            </div>

        </tags:sectionContainer2>

    </form:form>

</cti:standardPage>