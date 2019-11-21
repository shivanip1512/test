<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="dialogs">
<tags:styleguide page="dialogs">

<style>
.style-guide-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<p class="description">
    Popups and dialogs are made with html elements and data attributes.  (Dialogs are just popups with buttons.)  Use the 
    methods described here for popups and dialogs instead of the legacy popup/dialog tags which are being phased out. All 
    popups in Yukon utilize the JQuery dialog widget.
    <br><br>
    All data about a popup/dialog is stored in data attributes on the popup/dialog html element.<br>
    <h2>Popup/Dialog Options</h2>
    <tags:nameValueContainer>
        <tags:nameValue name="data-title">The text used for the title of the popup.</tags:nameValue>
        <tags:nameValue name="data-height">The initial height of the popup.</tags:nameValue>
        <tags:nameValue name="data-width">The initial width of the popup.</tags:nameValue>
        <tags:nameValue name="data-url">
            If present, the contents of the popup element will be replaced with the response of an ajax request to the url 
            before the popup is shown.
        </tags:nameValue>
        <tags:nameValue name="data-load-event">
            If present, this event will be fired right before the popup is shown. If 
            <span class="label label-attr">data-url</span> is used, the event will be fired after the dialog is loaded with 
            the response body.
        </tags:nameValue>
        <tags:nameValue name="data-dialog">
            If present the popup will have 'ok' and 'cancel' buttons.
        </tags:nameValue>
        <tags:nameValue name="data-event">
            If present and <span class="label label-attr">data-dialog</span> is present, the value of 
            <span class="label label-attr">data-event</span> will be the name of the event to fire when clicking the 'ok' 
            button.
        </tags:nameValue>
        <tags:nameValue name="data-target">
            If present and <span class="label label-attr">data-event</span> is present, the value of 
            <span class="label label-attr">data-target</span> is the css selector to the element that will be the target of 
            the event specified in <span class="label label-attr">data-event</span>.
        </tags:nameValue>
        <tags:nameValue name="data-form">
            If present, a form will be submitted when the 'ok' button is clicked.  If the value is not present, the first 
            form found inside the popup will be submitted.  Otherwise the value is a css selector to the form to submit.
        </tags:nameValue>
        <tags:nameValue name="data-position-my">
            When positioning a dialog, the part of the dialog to be used.<br>
            Values: 'left|center|right', followed by a space, followed by  'top|center|bottom'. Order matters.<br>
            Each of these can have +X or -X attached (X is an integer) to move them X pixels away.<br>
            Sample Value: left+2 bottom-2<br>
            Default Value: center center<br>
            See <a href="#positioning-example">Example below</a> for usage
        </tags:nameValue>
        <tags:nameValue name="data-position-at">
            When positioning a dialog, the part of the reference object to be used. 
            If <span class="label label-attr">data-position-of</span> is present, then that element. Otherwise, the window is used.<br>
            Values: 'left|center|right', followed by a space, followed by  'top|center|bottom'. Order matters.<br>
            Each of these can have +X or -X attached (X is an integer) to move them X pixels away.<br>
            Sample Value: left+2 bottom-2<br>
            Default Value: center center<br>
            See <a href="#positioning-example">Example below</a> for usage
        </tags:nameValue>
        <tags:nameValue name="data-position-of">
            When positioning a dialog, the reference object for position.<br>
            Values: css selector<br>
            Sample Value: '#button1'<br>
            Default Value: window object when not present<br>
            See <a href="#positioning-example">Example below</a> for usage
        </tags:nameValue>
        <tags:nameValue name="data-destroy-dialog-on-close">
            If present the dialog will be destroyed after the dialog is closed. <br>
            See jQuery UI dialog <a href="https://api.jqueryui.com/dialog/#method-destroy">destroy method</a>
            documentation.
        </tags:nameValue>
    </tags:nameValueContainer>
    <br><br>
    <h2>Popup/Dialog Trigger Options</h2>
    Popups/Dialogs can be opened by clicking elements with the <span class="label label-attr">data-popup</span> attribute. 
    Popup/Dialog trigger element options are as follows:
    <tags:nameValueContainer>
        <tags:nameValue name="data-popup">A css selector to the popup element to open.</tags:nameValue>
        <tags:nameValue name="data-popup-toggle">
            If present, the trigger element will also close the popup when clicked.
        </tags:nameValue>
    </tags:nameValueContainer>
</p>

<h2 id="simple-popup-example">Simple Popup</h2>

