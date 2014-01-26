import java.util.concurrent.TimeUnit;

import model.Grevistes;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;


public class Global extends GlobalSettings {
	@Override
    public void onStart(Application application) {
		Logger.info("STARTING APPLICATION");
		
		Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(60, TimeUnit.MINUTES),
                new Grevistes() , Akka.system().dispatcher()
        );
	
	}
}
