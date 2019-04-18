package com.flyaudio.flyradioonline.task.play.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.util.Flog;

public class FmBootReceiver extends BroadcastReceiver {
    private static final String TAG = "FmBootReceiver";
    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BOOT_COMPLETE.equals(intent.getAction())){
            Flog.e(TAG, "FmBootReceiver//我开机了");
            Intent intentService = new Intent(context, FmVoiceService.class);
            context.startService(intentService);
        }
    }
}
