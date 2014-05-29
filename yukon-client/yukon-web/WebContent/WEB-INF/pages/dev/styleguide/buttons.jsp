<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="buttons">
<tags:styleguide page="buttons">

<style>
.button-example .one { line-height: 26px; }
.description { line-height: 20px; }
.label-attr { 
    border: 1px solid #ccc;
    background-color: #efefef;
    color: inherit;
}
</style>

<p class="description">
    All buttons in Yukon need to be created using the <span class="label label-info">&lt;cti:button&gt;</span> tag. Never
    use any of the button or input html elements to create a button.  The 
    <span class="label label-info">&lt;cti:button&gt;</span> tag can handle any type of button needed.  
    <strong>Hint</strong>: This tag ALWAYS produces a button html element with a class name of 
    <span class="label label-info">.button</span>. Inside the button, the text will always be wrapped in span with a class
    name of <span class="label label-info">.b-label</span>.
</p>

<h2>Button Text</h2>

<p class="description">
    The <span class="label label-info">&lt;cti:button&gt;</span> tag's <span class="label label-attr">nameKey</span> 
    attribute is used to build the button text. The current scope plus the <span class="label label-attr">nameKey</span> 
    value will be added as a scope.  The <em>components.button</em> scope will also be added.  The key for the button's text 
    must always end in <em>.label</em>.  So if the current scope at the location of the button is 
    <em>yukon.web.modules.dev.buttons</em>, that means that in the example below, the scopes 
    <em>yukon.web.modules.dev.buttons.edit.label</em> and <em>yukon.web.components.buttons.edit.label</em> will both work to 
    get text on the button.  See <span class="label label-attr">components.xml</span> for a full list of button labels. 
    These are common button labels that should be reused as much as possible.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter"><cti:button nameKey="edit"/></div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot;/&gt;</pre>

<p class="description">
    You can also specify the text directly if need be using the <span class="label label-attr">label</span> attribute.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button label="Save"/>
        
        <cti:msg2 var="saveLabel" key="components.button.save.label"/>
        <cti:button label="${saveLabel}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Save&quot;/&gt;

&lt;cti:msg2 var=&quot;saveLabel&quot; key=&quot;components.button.save.label&quot;/&gt;
&lt;cti:button label=&quot;&#36;{saveLabel}&quot;/&gt;
</pre>

<h2>Disabling Buttons</h2>

<p class="description">You can disable a button by setting the <span class="label label-attr">disabled</span> attribute to <em>true</em>.</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter"><cti:button nameKey="edit" disabled="true"/></div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot; disabled=&quot;true&quot;/&gt;</pre>

<h2>Button Type</h2>

<p class="description">The <span class="label label-info">&lt;cti:button&gt;</span> tag has a 
    <span class="label label-attr">type</span> attribute that is equivalent to the <span class="label label-attr">type</span> 
    attribute on an html <span class="label label-info">button</span> element and supports the same values: <em>button</em> 
    (the default if not specified), <em>submit</em>, and <em>reset</em>.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form>
            <input type="text" name="email" value="bob@vila.com">
            <cti:button nameKey="save" type="submit"/>
            <cti:button nameKey="reset" type="reset"/>
            <cti:button nameKey="cancel"/>
        </form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;form&gt;
    &lt;input type=&quot;text&quot; name=&quot;email&quot; value=&quot;bob@vila.com&quot;&gt;
    &lt;cti:button nameKey=&quot;save&quot; type=&quot;submit&quot;/&gt;
    &lt;cti:button nameKey=&quot;reset&quot; type=&quot;reset&quot;/&gt;
    &lt;cti:button nameKey=&quot;cancel&quot;/&gt;
&lt;/form&gt;
</pre>
    
<h2>Using Icons with Buttons</h2>

