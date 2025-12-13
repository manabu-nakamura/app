/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.manabu_nakamura.editor;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.github.manabu_nakamura.editor.databinding.ActivityMainBinding;
import com.github.manabu_nakamura.editor.databinding.SheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BottomSheetBehavior<?> bottomSheetBehavior;
    private ViewModel2 viewModel2;
    private static final int[] THEMES = {AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(THEMES[PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 2)]);
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel2 = new ViewModelProvider(this).get(ViewModel2.class);
        binding.editText.requestFocus();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel2.modified = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void undo(View view) {
        binding.editText.onTextContextMenuItem(android.R.id.undo);
    }

    public void redo(View view) {
        binding.editText.onTextContextMenuItem(android.R.id.redo);
    }

    public void paste(View view) {
        binding.editText.onTextContextMenuItem(android.R.id.paste);
    }

    public void menu(View view) {
        new DialogFragment4().show(getSupportFragmentManager(), "");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.getRoot().post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        }
    }

    public void newFile(MenuItem menuItem) {
        if (viewModel2.modified) {
            new DialogFragment0().show(getSupportFragmentManager(), "");
        } else {
            newFile();
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void open(MenuItem menuItem) {
        if (viewModel2.modified) {
            new DialogFragment1().show(getSupportFragmentManager(), "");
        } else {
            open();
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void save(MenuItem menuItem) {
        save(ViewModel2.TODO.NOTHING);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void saveAs(MenuItem menuItem) {
        saveAs(ViewModel2.TODO.NOTHING);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void theme(MenuItem menuItem) {
        new DialogFragment2().show(getSupportFragmentManager(), "");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void about(MenuItem menuItem) {
        new DialogFragment3().show(getSupportFragmentManager(), "");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void newFile() {
        viewModel2.uri = null;
        binding.editText.setText("");
        viewModel2.modified = false;
    }

    private ActivityResultLauncher<String[]> activityResultLauncher0 = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (result != null) {
            viewModel2.uri = result;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(viewModel2.uri)))) {
                StringBuffer stringBuffer = new StringBuffer();
                char[] buffer = new char[4096];
                int n;
                while ((n = bufferedReader.read(buffer, 0, 4096)) != -1) {
                    stringBuffer.append(buffer, 0, n);
                }
                binding.editText.setText(stringBuffer);
                viewModel2.modified = false;
            } catch (Exception e) {
                Toast.makeText(this, R.string.cannotOpen, Toast.LENGTH_LONG).show();
                newFile();
            }
        }
    });

    private void open() {
        activityResultLauncher0.launch(new String[] {"*/*"});
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void save(ViewModel2.TODO todo) {
        if (viewModel2.uri == null) {
            saveAs(todo);
        } else if (viewModel2.modified) {
            save2(todo);
        }
    }

    private void save2(ViewModel2.TODO todo) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(getContentResolver().openOutputStream(viewModel2.uri, "wt")))) {
            bufferedWriter.write(binding.editText.getText().toString());
            viewModel2.modified = false;
            if (todo == ViewModel2.TODO.NEW_FILE) {
                newFile();
            } else if (todo == ViewModel2.TODO.OPEN) {
                open();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.cannotSave, Toast.LENGTH_LONG).show();
        }
    }

    private ActivityResultLauncher<String> activityResultLauncher1 = registerForActivityResult(new ActivityResultContracts.CreateDocument("*/*"), result -> {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (result != null) {
            viewModel2.uri = result;
            save2(viewModel2.todo);
        }
    });

    private void saveAs(ViewModel2.TODO todo) {
        viewModel2.todo = todo;
        activityResultLauncher1.launch("");
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static class DialogFragment0 extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MainActivity mainActivity = (MainActivity)requireActivity();
            return new MaterialAlertDialogBuilder(mainActivity)
                    .setTitle(mainActivity.viewModel2.getFilename(mainActivity))
                    .setMessage(R.string.message)
                    .setPositiveButton(R.string.save0, (dialog, which) -> mainActivity.save(ViewModel2.TODO.NEW_FILE))
                    .setNegativeButton(R.string.notSave0, (dialog, which) -> mainActivity.newFile())
                    .setNeutralButton(R.string.cancel, null)
                    .create();
        }
    }

    public static class DialogFragment1 extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MainActivity mainActivity = (MainActivity)requireActivity();
            return new MaterialAlertDialogBuilder(mainActivity)
                    .setTitle(mainActivity.viewModel2.getFilename(mainActivity))
                    .setMessage(R.string.message)
                    .setPositiveButton(R.string.save1, (dialog, which) -> mainActivity.save(ViewModel2.TODO.OPEN))
                    .setNegativeButton(R.string.notSave1, (dialog, which) -> mainActivity.open())
                    .setNeutralButton(R.string.cancel, null)
                    .create();
        }
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

    public static class DialogFragment4 extends BottomSheetDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MainActivity mainActivity = (MainActivity)requireActivity();
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mainActivity);
            SheetBinding binding = SheetBinding.inflate(getLayoutInflater());
            bottomSheetDialog.setContentView(binding.getRoot());
            mainActivity.bottomSheetBehavior = bottomSheetDialog.getBehavior();
            mainActivity.binding.getRoot().post(() -> {
                mainActivity.bottomSheetBehavior.setPeekHeight(mainActivity.binding.getRoot().getHeight() / 2);
                binding.textView.setText(getString((mainActivity.viewModel2.modified) ? R.string.modified : R.string.notModified, mainActivity.viewModel2.getFilename(mainActivity)));
            });
            return bottomSheetDialog;
        }
    }

    public static class ViewModel2 extends ViewModel {
        private Uri uri = null;
        private boolean modified = false;
        private enum TODO {NOTHING, NEW_FILE, OPEN}
        private TODO todo = TODO.NOTHING;

        public String getFilename(Context context) {
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            if (documentFile != null) {
                String filename = documentFile.getName();
                if (filename != null) {
                    return filename;
                }
            }
            uri = null;
            return context.getString(R.string.untitled);
        }
    }
}