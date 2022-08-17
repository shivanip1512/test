<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="buttons">
<tags:styleguide page="buttons">

<style>
.button-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<p class="description">
    All buttons in Yukon need to be created using the <span class="label label-attr">&lt;cti:button&gt;</span> tag. Never
    use any of the button or input html elements to create a button.  The 
    <span class="label label-attr">&lt;cti:button&gt;</span> tag can handle any type of button needed.  
    <strong>Hint</strong>: This tag ALWAYS produces a button html element with a class name of 
    <span class="label label-attr">.button</span>. Inside the button, the text will always be wrapped in span with a class
    name of <span class="label label-attr">.b-label</span>.
</p>

<h2 id="button-text-example">Button Text</h2>

<p class="description">
    The <span class="label label-attr">&lt;cti:button&gt;</span> tag's <span class="label label-attr">nameKey</span> 
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

<h2 id="disabled-buttons-example">Disabling Buttons</h2>

<p class="description">You can disable a button by setting the <span class="label label-attr">disabled</span> attribute to <em>true</em>.</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter"><cti:button nameKey="edit" disabled="true"/></div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">&lt;cti:button nameKey=&quot;edit&quot; disabled=&quot;true&quot;/&gt;</pre>

<h2 id="button-types-example">Button Type</h2>

<p class="description">The <span class="label label-attr">&lt;cti:button&gt;</span> tag has a 
    <span class="label label-attr">type</span> attribute that is equivalent to the <span class="label label-attr">type</span> 
    attribute on an html <span class="label label-attr">button</span> element and supports the same values: <em>button</em> 
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
    
<h2 id="icon-example">Using Icons with Buttons</h2>

<p class="description">
    The <span class="label label-attr">&lt;cti:button&gt;</span> tag has built in support for icons. Simply set the 
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

<h2 id="render-mode-example">Render Mode</h2>

<p class="description">
    The <span class="label label-attr">&lt;cti:button&gt;</span> tag has a <span class="label label-attr">renderMode</span>
    that will change the way the button looks.  Values are:
</p>
<ul>
    <li><em>button</em> - (the default when not specified) Just a normal looking button.</li>
    <li><em>image</em> - Looks just like an icon but functions as a button.</li>
    <li><em>buttonImage</em> - A button with an icon and no text.</li>
    <li><em>labeledImage</em> - A button that looks like a link and an icon.</li>
    <li><em>appButton</em> - A button that looks similar to an app button.  It is used with 32px icons and has rounded corners.</li>
</ul>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button nameKey="add"/>
        <cti:button renderMode="image" icon="icon-add"/>
        <cti:button renderMode="buttonImage" icon="icon-add"/>
        <cti:button renderMode="labeledImage" icon="icon-add" nameKey="add"/>
        <cti:button renderMode="appButton" icon="icon-app icon-app-32-lightbulb"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button nameKey=&quot;add&quot;/&gt;
&lt;cti:button renderMode=&quot;image&quot; icon=&quot;icon-add&quot;/&gt;
&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-add&quot;/&gt;
&lt;cti:button renderMode=&quot;labeledImage&quot; icon=&quot;icon-add&quot; nameKey=&quot;add&quot;/&gt;
&lt;cti:button renderMode=&quot;appButton&quot; icon=&quot;icon-app icon-app-32-lightbulb&quot;/&gt;
</pre>

<h2 id="button-groups-example">Button Groups</h2>

