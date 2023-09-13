package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.interfaces.WaterIntakeRepository;



public class WaterIntakeViewModel extends ViewModel {
    private final MutableLiveData<Float> totalWaterIntake = new MutableLiveData<>(0f);
    private WaterIntakeRepository repository;

    public WaterIntakeViewModel() {}  // Empty constructor

    public WaterIntakeViewModel(WaterIntakeRepository repository) {
        this.repository = repository;
    }

    public void setRepository(WaterIntakeRepository repository) {
        this.repository = repository;
    }

    public LiveData<Float> getTotalWaterIntake() {
        return totalWaterIntake;
    }

    public void addWater(float intake) {
        totalWaterIntake.setValue(totalWaterIntake.getValue() + intake);
        if (repository != null) {
            repository.insertWaterIntake(intake); // Insert into data source directly from ViewModel
        }
    }


}
