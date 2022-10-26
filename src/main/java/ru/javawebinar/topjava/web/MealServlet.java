package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTO;
import ru.javawebinar.topjava.repository.MealMapRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    public static final Logger log = getLogger(MealServlet.class);
    public MealRepository storage;
    public static final int MAX_CALORIES = 2000;

    @Override
    public void init() throws ServletException {
        storage = new MealMapRepository();
        log.debug("init is done");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            log.debug("get all");
            List<MealTO> meals = MealsUtil.toTOlist(storage.getAll(), MAX_CALORIES);
            request.setAttribute("list", meals);
            request.getRequestDispatcher("meals.jsp").forward(request, response);
        } else if (action.equals("delete")) {
            log.debug("delete meal");
            storage.delete(getId(request));
            response.sendRedirect("meals");
        } else {
            boolean isUpdate = action.equals("update");
            log.debug(isUpdate ? "update meal" : "add meal");
            request.setAttribute("meal", isUpdate
                    ? storage.get(getId(request))
                    : new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "", 1000));
            request.getRequestDispatcher("mealEdit.jsp").forward(request, response);
        }
    }

    private int getId(HttpServletRequest request) {
        return Integer.parseInt(Objects.requireNonNull(request.getParameter("id")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.parseInt(id)
                , LocalDateTime.of(LocalDate.parse(request.getParameter("date"))
                , LocalTime.parse(request.getParameter("time")))
                , request.getParameter("description")
                , Integer.parseInt(request.getParameter("calories")));
        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        storage.save(meal);
        response.sendRedirect("meals");
    }
}