<p class="description">
    To make a simple popup just start with a hidden <span class="label label-attr">&lt;div&gt;</span> and a trigger.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="popup-1" data-title="Popup Example">This is a simple popup.</div>
        <cti:button label="Open Popup" data-popup="#popup-1"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;popup-1&quot; data-title=&quot;My First Popup&quot;&gt;This is a simple popup.&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#popup-1&quot;/&gt;
</pre>

<h2 id="button-text-example">Width and Height</h2>

<p class="description">
    A popup with initial width of 500 pixels and height of 300 pixels.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="popup-2" 
                data-title="Popup Example"
                data-width="500"
                data-height="300">This is a popup with initial width of 500 pixels and height of 300 pixels.</div>
        <cti:button label="Open Popup" data-popup="#popup-2"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;popup-2&quot; 
        data-title=&quot;Popup Example&quot;
        data-width=&quot;500&quot;
        data-height=&quot;300&quot;&gt;This is a popup with initial width of 500 pixels and height of 300 pixels.&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#popup-2&quot;/&gt;
</pre>

<h2 id="positioning-example">Positioning</h2>

<p class="description">
    A popup that will be immediately above the button.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="positioned-popup-1" 
            data-title="Popup Example"
            data-position-my="right bottom"
            data-position-at="right top-4"
            data-position-of="#positioned-link-1"
            data-width="300">
                This popup is 4 pixels above the button, with their right edges aligned.
        </div>
        <cti:button id="positioned-link-1" label="Positioned Popup" data-popup="#positioned-popup-1" data-popup-toggle=""/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;positioned-popup-1&quot;
    data-title=&quot;Popup Example&quot;
    data-position-my=&quot;right bottom&quot;
    data-position-at=&quot;right top-4&quot;
    data-position-of=&quot;#positioned-link-1&quot;&gt;
        This popup is 4 pixels above the button, with their right edges aligned.
&lt;/div&gt;
&lt;cti:button id=&quot;positioned-link-1&quot; label=&quot;Positioned Popup&quot; data-popup=&quot;#positioned-popup-1&quot; data-popup-toggle=""/&gt;
</pre>

<h2 id="positioning-example">Dynamic Content</h2>

<p class="description">
    A popup whose content is automactially loaded via ajax from the <em>/yukon/dev/styleguide/new-person</em> url before 
    showing using the <span class="label label-attr">[data-url]</span> attribute.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="dynamic-content-popup" data-title="Dynamic Content Example" data-width="400"
                data-url="new-person"></div>
        <cti:button label="Open Popup" data-popup="#dynamic-content-popup"/>
    </div>
</div>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;dynamic-content-popup&quot; data-title=&quot;Dynamic Content Example&quot; data-width=&quot;400&quot;
        data-url=&quot;new-person&quot;&gt;&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#dynamic-content-popup&quot;/&gt;
</pre>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(value=&quot;/styleguide/new-person&quot;, method=RequestMethod.GET)
public String newPersonPopup(ModelMap model) {
    
    Person person = new Person();
    model.addAttribute(&quot;person&quot;, person);
    
    return &quot;styleguide/person.jsp&quot;;
}
</pre>

<h2 id="positioning-example">Dialog Buttons and Events</h2>

<p class="description">
    This popup has 'ok' and 'cancel' buttons by using <span class="label label-attr">[data-dialog]</span>.  
    The content is automactially loaded via ajax by using <span class="label label-attr">[data-url]</span>.
    The <em>yukon:dev:styleguide:dialogs:foo</em> event is fired when the 'ok' button is clicked by using
    <span class="label label-attr">[data-event]</span>.  The event's target by default will be the dialog div but it can be
    overridden using <span class="label label-attr">[data-target]</span>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="button-event-popup" data-dialog data-title="Buttons and Event Example" data-width="400"
                data-url="new-person" data-event="yukon:dev:styleguide:dialogs:foo"></div>
        <cti:button label="Open Popup" data-popup="#button-event-popup"/>
        <script>
        $('#button-event-popup').on('yukon:dev:styleguide:dialogs:foo', function (e) {
            $('#button-event-popup').dialog('close');
            alert('A winner is you!!');
        });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;button-event-popup&quot; data-dialog data-title=&quot;Buttons and Event Example&quot; data-width=&quot;400&quot;
        data-url=&quot;new-person&quot; data-event=&quot;yukon:dev:styleguide:dialogs:foo&quot;&gt;&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#button-event-popup&quot;/&gt;
