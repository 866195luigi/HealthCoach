package com.example.healthcoach.recordingapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;


public class Weight {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;
    private Context context;
    private FitnessOptions fitnessOptions;

    /**
     * Initializes the Weight object and sets up Google Fit for weight tracking.
     * Configures the DataSource and checks for necessary permissions before proceeding.
     *
     * @param context The application's context.
     */

    public Weight(Context context) {
        this.context = context;
        this.fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();
        if (!checkAndRequestPermissions()) {
            return;
        }
        refreshGoogleSignInAccount();
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_WEIGHT)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("WeightRecorderStream")
                .build();
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(aVoid -> Log.d("Weight", "Weight recording started"))
                .addOnFailureListener(e -> Log.e("Weight", "Failed to start weight recording", e));
    }

    public void refreshGoogleSignInAccount() {
        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);
    }

    private boolean isUserSignedIn() {
        refreshGoogleSignInAccount();
        return googleSignInAccount != null && GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions);
    }

    /**
     * Checks if the application has the necessary permissions for weight tracking.
     * If not, requests the permissions from the user.
     *
     * @return True if permissions are granted, false otherwise.
     */

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(context, "android.permission.BODY_SENSORS")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{"android.permission.BODY_SENSORS"},
                    PERMISSION_REQUEST_CODE
            );
            return false;
        }
        return true;
    }

    private boolean hasNecessaryPermissions() {
        return ContextCompat.checkSelfPermission(context, "android.permission.BODY_SENSORS")
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Reads weight data from Google Fit for a specified time range.
     * Checks if the user is signed in and has the necessary permissions before proceeding.
     * On success, returns the data through an OnSuccessListener. On failure, logs an error message.
     *
     * @param startTime The start time of the range in milliseconds.
     * @param endTime The end time of the range in milliseconds.
     * @param onRead The listener to be called upon successful retrieval of the weight data.
     */

    public void readWeightData(long startTime, long endTime, OnSuccessListener<DataReadResponse> onRead) {
        if (!isUserSignedIn()) {
            Log.e("Weight", "User is not signed in. Can't read data.");
            promptSignIn();
            return;
        }

        if (!hasNecessaryPermissions()) {
            Log.e("Weight", "No permissions to read data. Requesting permissions.");
            requestPermissions();
            return;
        }

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(onRead)
                .addOnFailureListener(e -> Log.e("Weight", "Failed to read weight data: " + e.getMessage(), e));
    }

    private void promptSignIn() {
        GoogleSignIn.requestPermissions(
                (Activity) context,
                PERMISSION_REQUEST_CODE,
                googleSignInAccount,
                fitnessOptions
        );
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{"android.permission.BODY_SENSORS"},
                PERMISSION_REQUEST_CODE
        );
    }
}
