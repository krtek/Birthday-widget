package cz.krtinec.birthday;

import android.app.Application;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 * Date: 3.1.11
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */

@ReportsCrashes(formKey="dHk5YVA0bHZTdG5PTmlGdTF4S3d3aEE6MQ",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class BirthdayApp extends Application {

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }

}
