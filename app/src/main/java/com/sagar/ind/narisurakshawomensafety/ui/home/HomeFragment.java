package com.sagar.ind.narisurakshawomensafety.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.google.android.material.snackbar.Snackbar;
import com.sagar.ind.narisurakshawomensafety.MainActivity;
import com.sagar.ind.narisurakshawomensafety.R;
import com.sagar.ind.narisurakshawomensafety.ui.LocationTracker;
import com.sagar.ind.narisurakshawomensafety.ui.settings.SettingsFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    //private HomeViewModel homeViewModel;
    View viewRoot;
    SharedPreferences shared_Preferences;
    SettingsFragment settingsFragment;
    Switch security_OnOff;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        //homeViewModel =
             //   ViewModelProviders.of(this).get(HomeViewModel.class);

        viewRoot = inflater.inflate(R.layout.fragment_home, container, false);

        security_OnOff = viewRoot.findViewById(R.id.security_on_off);

        ImageButton sendSMS = viewRoot.findViewById(R.id.smsButton);
        sendSMS.setOnClickListener(this);
        ImageButton sendWhatsAppSMS = viewRoot.findViewById(R.id.WhatsAppButton);
        sendWhatsAppSMS.setOnClickListener(this);
        ImageButton callButton = viewRoot.findViewById(R.id.callButton);
        callButton.setOnClickListener(this);

        settingsFragment = new SettingsFragment(getContext());

        //To set the state of switch from shared preferences when app runs
        security_OnOff.setChecked(settingsFragment.checkSecurity());

        if (security_OnOff != null)
        {
            security_OnOff.setOnCheckedChangeListener(this);
        }
        return viewRoot;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Context context = getActivity().getApplicationContext();
        shared_Preferences = context.getSharedPreferences(settingsFragment.MyPreferencesKey, 0);
        SharedPreferences.Editor editor = shared_Preferences.edit();

        //To Save the state of switch in shared preferences when switch is clicked
        if (isChecked) {
            editor.putBoolean(settingsFragment.security_OnOffKey, true);

            Snackbar.make(viewRoot, "Security turned ON", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

           // Toast.makeText(getActivity(), "Security turned ON", Toast.LENGTH_SHORT).show();
        } else {
            editor.putBoolean(settingsFragment.security_OnOffKey, false);
            Snackbar.make(viewRoot, "Security turned OFF", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        editor.commit();
    }


    /* Function Name : WhatsAppShare(View view)
    Usage : Send Message through WhatsApp through intent
  */
    public int whatsAppShare()
    {
       final Context context = getActivity().getApplicationContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            String[] Permissions={Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this.getActivity(), Permissions,MainActivity.getInstance().PermissionAll );
                return 0;
        }

            LocationTracker locationTracker = new LocationTracker(context);
            locationTracker.get_Location();

            shared_Preferences = context.getSharedPreferences(settingsFragment.MyPreferencesKey, 0);

            String Message = shared_Preferences.getString(settingsFragment.Msgs, null);
        final String pContact = shared_Preferences.getString(settingsFragment.pContactKey, null);

            if (settingsFragment.checkSecurity())
            {
                if (shared_Preferences.contains(SettingsFragment.pContactKey) || shared_Preferences.contains(SettingsFragment.sContactKey)) {
                    if (shared_Preferences.contains(settingsFragment.Msgs)) {
                        if (shared_Preferences.contains(SettingsFragment.pContactKey)) {

                            String url_With_Prefix = "";

                            if (!locationTracker.is_GPS_Enabled) {
                                MainActivity.getInstance().enableGPS();
                                return 0;
                            }

                            String stringLatitude = String.valueOf(locationTracker.myLatitude);
                            String stringLongitude = String.valueOf(locationTracker.myLongitude);

                            if (stringLatitude.equals("0.0") && stringLongitude.equals("0.0")) {
                                System.out.println("whatsappshare return AaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAA");
                                return whatsAppShare();
                            }

                            url_With_Prefix = " and I am at https://www.google.com/maps/search/?api=1&query=" + stringLatitude + "," + stringLongitude;
                            if (pContact != null && !pContact.isEmpty() && TextUtils.isDigitsOnly(pContact) && pContact.length() == 10) {
                                Message = Message + url_With_Prefix;

                                try {
                                    //WhatsApp 'Click To Chat' feature
                                    String url = "https://wa.me/+91" + pContact + "?text=" + URLEncoder.encode(Message, "UTF-8");

                                    //Intent of WhatsApp to access it
                                    Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW);

                                    whatsAppIntent.setData(Uri.parse(url));

                                    startActivity(Intent.createChooser(whatsAppIntent, "Share with...."));

                                } catch (UnsupportedEncodingException e) {
                                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Please setup Primary Contact",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(context, "Please setup Primary Contact",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context,
                                "You haven't setup any Emergency Message",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(viewRoot, "Please Configure contact details", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            } else {
                Snackbar.make(viewRoot, "Your Security is OFF", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }

        return 0;
    }

     /*
       Function Name : send_Message ()
       Usage : Send SMS to configured Number with GPS Location when it is turned on
     */
    public int send_Message()
    {   System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        final Context context = getActivity().getApplicationContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            String[] Permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS};

            ActivityCompat.requestPermissions(this.getActivity(),Permissions,
                    MainActivity.getInstance().PermissionAll);

            return 0;
        }
            Handler handler = new Handler();

            LocationTracker locationTracker = new LocationTracker(context);
            locationTracker.get_Location();

            shared_Preferences = context.getSharedPreferences(settingsFragment.MyPreferencesKey,0);
            SmsManager smsManager = SmsManager.getDefault();
            String Message = shared_Preferences.getString(settingsFragment.Msgs, null);
            final String pContact = shared_Preferences.getString(settingsFragment.pContactKey, null);
            final String sContact = shared_Preferences.getString(settingsFragment.sContactKey, null);

            if (settingsFragment.checkSecurity())
            {
                if (shared_Preferences.contains(SettingsFragment.pContactKey) || shared_Preferences.contains(SettingsFragment.sContactKey))
                {
                    if (shared_Preferences.contains(SettingsFragment.Msgs))
                    {
                        if (shared_Preferences.contains(SettingsFragment.pContactKey))
                        {
                            String url_With_Prefix = "";

                            if(!locationTracker.is_GPS_Enabled)
                            {
                                MainActivity.getInstance().enableGPS();
                                return 0;

                            }

                            String stringLatitude = String.valueOf(locationTracker.myLatitude);
                            String stringLongitude = String.valueOf(locationTracker.myLongitude);

                            if(stringLatitude.equals("0.0") && stringLongitude.equals("0.0")){
                                return send_Message();
                            }


                            url_With_Prefix = " and I am at https://www.google.com/maps/search/?api=1&query=" + stringLatitude + "," + stringLongitude;

                            if (pContact != null && !pContact.isEmpty() && TextUtils.isDigitsOnly(pContact) && pContact.length() == 10)
                            {
                                Message = Message + url_With_Prefix;

                                smsManager.sendTextMessage(pContact, null, Message, null, null);

                                Toast.makeText(context,"SOS Emergency sent to: " + pContact,Toast.LENGTH_SHORT).show();


                                if (sContact != null && !sContact.isEmpty() && TextUtils.isDigitsOnly(sContact) && sContact.length() == 10)
                                {

                                    String url = (pContact != null && !pContact.isEmpty() && TextUtils.isDigitsOnly(pContact)
                                            && pContact.length() == 10) ? "" : url_With_Prefix;

                                    Message = Message + url;
                                    smsManager.sendTextMessage(sContact, null, Message, null, null);


                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"SOS Emergency sent to: " + sContact,Toast.LENGTH_SHORT).show();
                                        }
                                    },2000);

                                }

                            } else {
                                Toast.makeText(context,
                                        "Please setup Primary Contact",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(context,
                                    "Please setup Primary Contact",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context,
                                "You haven't setup any Emergency Message",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(viewRoot, "Please Configure contact details", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            } else {
                Snackbar.make(viewRoot, "Your Security is OFF", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }

        return 0;
    }

    public int callingFunction()
    {
        final Context context = getActivity().getApplicationContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            String[] Permissions={Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this.getActivity(), Permissions,MainActivity.getInstance().PermissionAll );
            return 0;
        }

            if (settingsFragment.checkSecurity())
            {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                if (shared_Preferences.contains(SettingsFragment.pContactKey) || shared_Preferences.contains(SettingsFragment.sContactKey)) {
                    shared_Preferences = context.getSharedPreferences(SettingsFragment.MyPreferencesKey, 0);
                    String s1 = shared_Preferences.getString(SettingsFragment.pContactKey, null);
                    String s2 = shared_Preferences.getString(SettingsFragment.sContactKey, null);


                    assert s1 != null;
                    if (shared_Preferences.contains(SettingsFragment.pContactKey) && TextUtils.isDigitsOnly(s1) && s1.length() == 10) {
                        callIntent.setData(Uri.parse("tel: " + s1));
                        startActivity(callIntent);

                    } else if (shared_Preferences.contains(SettingsFragment.sContactKey) && TextUtils.isDigitsOnly(s2) && s2.length() == 10) {
                        callIntent.setData(Uri.parse("tel: " + s2));
                        startActivity(callIntent);

                    } else {
                        Toast.makeText(context, "Please setup Primary Contact", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(viewRoot, "Please Configure contact details", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            } else {
                Snackbar.make(viewRoot, "Your Security is OFF", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                }
            return 0;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())

        {
            case R.id.smsButton :
                send_Message();
                break;
            case R.id.WhatsAppButton :
                whatsAppShare();
                break;
            case R.id.callButton :
                callingFunction();
                break;
        }

    }
}