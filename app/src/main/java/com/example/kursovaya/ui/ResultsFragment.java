package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kursovaya.databinding.FragmentResultsBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;

public class ResultsFragment extends Fragment {
    private FragmentResultsBinding b;
    private ResultAdapter adapter;
    private TaskViewModel vm;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        b = FragmentResultsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        adapter = new ResultAdapter();
        b.list.setLayoutManager(new LinearLayoutManager(getContext()));
        b.list.setAdapter(adapter);

        b.btnShowDone.setOnClickListener(v ->
                vm.done.observe(getViewLifecycleOwner(),
                        tasks -> adapter.submit(tasks, "done")));

        b.btnShowRelapse.setOnClickListener(v ->
                vm.relapse.observe(getViewLifecycleOwner(),
                        tasks -> adapter.submit(tasks, "relapse")));

        b.btnShowDone.performClick();
    }
}
