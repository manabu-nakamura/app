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

package com.github.manabu_nakamura.editor

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

import com.github.manabu_nakamura.editor.databinding.ActivityMainBinding
import com.github.manabu_nakamura.editor.databinding.SheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val viewModel2 by viewModels<ViewModel2>()
    private companion object {
        private val THEMES = intArrayOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(THEMES[PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 2)])
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContentView(binding.root)
        binding.editText.requestFocus()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel2.modified = true
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun menu(view: View) {
        DialogFragment4().show(supportFragmentManager, "")
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.root.post { bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED }
        }
    }

    fun newFile(menuItem: MenuItem) {
        if (viewModel2.modified) {
            DialogFragment0().show(supportFragmentManager, "")
        } else {
            newFile()
        }
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun open(menuItem: MenuItem) {
        if (viewModel2.modified) {
            DialogFragment1().show(supportFragmentManager, "")
        } else {
            open()
        }
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun save(menuItem: MenuItem) {
        save(ViewModel2.TODO.NOTHING)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun saveAs(menuItem: MenuItem) {
        saveAs(ViewModel2.TODO.NOTHING)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun theme(menuItem: MenuItem) {
        DialogFragment2().show(supportFragmentManager, "")
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun about(menuItem: MenuItem) {
        DialogFragment3().show(supportFragmentManager, "")
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun undo(menuItem: MenuItem) {
        binding.editText.onTextContextMenuItem(android.R.id.undo)
    }

    fun redo(menuItem: MenuItem) {
        binding.editText.onTextContextMenuItem(android.R.id.redo)
    }

    fun paste(menuItem: MenuItem) {
        binding.editText.onTextContextMenuItem(android.R.id.paste)
    }

    private fun newFile() {
        viewModel2.uri = null
        binding.editText.setText("")
        viewModel2.modified = false
    }

    private val activityResultLauncher0 = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        it?.run {
            viewModel2.uri = this
            try {
                BufferedReader(InputStreamReader(contentResolver.openInputStream(this))).use {
                    val stringBuffer = StringBuffer()
                    val buffer = CharArray(4096)
                    var n: Int
                    while (it.read(buffer, 0, 4096).run { n = this; this } != -1) {
                        stringBuffer.append(buffer, 0, n)
                    }
                    binding.editText.setText(stringBuffer)
                    viewModel2.modified = false
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, R.string.cannotOpen, Toast.LENGTH_LONG).show()
                newFile()
            }
        }
    }

    private fun open() {
        activityResultLauncher0.launch(arrayOf("*/*"))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun save(todo: ViewModel2.TODO) {
        viewModel2.uri?.run {
            if (viewModel2.modified) {
                save2(todo)
            }
        } ?: saveAs(todo)
    }

    private fun save2(todo: ViewModel2.TODO) {
        try {
            BufferedWriter(OutputStreamWriter(contentResolver.openOutputStream(viewModel2.uri!!))).use {
                it.write(binding.editText.text.toString())
                viewModel2.modified = false
                when (todo) {
                    ViewModel2.TODO.NEW_FILE -> newFile()
                    ViewModel2.TODO.OPEN -> open()
                    else -> {}
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.cannotSave, Toast.LENGTH_LONG).show()
        }
    }

    private val activityResultLauncher1 = registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        it?.run {
            viewModel2.uri = this
            save2(viewModel2.todo)
        }
    }

    private fun saveAs(todo: ViewModel2.TODO) {
        viewModel2.todo = todo
        activityResultLauncher1.launch("")
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    class DialogFragment0 : AppCompatDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val mainActivity = requireActivity() as MainActivity
            return MaterialAlertDialogBuilder(mainActivity)
                .setTitle(mainActivity.viewModel2.getFilename(mainActivity))
                .setMessage(R.string.message)
                .setPositiveButton(R.string.save0) { _, _ -> mainActivity.save(ViewModel2.TODO.NEW_FILE) }
                .setNegativeButton(R.string.notSave0) { _, _ -> mainActivity.newFile() }
                .setNeutralButton(R.string.cancel, null)
                .create()
        }
    }

    class DialogFragment1 : AppCompatDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val mainActivity = requireActivity() as MainActivity
            return MaterialAlertDialogBuilder(mainActivity)
                .setTitle(mainActivity.viewModel2.getFilename(mainActivity))
                .setMessage(R.string.message)
                .setPositiveButton(R.string.save1) { _, _ -> mainActivity.save(ViewModel2.TODO.OPEN) }
                .setNegativeButton(R.string.notSave1) { _, _ -> mainActivity.open() }
                .setNeutralButton(R.string.cancel, null)
                .create()
        }
    }

    class DialogFragment2 : AppCompatDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val mainActivity = requireActivity() as MainActivity
            return MaterialAlertDialogBuilder(mainActivity)
                .setTitle(R.string.theme)
                .setSingleChoiceItems(R.array.theme, PreferenceManager.getDefaultSharedPreferences(mainActivity).getInt("theme", 2)) { dialog, which ->
                    dialog.dismiss()
                    PreferenceManager.getDefaultSharedPreferences(mainActivity).edit().putInt("theme", which).apply()
                    AppCompatDelegate.setDefaultNightMode(THEMES[which])
                }
                .create()
        }
    }

    class DialogFragment3 : AppCompatDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.copyright)
                .setPositiveButton(R.string.ok, null)
                .create()
            alertDialog.setOnShowListener {
                alertDialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
            }
            return alertDialog
        }
    }

    class DialogFragment4 : BottomSheetDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val mainActivity = requireActivity() as MainActivity
            val bottomSheetDialog = BottomSheetDialog(mainActivity)
            val binding = SheetBinding.inflate(layoutInflater)
            bottomSheetDialog.setContentView(binding.root)
            mainActivity.bottomSheetBehavior = bottomSheetDialog.behavior
            mainActivity.binding.root.post {
                mainActivity.bottomSheetBehavior?.peekHeight = mainActivity.binding.root.height / 2
                binding.textView.text = getString(if ((mainActivity.viewModel2.modified)) R.string.modified else R.string.notModified, mainActivity.viewModel2.getFilename(mainActivity))
            }
            return bottomSheetDialog
        }
    }

    class ViewModel2 : ViewModel() {
        var uri: Uri? = null
        var modified = false
        enum class TODO { NOTHING, NEW_FILE, OPEN }
        var todo = TODO.NOTHING

        fun getFilename(context: Context): String {
            return uri?.run {
                DocumentFile.fromSingleUri(context, this)?.name ?: run {
                    uri = null
                    null
                }
            } ?: context.getString(R.string.untitled)
        }
    }
}