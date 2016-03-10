/*
 * Copyright (c) 2013-2015 by appPlant UG. All rights reserved.
 *
 * @APPPLANT_LICENSE_HEADER_START@
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apache License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://opensource.org/licenses/Apache-2.0/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 *
 * @APPPLANT_LICENSE_HEADER_END@
 */

package de.appplant.cordova.plugin.localnotification;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.lang.StringBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import de.appplant.cordova.plugin.notification.Builder;
import de.appplant.cordova.plugin.notification.Notification;

/**
 * The alarm receiver is triggered when a scheduled alarm is fired. This class
 * reads the information in the intent and displays this information in the
 * Android notification bar. The notification uses the default notification
 * sound and it vibrates the phone.
 */
public class TriggerReceiver extends de.appplant.cordova.plugin.notification.TriggerReceiver {

    /**
     * Called when a local notification was triggered. Does present the local
     * notification, re-schedule the alarm if necessary and fire trigger event.
     *
     * @param notification
     *      Wrapper around the local notification
     * @param updated
     *      If an update has triggered or the original
     */
    @Override
    public void onTrigger (Notification notification, boolean updated) {
        if (!updated) {
            LocalNotification.fireEvent("trigger", notification);
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL("http://mobile.hc-sc.gc.ca/recallsv2/response.js");
            URLConnection urlConnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            // JSONObject json = new JSONObject(sb.toString());
            // conditional = json.getString("message");
            String conditional = sb.toString();

            if(conditional == "yes"){
                super.onTrigger(notification, updated);
            }

            // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
            //     .setTitle("Received web call")
            //     .setMessage(conditional)
            //     .setCancelable(false)
            //     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //         @Override
            //         public void onClick(DialogInterface dialog, int which) {
            //             // Whatever...
            //         }
            //     })
            //     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            //         @Override
            //         public void onClick(DialogInterface dialog, int which) {
            //             // Whatever...
            //         }
            //     })
            //     .create()
            //     .show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Build notification specified by options.
     *
     * @param builder
     *      Notification builder
     */
    @Override
    public Notification buildNotification (Builder builder) {
        return builder
                .setTriggerReceiver(TriggerReceiver.class)
                .setClickActivity(ClickActivity.class)
                .setClearReceiver(ClearReceiver.class)
                .build();
    }

}
