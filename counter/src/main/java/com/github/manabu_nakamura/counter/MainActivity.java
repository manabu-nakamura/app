package com.github.manabu_nakamura.counter;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.manabu_nakamura.counter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ViewModel2 viewModel2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel2 = new ViewModelProvider(this).get(ViewModel2.class);
        viewModel2.value.observe(this, value -> binding.textView.setText(value.toString()));
    }

    public void count(View view) {
        viewModel2.value.setValue(viewModel2.value.getValue() + 1);
    }

    public void reset(View view) {
        viewModel2.value.setValue(0);
    }

    public static class ViewModel2 extends ViewModel {
        private MutableLiveData<Integer> value = new MutableLiveData<>(0);
    }
}