<p class="description">
    Buttons can be grouped together by using a container class <span class="label label-attr">.button-group</span> or 
    individual classes <span class="label label-attr">.left</span>, <span class="label label-attr">.middle</span> and
    <span class="label label-attr">.right</span>.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="button-group stacked">
            <cti:button renderMode="buttonImage" icon="icon-box"/>
            <cti:button renderMode="buttonImage" icon="icon-flag-red"/>
            <cti:button renderMode="buttonImage" icon="icon-bin"/>
        </div>
        <div class="button-group stacked">
            <cti:button renderMode="buttonImage" icon="icon-resultset-first-gray"/>
            <cti:button renderMode="buttonImage" icon="icon-resultset-previous-gray"/>
            <cti:button label="1"/>
            <cti:button label="2"/>
            <cti:button label="3"/>
            <cti:button label="4"/>
            <cti:button label="5"/>
            <cti:button renderMode="buttonImage" icon="icon-resultset-next-gray"/>
            <cti:button renderMode="buttonImage" icon="icon-resultset-last-gray"/>
        </div>
        <div class="clearfix">
            <cti:button icon="icon-transmit-blue" label="Activate" classes="left"/>
            <cti:button icon="icon-transmit" label="Deactivate" classes="middle"/>
            <cti:button icon="icon-transmit-delete" label="Disabled" classes="right"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;button-group&quot;&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-box&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-flag-red&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bin&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;button-group&quot;&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-resultset-first-gray&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-resultset-previous-gray&quot;/&gt;
    &lt;cti:button label=&quot;1&quot;/&gt;
    &lt;cti:button label=&quot;2&quot;/&gt;
    &lt;cti:button label=&quot;3&quot;/&gt;
    &lt;cti:button label=&quot;4&quot;/&gt;
    &lt;cti:button label=&quot;5&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-resultset-next-gray&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-resultset-last-gray&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;clearfix&quot;&gt;
    &lt;cti:button icon=&quot;icon-transmit-blue&quot; label=&quot;Activate&quot; classes=&quot;left&quot;/&gt;
    &lt;cti:button icon=&quot;icon-transmit&quot; label=&quot;Deactivate&quot; classes=&quot;middle&quot;/&gt;
    &lt;cti:button icon=&quot;icon-transmit-delete&quot; label=&quot;Disabled&quot; classes=&quot;right&quot;/&gt;
&lt;/div&gt;
</pre>

<h3 id="toggle-buttons-example" class="subtle">Toggle Buttons</h3>

<p class="description">
    Button groups can be made to act like toggle or radio buttons by adding the 
    <span class="label label-attr">.button-group-toggle</span> class. Add the <span class="label label-attr">.on</span> 
    class to the toggled button to make it look punched in. Yukon's js library will do this automatically when one of the 
    buttons is clicked.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="button-group button-group-toggle">
            <cti:button renderMode="buttonImage" icon="icon-text-align-left"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-center" classes="on"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-justify"/>
            <cti:button renderMode="buttonImage" icon="icon-text-align-right"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-left&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-center&quot; classes=&quot;on&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-justify&quot;/&gt;
    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-text-align-right&quot;/&gt;
&lt;/div&gt;
</pre>
<p class="description">
    Show a little more visual state by adding the <span class="label label-attr">.yes</span> or 
    <span class="label label-attr">.no</span> classes to the buttons.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="button-group button-group-toggle">
            <cti:button nameKey="on" classes="yes on"/>
            <cti:button nameKey="off" classes="no"/>
        </div>
        <div class="button-group button-group-toggle">
            <cti:button label="Activated" classes="yes"/>
            <cti:button label="Deactivated"/>
            <cti:button label="Disabled" classes="no on"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button nameKey=&quot;on&quot; classes=&quot;yes on&quot;/&gt;
    &lt;cti:button nameKey=&quot;off&quot; classes=&quot;no&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button label=&quot;Activate&quot; classes=&quot;yes on&quot;/&gt;
    &lt;cti:button label=&quot;Deactivate&quot;/&gt;
    &lt;cti:button label=&quot;Disabled&quot; classes=&quot;no&quot;/&gt;
&lt;/div&gt;
</pre>
<p class="description">
    Show or hide elements when toggling using the <span class="label label-attr">data-show</span> attribute on the buttons.
    The attribute values should be css selectors to the desired element(s).
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="button-group button-group-toggle">
            <cti:button label="Show Red and Green" data-show="#row-red, #row-green" classes="on"/>
            <cti:button label="Show Blue" data-show="#row-blue"/>
        </div>
        <div class="separated-sections buffered">
            <div id="row-red" class="section red">This is the red row.</div>
            <div id="row-green" class="section green">This is the green row.</div>
            <div id="row-blue" class="section blue dn">This is the blue row.</div>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button label=&quot;Show Red and Green&quot; data-show=&quot;#row-red, #row-green&quot; classes=&quot;on&quot;/&gt;
    &lt;cti:button label=&quot;Show Blue&quot; data-show=&quot;#row-blue&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;separated-sections buffered&quot;&gt;
    &lt;div id=&quot;row-red&quot; class=&quot;section red&quot;&gt;This is the red row.&lt;/div&gt;
    &lt;div id=&quot;row-green&quot; class=&quot;section green&quot;&gt;This is the green row.&lt;/div&gt;
    &lt;div id=&quot;row-blue&quot; class=&quot;section blue dn&quot;&gt;This is the blue row.&lt;/div&gt;