&lt;script&gt;
$('#button-event-popup').on('yukon:dev:styleguide:dialogs:foo', function (e) {
    $('#button-event-popup').dialog('close');
    alert('A winner is you!!');
});
&lt;/script&gt;
</pre>

<h2 id="confirm-example">Confirm</h2>

<p class="description">
    Dialogs with <span class="label label-attr">[data-confirm]</span> will first ask for confirmation before performing
    the 'OK' action.  If <span class="label label-attr">[data-event]</span> and 
    <span class="label label-attr">[data-target]</span> are present they will still be used but only after the confirmation
    'yes' button is clicked.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="confirm-popup" data-dialog data-confirm data-target="#confirm-btn" 
        data-title="Confirmation Example" data-width="400" data-event="yukon:dev:styleguide:dialogs:bar">
            We will get an alert that tells us who fired the event.
        </div>
        <cti:button id="confirm-btn" label="Open Confirm Popup" data-popup="#confirm-popup"/>
        <script>
        $('#confirm-btn').on('yukon:dev:styleguide:dialogs:bar', function (ev) {
            $('#confirm-popup').dialog('close');
            alert('Target is: ' + ev.target.tagName + ' with text "' + ev.target.textContent + '"');
        });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;confirm-popup&quot; data-dialog data-confirm data-target=&quot;#confirm-btn&quot; 
data-title=&quot;Confirmation Example&quot; data-width=&quot;400&quot; data-event=&quot;yukon:dev:styleguide:dialogs:bar&quot;&gt;
    We will get an alert that tells us who fired the event.
&lt;/div&gt;
&lt;cti:button id=&quot;confirm-btn&quot; label=&quot;Open Confirm Popup&quot; data-popup=&quot;#confirm-popup&quot;/&gt;
&lt;script&gt;
$('#confirm-btn').on('yukon:dev:styleguide:dialogs:bar', function (ev) {
    $('#confirm-popup').dialog('close');
    alert('Target is: ' + ev.target.tagName + ' with text &quot;' + ev.target.textContent + '&quot;');
});
&lt;/script&gt;
</pre>

<h2 id="form-ajax-validation-example">Dialogs and Forms</h2>

<p class="description">
    A popup with a form using 
    <a target="_blank" href="http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/view.html">spring binding</a> 
    that is submitted via ajax. The popup contents are from a separate jsp that uses our special tags which provide
    nice error rendering when validation fails. The controller returns the popup jsp as 400 when validation fails or 
    simply returns a no-content success when validation passes.  This is a very simple example that doesn't actually save
    anything but you get the point.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="form-example" data-title="Ajax Form Submission Example" data-width="400" data-dialog 
                data-url="new-person"
                data-event="yukon:dev:styleguid:dialogs:create:person">
        </div>
        <cti:button label="Add Person" data-popup="#form-example" icon="icon-plus-green"/>
        <script>
        $('#form-example').on('yukon:dev:styleguid:dialogs:create:person', function (ev) {
            $('#form-example form').ajaxSubmit({
                success: function (data, status, xhr, $form) {
                    $('#form-example').dialog('close');
                    debug.log('A winner is you!!');
                },
                error: function (xhr, status, error, $form) {
                    $('#form-example').html(xhr.responseText);
                }
            });
        });
        </script>
    </div>
</div>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(value=&quot;/styleguide/new-person&quot;, method=RequestMethod.GET)
public String newPersonPopup(ModelMap model) {
    
    Person person = new Person();
    model.addAttribute(&quot;person&quot;, person);
    
    return &quot;styleguide/person.jsp&quot;;
}

@RequestMapping(value=&quot;/styleguide/person&quot;, method=RequestMethod.POST)
public String createPerson(ModelMap model, HttpServletResponse resp, 
        @ModelAttribute(&quot;person&quot;) Person person, BindingResult result) {
    
    new PersonValidator().validate(person, result);
    if (result.hasErrors()) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return &quot;styleguide/person.jsp&quot;;
    }
    
    resp.setStatus(HttpStatus.NO_CONTENT.value());
    return null;
}

public class PersonValidator extends SimpleValidator&lt;Person&gt; {
        
    public PersonValidator() { super(Person.class); }
    