<p class="description">
    The <span class="label label-info">&lt;cti:button&gt;</span> tag has built in support for icons. Simply set the 
    <span class="label label-attr">icon</span> attribute to the icon class name you need. See <a href="icons">icons</a> for
    a full list of available icons.  Setting the <span class="label label-attr">disabled</span> attribute to true will 
    automatically gray out the icon as well.  <strong>Note</strong>: Do not put icons on buttons unless instructed to do so.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button nameKey="edit"  icon="icon-pencil"/>
        <cti:button nameKey="delete" icon="icon-cross" disabled="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button nameKey=&quot;edit&quot; icon=&quot;icon-pencil&quot;/&gt;
&lt;cti:button nameKey=&quot;delete&quot; icon=&quot;icon-cross&quot; disabled=&quot;true&quot;/&gt;
</pre>

<h2>Render Mode</h2>

<p class="description">
    The <span class="label label-info">&lt;cti:button&gt;</span> tag has a <span class="label label-attr">renderMode</span>
    that will change the way the button looks.  Values are:
</p>
<ul>
    <li><em>button</em> - (the default when not specified) Just a normal looking button.</li>
    <li><em>image</em> - Looks just like an icon but functions as a button.</li>
    <li><em>buttonImage</em> - A button with an icon and no text.</li>
    <li><em>labeledImage</em> - A button that looks like a link and an icon.</li>
</ul>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button nameKey="add"/>
        <cti:button renderMode="image" icon="icon-add"/>
        <cti:button renderMode="buttonImage" icon="icon-add"/>
        <cti:button renderMode="labeledImage" icon="icon-add" nameKey="add"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button nameKey=&quot;add&quot;/&gt;
&lt;cti:button renderMode=&quot;image&quot; icon=&quot;icon-add&quot;/&gt;
&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-add&quot;/&gt;
&lt;cti:button renderMode=&quot;labeledImage&quot; icon=&quot;icon-add&quot; nameKey=&quot;add&quot;/&gt;
</pre>

<h2>Button Groups</h2>

<p class="description">
    Buttons can be grouped together by using a container class <span class="label label-info">.button-group</span> or 
    individual classes <span class="label label-info">.left</span>, <span class="label label-info">.middle</span> and
    <span class="label label-info">.right</span>.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="clearfix stacked button-group">
            <cti:button renderMode="buttonImage" icon="icon-text-align-left"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-center"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-justify"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-right"/>
        </div>
        <div class="clearfix">
            <cti:button icon="icon-transmit-blue" label="Active" classes="left"/>
            <cti:button icon="icon-transmit" label="Inactive" classes="middle"/>
            <cti:button icon="icon-transmit-delete" label="Disabled" classes="middle"/>
            <cti:button label="All" classes="right"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;clearfix stacked button-group&quot;&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-left&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-center&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-justify&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-right&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;clearfix&quot;&gt;
    &lt;cti:button icon=&quot;icon-transmit-blue&quot; label=&quot;Active&quot; classes=&quot;left&quot;/&gt;
    &lt;cti:button icon=&quot;icon-transmit&quot; label=&quot;Inactive&quot; classes=&quot;middle&quot;/&gt;
    &lt;cti:button icon=&quot;icon-transmit-delete&quot; label=&quot;Disabled&quot; classes=&quot;middle&quot;/&gt;
    &lt;cti:button label=&quot;All&quot; classes=&quot;right&quot;/&gt;
&lt;/div&gt;
</pre>

<h3 class="subtle">Toggle Buttons</h3>

<p class="description">
    Button groups can be made to act like toggle or radio buttons by add the <span class="label label-info">.js-toggle</span>
    class the container element.  Yukon's js library will automatically make one of the buttons look punched in when clicked
    by simply added the <span class="label label-info">.on</span> class to the right button.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="clearfix stacked button-group js-toggle">
            <cti:button renderMode="buttonImage" icon="icon-text-align-left"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-center" classes="on"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-justify"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-right"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;clearfix stacked button-group js-toggle&quot;&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-left&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-center&quot; classes=&quot;on&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-justify&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-right&quot;/&gt;
