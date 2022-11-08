package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.ADMIN_ID;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        save(new Meal(LocalDateTime.of(1994, 11, 23, 1, 4), "admin meal1", 510), ADMIN_ID);
        save(new Meal(LocalDateTime.of(1994, 11, 23, 1, 4), "admin meal2", 900), ADMIN_ID);
    }

    @Override // null if updated meal does not belong to userId
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> usersMap = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            usersMap.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return usersMap.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override // false if meal does not belong to userId
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> usersMap = repository.get(userId);
        return usersMap != null && usersMap.remove(id) != null;
    }

    @Override // null if meal does not belong to userId
    public Meal get(int id, int userId) {
        Map<Integer, Meal> usersMap = repository.get(userId);
        return (usersMap == null) ? null : usersMap.get(id);
    }

    @Override // ORDERED dateTime desc
    public Collection<Meal> getAll(int userId) {
        return getAllFiltered(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetweenByDate(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return getAllFiltered(userId, meal -> Util.isBetweenClosed(meal.getDateTime(), startDate, endDate));
    }

    private List<Meal> getAllFiltered(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> usersMap = repository.get(userId);
        return usersMap.isEmpty() ? Collections.emptyList() : usersMap.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

