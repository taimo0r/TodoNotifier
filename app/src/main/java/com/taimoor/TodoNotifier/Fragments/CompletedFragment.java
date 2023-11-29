package com.taimoor.TodoNotifier.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.taimoor.TodoNotifier.Adapter.CompletedRecyclerAdapter;
import com.taimoor.TodoNotifier.Adapter.OnTodoClickListener;
import com.taimoor.TodoNotifier.Fragments.BottomSheetFragment;
import com.taimoor.TodoNotifier.Model.SharedViewModel;
import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.taimoor.TodoNotifier.R;

public class CompletedFragment extends Fragment implements OnTodoClickListener {

    RecyclerView recyclerView;
    private TaskViewModel taskViewModel;
    private CompletedRecyclerAdapter Adapter;
    private SharedViewModel sharedViewModel;
    boolean taskStatus = false;
    boolean status = true;
    Task task;


    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        //Getting the completed tasks from database
        if (sharedViewModel.getRadioSelectedItem().getValue() != null) {
            task = sharedViewModel.getRadioSelectedItem().getValue();
            task.setDone(true);
            TaskViewModel.update(task);
            taskStatus = task.isDone();
        }

        if (taskStatus) {
            taskViewModel.getTaskState(taskStatus).observe(getViewLifecycleOwner(), tasks -> {
                Adapter = new CompletedRecyclerAdapter(tasks, this);
                recyclerView.setAdapter(Adapter);
            });
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        //Setting up recyclerview its adapter
        recyclerView = view.findViewById(R.id.completed_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(TaskViewModel.class);

        //Getting the completed tasks from database and setting it up in recyclerView
        taskViewModel.getTaskState(status).observe(getViewLifecycleOwner(), tasks -> {
            Adapter = new CompletedRecyclerAdapter(tasks, this);
            recyclerView.setAdapter(Adapter);

        });

        return view;
    }

    @Override
    public void onTodoClick(Task task) {
    }


}