&lt;/div&gt;
</pre>
    
<h2>Primary Buttons</h2>

<p class="description">
    Primary buttons are buttons that usually are part of a group.  They represent the primary action a user is probably
    looking for.  To make one add two classes <span class="label label-info">.action</span> and 
    <span class="label label-info">.primary</span> to it your button.  
</p>
<p class="description">
    Dialogs should always have a primary button.  Traditional forms
    or pages that are in edit mode should have a primary button.  Delete buttons on these type of pages are also special,
    they should have the <span class="label label-info">.delete</span> class.  
</p>
<h3 class="subtle">Button Containers</h4>
<p class="description">
    Buttons on these type of pages should also be contained in a <span class="label label-info">.page-action-area</span> 
    element.  This will float the buttons left and add margin above the buttons.  When using 
    <span class="label label-info">.page-action-area</span> the most significant buttons should be first from left to right.  
    Use the <span class="label label-info">.action-area</span> class when containing buttons in a widget, a dialog or a 
    container that does not use the whole page width.  This class will float the buttons right and therefore the most 
    significant buttons should be from right to left.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".firstName"><input type="text" value="Bob"></tags:nameValue2>
                <tags:nameValue2 nameKey=".lastName"><input type="text" value="Vila"></tags:nameValue2>
                <tags:nameValue2 nameKey=".email"><input type="text" value="bob@vila.com"></tags:nameValue2>
            </tags:nameValueContainer2>
        </form>
        <div class="page-action-area">
            <cti:button nameKey="save" classes="action primary" type="submit"/>
            <cti:button nameKey="delete" classes="delete"/>
            <cti:button nameKey="cancel"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;form&gt;
    &lt;tags:nameValueContainer2&gt;
        &lt;tags:nameValue2 nameKey=&quot;.firstName&quot;&gt;&lt;input type=&quot;text&quot; value=&quot;Bob&quot;&gt;&lt;/tags:nameValue2&gt;
        &lt;tags:nameValue2 nameKey=&quot;.lastName&quot;&gt;&lt;input type=&quot;text&quot; value=&quot;Vila&quot;&gt;&lt;/tags:nameValue2&gt;
        &lt;tags:nameValue2 nameKey=&quot;.email&quot;&gt;&lt;input type=&quot;text&quot; value=&quot;bob@vila.com&quot;&gt;&lt;/tags:nameValue2&gt;
    &lt;/tags:nameValueContainer2&gt;
&lt;/form&gt;
&lt;div class=&quot;page-action-area&quot;&gt;
    &lt;cti:button nameKey=&quot;save&quot; classes=&quot;action primary&quot; type=&quot;submit&quot;/&gt;
    &lt;cti:button nameKey=&quot;delete&quot; classes=&quot;delete&quot;/&gt;
    &lt;cti:button nameKey=&quot;cancel&quot;/&gt;
&lt;/div&gt;
</pre>
    
<h2>Busy Buttons and Group Disabling</h2>

<p class="description">
    The <span class="label label-info">&lt;cti:button&gt;</span> tag has the ability disable itself and show an internal 
    spinner icon when clicked by setting the <span class="label label-attr">busy</span> attribute to <em>true</em>.  You can 
    change the button text when in the busy state by defining a <em>.labelBusy</em> i18n key. You can programatically busy 
    and un-busy a button using the <span class="label label-attr">yukon.ui.busy</span> and 
    <span class="label label-attr">yukon.ui.unbusy</span> javascript functions.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button id="busy-test-1" nameKey="readNow" icon="icon-read" busy="true"/>
        <cti:button id="busy-test-2" nameKey="readNow" busy="true" classes="action primary"/>
        <cti:button id="busy-test-3" busy="true" icon="icon-cross" renderMode="buttonImage"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button id=&quot;busy-test-1&quot; nameKey=&quot;readNow&quot; icon=&quot;icon-read&quot; busy=&quot;true&quot;/&gt;