&lt;/div&gt;
</pre>
<p class="description">
    Set an input value using the <span class="label label-attr">data-value</span> attribute on the buttons.  The input can be
    be a sibling with the <span class="label label-attr">data-input</span> attribute or it can be specified as the value of 
    the <span class="label label-attr">data-input</span> on the button.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="stacked">
            <div class="button-group button-group-toggle">
                <cti:button label="Set Input to 'Bob'" data-input="#first-name" data-value="Bob" classes="on"/>
                <cti:button label="Set Input to 'Jim'" data-input="#first-name" data-value="Jim"/>
            </div>
            <input id="first-name" type="text" name="firstName" value="Bob">
        </div>
        <div class="button-group button-group-toggle full-width">
            <cti:button label="Set Input to 'Vila'" data-value="Vila" classes="on"/>
            <cti:button label="Set Input to 'Belushi'" data-value="Belushi"/>
            <input data-input type="text" name="lastName" value="Vila">
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button label=&quot;Set Input to 'Bob'&quot; data-input=&quot;#first-name&quot; data-value=&quot;Bob&quot; classes=&quot;on&quot;/&gt;
    &lt;cti:button label=&quot;Set Input to 'Jim'&quot; data-input=&quot;#first-name&quot; data-value=&quot;Jim&quot;/&gt;
&lt;/div&gt;
&lt;input id=&quot;first-name&quot; type=&quot;text&quot; name=&quot;firstName&quot; value=&quot;Bob&quot;&gt;
...
&lt;div class=&quot;button-group button-group-toggle&quot;&gt;
    &lt;cti:button label=&quot;Set Input to 'Vila'&quot; data-value=&quot;Vila&quot; classes=&quot;on&quot;/&gt;
    &lt;cti:button label=&quot;Set Input to 'Belushi'&quot; data-value=&quot;Belushi&quot;/&gt;
    &lt;input data-input type=&quot;text&quot; name=&quot;lastName&quot; value=&quot;Vila&quot;/&gt;
&lt;/div&gt;
</pre>

<h2 id="radio-buttons-example">Radio Buttons</h2>

<p class="description">
    A set of radio buttons built with <span class="label label-attr">&lt;tags:radio&gt;</span>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:radio name="pet" value="cats" classes="left" checked="true">Cats</tags:radio>
        <tags:radio name="pet" value="dogs" classes="right">Dogs</tags:radio>
        
        <div class="button-group">
            <tags:radio name="sex" value="M" checked="true">Male</tags:radio>
            <tags:radio name="sex" value="F">Female</tags:radio>
        </div>
        
        <tags:radio name="vehicle" value="red" classes="left">Train</tags:radio>
        <tags:radio name="vehicle" value="red" classes="middle" checked="true">Car</tags:radio>
        <tags:radio name="vehicle" value="red" classes="middle" disabled="true">JetSki</tags:radio>
        <tags:radio name="vehicle" value="red" classes="right">Airplane</tags:radio>
        
        <form:form modelAttribute="bean" cssClass="buffered">
            <tags:radio path="phase" key=".phase.A" classes="left" checked="true" value="A"/>
            <tags:radio path="phase" key=".phase.B" classes="middle" value="B"/>
            <tags:radio path="phase" key=".phase.C" classes="right" value="C"/>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:radio name=&quot;pet&quot; value=&quot;cats&quot; classes=&quot;left&quot; checked=&quot;true&quot;&gt;Cats&lt;/tags:radio&gt;
&lt;tags:radio name=&quot;pet&quot; value=&quot;dogs&quot; classes=&quot;right&quot;&gt;Dogs&lt;/tags:radio&gt;

