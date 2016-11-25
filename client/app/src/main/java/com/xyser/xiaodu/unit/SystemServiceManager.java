package com.xyser.xiaodu.unit;

import android.content.ClipData;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by dingdayu on 2016/11/23.
 */

public class SystemServiceManager {

    public static void setClipboardText(Context context, String msg)
    {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        android.content.ClipboardManager myClipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        ClipData myClip = ClipData.newPlainText("text", msg);
        myClipboard.setPrimaryClip(myClip);
    }

    public static void shortVibrator(Context context)
    {
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         *
         */
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern,-1); //重复两次上面的pattern 如果只想震动一次，index设为-1
    }
}
