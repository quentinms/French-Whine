package controllers;

import model.Grevistes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.main;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render(Grevistes.grevistes));
    }
    
    public static Result main() {
        return ok(main.render(Grevistes.strikers));
    }
    
}