&lt;div class=&quot;button-group&quot;&gt;
    &lt;tags:radio name=&quot;sex&quot; value=&quot;M&quot; checked=&quot;true&quot;&gt;Male&lt;/tags:radio&gt;
    &lt;tags:radio name=&quot;sex&quot; value=&quot;F&quot;&gt;Female&lt;/tags:radio&gt;
&lt;/div&gt;

&lt;tags:radio name=&quot;vehicle&quot; value=&quot;red&quot; classes=&quot;left&quot;&gt;Train&lt;/tags:radio&gt;
&lt;tags:radio name=&quot;vehicle&quot; value=&quot;red&quot; classes=&quot;middle&quot; checked=&quot;true&quot;&gt;Car&lt;/tags:radio&gt;
&lt;tags:radio name=&quot;vehicle&quot; value=&quot;red&quot; classes=&quot;middle&quot; disabled=&quot;true&quot;&gt;JetSki&lt;/tags:radio&gt;
&lt;tags:radio name=&quot;vehicle&quot; value=&quot;red&quot; classes=&quot;right&quot;&gt;Airplane&lt;/tags:radio&gt;

&lt;form:form modelAttribute=&quot;bean&quot; cssClass=&quot;buffered&quot;&gt;
    &lt;tags:radio path=&quot;phase&quot; key=&quot;.phase.A&quot; classes=&quot;left&quot; checked=&quot;true&quot; value=&quot;A&quot;/&gt;
    &lt;tags:radio path=&quot;phase&quot; key=&quot;.phase.B&quot; classes=&quot;middle&quot; value=&quot;B&quot;/&gt;
    &lt;tags:radio path=&quot;phase&quot; key=&quot;.phase.C&quot; classes=&quot;right&quot; value=&quot;C&quot;/&gt;
&lt;/form:form&gt;
</pre>

<h2 id="check-buttons-example">Check Buttons</h2>

<p class="description">
    A set of checkboxes built with <span class="label label-attr">&lt;tags:check&gt;</span>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:check name="bacon" label="Bacon"/>

        <tags:check name="lettuce" label="Lettuce" classes="left"/>
        <tags:check name="tomato" label="Tomato" classes="right" checked="true"/>
        
        <tags:check name="taco" label="Taco" classes="left"/>
        <tags:check name="burrito" label="Burrito" classes="middle" checked="true"/>
        <tags:check name="enchilada" label="Enchilada" classes="middle" disabled="true"/>
        <tags:check name="churro" label="Churro" classes="right"/>
        
        <div class="button-group">
            <tags:check name="fries" label="Fries" checked="true"/>
            <tags:check name="soda" label="Soda"/>
        </div>
        
        <form:form modelAttribute="jobfilter" cssClass="buffered">
            <div class="button-group">
                <tags:check path="status" value="CANCELLED" key=".jobRunStatus.CANCELLED"/>
                <tags:check path="status" value="COMPLETED" key=".jobRunStatus.COMPLETED"/>
                <tags:check path="status" value="FAILED" key=".jobRunStatus.FAILED" disabled="true"/>
                <tags:check path="status" value="RESTARTED" key=".jobRunStatus.RESTARTED"/>
                <tags:check path="status" value="STARTED" key=".jobRunStatus.STARTED"/>
            </div>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:check name="bacon" label="Bacon"/&gt;

&lt;tags:check name="lettuce" label="Lettuce" classes="left"/&gt;
&lt;tags:check name="tomato" label="Tomato" classes="right" checked="true"/&gt;

&lt;tags:check name="taco" label="Taco" classes="left"/&gt;
&lt;tags:check name="burrito" label="Burrito" classes="middle" checked="true"/&gt;
&lt;tags:check name="enchilada" label="Enchilada" classes="middle" disabled="true"/&gt;
&lt;tags:check name="churro" label="Churro" classes="right"/&gt;

&lt;div class="button-group"&gt;
  &lt;tags:check name="fries" label="Fries" checked="true"/&gt;
  &lt;tags:check name="soda" label="Soda"/&gt;
&lt;/div&gt;

