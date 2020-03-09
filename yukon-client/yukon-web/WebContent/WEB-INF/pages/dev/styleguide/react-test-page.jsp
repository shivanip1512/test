<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Test REACT Page</title>
  </head>
  <body>
    <h1>This page is rendered using React JS</h1>
    <div id="like_button_container"></div>
    
    <script src="<c:url value="/resources/js/lib/React/react.development.js"/>"></script>
    <script src="<c:url value="/resources/js/lib/React/react-dom.development.js"/>"></script>
    <script src="<c:url value="/resources/js/pages/like_button.js"/>"></script>
    
  </body>
</html>