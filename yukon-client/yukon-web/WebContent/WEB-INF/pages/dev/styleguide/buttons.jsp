<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="buttons">
<tags:styleguide page="buttons">

<style type="text/css">
.description { 
    line-height: 26px;
    margin-left:20px; 
}
</style>

<tags:sectionContainer title="normal buttons" styleClass="separated-sections">
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="edit"/>
            <span class="description">normal button</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="edit" disabled="true"/>
            <span class="description">normal disabled button</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot; disabled=&quot;true&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="edit" icon="icon-pencil"/>
            <span class="description">button with icon</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot; icon=&quot;icon-pencil&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="edit" icon="icon-pencil" disabled="true"/>
            <span class="description">disabled button with icon</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot; icon=&quot;icon-pencil&quot; disabled=&quot;true&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cm:dropdown type="button" key="yukon.web.defaults.actions" triggerClasses="fl">
                <cm:dropdownOption icon="icon-pencil">Edit</cm:dropdownOption>
                <cm:dropdownOption icon="icon-page-copy">Copy</cm:dropdownOption>
                <cm:dropdownOption icon="icon-cross">Delete</cm:dropdownOption>
                <li class="divider"></li>
                <cm:dropdownOption icon="icon-plus-green">Create</cm:dropdownOption>
            </cm:dropdown>
            <span class="description">dropdown menu button</span>
        </div>
        <pre class="code prettyprint">&lt;cm:dropdown type=&quot;button&quot; key=&quot;yukon.web.defaults.actions&quot; triggerClasses=&quot;fl&quot;&gt;
    &lt;cm:dropdownOption icon=&quot;icon-pencil&quot;&gt;Edit&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-page-copy&quot;&gt;Copy&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-cross&quot;&gt;Delete&lt;/cm:dropdownOption&gt;
    &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;
    &lt;cm:dropdownOption icon=&quot;icon-plus-green&quot;&gt;Create&lt;/cm:dropdownOption&gt;
&lt;/cm:dropdown&gt;</pre>
    </div>
</tags:sectionContainer>

<tags:sectionContainer title="primary buttons" styleClass="separated-sections">
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="save" classes="action primary"/>
            <span class="description">primary action button</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;save&quot; classes=&quot;action primary&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="save" classes="action primary" disabled="true"/>
            <span class="description">disabled primary action button</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;save&quot; classes=&quot;action primary&quot; disabled=&quot;true&quot;/&gt;</pre>
    </div>
</tags:sectionContainer>

<tags:sectionContainer title="f-disable-after-click and busy buttons" styleClass="separated-sections">
    <h4>All of these buttons can be submit buttons and whatever form them maybe inside of will still be submitted on button click</h4>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="save" classes="action primary f-disable-after-click"/>
            <span class="description">primary action button that will disable after clicking</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;save&quot; classes=&quot;action primary f-disable-after-click&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="readNow" classes="action primary" busy="true"/>
            <span class="description">primary action button that will disable after clicking with busy text</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;readNow&quot; classes=&quot;action primary f-disable-after-click&quot; busy=&quot;true&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
	        <cti:button nameKey="readNow" busy="true" icon="icon-read"/>
            <span class="description">button that will disable after clicking with busy text and busy icon</span>
	    </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;readNow&quot; classes=&quot;f-disable-after-click&quot; busy=&quot;true&quot; icon=&quot;icon-read&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="delete" busy="true" icon="icon-cross" renderMode="buttonImage"/>
            <span class="description">button with no text that will disable after clicking with busy icon</span>
        </div>
        <pre class="code prettyprint">&lt;cti:button nameKey=&quot;delete&quot; classes=&quot;f-disable-after-click&quot; busy=&quot;true&quot; icon=&quot;icon-cross&quot; renderMode=&quot;buttonImage&quot;/&gt;</pre>
    </div>
    <div class="button-example section">
        <div class="clearfix">
            <cti:button nameKey="update" classes="f-disable-after-click action primary" data-disable-group="them-buttons"/>
            <cti:button nameKey="delete" classes="f-disable-after-click delete" data-disable-group="them-buttons"/>
            <cti:button nameKey="cancel" classes="f-disable-after-click" data-disable-group="them-buttons"/>
            <span class="description">a group of buttons that will all disable after clicking any one of them</span>
        </div>
        <div class="clearfix">
            <pre class="code prettyprint">&lt;cti:button nameKey=&quot;update&quot; classes=&quot;f-disable-after-click action primary&quot; data-disable-group=&quot;them-buttons&quot;/&gt;<br/>&lt;cti:button nameKey=&quot;delete&quot; classes=&quot;f-disable-after-click delete&quot; data-disable-group=&quot;them-buttons&quot;/&gt;<br/>&lt;cti:button nameKey=&quot;cancel&quot; classes=&quot;f-disable-after-click&quot; data-disable-group=&quot;them-buttons&quot;/&gt;</pre>
        </div>
    </div>
</tags:sectionContainer>

<tags:sectionContainer title="Criteria Buttons" styleClass="separated-sections">
    <div class="button-example section">
        <div class="clearfix">
 
            <cm:criteria label="Device Type:">
                <cm:criteriaOption value="RFN420FX" checked="true">RFN-420fX</cm:criteriaOption>
                <cm:criteriaOption value="RFN420FD" checked="true">RFN-420fD</cm:criteriaOption>
                <cm:criteriaOption value="RFN420FL" checked="true">RFN-420fL</cm:criteriaOption>
                <cm:criteriaOption value="RFN420FRX">RFN-420fRX</cm:criteriaOption>
                <cm:criteriaOption value="RFN420FRD">RFN-420fRD</cm:criteriaOption>
                <li class="divider"></li>
                <cm:criteriaOption value="RFN410CL">RFN-410cL</cm:criteriaOption>
                <cm:criteriaOption value="RFN420CL">RFN-420cL</cm:criteriaOption>
            </cm:criteria>
        
            <span class="description">a button that chooses criteria</span>
        </div>
        <div class="clearfix">
            <pre class="code prettyprint">&lt;cm:criteria label=&quot;Device Type:&quot;&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FX&quot; checked=&quot;true&quot;&gt;RFN-420fX&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FD&quot; checked=&quot;true&quot;&gt;RFN-420fD&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FL&quot; checked=&quot;true&quot;&gt;RFN-420fL&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FRX&quot;&gt;RFN-420fRX&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FRD&quot;&gt;RFN-420fRD&lt;/cm:criteriaOption&gt;
    &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;
    &lt;cm:criteriaOption value=&quot;RFN410CL&quot;&gt;RFN-410cL&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420CL&quot;&gt;RFN-420cL&lt;/cm:criteriaOption&gt;
&lt;/cm:criteria&gt;</pre>
        </div>
    </div>
</tags:sectionContainer>

</tags:styleguide>
</cti:standardPage>