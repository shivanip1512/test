<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer>
    <tags:nameValue name="Name">
        <input length="30" size="30"/>
    </tags:nameValue>
    <tags:nameValue name="Description">
            <textarea cols="31" rows="5"></textarea>
    </tags:nameValue>
    <tags:nameValue name="Template">
        <select>
            <option>Blank dashboard</option>
            <option>Yukon Default Dashboard</opiton>
            <option>Utility Company Sample Dashboard</option>
            <option>User Specific Dashboard</option>
        </select>
    </tags:nameValue>
    <tags:nameValue name="Visibility">
        <select>
            <option>Private</option>
            <option>Shared With User Group</opiton>
            <option>Public</option>
        </select>
    </tags:nameValue>
</tags:nameValueContainer>