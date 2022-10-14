package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        //List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        //mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static MealTo mealToMealWithExcess(Meal meal, boolean isExceed) {
        return new MealTo(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExceed);
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateIntegerMap = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> mealToMealWithExcess(meal, dateIntegerMap.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static boolean isExceed(List<MealTo> meals, int caloriesLimit) {
        return meals.stream().mapToInt(MealTo::getCalories).sum() > caloriesLimit;
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<MealTo> resultList = new LinkedList<>();
        Map<LocalDate, ListWithCounter> dateListMap = new HashMap<>();
        for (Meal meal : meals) {
            boolean mealIsSuitable = TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime);
            LocalDate key = meal.getDate();
            dateListMap.merge(key, new ListWithCounter(resultList, caloriesPerDay), (r, u) -> r).processMeal(meal, mealIsSuitable);
        }
        return resultList;
    }

    /**
     * This inner class helps you to collect meals by dates and count calories. It also tracks calories excess.
     * The first meals are included in the local meal list until calories limit is occurred.
     * When the calories limit is reached local meal storage is loading into result excess list which is common for all dates.
     * After that all subsequent meals are added not to the local, but to the result excess list.
     * All of these actions also take into account the correspondence of meals to the given time period
     */
    private static class ListWithCounter {
        private final List<MealTo> resultList;
        private final List<MealTo> currentList;
        private final int caloriesLimit;
        private int calories;
        private boolean excess;

        private ListWithCounter(List<MealTo> excessList, int limit) {
            resultList = excessList;
            currentList = new ArrayList<>();
            caloriesLimit = limit;
        }

        /**
         * @param num calories which should be added to calories field.
         * @return true if excess field was changed by this method.
         */
        private boolean addCalories(int num) {
            calories += num;
            if (excess) {
                return false;
            } else {
                return excess = calories > caloriesLimit;
            }
        }

        public void processMeal(Meal meal, boolean suitableByTime) {
            if (addCalories(meal.getCalories())) {
                updateExcessStatus();
            }
            if (suitableByTime) {
                addMeal(meal);
            }
        }

        public void updateExcessStatus() {
            for (MealTo meal : currentList) {
                meal.setExcess(true);
            }
            currentList.clear();
        }

        public void addMeal(Meal meal) {
            MealTo mealWithExcess = MealsUtil.mealToMealWithExcess(meal, excess);
            resultList.add(mealWithExcess);
            if (!excess) currentList.add(mealWithExcess);
        }

    }


}