&lt;form:form modelAttribute="jobfilter" cssClass="buffered"&gt;
  &lt;div class="button-group"&gt;
      &lt;tags:check path="jobRunStatus" value="CANCELLED" key=".jobRunStatus.CANCELLED"/&gt;
      &lt;tags:check path="jobRunStatus" value="COMPLETED" key=".jobRunStatus.COMPLETED"/&gt;
      &lt;tags:check path="jobRunStatus" value="FAILED" key=".jobRunStatus.FAILED" disabled="true"/&gt;
      &lt;tags:check path="jobRunStatus" value="RESTARTED" key=".jobRunStatus.RESTARTED"/&gt;
      &lt;tags:check path="jobRunStatus" value="STARTED" key=".jobRunStatus.STARTED"/&gt;
  &lt;/div&gt;
&lt;/form:form&gt;
</pre>

<h2 id="primary-buttons-example">Primary Buttons</h2>

<p class="description">
    Primary buttons are buttons that usually are part of a group.  They represent the primary action a user is probably
    looking for.  To make one, add two classes <span class="label label-attr">.action</span> and 
    <span class="label label-attr">.primary</span> to your button.  
</p>
<p class="description">
    Dialogs should always have a primary button.  Traditional forms
    or pages that are in edit mode should have a primary button.  Delete buttons on these type of pages are also special,
    they should have the <span class="label label-attr">.delete</span> class.  
</p>
<h3 class="subtle">Button Containers</h4>
<p class="description">
    Buttons on these type of pages should also be contained in a <span class="label label-attr">.page-action-area</span> 
    element.  This will float the buttons left and add margin above the buttons.  When using 
    <span class="label label-attr">.page-action-area</span> the most significant buttons should be first from left to right.  
    Use the <span class="label label-attr">.action-area</span> class when containing buttons in a widget, a dialog or a 
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
    
<h2 id="busy-buttons-example">Busy Buttons and Group Disabling</h2>

<p class="description">
    The <span class="label label-attr">&lt;cti:button&gt;</span> tag has the ability disable itself and show an internal 
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

<h2 id="block-page-example">Blocking the Page</h2>

<p class="description">
    The <span class="label label-attr">&lt;cti:button&gt;</span> tag has the ability to block/disable the page when clicked by setting the 
    <span class="label label-attr">blockPage</span> attribute to <em>true</em>.  This can be used in conjunction with the busy attribute.
    <span class="warning">Blocking should only be used as a last resort since it is visually jarring to users.</span>
    You can programatically block and un-block the page using the <span class="label label-attr">yukon.ui.blockPage</span> and 
    <span class="label label-attr">yukon.ui.unblockPage</span> javascript functions.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button id="block-test-1" nameKey="readNow" icon="icon-read" busy="true" blockPage="true"/>
        <cti:button id="block-test-2" nameKey="readNow" busy="true" classes="action primary" blockPage="true"/>
        <cti:button id="block-test-3" busy="true" icon="icon-cross" renderMode="buttonImage" blockPage="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button id=&quot;block-test-1&quot; nameKey=&quot;readNow&quot; icon=&quot;icon-read&quot; busy=&quot;true&quot; blockPage=&quot;true&quot;/&gt;
&lt;cti:button id=&quot;busy-test-2&quot; nameKey=&quot;readNow&quot; busy=&quot;true&quot; blockPage=&quot;true&quot; classes=&quot;action primary&quot;/&gt;
&lt;cti:button id=&quot;busy-test-3&quot; busy=&quot;true&quot; blockPage=&quot;true&quot; icon=&quot;icon-cross&quot; renderMode=&quot;buttonImage&quot;/&gt;
</pre>
    
<h2 id="drop-down-example">Drop Downs</h2>

<p class="description">
    You can create drop down menu buttons using two tags: <span class="label label-attr">&lt;tags:dropdown&gt;</span> and
    <span class="label label-attr">&lt;tags:dropdownOption&gt;</span>.  See <a href="tables#has-actions-example">tables</a>
    to learn about using drop down menus in tables.
</p>
<div class="column-4-20 clearfix button-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cm:dropdown type="button" key="yukon.common.actions">
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
&lt;cm:dropdown type=&quot;button&quot; key=&quot;yukon.common.actions&quot; triggerClasses=&quot;fl&quot;&gt;
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
    similar attributes to <span class="label label-attr">dropdown</span> and 
    <span class="label label-attr">dropdownOption</span> for button and option text.
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