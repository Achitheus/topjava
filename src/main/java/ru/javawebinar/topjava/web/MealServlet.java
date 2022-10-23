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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    public static final Logger log = getLogger(MealServlet.class);
    //public MealStorage storage;
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
        } else if (action.equals("delete")) {
            log.debug("delete meal");
            storage.delete(getId(request));
            response.sendRedirect("meals");
            return;
        } else if (action.equals("update")) {
            log.debug("update meal");
            int id = getId(request);
            request.setAttribute("meal", storage.get(id));
            request.getRequestDispatcher("mealEdit.jsp").forward(request, response);
        } else if (action.equals("add")) {
            log.debug("add meal");
            Meal meal = new Meal(LocalDateTime.now(), "", 1000);
            request.setAttribute("meal", meal);
            request.getRequestDispatcher("mealEdit.jsp").forward(request, response);
            return;
        }
        List<MealTO> meals = MealsUtil.toTOlist(storage.getAll(), MAX_CALORIES);
        request.setAttribute("list", meals);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    private int getId(HttpServletRequest request) {
        return Integer.parseInt(Objects.requireNonNull(request.getParameter("id")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = (Meal) request.getAttribute("meal");
        storage.save(meal);
        List<MealTO> meals = MealsUtil.toTOlist(storage.getAll(), MAX_CALORIES);
        response.sendRedirect("meals");

    }
}
