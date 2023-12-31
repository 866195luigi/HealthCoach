package com.example.healthcoach.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;



public class ForgotPasswordViewModel extends ViewModel {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeActivityEvent = new MutableLiveData<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getCloseActivityEvent() {
        return closeActivityEvent;
    }

    /**
     * Sends a password reset email to the given email address using Firebase Authentication.
     * Updates LiveData objects to trigger UI events, such as displaying a toast message or closing the activity.
     *
     * @param email The email address to which the password reset email will be sent.
     */
    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        toastMessage.setValue("Password reset email sent");
                        closeActivityEvent.setValue(true);
                    } else {
                        toastMessage.setValue("Failed to send password reset email");
                    }
                });
    }


}