    @Override
    protected void doValidation(Person person, Errors errors) {
        YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, &quot;name&quot;, person.getName(), false, 60);
        int age = person.getAge();
        String email = person.getEmail();
        if (age &lt; 1 || age &gt; 120) errors.rejectValue(&quot;age&quot;, null, &quot;Age should be between 1 and 120.&quot;);
        if (!Validator.isEmailAddress(email)) errors.rejectValue(&quot;email&quot;, null, &quot;Invalid email address.&quot;);
    }
}
</pre>
<h4 class="subtle">JSP Page Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;form-example&quot; data-title=&quot;Ajax Form Submission Example&quot; data-width=&quot;400&quot; data-dialog 
        data-url=&quot;new-person&quot;
        data-event=&quot;yukon:dev:styleguid:dialogs:create:person&quot;&gt;
&lt;/div&gt;
&lt;cti:button label=&quot;Add Person&quot; data-popup=&quot;#form-example&quot; icon=&quot;icon-plus-green&quot;/&gt;
</pre>
<h4 class="subtle">person.jsp Code:</h4>
<pre class="code prettyprint">
&lt;cti:msgScope paths=&quot;modules.dev.dialogs&quot;&gt;
&lt;form:form modelAttribute=&quot;person&quot; action=&quot;person&quot; method=&quot;post&quot; cssClass=&quot;js-new-person-form&quot;&gt;
    &lt;tags:nameValueContainer2 tableClass=&quot;with-form-controls&quot;&gt;
        &lt;tags:inputNameValue nameKey=&quot;.name&quot; path=&quot;name&quot;/&gt;
        &lt;tags:inputNameValue nameKey=&quot;.age&quot; path=&quot;age&quot;/&gt;
        &lt;tags:inputNameValue nameKey=&quot;.email&quot; path=&quot;email&quot;/&gt;
        &lt;tags:nameValue2 excludeColon=&quot;true&quot;&gt;
            &lt;label&gt;&lt;tags:switch path=&quot;spam&quot;/&gt;Send me Spam?&lt;/label&gt;
        &lt;/tags:nameValue2&gt;
    &lt;/tags:nameValueContainer2&gt;
&lt;/form:form&gt;
&lt;/cti:msgScope&gt;
</pre>
<h4 class="subtle">Javascript Code:</h4>
<pre class="code prettyprint lang-js">
$('#form-example').on('yukon:dev:styleguid:dialogs:create:person', function (ev) {
    $('#form-example form').ajaxSubmit({
        success: function (data, status, xhr, $form) {
            $('#form-example').dialog('close');
            debug.log('A winner is you!!');
        },
        error: function (xhr, status, error, $form) {
            $('#form-example').html(xhr.responseText);
        }
    });
});
</pre>

<h2 id="js-api-example">Javascrip API: Programatic Dialog Opening</h2>

<p class="description">
    Sometimes you need to open one of these popups from javascript.  Yukon's Javascript api has some dialog functions to 
    handle that. Calling <em>yukon.ui.dialog</em> will open these dialogs using all the data stored in the data attributes. 
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="js-api-show-popup" data-dialog data-title="JS API Show Example" data-width="400"
                data-url="new-person" data-event="yukon:dev:styleguide:dialogs:foo"></div>
        <cti:button label="Open Popup" classes="js-api-show-btn"/>
        <script>
        $('.js-api-show-btn').click(function (e) { yukon.ui.dialog('#js-api-show-popup'); });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;js-api-show-popup&quot; data-dialog data-title=&quot;JS API Show Example&quot; data-width=&quot;400&quot;
        data-url=&quot;new-person&quot; data-event=&quot;yukon:dev:styleguide:dialogs:foo&quot;&gt;&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; classes=&quot;js-api-show-btn&quot;/&gt;
&lt;script&gt;
    $('.js-api-show-btn').click(function (e) { yukon.ui.dialog('#js-api-show-popup'); });
&lt;/script&gt;
</pre>

<h2 id="js-api-example">Tables and Advanced Dialogs</h2>

<p class="description">
    Sometimes you need one popup to handle many interactions, like a user clicking an edit button in a table row.  Instead
    of creating many popups you should use one.  At this point you probably need to bypass Yukon's automated dialog handling
    and code it yourself usings jquery's <a href="http://api.jqueryui.com/dialog/">dialog widget</a> api.
