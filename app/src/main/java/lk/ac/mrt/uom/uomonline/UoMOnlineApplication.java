package lk.ac.mrt.uom.uomonline;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by sineth on 8/12/17.
 */

public class UoMOnlineApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }
}
