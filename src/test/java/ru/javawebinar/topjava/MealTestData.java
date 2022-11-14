package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;

public class MealTestData {

    public static final Meal userMeal1 = new Meal(LocalDateTime.of(2022, 6, 22, 19, 10, 25), "user havka", 1000);
    public static final Meal userMeal2 = new Meal(LocalDateTime.of(2022, 6, 22, 10, 10, 25), "user big havka", 2000);
    public static final Meal userMeal3 = new Meal(LocalDateTime.of(2022, 6, 22, 15, 10, 25), "another user havka", 1400);
    public static final Meal adminMeal1 = new Meal(LocalDateTime.of(2022, 6, 22, 19, 10, 25), "admin havka", 1000);
    public static final Meal adminMeal2 = new Meal(LocalDateTime.of(2022, 6, 22, 10, 10, 25), "admin big havka", 2000);

}
