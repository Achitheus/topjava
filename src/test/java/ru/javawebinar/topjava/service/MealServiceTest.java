package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;


import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
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
        assertThrows(DataAccessException.class, () ->
            mealService.create(new Meal(LocalDateTime.of(2022, 6, 27, 15, 10), "duplicate dateTime havka", 1400), USER_ID));
    }

    @Test
    public void delete() {
        mealService.delete(userMeal1.getId(), USER_ID);
        assertThrows(NotFoundException.class,
                () -> mealService.get(userMeal1.getId(), USER_ID));
    }

    @Test
    public void deleteAbsent() {
        assertThrows(NotFoundException.class,
                () -> mealService.get(10, USER_ID));
    }

    @Test
    public void deleteSomeoneElsesObject() {
        assertThrows(NotFoundException.class,
                () -> mealService.get(userMeal3.getId(), ADMIN_ID));
    }

    @Test
    public void get() {
        Meal meal = mealService.get(userMeal2.getId(), USER_ID);
        assertMatch(meal, userMeal2);
    }

    @Test
    public void getAbsent() {
        assertThrows(NotFoundException.class,
                () -> mealService.get(10, USER_ID));
    }

    @Test
    public void getSomeoneElsesObject() {
        assertThrows(NotFoundException.class,
                () -> mealService.get(userMeal3.getId(), ADMIN_ID));
    }

    @Test
    public void update() {
        int mealId = userMeal1.getId();
        Meal updatedMeal = getUpdated();
        updatedMeal.setId(mealId);
        mealService.update(updatedMeal, USER_ID);
        assertMatch(mealService.get(mealId, USER_ID), updatedMeal);
    }

    @Test
    public void updateSomeoneElsesObject() {
        int mealId = userMeal1.getId();
        Meal updatedMeal = getUpdated();
        updatedMeal.setId(mealId);
        assertThrows(NotFoundException.class,
                () -> mealService.update(updatedMeal, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        assertMatch(mealService.getBetweenInclusive(LocalDate.of(2022, 06,23), LocalDate.of(2022, 06, 27), USER_ID),
                userMeal3,
                userMeal2);
    }

    @Test
    public void getAll() {
        assertMatch(mealService.getAll(USER_ID), userMeal3, userMeal2, userMeal1);
    }
}