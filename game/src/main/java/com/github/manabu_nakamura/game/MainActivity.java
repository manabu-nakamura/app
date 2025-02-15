package com.github.manabu_nakamura.game;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.github.manabu_nakamura.game.databinding.ActivityMainBinding;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private ViewModel2 viewModel2;
    private static final int MAX = 20;
    private static final int[] THEMES = {AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(THEMES[PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 2)]);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBar);
        viewModel2 = new ViewModelProvider(this).get(ViewModel2.class);
        if (viewModel2.message.isEmpty()) {
            viewModel2.message = getString(R.string.message0, MAX - 1);
        }
        binding.textView.setText(viewModel2.message);
        for (int i = 0; i < MAX; i++) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox(this);
            materialCheckBox.setText(getString(R.string.m, i));
            materialCheckBox.setId(i);
            materialCheckBox.setOnClickListener(this);
            binding.linearLayout.addView(materialCheckBox);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void theme(MenuItem menuItem) {
        new DialogFragment2().show(getSupportFragmentManager(), "");
    }

    public void about(MenuItem menuItem) {
        new DialogFragment3().show(getSupportFragmentManager(), "");
    }

    public static class DialogFragment2 extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MainActivity mainActivity = (MainActivity)requireActivity();
            return new MaterialAlertDialogBuilder(mainActivity)
                    .setTitle(R.string.theme)
                    .setSingleChoiceItems(R.array.theme, PreferenceManager.getDefaultSharedPreferences(mainActivity).getInt("theme", 2), (dialog, which) -> {
                        dialog.dismiss();
                        PreferenceManager.getDefaultSharedPreferences(mainActivity).edit().putInt("theme", which).apply();
                        AppCompatDelegate.setDefaultNightMode(THEMES[which]);
                    })
                    .create();
        }
    }

    public static class DialogFragment3 extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.copyright)
                    .setPositiveButton(R.string.ok, null)
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                TextView textView = alertDialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }
            });
            return alertDialog;
        }
    }

    @Override
    public void onClick(View view) {
        ((MaterialCheckBox)view).setChecked(true);
        int number = view.getId();
        viewModel2.count++;
        int message;
        if (number < viewModel2.answer) {
            message = R.string.message1;
        } else if (number == viewModel2.answer) {
            message = R.string.message2;
        } else {
            message = R.string.message3;
        }
        viewModel2.message = getString(message, viewModel2.count, MAX - 1);
        binding.textView.setText(viewModel2.message);
        if (number == viewModel2.answer) {
            viewModel2.count = 0;
            viewModel2.answer = (int)(Math.random() * MAX);
            for (int i = 0; i < MAX; i++) {
                ((MaterialCheckBox)binding.linearLayout.getChildAt(i)).setChecked(false);
            }
        }
    }

    public static class ViewModel2 extends ViewModel {
        private String message = "";
        private int count = 0;
        private int answer = (int)(Math.random() * MAX);//0 <= answer <= MAX - 1
    }
}