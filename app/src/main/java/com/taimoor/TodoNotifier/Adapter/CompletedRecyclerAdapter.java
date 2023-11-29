package com.taimoor.TodoNotifier.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Model.TaskViewModel;
import com.taimoor.TodoNotifier.R;
import com.taimoor.TodoNotifier.Utilities.Converter;
import com.taimoor.TodoNotifier.Utilities.Utils;

import java.util.List;

public class CompletedRecyclerAdapter extends RecyclerView.Adapter<CompletedRecyclerAdapter.viewHolder> {

    private final List<Task> taskList;
    private final OnTodoClickListener todoClickListener;

    //Constructor
    public CompletedRecyclerAdapter(List<Task> taskList, OnTodoClickListener todoClickListener) {
        this.taskList = taskList;
        this.todoClickListener = todoClickListener;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_row_completed,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        //Get the task which was clicked
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

        //Set data of task to the layout
        holder.task.setText(task.getTask());
        holder.todayChip.setText(formatted);
        holder.image.setImageBitmap(Converter.getBitmapFromStr(task.getImages()));

        //setting the colors according to priority
        holder.todayChip.setTextColor(Utils.priorityColor(task));
        holder.todayChip.setChipIconTint(colorStateList);
        holder.radioButton.setButtonTintList(colorStateList);

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task1 = taskList.get(holder.getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.popup__menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                //When clicked on Delete Task
                                TaskViewModel.delete(task1);
                                notifyDataSetChanged();
                                Toast.makeText(v.getContext(), "Task Deleted Successfully" , Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.add_again:
                                //When clicked on Add the task again
                                task1.setDone(false);
                                TaskViewModel.update(task1);
                                notifyDataSetChanged();
                                Toast.makeText(v.getContext(), "Please Update the Date and Time again!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatRadioButton radioButton;
        public AppCompatTextView task;
        public Chip todayChip;
        ImageButton menu;
        ImageView image;

        OnTodoClickListener onTodoClickListener;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.todo_radio_button_completed);
            task = itemView.findViewById(R.id.todo_row_todo_completed);
            todayChip = itemView.findViewById(R.id.todo_row_chip_completed);
            menu = itemView.findViewById(R.id.popup_menu);
            image = itemView.findViewById(R.id.display_image_completed);
            this.onTodoClickListener = todoClickListener;

            task.setPaintFlags(task.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Task currTask = taskList.get(getAdapterPosition());
            //click Listener for the row layout item
            if (id == R.id.todo_row_layout){
                onTodoClickListener.onTodoClick(currTask);
            }
        }

    }
}
