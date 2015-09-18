package ensurance.yoav.smsensurance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void switchInsurance_clicked(View v)
    {
        Switch switchInsurance = (Switch)v;
        String message = null;

        if (switchInsurance.isChecked()) {
            message = "Turning on...";
            sendInsuranceSms(true);

            Intent intent = new Intent(getApplicationContext(), DriveAwareService.class);
            startService(intent);
        }
        else {
            message = "Turning off...";
            sendInsuranceSms(false);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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
        try {
            smsManager.sendTextMessage(smsNumber, null, message, null, null);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getApplicationContext(), "Invalid SMS Parameters.", Toast.LENGTH_SHORT).show();
        }
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
