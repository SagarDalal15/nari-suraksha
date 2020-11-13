package com.sagar.ind.narisurakshawomensafety.ui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.sagar.ind.narisurakshawomensafety.MainActivity;
import com.sagar.ind.narisurakshawomensafety.R;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public SettingsFragment(){
        //constructor
    }

    //private SettingsFragmentViewModel settingsFragmentViewModel;

    public EditText msg, priContact, secContact;

    public static final String MyPreferencesKey = "SagarPreferences";

    public static final String Msgs = "MessageKey";

    public static final String pContactKey = "pContactKey";

    public static final String sContactKey = "sContactKey";

    public static final String security_OnOffKey = "securityKey";

    SharedPreferences.Editor editor;

    SharedPreferences shared_Preferences;

    View viewRoot;


    //Constructor to get the SharedPreferences with MyPreferences key and Private Mode then saving it
    public SettingsFragment(Context context)
    {
        shared_Preferences = context.getSharedPreferences(MyPreferencesKey, Context.MODE_PRIVATE);
        editor=shared_Preferences.edit();
        editor.apply();

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


       // settingsFragmentViewModel =
               // ViewModelProviders.of(this).get(SettingsFragmentViewModel.class);
        viewRoot = inflater.inflate(R.layout.fragment_settings, container, false);

        msg =  viewRoot.findViewById(R.id.emergencyMessageTextField);
        priContact =  viewRoot.findViewById(R.id.primaryContactTextField);
        secContact =  viewRoot.findViewById(R.id.secondaryContactTextField);
        ImageButton button = viewRoot.findViewById(R.id.smsButton);
        button.setOnClickListener(this);

        Context context = getActivity().getApplicationContext();

        shared_Preferences = context.getSharedPreferences(MyPreferencesKey, 0);

        if(shared_Preferences.contains(Msgs))
        {
            msg.setText(shared_Preferences.getString(Msgs, ""));
        }
        if(shared_Preferences.contains(pContactKey))
        {
            priContact.setText(shared_Preferences.getString(pContactKey, ""));
        }
        if(shared_Preferences.contains(sContactKey))
        {
            secContact.setText(shared_Preferences.getString(sContactKey, ""));
        }


        return viewRoot;

    }



    public void setting_Details(){
        Context context = getActivity().getApplicationContext();
        shared_Preferences = context.getSharedPreferences(MyPreferencesKey, 0);
        editor= shared_Preferences.edit();
        String emergencyMessage = msg.getText().toString();
        String pc = priContact.getText().toString();

        String sc = secContact.getText().toString();

        editor.putString(Msgs, emergencyMessage);
        editor.putString(pContactKey, pc);

        editor.putString(sContactKey, sc);

        editor.commit();


        Snackbar.make(viewRoot, "Details have been saved", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        //Toast.makeText(getActivity(),"Details have been saved", Toast.LENGTH_SHORT).show();
    }

    public boolean checkSecurity ()
    {
        return shared_Preferences.getBoolean(security_OnOffKey, false);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.smsButton:
                setting_Details();
                break;
        }

    }


}