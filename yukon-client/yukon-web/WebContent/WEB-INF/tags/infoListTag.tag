<%@ attribute name="text" type="java.lang.String" required="true" description="The text to display in the tag." %>
<%@ attribute name="backgroundColor" type="java.lang.String" required="true" description="The class to use that specifies the background color for the tag." %>
<%@ attribute name="textColor" type="java.lang.String" description="The class to use that specifies the text color for the tag. Default is white" %>

<div class="tag-item ${backgroundColor}" style="border-radius:0.125rem;padding:0;padding-left:0.25rem;padding-right:calc(0.25rem - 1px);line-height: 1rem">
    <span class="${textColor}" style="font-size:10px;font-weight:700;letter-spacing:1px;color:white;text-transform:uppercase">${text}</span>
</div>
