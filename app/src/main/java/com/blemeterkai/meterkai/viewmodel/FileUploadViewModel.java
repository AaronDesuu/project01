// FileUploadViewModel.java
package com.blemeterkai.meterkai.viewmodel; // Or your common ui package

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FileUploadViewModel extends ViewModel {

    private final MutableLiveData<String> newFileUploaded = new MutableLiveData<>();

    public LiveData<String> getNewFileUploaded() {
        return newFileUploaded;
    }

    public void signalNewFileUploaded(String fileName) {
        newFileUploaded.setValue(fileName);
        // To make it an event that's consumed, you might want to reset it after a short delay
        // or after it's observed. For simplicity, we'll leave it as is,
        // but be aware that if FirstFragment re-observes (e.g., after rotation
        // without the value being cleared), it might re-trigger.
        // A common pattern for events is to wrap data in an Event class.
    }

    // Optional: Call this from FirstFragment after processing the event
    public void clearNewFileUploadedSignal() {
        newFileUploaded.postValue(null); // Use postValue if called from a background thread, setValue if from main
    }
}