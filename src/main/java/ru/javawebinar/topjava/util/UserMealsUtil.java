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

        //System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static UserMealWithExcess mealToMealWithExcess(UserMeal meal) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), true);
    }

    /*public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCaloriesMealMap = new HashMap<>();
        for (UserMeal meal : meals) {
            dateCaloriesMealMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> resultList = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (dateCaloriesMealMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                    && TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                resultList.add(mealToMealWithExcess(meal));
            }
        }
        return resultList;
    }*/
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCaloriesMealMap = new HashMap<>();
        for (UserMeal meal : meals) {
            dateCaloriesMealMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> resultList = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (dateCaloriesMealMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                    && TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                resultList.add(mealToMealWithExcess(meal));
            }
        }
        return resultList;
    }

    /*public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .values().stream()
                .filter(listMeals -> listMeals.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay)
                .flatMap(Collection::stream)
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(UserMealsUtil::mealToMealWithExcess)
                .collect(Collectors.toList());
    }*/

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .map(UserMealsUtil::mealToMealWithExcess)
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .values().stream().filter(listMeals -> listMeals.stream().mapToInt(UserMealWithExcess::getCalories).sum() > caloriesPerDay)
                .

                return new ArrayList<UserMealWithExcess>();
    }

    public static List<UserMealWithExcess> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> resultList = new LinkedList<>();
        Map<LocalDate, ListWithCounter> dateListMap = new HashMap<>();
        for (UserMeal meal : meals) {
            boolean mealIsSuitable = TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime);
            LocalDate key = meal.getDateTime().toLocalDate();
            dateListMap.merge(key, new ListWithCounter(meal, mealIsSuitable, resultList, caloriesPerDay),
                    (oldV, newV) -> {
                        oldV.processMeal(meal, mealIsSuitable);
                        return oldV;
                    });
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
        private final List<UserMealWithExcess> currentList;
        private final List<UserMealWithExcess> resultExcessList;
        private int calories;
        private final int caloriesLimit;
        private boolean excess;

        private ListWithCounter(UserMeal meal, boolean suitableByTime, List<UserMealWithExcess> excessList, int limit) {
            caloriesLimit = limit;
            currentList = new LinkedList<>();
            this.resultExcessList = excessList;
            calories = meal.getCalories();

            if (calories > caloriesLimit) {
                excess = true;
                if (suitableByTime) {
                    this.resultExcessList.add(mealToMealWithExcess(meal));
                }
                return;
            }
            if (suitableByTime) {
                currentList.add(mealToMealWithExcess(meal));
            }
        }

        private void addCalories(int num) {
            calories += num;
            if (calories > caloriesLimit) {
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
                resultExcessList.add(mealToMealWithExcess(meal));
                addCalories(meal.getCalories());
                return;
            }
            addCalories(meal.getCalories());
            if (excess) {
                resultExcessList.addAll(currentList);
                resultExcessList.add(mealToMealWithExcess(meal));
            } else {
                currentList.add(mealToMealWithExcess(meal));
            }
        }

        private void processUnsuitableMeal(UserMeal meal) {
            if (excess) {
                addCalories(meal.getCalories());
                return;
            }
            addCalories(meal.getCalories());
            if (excess) {
                resultExcessList.addAll(currentList);
            }
        }
    }
}
