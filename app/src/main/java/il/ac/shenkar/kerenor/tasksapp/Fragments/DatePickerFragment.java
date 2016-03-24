package il.ac.shenkar.kerenor.tasksapp.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import il.ac.shenkar.kerenor.tasksapp.R;

import java.util.Calendar;

/**
 * DatePickerFragment.java - a class that control the calender dialog
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener  {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        EditText dueDateField = (EditText)getActivity().findViewById(R.id.due_time_field);
        month++;
        String date = day + "/" + month+ "/" + year;
        dueDateField.setText(date);
    }



}
