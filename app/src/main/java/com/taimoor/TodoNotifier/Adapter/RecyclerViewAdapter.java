package com.taimoor.TodoNotifier.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.taimoor.TodoNotifier.Activities.MainActivity;
import com.taimoor.TodoNotifier.Fragments.BottomSheetFragment;
import com.taimoor.TodoNotifier.Model.Priority;
import com.taimoor.TodoNotifier.Model.SharedViewModel;
import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.taimoor.TodoNotifier.R;
import com.taimoor.TodoNotifier.Utilities.Converter;
import com.taimoor.TodoNotifier.Utilities.Utils;

import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Task> taskList;
    private final OnTodoClickListener todoClickListener;
    private Context context;
    SharedViewModel sharedViewModel;

    public RecyclerViewAdapter(List<Task> taskList, OnTodoClickListener onTodoClickListener, Context context) {
        this.taskList = taskList;
        this.todoClickListener = onTodoClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the clicked task
        Task task = taskList.get(position);
        String formatted = Utils.formatDate(task.getDueDate());

        //Changing the colors of radioButtons according to priority
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        }, new int[]{
                Color.LTGRAY,//disabled state
                Utils.priorityColor(task)
        });

        //Setting up the data of the task to layout
        holder.image.setImageBitmap(Converter.getBitmapFromStr(task.getImages()));
        holder.task.setText(task.getTask());
        holder.todayChip.setText(formatted);
        holder.locationChip.setText(task.getAddress());

        //Setting the colors according to priority
        holder.todayChip.setTextColor(Utils.priorityColor(task));
        holder.todayChip.setChipIconTint(colorStateList);
        holder.locationChip.setChipIconTint(colorStateList);
        holder.locationChip.setTextColor(Utils.priorityColor(task));
        holder.radioButton.setButtonTintList(colorStateList);
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        holder.pendingMenu.setOnClickListener(v -> {

            Task task1 = taskList.get(holder.getAdapterPosition());
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            popupMenu.inflate(R.menu.pending_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.set_done:
                        //When clicked on Task done
                        task1.setDone(true);
                        String work_tag = task1.getNotifyWorkTag();
                        WorkManager.getInstance(context).cancelAllWorkByTag(work_tag);
                        TaskViewModel.update(task1);
                        notifyDataSetChanged();
                        Toast.makeText(activity, "Congratulations! Task Completed successfully :)", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.update:
                        //When clicked on Update the task
                        //Updating the task  by passing the clicked task to sharedView model
                        sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
                        sharedViewModel.setSelectItem(task1);
                        sharedViewModel.setIsEdit(true);
                        bottomSheetFragment.show(activity.getSupportFragmentManager(), bottomSheetFragment.getTag());
                         break;
                    default:
                        return false;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatRadioButton radioButton;
        public AppCompatTextView task;
        public Chip todayChip, locationChip;
        ImageButton pendingMenu;
        ImageView image;

        OnTodoClickListener onTodoClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.todo_radio_button);
            task = itemView.findViewById(R.id.todo_row_todo);
            todayChip = itemView.findViewById(R.id.todo_row_chip);
            locationChip = itemView.findViewById(R.id.location_row_chip);
            pendingMenu = itemView.findViewById(R.id.pending_popup_menu);
            image = itemView.findViewById(R.id.display_image);
            radioButton.setChecked(false);
            this.onTodoClickListener = todoClickListener;

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Task currTask = taskList.get(getAdapterPosition());
            //click Listener for the row layout item
            if (id == R.id.todo_row_layout) {
                onTodoClickListener.onTodoClick(currTask);
            }
        }
    }
}
