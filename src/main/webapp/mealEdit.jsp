<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<html>
<head>
    <title>Meal change</title>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</head>

<body>
<section>
    <h2><a href="index.html">Home</a></h2>
    <h3>Edit meal</h3>
    <hr>
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <form action="meals" method="post">
        <input type="hidden" name="id" value="${meal.id}">
        <dl>
            <dt>Date</dt>
            <dd><input type="date" value="${meal.date}" name="date"></dd>
        </dl>
        <dl>
            <dt>Time</dt>
            <dd><input type="time" value="${meal.time}" name="time"></dd>
        </dl>
        <dl>
            <dt>Description</dt>
            <dd><input type="text" value="${meal.description}" name="description"></dd>
        </dl>
        <dl>
            <dt>Calories</dt>
            <dd><input type="number" value="${meal.calories}" name="calories"></dd>
        </dl>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button"> Cancel</button>
    </form>

</section>
</body>
</html>
