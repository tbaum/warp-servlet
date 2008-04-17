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
  Here is an included JSP (named sampleInclude.jsp):

  <jsp:include page="sampleInclude.jsp"/>
  </p>

  <p>
  Here is an included Servlet (managed by warp-servlet):
  <br/><br/>
  <b><jsp:include page="/hello/dude"/></b>
  </p>
  </body>
</html>