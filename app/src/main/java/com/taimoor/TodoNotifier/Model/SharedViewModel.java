package com.taimoor.TodoNotifier.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//This ViewModel class will hold all the data which would be shared
// from/between activities and fragments
public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Task> selectedItem = new MutableLiveData<>();
    private final MutableLiveData<Task> radioSelectedItem = new MutableLiveData<>();
    private boolean isEdit;


    //getter and setter methods
    public void setSelectItem(Task task) {
        selectedItem.setValue(task);
    }

    public LiveData<Task> getSelectedItem() {
        return selectedItem;
    }

    public void setRadioSelectedItem(Task task){radioSelectedItem.setValue(task);}

    public MutableLiveData<Task> getRadioSelectedItem() {
        return radioSelectedItem;
    }


    public void setIsEdit(boolean isEdit){
        this.isEdit = isEdit;
    }

    public boolean getIsEdit(){
        return isEdit;
    }
}
