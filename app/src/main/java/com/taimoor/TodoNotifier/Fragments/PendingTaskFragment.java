package com.taimoor.TodoNotifier.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.taimoor.TodoNotifier.Adapter.OnTodoClickListener;
import com.taimoor.TodoNotifier.Adapter.RecyclerViewAdapter;
import com.taimoor.TodoNotifier.Fragments.BottomSheetFragment;
import com.taimoor.TodoNotifier.Model.SharedViewModel;
import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.taimoor.TodoNotifier.R;

public class PendingTaskFragment extends Fragment implements OnTodoClickListener {

    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SharedViewModel sharedViewModel;
    boolean Status = false;
    public PendingTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_task, container, false);

        //Setting up recyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(TaskViewModel.class);

        //Getting incomplete tasks from the database
        taskViewModel.getTaskState(Status).observe(getViewLifecycleOwner(), tasks -> {
            recyclerViewAdapter = new RecyclerViewAdapter(tasks, this, getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
        });

        return view;
    }
    @Override
    public void onTodoClick(Task task) {

    }

}













