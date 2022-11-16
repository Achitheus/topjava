package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {

    public static final Meal userMeal1 = new Meal(100003, LocalDateTime.of(2022, 6, 22, 19, 10, 25), "user havka", 1000);
    public static final Meal userMeal2 = new Meal(100004, LocalDateTime.of(2022, 6, 24, 10, 10, 25), "user big havka", 2000);
    public static final Meal userMeal3 = new Meal(100005, LocalDateTime.of(2022, 6, 27, 15, 10, 25), "another user havka", 1400);
    public static final Meal adminMeal1 = new Meal(100006, LocalDateTime.of(2022, 6, 22, 19, 10, 25), "admin havka", 1000);
    public static final Meal adminMeal2 = new Meal(100007, LocalDateTime.of(2022, 6, 23, 10, 10, 25), "admin big havka", 2000);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2022, 6, 23, 10, 10, 25), "new havka", 2000);
    }

    public static Meal getUpdated() {
        return new Meal(LocalDateTime.of(2022, 7, 15, 15, 10, 25),"updated meal", 3999);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
