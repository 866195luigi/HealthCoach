package com.example.healthcoach.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.SignUpViewModel;

import java.util.Calendar;


public class SignUpInformationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private SignUpViewModel viewModel;
    private EditText fullNameText, weightInput, heightInput, stepsInput, waterInput, kcalInput;
    private ImageView profilePic;
    private TextView hoverProfilePic;
    private Spinner genderInfo;
    private DatePicker birthdayPicker;
    private Button submitButton;
    private String imageUri;


    /**
     * Initializes the SignUpInfoActivity.
     *
     * - Sets up the view model, UI elements, and event listeners.
     * - Retrieves user information from the ViewModel.
     * - Sets the maximum selectable date for the birth date picker.
     *
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        // Initialize ViewModel and other functionalities
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.retriveUserInformation();

        // Initialize UI elements and listeners
        inizialiseUI();
        setupListeners();

        // Add the code here to set the maximum date for the DatePicker
        DatePicker birthDatePicker = findViewById(R.id.birthDatePicker); // Make sure this ID matches the one in your XML

        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Subtract 18 years from the current year
        calendar.add(Calendar.YEAR, -18);

        // Set the maximum selectable date for the DatePicker
        birthDatePicker.setMaxDate(calendar.getTimeInMillis());
    }

    /**
     * Handles the results of external activities initiated from this activity.
     *
     * - Processes the image selected for the profile picture.
     *
     * @param requestCode The request code initially supplied.
     * @param resultCode  The result code returned.
     * @param data        Additional data returned from the activity.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                // Ottieni l'URI dell'immagine selezionata
                Uri selectedImageUri = data.getData();

                profilePic.setImageURI(selectedImageUri);
                profilePic.setImageTintList(null);
                hoverProfilePic.setText("");
                imageUri = selectedImageUri.toString();


            } else {

                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
                int colorOnPrimary = typedValue.data;

                profilePic.setImageResource(R.drawable.ic_profile);
                profilePic.setImageTintList(ColorStateList.valueOf(colorOnPrimary));
                hoverProfilePic.setText(R.string.click_to_edit);

            }
        }
    }

    /**
     * Initializes the UI elements for SignUpInfoActivity.
     *
     * - Finds and references all relevant fields and buttons for sign-up information.
     */
    private void inizialiseUI() {

        fullNameText = findViewById(R.id.fullNameText);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        stepsInput = findViewById(R.id.stepsInput);
        waterInput = findViewById(R.id.waterInput);
        kcalInput = findViewById(R.id.kcalInput);
        profilePic = findViewById(R.id.profilePicImg);
        hoverProfilePic = findViewById(R.id.hoverProfilePic);
        genderInfo = findViewById(R.id.genderList);
        birthdayPicker = findViewById(R.id.birthDatePicker);
        submitButton = findViewById(R.id.submitButton);

    }

    /**
     * Sets up event listeners for the UI elements.
     *
     * - Handles profile picture selection.
     * - Handles form submission, including validation and user information update.
     */

    private void setupListeners() {

        profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        submitButton.setOnClickListener(view -> {
            String fullName = fullNameText.getText().toString();
            String weightStr = weightInput.getText().toString();
            String heightStr = heightInput.getText().toString();
            String stepsStr = stepsInput.getText().toString();
            String waterStr = waterInput.getText().toString();
            String kcalStr = kcalInput.getText().toString();

            if (fullName.length() < 2 || fullName.length() > 50) {
                Toast.makeText(view.getContext(), "Full Name must be between 2 and 50 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int weight = Integer.parseInt(weightStr);
                if (weight < 30 || weight > 140) {
                    Toast.makeText(view.getContext(), "Weight must be between 30 and 140.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int height = Integer.parseInt(heightStr);
                if (height < 130 || height > 220) {
                    Toast.makeText(view.getContext(), "Height must be between 130 and 220.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int steps = Integer.parseInt(stepsStr);
                if (steps < 1000 || steps > 100000) {
                    Toast.makeText(view.getContext(), "Daily steps must be between 1000 and 100000.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int water = Integer.parseInt(waterStr);
                if (water < 1000 || water > 8000) {
                    Toast.makeText(view.getContext(), "Daily water must be between 1000 and 8000.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int kcal = Integer.parseInt(kcalStr);
                if (kcal < 1000 || kcal > 5000) {
                    Toast.makeText(view.getContext(), "Daily kcal must be between 1000 and 5000.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserProfile user = viewModel.getUser().getValue();
                user.setFullName(fullName);
                user.setWeight(weight);
                user.setHeight(height);
                user.setDailySteps(steps);
                user.setDailyWater(water);
                user.setDailyKcal(kcal);


                if (imageUri != null) {
                    user.setImage(imageUri.toString());
                } else {
                    Toast.makeText(view.getContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }


                viewModel.setUser(user, this);
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
