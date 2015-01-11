package controllers;

import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import services.UserDAO;
import views.html.login;
import views.html.register;

import static play.data.Form.form;

@org.springframework.stereotype.Controller
public class Registration extends Controller {

    @Autowired
    private UserDAO userDAO;

    public Result doRegistration() {
        boolean created = false;
        // Form mit Inputdaten
        Form<Application.InputForm> registrationForm = Form.form(Application.InputForm.class);
        Application.InputForm regData = registrationForm.bindFromRequest().get();

        User user = userDAO.findUserByName(regData.username);

        // Wenn user nicht vorhanden registrieren
        if (user == null) {
            user = new User(regData.email, regData.password, regData.username, User.ROLE_USER);
            userDAO.createUser(user);
            created = true;
        }

        Logger.info(created ? "User " + user.getName() + " erstellt." : "    User existiert bereits.");

        return created ? created(login.render(form(Application.InputForm.class))) : forbidden(register.render(form(Application.InputForm.class))) ;
    }
}
