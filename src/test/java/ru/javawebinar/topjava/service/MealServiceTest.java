package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({"classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService mealService;

    @Test
    public void create() {
        Meal created = mealService.create(getNew(), USER_ID);
        Meal compareMeal = getNew();
        Integer id = created.getId();
        compareMeal.setId(id);
        assertMatch(created, compareMeal);
        assertMatch(mealService.get(id, USER_ID), compareMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        Meal created = mealService.create(getNew(), USER_ID);
        Meal compareMeal = getNew();
        Integer id = created.getId();
        compareMeal.setId(id);
        assertMatch(created, compareMeal);
        assertMatch(mealService.get(id, USER_ID), compareMeal);
    }

    @Test
    public void delete() {
    }

    @Test
    public void get() {
        Meal meal = mealService.get(userMeal2.getId(), USER_ID);
        assertMatch(meal, userMeal2);
    }

    @Test
    public void update() {
    }

    @Test
    public void getBetweenInclusive() {
    }

    @Test
    public void getAll() {
    }
}