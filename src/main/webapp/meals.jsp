<%@ page import="ru.javawebinar.topjava.model.MealTO" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="ru">
<head>
    <title>Meals</title>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <style>
        .normal {color: darkgreen}
        .exceeded {color: red}
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Meals</h2>
<h3><a href="${pageContext.request.contextPath}/meals?action=add">Add meal</a> </h3>
<h3>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>description</th>
            <th>date</th>
            <th>time</th>
            <th>calories</th>
            <th>day excess</th>
            <th>options</th>
        </tr>
        </thead>
        <tbody>
        <jsp:useBean id="list" scope="request" type="java.util.List"/>
        <c:forEach items="${list}" var="meal">
            <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.model.MealTO"/>
            <tr class=" ${meal.excess ? 'exceeded' : 'normal'}" >

                <th>${meal.description}</th>
                <th>${meal.date}</th>
                <th>${meal.time}</th>
                <th>${meal.calories}</th>
                <th>${meal.excess}</th>
                <th><a href="${pageContext.request.contextPath}/meals?action=update&id=${meal.id}">Update</a> </th>
                <th><a href="${pageContext.request.contextPath}/meals?action=delete&id=${meal.id}">Delete</a> </th>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</h3>
</body>
</html>
