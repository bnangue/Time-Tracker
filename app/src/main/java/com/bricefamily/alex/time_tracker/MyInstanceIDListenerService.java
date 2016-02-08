package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;
/**
 * Created by bricenangue on 07/02/16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

 @Override
 public void onTokenRefresh() {

 Intent intent = new Intent(this, RegistrationIntentService.class);
 startService(intent);
 }

}