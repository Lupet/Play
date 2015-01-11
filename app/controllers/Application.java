package controllers;

import models.Picture;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.PictureDAO;
import services.UserDAO;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    @Autowired
    PictureDAO pictureDAO;
    @Autowired
    UserDAO userDAO;

    // Navigation
    public Result index() {
        return ok(index.render());
    }
    public Result login() {
        return ok(login.render(form(InputForm.class)));
    }
    public Result register() {
        return ok(register.render(form(InputForm.class)));
    }
    public Result upload() {
        return ok(upload.render(form(InputForm.class)));
    }
    public Result view() {

        // Form mit Namen des gesuchten Users
        Form<InputForm> userForm = Form.form(Application.InputForm.class);
        Application.InputForm userData = userForm.bindFromRequest().get();

        // Bilder des anderen Users suchen
        User user = userDAO.findUserByName(userData.username);
        List<Picture> userPictures = new ArrayList<>();
        if (user != null){
            Logger.info("Gefundener User: " + user.getName());
            userPictures = pictureDAO.getPictures(user, 0, 200, true);
        }
        else{
            Logger.info("Kein User mit dem Namen " + userData.username + " gefunden.");
        }

        // Eigene Bilder
        List<Picture> ownPictures = pictureDAO.getPictures(userDAO.findUserByName(session("connected")), 0, 200, false);

        return ok(view.render(ownPictures, userPictures ,form(Application.InputForm.class)));
    }

    // Als Willkommenstext den Name aus der Session fischen
    public Result member() {
        String name = session("connected");
        return ok(member.render(name));
    }

    // Logout, Session löschen
    public Result logout(){
        session().clear();
        return ok(index.render());
    }


    // Form
    public static class InputForm {
        public String username;
        public String email;
        public String password;
        public String description;
        public String pictureName;
        public boolean isPublic;
    }
}