&lt;cti:button id=&quot;busy-test-2&quot; nameKey=&quot;readNow&quot; busy=&quot;true&quot; classes=&quot;action primary&quot;/&gt;
&lt;cti:button id=&quot;busy-test-3&quot; busy=&quot;true&quot; icon=&quot;icon-cross&quot; renderMode=&quot;buttonImage&quot;/&gt;
...
yukon.ui.unbusy('#busy-test-1');
yukon.ui.unbusy('#busy-test-2');
yukon.ui.unbusy('#busy-test-3');
</pre>
    
<p class="description">You can make a group of busy buttons disable together by adding a the 
    <span class="label label-attr">data-disable-group</span> attribute with a common value.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button nameKey="update" busy="true" data-disable-group="them-buttons" classes="action primary" />
        <cti:button nameKey="delete" busy="true" data-disable-group="them-buttons" classes="delete" />
        <cti:button nameKey="cancel" busy="true" data-disable-group="them-buttons"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button nameKey=&quot;update&quot; busy=&quot;true&quot; data-disable-group=&quot;them-buttons&quot; classes=&quot;action primary&quot; /&gt;
&lt;cti:button nameKey=&quot;delete&quot; busy=&quot;true&quot; data-disable-group=&quot;them-buttons&quot; classes=&quot;delete&quot; /&gt;
&lt;cti:button nameKey=&quot;cancel&quot; busy=&quot;true&quot; data-disable-group=&quot;them-buttons&quot;/&gt;
</pre>
    
<h2 id="drop-down-example">Drop Downs</h2>

<p class="description">
    You can create drop down menu buttons using two tags: <span class="label label-info">&lt;tags:dropdown&gt;</span> and
    <span class="label label-info">&lt;tags:dropdownOption&gt;</span>.  
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cm:dropdown type="button" key="yukon.web.defaults.actions">
            <cm:dropdownOption icon="icon-pencil" href="buttons#drop-down-example">Edit</cm:dropdownOption>
            <cm:dropdownOption icon="icon-page-copy" disabled="true" key="yukon.web.components.button.copy.label"/>
            <cm:dropdownOption icon="icon-cross" label="Delete" classes="js-delete-all-the-things"/>
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-plus-green" key=".create" id="create-option"/>
        </cm:dropdown>
        &nbsp;&nbsp;
        <cm:dropdown triggerClasses="cog-class-1 cog-class-2" menuClasses="menu-class-1 menu-class-2">
            <cm:dropdownOption icon="icon-pencil" href="buttons#drop-down-example">Edit</cm:dropdownOption>
            <cm:dropdownOption icon="icon-page-copy" disabled="true" key="yukon.web.components.button.copy.label"/>
            <cm:dropdownOption icon="icon-cross" label="Delete" classes="js-delete-all-the-things"/>
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-plus-green" key=".create" id="create-option"/>
        </cm:dropdown>
        &nbsp;&nbsp;
        <cm:dropdown icon="icon-emoticon-smile">
            <cm:dropdownOption icon="icon-emoticon-smile">Smile</cm:dropdownOption>
            <cm:dropdownOption icon="icon-emoticon-tongue">Tongue</cm:dropdownOption>
            <cm:dropdownOption icon="icon-emoticon-happy">Happy</cm:dropdownOption>
            <cm:dropdownOption icon="icon-emoticon-surprised">Surprised</cm:dropdownOption>
        </cm:dropdown>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cm:dropdown type=&quot;button&quot; key=&quot;yukon.web.defaults.actions&quot; triggerClasses=&quot;fl&quot;&gt;
    &lt;cm:dropdownOption icon=&quot;icon-pencil&quot; href=&quot;buttons#drop-down-example&quot;&gt;Edit&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-page-copy&quot; disabled=&quot;true&quot; key=&quot;yukon.web.components.button.copy.label&quot;/&gt;
    &lt;cm:dropdownOption icon=&quot;icon-cross&quot; label=&quot;Delete&quot; classes=&quot;js-delete-all-the-things&quot;/&gt;
    &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;
    &lt;cm:dropdownOption icon=&quot;icon-plus-green&quot; key=&quot;.create&quot; id=&quot;create-option&quot;/&gt;
