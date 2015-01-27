<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="groupPickers">
<tags:styleguide page="group-pickers">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Device group selection is handled with special component built by the  
    <span class="label label-attr">&lt;tags:deviceGroupPicker&gt;</span> tag. Note: When a selection is made the
    picker component fires the <em>yukon:device:group:picker:selection</em> event that also passes an array of 
    groups names selected as data <a href="#selection-listener-example">See Example</a>.
</p>

<h2>Tag Attributes</h2>
<h4>Required</h4>
<tags:nameValueContainer tableClass="description">
    <tags:nameValue name="inputName">
        The name of the input(s). If <span class="label label-attr">multi</span> is true, multiple hidden inputs with 
        the same name are created and the controller should expect a <span class="label label-attr">String[]</span> 
        instead of <span class="label label-attr">String</span>.
    </tags:nameValue>
</tags:nameValueContainer>

<h4>Optional</h4>
<tags:nameValueContainer tableClass="description">
    <tags:nameValue name="inputValue">
        <strong>List</strong> of initial group names selected.
    </tags:nameValue>
    <tags:nameValue name="predicates">
        Comma separated strings of <span class="label label-attr">DeviceGroupPredicateEnum</span> entry names to filter 
        the tree data by. Defaults to <strong>NON_HIDDEN</strong>.
    </tags:nameValue>
    <tags:nameValue name="callbacks">
        A <span class="label label-attr">Set</span> of Java callbacks to be preformed on each node before rendering. 
        Can be used to disable selecting certain nodes.
    </tags:nameValue>
    <tags:nameValue name="multi">
        If <strong>true</strong>, selection mode will be multi-selection. Default: <strong>false</strong>.
    </tags:nameValue>
    <tags:nameValue name="classes">
        CSS class names applied to the picker component element.
    </tags:nameValue>
    <tags:nameValue name="id">
        The html <span class="label label-attr">id</span> attribute of the picker component element.
    </tags:nameValue>
    <tags:nameValue name="dialogId">
        The html <span class="label label-attr">id</span> attribute of the picker dialog element.
    </tags:nameValue>
</tags:nameValueContainer>

<br><h1 class="dib buffered">Examples</h1><br>
<h2>Basic Group Picker</h2>

<p class="description">
    A simple device group picker, single selection.
</p>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceGroupPicker inputName="group"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceGroupPicker inputName=&quot;group&quot;/&gt;
</pre>

<h2>Multi Group Picker with Preselected Groups</h2>

<p class="description">
    A device group picker using multi selection with preselected groups.
</p>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceGroupPicker inputName="groups" inputValue="${groups}" multi="true"/>
    </div>
</div>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(&quot;/styleguide/group-pickers&quot;)
public String groupPickers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
    
    List&lt;String&gt; groups = Lists.newArrayList(&quot;/Meters/Billing&quot;, &quot;/Meters/Alternate&quot;, &quot;/Meters/Collection&quot;);
    model.addAttribute(&quot;groups&quot;, groups);
    
    return &quot;styleguide/group.pickers.jsp&quot;;
}
</pre>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceGroupPicker inputName=&quot;groups&quot; inputValue=&quot;&#36;{groups}&quot; multi=&quot;true&quot;/&gt;
</pre>

<h2>Callbacks</h2>

<p class="description">
    A group picker with the <em>/Meters/Billing</em> preselected and using a callback that disables the 
    <em>/Meters/Monitors</em> group from selection.
</p>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceGroupPicker inputName="group" inputValue="${group}" callbacks="${noMonitors}"/>
    </div>
</div>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(&quot;/styleguide/group-pickers&quot;)
public String groupPickers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
    
    DeviceGroup monitors = deviceGroupService.findGroupName(&quot;/Meters/Monitors&quot;);
    DisableCurrentCallback noMonitors = DisableCurrentCallback.of(monitors);
    model.addAttribute(&quot;group&quot;, Lists.newArrayList(&quot;/Meters/Billing&quot;));
    model.addAttribute(&quot;noMonitors&quot;, ImmutableSet.of(noMonitors));
    
    return &quot;styleguide/group.pickers.jsp&quot;;
}
</pre>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceGroupPicker inputName=&quot;group&quot; inputValue=&quot;&#36;{group}&quot; callbacks=&quot;&#36;{noMonitors}&quot;/&gt;
</pre>

<h2>Predicates</h2>

<p class="description">
    A group picker with the <em>MODIFIABLE</em> and <em>NON_HIDDEN</em> predicates meaning it only shows modifiable and 
    non-hidden device groups, i.e. no hidden, composed, or system groups.
</p>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceGroupPicker inputName="group" predicates="MODIFIABLE"/>
    </div>
</div>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceGroupPicker inputName=&quot;group&quot; predicates=&quot;MODIFIABLE, NON_HIDDEN&quot;/&gt;
</pre>

<h2 id="selection-listener-example">Listening For Selections</h2>

<p class="description">
    A group picker and an event handler that listens for group selections. After a selection, the group picker component
    fires the <em>yukon:device:group:picker:selection</em> event that also passes an array of groups names selected as data.
</p>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceGroupPicker multi="true" inputName="group" id="listener-example-picker"/>
        <script>
        $('#listener-example-picker').on('yukon:device:group:picker:selection', function (ev, groups) {
            if (groups.length) {
                alert('You selected: ' + groups);
            } else {
                alert('You didn\'t select anything');
            }
        });
        </script>
    </div>
</div>
<h4 class="subtle">Javascript Code:</h4>
<pre class="code prettyprint">
$('#listener-example-picker').on('yukon:device:group:picker:selection', function (ev, groups) {
    if (groups.length) {
        alert('You selected: ' + groups);
    } else {
        alert('You didn\'t select anything');
    }
});
</pre>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceGroupPicker multi=&quot;true&quot; inputName=&quot;group&quot; id=&quot;listener-example-picker&quot;/&gt;
</pre>

</tags:styleguide>
</cti:standardPage>