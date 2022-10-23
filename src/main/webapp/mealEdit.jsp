<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<html>
<head>
    <title>Meal change</title>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <style>
    dl {
      background: no-repeat scroll 0 0 #FAFAFA;
      margin: 8px 0;
      padding: 0;
    }
  </style>
</head>

<body>
<section>
  <h2><a href="index.html">Home</a> </h2>
  <h3>Edit meal</h3>
  <hr>
  <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request" />
  <input type="hidden" name="id" value="${meal.id}">
  <dl>
    <dt>Date Time</dt>
    <dd><input type="datetime-local" value="${meal.date}"></dd>
  </dl>

</section>
</body>
</html>
