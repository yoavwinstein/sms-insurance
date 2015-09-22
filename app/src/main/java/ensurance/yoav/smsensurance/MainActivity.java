package ensurance.yoav.smsensurance;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    boolean toggle = false;
    final int transitionTime = 500;

    public void mainActivity_clicked(View v) {

        if (toggle) {
            // Toggling off...
            try {
                toggleInsuranceOff();
                guiToggleOff();
                removeInsuranceNotification();
                toggle = !toggle;
            } catch (IllegalArgumentException ex) {
                askUserStartSettings();
            }
        } else {
            // Toggling on...
            try {
                toggleInsuranceOn();
                guiToggleOn();
                addInsuranceNotification();
                toggle = !toggle;
            } catch (IllegalArgumentException ex) {
                askUserStartSettings();
                addInsuranceNotification();
            }
        }
    }

    private void removeInsuranceNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.id.insurance_active_notification);

    }

    private void addInsuranceNotification() {
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setContentText(getString(R.string.insurance_active_notification_text));
        mBuilder.setContentTitle(getString(R.string.insurance_active_notification_title));
        mBuilder.setAutoCancel(false);
        mBuilder.setDefaults(Notification.FLAG_NO_CLEAR);
        mBuilder.setOngoing(true);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.insurance_active_notification, mBuilder.build());
    }

    private void guiToggleOn() {
        View v = findViewById(R.id.main_activity);
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        transition.reverseTransition(transitionTime);
        ((TextView)findViewById(R.id.textViewToggle)).setText(R.string.end_insurance);
    }

    private void guiToggleOff() {
        View v = findViewById(R.id.main_activity);
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        transition.reverseTransition(transitionTime);
        ((TextView) findViewById(R.id.textViewToggle)).setText(R.string.start_insurance);
    }

    private void toggleInsuranceOff() {
        sendInsuranceSms(false);
    }

    private void toggleInsuranceOn() {
        sendInsuranceSms(true);
    }

    private void sendInsuranceSms(boolean startMessage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String smsNumber = sharedPreferences.getString(getString(R.string.key_insurance_number), "");
        String message = null;

        if (startMessage) {
            message = sharedPreferences.getString(getString(R.string.key_insurance_start_message), "");
        }else {
            message = sharedPreferences.getString(getString(R.string.key_insurance_stop_message), "");
        }

        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(smsNumber, null, message, null, null);
    }

    private void askUserStartSettings() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_for_settings_dialog_after_sms_error);
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
