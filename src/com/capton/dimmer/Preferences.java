package com.capton.dimmer;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class Preferences extends PreferenceActivity {

	SharedPreferences configPrefFile;
	SharedPreferences.Editor prefEditor;
	private Calendar cal;
	private int day, month, year, minute, hour, intensity;
	private boolean checkStatus;
	private String $btDeviceName;
	Preference datePrf;
	Preference timePrf;
	ListPreference intensityPrf;
	CheckBoxPreference checkPrf;
	Preference btDevicePrf;
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(getBaseContext(), R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
		datePrf = (Preference) findPreference("pref_date");
		timePrf = (Preference) findPreference("pref_time");
		btDevicePrf = findPreference("pref_btdevice");
		intensityPrf = (ListPreference) findPreference("pref_intensity");
		checkPrf = (CheckBoxPreference) findPreference("pref_activate_check");

		defaultPreferences();
		checkPrf.setChecked(checkStatus);
		datePrf.setSummary(day + "/" + (month + 1) + "/" + year);
		displayTime(hour, minute);
		if(intensity==0)
			intensityPrf.setSummary("Off");
		else
			intensityPrf.setSummary("level " + intensity);
		
		
		configPrefFile = getSharedPreferences("dimmer.conf", 0);
		prefEditor = configPrefFile.edit();

		// this.setTheme(R.style.Holo);
		datePrf.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});
		timePrf.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG_ID);
				return false;
			}
		});
		intensityPrf.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if(newValue.toString().equals( "0") )
					intensityPrf.setSummary("Off");
				else
					intensityPrf.setSummary("level " + newValue.toString());
				return false;
			}
		});
		btDevicePrf.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				final EditText txtUrl = new EditText(Preferences.this);
				// Set the default text to a link of the Queen
				txtUrl.setHint("enter name of bluetooth sensor");
				final AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
				builder.setMessage("Enter Sensor Name");
				builder.setCancelable(false);
				builder.setView(txtUrl);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichBtn) {
						$btDeviceName = txtUrl.getText().toString();
						if ($btDeviceName == "")
							btDevicePrf.setSummary("Not Settt");
						else btDevicePrf.setSummary($btDeviceName);
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichBtn) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				return false;
			}
		});
		
	}

	private boolean loadPreferences() {

		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		savePreferences();
	}

	private boolean savePreferences() {
		prefEditor.putString("day", Integer.toString(day));
		prefEditor.putString("month", Integer.toString(month));
		prefEditor.putString("year", Integer.toString(year));
		prefEditor.putString("hour", Integer.toString(hour));
		prefEditor.putString("minute", Integer.toString(minute));
		prefEditor.putBoolean("pref_activate_check", checkStatus);
		prefEditor.putString("pref_intensity", Integer.toString(intensity));
		prefEditor.putString("pref_btdevice", $btDeviceName);
		prefEditor.commit();
		return true;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDatePickerListener, year, month,
					day);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, hour, minute,
					false);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDatePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			datePrf.setSummary(selectedDay + "/" + (selectedMonth + 1) + "/"
					+ selectedYear);
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		// the callback received when the user "sets" the TimePickerDialog in
		// the dialog
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			displayTime(hourOfDay, min);

		}
	};

	private boolean defaultPreferences() {
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		checkStatus = false;
		intensity = 0;
		$btDeviceName = new String();

		return true;
	}

	private void displayTime(int hourOfDay, int min) {
		hour = hourOfDay;
		minute = min;
		String minStr = String.format("%02d", min);
		if (hour == 0)
			timePrf.setSummary("12:" + minStr + " AM");
		else if (hour == 12)
			timePrf.setSummary(hour + ":" + minStr + " PM");
		else if (hour > 12)
			timePrf.setSummary((hour - 12) + ":" + minStr + " PM");
		else
			timePrf.setSummary(hour + ":" + minStr + " AM");

	}
}
