package gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class CommonUtilities {
	

    // Google project id
    public static final String SENDER_ID = "834527091831";

    /**
     * Tag used on log messages.
     */


    public static final String DISPLAY_MESSAGE_ACTION =
            "com.csharp.solutions.validations.DISPLAY_MESSAGE";


    public static final String DISPLAY_NOTIFICATION_ACTION =
            "com.csharp.solutions.validations.NOTIFICATION_MESSAGE";

    public static final String TRIGGER_NFC_ACTION =
            "com.csharp.solutions.validations.NFC_TRIGGER";

    public static final String CANCEL_PROGRESS_DIALOG =
            "com.csharp.solutions.validations.CANCEL_PROGRESS_DIALOG";

    public static final String EXTRA_MESSAGE = "message";

    public static final String NOTIFICATION_MESSAGE = "notification_message";

    public static final String SHOW_PROGRESS_DIALOG = "show_progress_dialog";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
   public static void displayMessage(Context context, String message) {

        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public static void displayNotification(Context context, String message) {

        Intent intent = new Intent(DISPLAY_NOTIFICATION_ACTION);
        intent.putExtra(NOTIFICATION_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}
