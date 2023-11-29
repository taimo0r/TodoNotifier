package com.taimoor.TodoNotifier.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.taimoor.TodoNotifier.Activities.MainActivity;
import com.taimoor.TodoNotifier.Model.Priority;
import com.taimoor.TodoNotifier.Model.SharedViewModel;
import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.taimoor.TodoNotifier.Notification.NotifyWorker;
import com.taimoor.TodoNotifier.R;
import com.taimoor.TodoNotifier.Utilities.Converter;
import com.taimoor.TodoNotifier.Utilities.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BottomSheetFragment extends BottomSheetDialogFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private EditText enterTodo;
    private Button saveButton;
    private ImageButton uploadImage, speechInput, usersLocation;
    private RadioGroup priorityRadioGroup, repeatingRadioGroup;
    private RadioButton selectedRadioButton, selectedRepeatRadio;
    private int selectedButtonId, repeatSelectedID, notificationKey;
    Spinner repeatingSpinner;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    private EditText date, time;
    String selectedTime, selectedDate, task, address, workTag;
    String spinnerItem;
    Chip locationChip;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    TimePickerDialog tpd;
    DatePickerDialog dpd;
    private SharedViewModel sharedViewModel;
    private boolean isEdit, repeating;
    private Priority priority;
    RadioButton radioButtonHigh, radioButtonLow, radioButtonMedium, oneTimeRadioBtn, dailyRadioBtn;
    Intent audioData;
    ArrayList<String> audioResults;
    ConstraintLayout dateLayout, timeLayout;
    ImageView bigImage;

    Bitmap bitmap = null;
    int Select_Picture = 200;
    byte[] imageSources;
    Context applicationContext;
    Random random = new Random();
    AlertDialog alertDialog;
    private ActivityResultLauncher<Intent> getPictures;
    private ActivityResultLauncher<Intent> recognizeAudio;

    FusedLocationProviderClient locationProviderClient;

    public BottomSheetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        applicationContext = MainActivity.GetContextOfActivity();

        //To get current location of user
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        //Creating Hooks for the BottomSheet fragment XML
        enterTodo = view.findViewById(R.id.newTask_enter_todo);
        date = view.findViewById(R.id.enter_date);
        time = view.findViewById(R.id.enter_time);
        priorityRadioGroup = view.findViewById(R.id.newTask_radioGroup_priority);
        radioButtonHigh = view.findViewById(R.id.newTask_radioButton_high);
        radioButtonLow = view.findViewById(R.id.newTask_radioButton_low);
        radioButtonMedium = view.findViewById(R.id.newTask_radioButton_med);
        repeatingRadioGroup = view.findViewById(R.id.repeating_radioGroup);
        oneTimeRadioBtn = view.findViewById(R.id.one_time_radioBtn);
        dailyRadioBtn = view.findViewById(R.id.daily_radioBtn);
        repeatingSpinner = view.findViewById(R.id.repetition_spinner);
        uploadImage = view.findViewById(R.id.newTask_image_button);
        speechInput = view.findViewById(R.id.newTask_speech);
        usersLocation = view.findViewById(R.id.newTask_location_button);
        saveButton = view.findViewById(R.id.SaveBtn);
        locationChip = view.findViewById(R.id.newTask_locationChip);
        dateLayout = view.findViewById(R.id.dateLayout);
        timeLayout = view.findViewById(R.id.timeLayout);
        bigImage = view.findViewById(R.id.imageView);


        //To get the selected item of the spinner
        repeatingSpinner.setOnItemSelectedListener(this);

        //Setting up data inside the spinner using a String Array
        String[] repeating_time = getResources().getStringArray(R.array.repeating_time);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, repeating_time);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatingSpinner.setAdapter(adapter);

        dailyRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatingSpinner.setVisibility(View.VISIBLE);
            }
        });

        //Initializing TimePicker Dialog instance
        tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(BottomSheetFragment.this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                false);

        //Initializing Date Picker Dialog Instance
        dpd = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        //Registering activity for getting picture
        getPictures = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uri);
                            bigImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        //Registering Activity for getting Audio
        recognizeAudio = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    audioData = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK && audioData != null) {
                        audioResults = audioData.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        enterTodo.setText(audioResults.get(0));
                    }
                });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Getting data from sharedView model to update the Task
        if (sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();

            //Setting up audio data
            if (audioData != null) {
                enterTodo.setText(audioResults.get(0));
            } else {
                enterTodo.setText(task.getTask());
            }

            //Setting up Priority
            Priority imp = task.getPriority();
            int importance;
            if (imp == Priority.HIGH) {
                importance = 1;
            } else if (imp == Priority.MEDIUM) {
                importance = 0;
            } else {
                importance = -1;
            }

            if (importance == 1) {
                priority = Priority.HIGH;
                radioButtonHigh.setChecked(true);
            } else if (importance == 0) {
                priority = Priority.MEDIUM;
                radioButtonMedium.setChecked(true);
            } else if (importance == -1) {
                priority = Priority.LOW;
                radioButtonLow.setChecked(true);
            }

            //Setting up image
            if (bitmap == null) {
                imageSources = task.getImages();
                bitmap = Converter.getBitmapFromStr(imageSources);
                bigImage.setImageBitmap(bitmap);
            }
            //Setting  other data
            locationChip.setText(task.getAddress());
            String formattedDate = Utils.formatDate(task.getDueDate());
            selectedDate = formattedDate;
            date.setText(formattedDate);
        }

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ViewModel will be used to get the task data in the fragment for updating the task
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //Getting audio into text
        speechInput.setOnClickListener(v -> getTextFromSpeech());

        //Show date picker dialog on Click
        date.setOnClickListener(v -> dpd.show(getFragmentManager(), "DatePickerDialog"));

        //Show timePicker Dialog on click
        time.setOnClickListener(v -> tpd.show(getFragmentManager(), "TimePickerDialog"));


        //Setting up Priority Buttons
        priorityRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedButtonId = checkedId;
            selectedRadioButton = view.findViewById(selectedButtonId);
            if (selectedRadioButton.getId() == R.id.newTask_radioButton_high) {
                priority = Priority.HIGH;
            } else if (selectedRadioButton.getId() == R.id.newTask_radioButton_med) {
                priority = Priority.MEDIUM;
            } else if (selectedRadioButton.getId() == R.id.newTask_radioButton_low) {
                priority = Priority.LOW;
            } else {
                priority = Priority.LOW;
            }
        });


        //Checking which radio button is clicked repeat daily or One time
        repeatingRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            repeatSelectedID = checkedId;
            selectedRepeatRadio = view.findViewById(repeatSelectedID);
            if (selectedRepeatRadio.getId() == R.id.daily_radioBtn) {
                repeatingSpinner.setVisibility(View.VISIBLE);
                repeating = true;
            } else {
                repeatingSpinner.setVisibility(View.GONE);
                spinnerItem = null;
                repeating = false;
            }
        });

        uploadImage.setOnClickListener(v -> loadImages());

        usersLocation.setOnClickListener(v -> selectLocationDialog());

        saveButton.setOnClickListener(v -> saveData());
    }

    //Creating a dialog to choose to enter current location or add location manually
    private void selectLocationDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.location_dialog, null);
        LinearLayout currLocation = (LinearLayout) dialogView.findViewById(R.id.current_location_view);
        LinearLayout manualLocation = (LinearLayout) dialogView.findViewById(R.id.manual_location_view);
        LinearLayout addManualLocation = (LinearLayout) dialogView.findViewById(R.id.add_manual_location_view);
        RelativeLayout addLocationView = (RelativeLayout) dialogView.findViewById(R.id.add_location_view);
        EditText locationEdit = (EditText) dialogView.findViewById(R.id.locationEditText);
        Button OkBtn = (Button) dialogView.findViewById(R.id.ok_btn);
        TextView choose = (TextView) dialogView.findViewById(R.id.choose_txt);

        alert.setView(dialogView);

        alertDialog = alert.create();

        //When clicked on current location button
        currLocation.setOnClickListener(v -> getLocation());

        //When Clicked on Manual location button
        manualLocation.setOnClickListener(v -> {
            currLocation.setVisibility(View.GONE);
            manualLocation.setVisibility(View.GONE);
            choose.setVisibility(View.GONE);
            addManualLocation.setVisibility(View.VISIBLE);
            addLocationView.setVisibility(View.VISIBLE);


            OkBtn.setOnClickListener(v1 -> {
                address = locationEdit.getText().toString();
                locationChip.setText(address);
                alertDialog.dismiss();
            });

        });

        alertDialog.show();

    }


    //Saving the data to Room Database
    private void saveData() {
        task = enterTodo.getText().toString().trim();
        //Displaying a toast if any of the required data is missing
        if (TextUtils.isEmpty(task)) {
            Toast.makeText(applicationContext, R.string.empty_field, Toast.LENGTH_SHORT).show();
        } else if (selectedDate == null) {
            Toast.makeText(applicationContext, "Date not Entered", Toast.LENGTH_SHORT).show();
        } else if (selectedTime == null) {
            Toast.makeText(applicationContext, "Time not Set", Toast.LENGTH_SHORT).show();
        } else if (priority == null) {
            Toast.makeText(applicationContext, "Priority not Selected", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            Toast.makeText(applicationContext, "Image not Selected", Toast.LENGTH_SHORT).show();
        } else if (address == null) {
            Toast.makeText(applicationContext, "Location not Selected", Toast.LENGTH_SHORT).show();
        }
        if (selectedDate != null && dueDate == null) {
            Toast.makeText(applicationContext, "Please Select date again", Toast.LENGTH_SHORT).show();
        }

        //generating a random key for notifications
        notificationKey = random.nextInt(9999 - 1000) + 1000;
        //Generating a random String tag for workManager
        workTag = getRandomString(8);

        //If any of the data is missing it will not be saved to room database
        if (!TextUtils.isEmpty(task) && dueDate != null && priority != null && bitmap != null && selectedTime != null) {
            //Converting bitmap to string resource using a user defined function in Converter.class
            imageSources = Converter.getStringFromBitmap(bitmap);
            //Creating a task instance and initializing it with the data that user entered
            Task myTask = new Task(task, priority, dueDate, Calendar.getInstance().getTime(), false, imageSources, address, notificationKey, workTag);

            //Condition to check whether to enter new task or update a previous one
            if (isEdit) {
                Task updateTask = sharedViewModel.getSelectedItem().getValue();
                updateTask.setTask(task);
                updateTask.setDateCreated(Calendar.getInstance().getTime());
                updateTask.setPriority(priority);
                updateTask.setDueDate(dueDate);
                updateTask.setImages(imageSources);
                TaskViewModel.update(updateTask);
                if (priority == Priority.HIGH) {
                    SetNotifications_High();
                } else if (priority == Priority.MEDIUM) {
                    SetNotifications_Medium();
                } else {
                    SetNotifications_Low();
                }
            } else {
                TaskViewModel.insert(myTask);
                if (priority == Priority.HIGH) {
                    SetNotifications_High();
                } else if (priority == Priority.MEDIUM) {
                    SetNotifications_Medium();
                } else {
                    SetNotifications_Low();
                }
            }
            enterTodo.setText("");
            if (this.isVisible()) {
                this.dismiss();
            }
        }
    }

    //Get current location of user
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        address = addressList.get(0).getSubLocality() + ", " + addressList.get(0).getLocality();
                        locationChip.setText(address);
                        alertDialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(applicationContext, "Location null", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        } else
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    //Get text from audio
    private void getTextFromSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            recognizeAudio.launch(intent);
        } else {
            Toast.makeText(applicationContext, "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    //Load the image from camera or gallery using an ImagePicker Library
    private void loadImages() {
        ImagePicker.with(requireActivity())
                .crop()                                  //Crop image(Optional), Check Customization for more option
                .compress(1024)                          //Final image size will be less than 10 KB(Optional)
                .maxResultSize(800, 600)     //Final image resolution will be less than 800 x 600(Optional)
                .createIntent(intent -> {
                    getPictures.launch(intent);
                    return null;
                });
    }

    //Getting the selected time from Time picker dialog
    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        selectedTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        time.setText(selectedTime);
    }

    //Getting the selected date from Date picker dialog
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dueDate = calendar.getTime();

        selectedDate = Utils.formatDate(dueDate);
        date.setText(selectedDate);
    }


    //Method to create a random string (for work tag)
    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    //Scheduling notification according to priority
    public void SetNotifications_Low() {
        String msg = enterTodo.getText().toString();

        int repeatingTime = 0;
        int importance = -1;
        byte[] largeIcon = Converter.getStringFromBitmap(bitmap);


        //input data to pass to notification
        Data inputData = new Data.Builder()
                .putString("Msg", msg)
                .putInt("priority", importance)
                .putInt("key", notificationKey)
                //          .putByteArray("pic", largeIcon)
                .build();

        //Calculating time difference between now and selected time
        Calendar today = Calendar.getInstance();
        long diff = calendar.getTimeInMillis() - today.getTimeInMillis();

        //Condition to check if user selected the repeating notification or not and scheduling notification accordingly
        if (!repeating) {
            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build();
            WorkManager.getInstance(requireContext()).enqueue(notificationWork);
        } else {

            //Get the selected item from spinner and set time accordingly
            switch (spinnerItem) {
                case "15 Minutes":
                    repeatingTime = 15;
                    break;
                case "30 Minutes":
                    repeatingTime = 30;
                    break;
                case "Hourly":
                    repeatingTime = 1;
                    break;
                case "Daily":
                    repeatingTime = 24;
                    break;
                case "Weekly":
                    repeatingTime = 168;
                    break;
                case "Monthly":
                    repeatingTime = 720;
                    break;
                default:
                    repeatingTime = 24;
                    break;
            }

            if (repeatingTime == 15 || repeatingTime == 30) {
                PeriodicWorkRequest periodicSyncDataWork =
                        new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.MINUTES)
                                .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                                .setInputData(inputData)
                                .addTag("TAG_SYNC_DATA")
                                // setting a backoff on case the work needs to retry
                                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            } else {
                PeriodicWorkRequest periodicSyncDataWork =
                        new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.HOURS)
                                .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                                .setInputData(inputData)
                                .addTag("TAG_SYNC_DATA")
                                // setting a backoff on case the work needs to retry
                                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            }
        }

    }


    public void SetNotifications_Medium() {

        String msg = enterTodo.getText().toString();
        int importance = 0;
        int repeatingTime = 0;
        final String workTag = "notificationWork";
        byte[] largeIcon = Converter.getStringFromBitmap(bitmap);

        Data inputData = new Data.Builder()
                .putString("Msg", msg)
                .putInt("priority", importance)
                .putInt("key", notificationKey)
                //        .putByteArray("pic", largeIcon)
                .build();


        Calendar today = Calendar.getInstance();
        long diff = calendar.getTimeInMillis() - today.getTimeInMillis();

        if (!repeating) {
            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(notificationWork);
        } else {

            switch (spinnerItem) {
                case "15 Minutes":
                    repeatingTime = 15;
                    break;
                case "30 Minutes":
                    repeatingTime = 30;
                    break;
                case "Hourly":
                    repeatingTime = 1;
                    break;
                case "Daily":
                    repeatingTime = 24;
                    break;
                case "Weekly":
                    repeatingTime = 168;
                    break;
                case "Monthly":
                    repeatingTime = 720;
                    break;
                default:
                    repeatingTime = 24;
                    break;
            }

            if (repeatingTime == 15 || repeatingTime == 30) {
                PeriodicWorkRequest periodicSyncDataWork = new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.MINUTES)
                        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag("TAG_SYNC_DATA")
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            } else {
                PeriodicWorkRequest periodicSyncDataWork = new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.HOURS)
                        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag("TAG_SYNC_DATA")
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();
                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            }
        }
    }

    public void SetNotifications_High() {
        String msg = enterTodo.getText().toString();
        int importance = 1;
        int repeatingTime = 0;
        byte[] largeIcon = Converter.getStringFromBitmap(bitmap);
        final String workTag = "notificationWork";

        Data inputData = new Data.Builder()
                .putString("Msg", msg)
                .putInt("priority", importance)
                .putInt("key", notificationKey)
                //        .putByteArray("pic", largeIcon)
                .build();

        Calendar today = Calendar.getInstance();
        long diff = calendar.getTimeInMillis() - today.getTimeInMillis();


        if (!repeating) {

            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(notificationWork);
        } else {
            switch (spinnerItem) {
                case "15 Minutes":
                    repeatingTime = 15;
                    break;
                case "30 Minutes":
                    repeatingTime = 30;
                    break;
                case "Hourly":
                    repeatingTime = 1;
                    break;
                case "Daily":
                    repeatingTime = 24;
                    break;
                case "Weekly":
                    repeatingTime = 168;
                    break;
                case "Monthly":
                    repeatingTime = 720;
                    break;
                default:
                    repeatingTime = 24;
                    break;
            }

            if (repeatingTime == 15 || repeatingTime == 30) {
                PeriodicWorkRequest periodicSyncDataWork =
                        new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.MINUTES)
                                .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                                .setInputData(inputData)
                                .addTag("TAG_SYNC_DATA")
                                // setting a backoff on case the work needs to retry
                                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            } else {
                PeriodicWorkRequest periodicSyncDataWork =
                        new PeriodicWorkRequest.Builder(NotifyWorker.class, repeatingTime, TimeUnit.HOURS)
                                .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                                .setInputData(inputData)
                                .addTag("TAG_SYNC_DATA")
                                // setting a backoff on case the work needs to retry
                                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(requireContext()).enqueue(periodicSyncDataWork);
            }
        }

    }

    //Getting the selected item of spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.repetition_spinner) {
            spinnerItem = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}