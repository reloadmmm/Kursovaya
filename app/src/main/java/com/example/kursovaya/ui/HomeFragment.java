package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kursovaya.data.Task;
import com.example.kursovaya.databinding.FragmentHomeBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding b;
    private TaskViewModel vm;
    private TaskAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        b = FragmentHomeBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        adapter = new TaskAdapter(
                t -> vm.markDone(t),
                t -> vm.markRelapse(t)
        );
        b.list.setLayoutManager(new LinearLayoutManager(getContext()));
        b.list.setAdapter(adapter);

        View.OnClickListener add = v -> new AddTaskDialog().show(getParentFragmentManager(),"add");
        b.centerPencil.setOnClickListener(add);
        b.fab.setOnClickListener(add);

        vm.pending.observe(getViewLifecycleOwner(), tasks -> {
            adapter.submit(tasks);
            boolean empty = tasks == null || tasks.isEmpty();
            b.centerPencil.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.fab.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }
}