&lt;/cm:dropdown&gt;
...
&lt;cm:dropdown triggerClasses=&quot;cog-class-1 cog-class-2&quot; menuClasses=&quot;menu-class-1 menu-class-2&quot;&gt;
    &lt;cm:dropdownOption icon=&quot;icon-pencil&quot; href=&quot;buttons#drop-down-example&quot;&gt;Edit&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-page-copy&quot; disabled=&quot;true&quot; key=&quot;yukon.web.components.button.copy.label&quot;/&gt;
    &lt;cm:dropdownOption icon=&quot;icon-cross&quot; label=&quot;Delete&quot; classes=&quot;js-delete-all-the-things&quot;/&gt;
    &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;
    &lt;cm:dropdownOption icon=&quot;icon-plus-green&quot; key=&quot;.create&quot; id=&quot;create-option&quot;/&gt;
&lt;/cm:dropdown&gt;
...
&lt;cm:dropdown icon=&quot;icon-emoticon-smile&quot;&gt;
    &lt;cm:dropdownOption icon=&quot;icon-emoticon-smile&quot;&gt;Smile&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-emoticon-tongue&quot;&gt;Tongue&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-emoticon-happy&quot;&gt;Happy&lt;/cm:dropdownOption&gt;
    &lt;cm:dropdownOption icon=&quot;icon-emoticon-surprised&quot;&gt;Surprised&lt;/cm:dropdownOption&gt;
&lt;/cm:dropdown&gt;
</pre>
    
<h2 id="criteria-button-example">Criteria Buttons</h2>

<p class="description">
    Criteria buttons are a specialized type of drop down button that is meant for choosing multiple grouped options.  It has
    similar attributes to <span class="label label-info">dropdown</span> and 
    <span class="label label-info">dropdownOption</span> for button and option text.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cm:criteria label="Type">
            <cm:criteriaOption value="RFN420FX" checked="true">RFN-420fX</cm:criteriaOption>
            <cm:criteriaOption value="RFN420FD" checked="true">RFN-420fD</cm:criteriaOption>
            <cm:criteriaOption value="RFN420FL" checked="true">RFN-420fL</cm:criteriaOption>
            <cm:criteriaOption value="RFN420FRX">RFN-420fRX</cm:criteriaOption>
            <cm:criteriaOption value="RFN420FRD" label="RFN-420fRD"/>
            <li class="divider"></li>
            <cm:criteriaOption value="RFN410CL">RFN-410cL</cm:criteriaOption>
            <cm:criteriaOption value="RFN420CL">RFN-420cL</cm:criteriaOption>
        </cm:criteria>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cm:criteria label=&quot;Type&quot;&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FX&quot; checked=&quot;true&quot;&gt;RFN-420fX&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FD&quot; checked=&quot;true&quot;&gt;RFN-420fD&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FL&quot; checked=&quot;true&quot;&gt;RFN-420fL&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FRX&quot;&gt;RFN-420fRX&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420FRD&quot;&gt;RFN-420fRD&lt;/cm:criteriaOption&gt;
    &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;
    &lt;cm:criteriaOption value=&quot;RFN410CL&quot;&gt;RFN-410cL&lt;/cm:criteriaOption&gt;
    &lt;cm:criteriaOption value=&quot;RFN420CL&quot;&gt;RFN-420cL&lt;/cm:criteriaOption&gt;
&lt;/cm:criteria&gt;
</pre>
    
</tags:styleguide>
</cti:standardPage>