</p>
<p class="description">
    Below is an example of handling edit popups for any row of a table.  Jquery's dialog widget api defines buttons as an 
    array of json objects.  Yukon has a helper function: <em>yukon.ui.buttons</em> to build an 'ok','cancel' button set 
    for you. We use it here to also specify that the target of the event fired when clicking ok should be the table row we 
    are editing.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        
        <div id="edit-person-popup" class="dn"></div>
        
        <table id="person-table" class="compact-results-table">
            <thead>
                <tr>
                    <th>Name</th><th>Age</th><th>Email</th><th colspan="2">Spam</th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="person" items="${people}">
                    <tr data-id="${person.id}">
                    <td>${person.name}</td><td>${person.age}</td><td>${person.email}</td><td>${person.spam ? 'yes' : 'no'}</td>
                    <td><cti:button renderMode="buttonImage" icon="icon-pencil" classes="fr js-edit"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <script>
        $(document).on('click', '#person-table .js-edit', function (ev) {
            
            var 
            popup = $('#edit-person-popup'),
            row = $(this).closest('tr'),
            id = row.data('id');
            
            $.get('edit-person', { id: id })
            .done(function (data) {
                popup.html(data);
                popup.dialog({
                    title: 'Edit Person',
                    width: 'auto',
                    buttons: yukon.ui.buttons({
                        event: 'yukon:dev:sytleguide:dialogs:person:update', 
                        target: row
                    })
                });
            });
            
        });
        
        $(document).on('yukon:dev:sytleguide:dialogs:person:update', function (ev) {
            
            var row = $(ev.target);
            
            $('#edit-person-popup form').ajaxSubmit({
                success: function (person, status, xhr, $form) {
                    
                    $('#edit-person-popup').dialog('close');
                    row.find('td').eq(1).text(person.age)
                    .next().text(person.email)
                    .next().text(person.spam === true ? 'yes' : 'no');
                },
                error: function (xhr, status, error, $form) {
                    // show the validated form
                    $('#edit-person-popup').html(xhr.responseText);
                }
            });
        });
        </script>
    </div>
</div>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;div id=&quot;edit-person-popup&quot; class=&quot;dn&quot;&gt;&lt;/div&gt;
        
&lt;table id=&quot;person-table&quot; class=&quot;compact-results-table&quot;&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;th&gt;Name&lt;/th&gt;&lt;th&gt;Age&lt;/th&gt;&lt;th&gt;Email&lt;/th&gt;&lt;th colspan=&quot;2&quot;&gt;Spam&lt;/th&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;c:forEach var=&quot;person&quot; items=&quot;&#36;{people}&quot;&gt;
            &lt;tr data-id=&quot;&#36;{person.id}&quot;&gt;
                &lt;td&gt;&#36;{person.name}&lt;/td&gt;
                &lt;td&gt;&#36;{person.age}&lt;/td&gt;
                &lt;td&gt;&#36;{person.email}&lt;/td&gt;
                &lt;td&gt;&#36;{person.spam ? 'yes' : 'no'}&lt;/td&gt;
                &lt;td&gt;&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-pencil&quot; classes=&quot;fr js-edit&quot;/&gt;&lt;/td&gt;
            &lt;/tr&gt;
        &lt;/c:forEach&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
<h4 class="subtle">Javascript Code:</h4>
<pre class="code prettyprint">
$(document).on('click', '#person-table .js-edit', function (ev) {
            
    var 
    popup = $('#edit-person-popup'),
    row = $(this).closest('tr'),
    id = row.data('id');
    
    $.get('edit-person', { id: id })
    .done(function (data) {
        popup.html(data);
        popup.dialog({
            title: 'Edit Person',
            width: 'auto',
            buttons: yukon.ui.buttons({
                event: 'yukon:dev:sytleguide:dialogs:person:update', 
                target: row
            })
        });
    });
    
});

$(document).on('yukon:dev:sytleguide:dialogs:person:update', function (ev) {
    
    var row = $(ev.target);
    
    $('#edit-person-popup form').ajaxSubmit({
        success: function (person, status, xhr, $form) {
            
            $('#edit-person-popup').dialog('close');
            row.find('td').eq(1).text(person.age)
            .next().text(person.email)
            .next().text(person.spam === true ? 'yes' : 'no');
        },
        error: function (xhr, status, error, $form) {
            // show the validated form
            $('#edit-person-popup').html(xhr.responseText);
        }
    });
});
</pre>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(value=&quot;/styleguide/edit-person&quot;, method=RequestMethod.GET)
public String editPersonPopup(ModelMap model, int id) {
    
    Person person = people.get(id);
    model.addAttribute(&quot;person&quot;, person);
    
    return &quot;styleguide/person.jsp&quot;;
}

