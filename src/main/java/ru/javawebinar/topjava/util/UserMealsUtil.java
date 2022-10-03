package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {

    /**
     * This inner class helps you to collect meals by dates and count calories. It also tracks calories excess.
     * The first meals are included in the local meal list until calories limit is occurred.
     * When the calories limit is reached local meal storage is loading into result excess list which is common for all dates.
     * After that all subsequent meals are added not to the local, but to the result excess list.
     * All of these actions also take into account the correspondence of meals to the given time period
     */
    private static class ListWithCounter {
        private final List<UserMealWithExcess> currentList;
        private final List<UserMealWithExcess> resultExcessList;
        private int calories;
        private final int caloriesLimit;
        private boolean excess = false;

        private ListWithCounter(UserMeal meal, boolean suitableByTime, List<UserMealWithExcess> excessList, int limit) {
            caloriesLimit = limit;
            currentList = new LinkedList<>();
            this.resultExcessList = excessList;
            calories = meal.getCalories();

            if (calories >= caloriesLimit) {
                excess = true;
                if (suitableByTime) {
                    this.resultExcessList.add(new UserMealWithExcess(meal));
                }
                return;
            }
            if (suitableByTime) {
                currentList.add(new UserMealWithExcess(meal));
            }

        }

        private void addCalories(int num) {
            calories += num;
            if (calories >= caloriesLimit) {
                excess = true;
            }
        }

        public void processMeal(UserMeal meal, boolean suitableByTime) {
            if (suitableByTime) {
                processSuitableMeal(meal);
            } else {
                processUnsuitableMeal(meal);
            }
        }

        private void processSuitableMeal(UserMeal meal) {
            if (excess) {
                resultExcessList.add(new UserMealWithExcess(meal));
                addCalories(meal.getCalories());
                return;
            }
            addCalories(meal.getCalories());
            if (excess) {
                resultExcessList.addAll(currentList);
                resultExcessList.add(new UserMealWithExcess(meal));
            } else {
                currentList.add(new UserMealWithExcess(meal));
            }
        }

        private void processUnsuitableMeal(UserMeal value) {
            if (excess) {
                addCalories(value.getCalories());
                return;
            }
            addCalories(value.getCalories());
            if (excess) {
                resultExcessList.addAll(currentList);
            }
        }

    }

    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mealMap = new HashMap<>();
        for (UserMeal meal : meals) {
            mealMap.merge(LocalDate.from(meal.getDateTime()), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> resultList = new ArrayList<>();
        LocalDateTime mealDateTime;
        for (UserMeal meal : meals) {
            mealDateTime = meal.getDateTime();
            if (mealMap.get(LocalDate.from(mealDateTime)) >= caloriesPerDay
                    && TimeUtil.isBetweenHalfOpen(LocalTime.from(mealDateTime), startTime, endTime)) {
                resultList.add(new UserMealWithExcess(meal));
            }
        }
        return resultList;
    }

    public static List<UserMealWithExcess> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> resultList = new LinkedList<>();
        Map<LocalDate, ListWithCounter> map = new HashMap<>();
        for (UserMeal meal : meals) {
            boolean mealIsSuitable = TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime);
            LocalDate key = LocalDate.from(meal.getDateTime());
            if (!map.containsKey(key)) {
                map.put(key, new ListWithCounter(meal, mealIsSuitable, resultList, caloriesPerDay));
            } else {
                map.get(key).processMeal(meal, mealIsSuitable);
            }
        }
        return resultList;
    }


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> LocalDate.from(meal.getDateTime())))
                .values().stream()
                .filter(listMeals -> listMeals.stream().mapToInt(UserMeal::getCalories).sum() >= caloriesPerDay)
                .flatMap(Collection::stream)
                .filter(meal -> TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime))
                .map(UserMealWithExcess::new)
                .collect(Collectors.toList());
    }

}
