package ru.javawebinar.topjava.util;

//import jdk.vm.ci.meta.Local;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {


    private static class ListWithCounter {
        private final List<UserMealWithExcess> currentList;
        private final List<UserMealWithExcess> resultList;
        private int calories;
        private final int caloriesLimit;
        private boolean excess = false;

        private ListWithCounter(UserMeal meal, boolean mealIsSuitable, List<UserMealWithExcess> excessList, int limit) {
            currentList = new LinkedList<>();
            resultList = excessList;
            caloriesLimit = limit;
            calories = meal.getCalories();
            if (calories >= caloriesLimit) {
                excess = true;
                if (mealIsSuitable) {
                    resultList.add(new UserMealWithExcess(meal));
                }
                return;
            }
            if (mealIsSuitable) {
                currentList.add(new UserMealWithExcess(meal));
            }

        }

        private void addToNum(Integer num) {
            calories += num;
            if (calories >= caloriesLimit) {
                excess = true;
            }
        }
        public void processMeal(UserMeal value, boolean mealIsSuitable) {
            if(mealIsSuitable) {
                processSuitableMeal(value);
            } else {
                processUnsuitableMeal(value);
            }
        }
        private void processSuitableMeal(UserMeal value) {
            if (excess) {
                resultList.add(new UserMealWithExcess(value));
                addToNum(value.getCalories());
                return;
            }

            addToNum(value.getCalories());
            if (excess) {
                resultList.addAll(currentList);
                resultList.add(new UserMealWithExcess(value));
            } else {
                currentList.add(new UserMealWithExcess(value));
            }
        }

        private void processUnsuitableMeal(UserMeal value) {
            if(excess){
                addToNum(value.getCalories());
                return;
            }

            addToNum(value.getCalories());
            if(excess) {
                resultList.addAll(currentList);
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

       // List<UserMealWithExcess> mealsTo = filteredByCycles2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
       // mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mealMap = new HashMap<>();
        for (UserMeal meal : meals) { // we can also try map<LocalDate, List<meals> >
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