@RequestMapping(value=&quot;/styleguide/person&quot;, method=RequestMethod.PUT)
public String updatePerson(ModelMap model, HttpServletResponse resp, 
        @ModelAttribute(&quot;person&quot;) Person person, BindingResult result) throws Exception {
    
    new PersonValidator().validate(person, result);
    if (result.hasErrors()) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return &quot;styleguide/person.jsp&quot;;
    }
    
    people.put(person.getId(), person);
    resp.setContentType(&quot;application/json&quot;);
    JsonUtils.getWriter().writeValue(resp.getOutputStream(), person);
    
    return null;
}
</pre>

<h2 id="tabbed-dialog-example">Tabbed Dialog</h2>

<p class="description">
    A dialog with tabs for the title bar, also has an optional title which will appear in front of the tabs.
    Note: the proper JQuery tabs markup is required for this to work.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div id="tabbed-dialog" class="dn" data-dialog data-dialog-tabbed data-title="Pets:">
            <ul>
                <li><a href="#cats-tab">Cats</a></li>
                <li><a href="#dogs-tab">Dogs</a></li>
                <li><a href="#goldfish-tab">Goldfish</a></li>
            </ul>
            <div id="cats-tab">This is the cats content.<input type="text"> It's so cool you don't even know.</div>
            <div id="dogs-tab">This is the dogs content. It's so cool you don't even know.</div>
            <div id="goldfish-tab">This is the goldfish content. It's so cool you don't even know.</div>
        </div>
        <cti:button label="Open Popup" data-popup="#tabbed-dialog"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div id=&quot;tabbed-dialog&quot; class=&quot;dn&quot; data-dialog data-dialog-tabbed data-title=&quot;Pets:&quot;&gt;
    &lt;ul&gt;
        &lt;li&gt;&lt;a href=&quot;#cats-tab&quot;&gt;Cats&lt;/a&gt;&lt;/li&gt;
        &lt;li&gt;&lt;a href=&quot;#dogs-tab&quot;&gt;Dogs&lt;/a&gt;&lt;/li&gt;
        &lt;li&gt;&lt;a href=&quot;#goldfish-tab&quot;&gt;Goldfish&lt;/a&gt;&lt;/li&gt;
    &lt;/ul&gt;
    &lt;div id=&quot;cats-tab&quot;&gt;This is the cats content.&lt;input type=&quot;text&quot;&gt; It's so cool you don't even know.&lt;/div&gt;
    &lt;div id=&quot;dogs-tab&quot;&gt;This is the dogs content. It's so cool you don't even know.&lt;/div&gt;
    &lt;div id=&quot;goldfish-tab&quot;&gt;This is the goldfish content. It's so cool you don't even know.&lt;/div&gt;
&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#tabbed-dialog&quot;/&gt;
</pre>

<h2>Ajaxed Tabbed Dialog</h2>

<p class="description">
    A dialog with tabs for the title bar, also has an optional title which will appear in front of the tabs.  The dialog
    content is ajaxed in before shown.  Note: the proper JQuery tabs markup is required for this to work.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div id="ajax-tabbed-dialog" class="dn" data-dialog data-dialog-tabbed data-title="Vehicles:" 
            data-width="500" data-height="500"
            data-url="dialogs/ajax-tabbed-dialog"></div>
        <cti:button label="Open Popup" data-popup="#ajax-tabbed-dialog"/>
    </div>
</div>
<h4 class="subtle">Page JSP Code:</h4>
<pre class="code prettyprint">
&lt;div id=&quot;ajax-tabbed-dialog&quot; class=&quot;dn&quot; data-dialog data-dialog-tabbed data-title=&quot;Vehicles:&quot; 
    data-width=&quot;500&quot; data-height=&quot;500&quot; data-url=&quot;dialogs/ajax-tabbed-dialog&quot;&gt;&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#ajax-tabbed-dialog&quot;/&gt;
</pre>

<h4 class="subtle">Popup JSP Code:</h4>
<pre class="code prettyprint">
&lt;ul&gt;
    &lt;li&gt;&lt;a href=&quot;#cars-tab&quot;&gt;Cars&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href=&quot;#trucks-tab&quot;&gt;Trucks&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href=&quot;#airplane-tab&quot;&gt;Airplanes&lt;/a&gt;&lt;/li&gt;
&lt;/ul&gt;
&lt;div id=&quot;cars-tab&quot;&gt;...&lt;/div&gt;
&lt;div id=&quot;trucks-tab&quot;&gt;...&lt;/div&gt;
&lt;div id=&quot;airplane-tab&quot;&gt;...&lt;/div&gt;
</pre>

</tags:styleguide>
</cti:standardPage>