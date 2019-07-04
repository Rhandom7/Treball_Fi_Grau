package com.example.orientacioeps.estimote;

/*import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentManager extends AppCompatActivity {

    private Context context;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;

    public ProximityContentManager(Context context, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.cloudCredentials = cloudCredentials;
    }

    public void start() {

        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("orientacioeps-1eh")
                .inCustomRange(5.0)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                        for (ProximityZoneContext proximityContext : contexts) {
                            String title = proximityContext.getAttachments().get("orientacioeps-1eh/title");
                            if (title == null) {
                                title = "unknown";
                            }

                            Toast toast = Toast.makeText(context, "Aprop de " + title, Toast.LENGTH_SHORT);
                            toast.show();
                            Log.d("Aprop", title);
                            Log.d("Aprop", proximityContext.getDeviceId());
                        }
                        return null;
                    }
                })
                .build();

        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }
}*/

