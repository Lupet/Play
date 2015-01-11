package controllers;

import models.Picture;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import services.PictureDAO;
import services.UserDAO;
import views.html.upload;

import java.io.*;


import static play.data.Form.form;

@org.springframework.stereotype.Controller
public class Pictures extends Controller{

    @Autowired
    PictureDAO pictureDAO;
    @Autowired
    UserDAO userDAO;

    public Result doUpload() throws IOException {

        // Daten aus der Form holen
        Form<Application.InputForm> uploadForm = Form.form(Application.InputForm.class);
        Application.InputForm pictureData = uploadForm.bindFromRequest().get();

        // Hochgeladenes Image holen
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        InputStream stream = new FileInputStream(file.getFile());

        // User aus session ziehen
        User user = userDAO.findUserByName(session("connected"));

        // Bild erstellen
        Picture picture = new Picture();
        picture.setDescription(pictureData.description);
        picture.setName(pictureData.pictureName);
        picture.setCreator(user);
        picture.setMimeType(file.getContentType());
        picture.setData(getFileContents(stream));
        picture.setPublicVisible(pictureData.isPublic);

        pictureDAO.createPicture(user, picture);

        Logger.info("Picture name: " + picture.getName());
        Logger.info("Public? " + picture.isPublicVisible());
        Logger.info("Owner? " + picture.getOwner().getName());
        return ok(upload.render(form(Application.InputForm.class)));
    }

    // Einzelnes Bild unter Angabe der ID suchen und an view schicken
    public Result getImage(int id) {
        Picture pic = pictureDAO.getPicture(id);
        ByteArrayInputStream input = new ByteArrayInputStream(pic.getData());

        return ok(input).as("image/jpeg");
    }

    // Byte-Array aus Inputstream ziehen
    private byte[] getFileContents(InputStream in) throws IOException {
        byte[] bytes = null;
        // write the inputStream to a FileOutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read = 0;
        bytes = new byte[1024];

        while ((read = in.read(bytes)) != -1) {
            bos.write(bytes, 0, read);
        }
        bytes = bos.toByteArray();
        in.close();
        in = null;
        bos.flush();
        bos.close();
        bos = null;
        return bytes;
    }
}
