package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class BodyFat {
    private Context context;
    private GoogleSignInAccount googleSignInAccount;

    public BodyFat(Context context, GoogleSignInAccount account) {
        this.context = context;
        this.googleSignInAccount = account;
    }

    /**
     * Inserts a body fat percentage record into Google Fit for a specified time interval.
     * Creates a DataSource, DataSet, and DataPoint to encapsulate the data.
     *
     * @param bodyFatPercentage The body fat percentage to insert.
     * @param startTime The start time of the record in milliseconds.
     * @param endTime The end time of the record in milliseconds.
     * @return True if the insertion is successful, false otherwise.
     */
    public boolean insertBodyFat(float bodyFatPercentage, long startTime, long endTime) {
        DataSource bodyFatSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet bodyFatDataSet = DataSet.create(bodyFatSource);
        DataPoint bodyFatPoint = bodyFatDataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        bodyFatPoint.getValue(Field.FIELD_PERCENTAGE).setFloat(bodyFatPercentage);
        bodyFatDataSet.add(bodyFatPoint);

        Task<Void> task = Fitness.getHistoryClient(context, googleSignInAccount).insertData(bodyFatDataSet);
        return task.isSuccessful();
    }


    /**
     * Retrieves the latest body fat percentage record for a specified day from Google Fit.
     * Builds a DataReadRequest to query Google Fit and returns the latest value through an OnSuccessListener.
     *
     * @param startTime The start time of the day in milliseconds.
     * @param endTime The end time of the day in milliseconds.
     * @param onSuccessListener The listener to be called upon successful retrieval of the latest body fat percentage.
     */
    public void getLatestBodyFatForDay(long startTime, long endTime, OnSuccessListener<Float> onSuccessListener) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        Task<DataReadResponse> task = Fitness.getHistoryClient(context, googleSignInAccount).readData(readRequest);

        task.addOnSuccessListener(dataReadResponse -> {
            float latestBodyFat = 0;
            long latestTime = 0;
            for (DataSet dataSet : dataReadResponse.getDataSets()) {
                for (DataPoint point : dataSet.getDataPoints()) {
                    long pointEndTime = point.getEndTime(TimeUnit.MILLISECONDS);
                    float bodyFatValue = point.getValue(Field.FIELD_PERCENTAGE).asFloat();

                    if (pointEndTime > latestTime) {
                        latestTime = pointEndTime;
                        latestBodyFat = bodyFatValue;
                    }
                }
            }
            onSuccessListener.onSuccess(latestBodyFat);
        });
    }



}
