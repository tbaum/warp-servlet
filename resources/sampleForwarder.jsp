<%--
  Created by IntelliJ IDEA.
  User: dhanji
  Date: Apr 17, 2008
  Time: 3:51:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Simple jsp page</title></head>
  <body>
  <p>
  This JSP just forwards to a managed servlet and should never be seen.

  <jsp:forward page="/hello/dude"/>
  </p>
  </body>
</html>