<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:view>

<cti:standardPage module="commercialcurtailment" page="home">
<cti:standardMenu menuSelection=".ccurt_setup"/>
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">
    <div class="column-16-8">
        <div class="column one nogutter">
            <cti:tabs>
                <cti:msg2 var="programsName" key=".ccurtPrograms"/>
                <cti:tab title="${programsName}">
            <h:form>
                <h2><i:inline key=".ccurtAvailable"/></h2>

                <f:verbatim><cti:csrfToken/></f:verbatim>
                <t:dataTable id="programList" value="#{rProgramList.programList}" var="thisProgram" 
                   styleClass="horizBorders programSelectionTable" renderedIfEmpty="false">
                    <t:column styleClass="programName">
                        <h:outputText value="#{thisProgram.programType.name}"/>
                    </t:column>
                    <t:column>
                        <h:outputText value="#{thisProgram.name}"/>
                    </t:column>
                    <t:column>
                        <h:commandLink action="#{rEventInit.initEvent}">
                            <f:param name="programId" value="#{thisProgram.id}"/>
                            <h:outputText value="Start"/>
                        </h:commandLink>
                    </t:column>
                    <t:column>
                        <h:commandLink action="#{sEventList.showProgram}">
                            <t:updateActionListener property="#{sEventList.program}" value="#{thisProgram}"/>
                            <h:outputText value="History"/>
                        </h:commandLink>
                    </t:column>
                </t:dataTable>

                <h3><i:inline key=".ccurtEventType.CURRENT"/></h3>
                <t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.currentEventListModel}">
                    <jsp:include page="include/operatorEventList.jsp"/>
                </t:aliasBean>

                <h3><i:inline key=".ccurtEventType.PENDING"/></h3>
                <t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.pendingEventListModel}">
                    <jsp:include page="include/operatorEventList.jsp"/>
                </t:aliasBean>

                <h3><i:inline key=".ccurtEventType.RECENT"/></h3>
                <t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.recentEventListModel}">
                    <jsp:include page="include/operatorEventList.jsp"/>
                </t:aliasBean>
            </h:form>
                </cti:tab>

                <cti:msg2 var="setupName" key=".setup"/>
                <cti:tab title="${setupName}">
                    <table class="link-table">
                        <tr>
                            <td><a href="<cti:url value="/cc/programList.jsf"/>"><i:inline key=".programList"/></a></td>
                            <td><i:inline key=".program"/></td>
                        </tr>
                        <tr>
                            <td><a href="<cti:url value="/cc/groupList.jsf"/>"><i:inline key=".groupList"/></a></td>
                            <td><i:inline key=".group"/></td>
                        </tr>
                        <tr>
                            <td><a href="<cti:url value="/cc/customerList.jsf"/>"><i:inline key=".customerList"/></a></td>
                            <td><i:inline key=".customer"/></td>
                        </tr>
                    </table>
                </cti:tab>
                <cti:msg2 var="trendName" key=".ccurtTrends"/>
                <cti:tab title="${trendName}">
                </cti:tab>
            </cti:tabs>

        </div>
    </div>
</cti:msgScope>
</cti:standardPage>
</f:view>