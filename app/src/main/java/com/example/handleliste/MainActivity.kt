package com.example.handleliste

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.ClipboardManager
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.ClipDescription
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private var dataList = mutableListOf<ListItem>()
    private lateinit var adapter: ListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.dataList = loadData(this)
        populateDataList()
    }
    override fun onPause() {
        super.onPause()
        saveData(this, dataList)
    }
    override fun onStop() {
        super.onStop()
        saveData(this,dataList)
    }
    override fun onDestroy() {
        super.onDestroy()
        saveData(this,dataList)
    }


    override fun onResume() {
        super.onResume()
        this.dataList = loadData(this)
        populateDataList()
    }

    private fun saveData(context: Context, list: List<ListItem>) {
        val sharedPreferences = context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString("data_list", json)
        editor.apply()
    }

    private fun loadData(context: Context): MutableList<ListItem> {
        val sharedPreferences = context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("data_list", null)
        val type = object : TypeToken<MutableList<ListItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }


    private fun populateDataList() {
        val recyclerView: RecyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter(dataList);
        recyclerView.adapter = adapter
    }

    fun addItem(view: View) {
        val input_field = findViewById<AutoCompleteTextView>(R.id.text_input)
        var lastId = 0;
        var exists = false;
        if (input_field.text.isNotEmpty()) {
            dataList.forEach {item ->
                if (item.id > lastId) {
                    lastId = item.id
                }
                if (item.title.lowercase().equals(input_field.text.toString().lowercase())) {
                    exists = true;
                }
            }
            if (!exists) {
                lastId++;
                dataList.add(ListItem(lastId, input_field.text.toString()))
                populateDataList()
            } else {
                Toast.makeText(this, R.string.exists_message, Toast.LENGTH_LONG).show()
            }
            input_field.text.clear()
        }
    }
    fun clearSelected(view: View) {
        var someAreChecked = false;

        dataList.forEach{item ->
            if (item.isChecked) {
                someAreChecked = true;
            }
        }
        if (someAreChecked) {
            adapter.removeCheckedItems()
        } else {
            AlertDialog.Builder(view.context,  R.style.CustomAlertDialogStyle)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.positive) { dialog, which ->
                    dataList.clear()
                    populateDataList()
                }
                .setNegativeButton(R.string.negative, null)
                .show().also { dialog ->
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(view.context.getColor(R.color.white))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view.context.getColor(R.color.white))
                }
        }
        populateDataList()

    }
    fun copy(view: View) {
        val inputField = getClipboardText() ?: return // Return if clipboard text is null
        var lastId = dataList.maxOfOrNull { it.id } ?: 0 // Get the highest ID or 0 if list is empty

        val itemsToAdd = inputField.split("\n")
            .filter { line ->
                line.isNotEmpty() && dataList.none { it.title.equals(line, ignoreCase = true) }
            }
            .map { line ->
                ListItem(++lastId, line) // Increment lastId for each new item
            }

        if (itemsToAdd.isNotEmpty()) {
            dataList.addAll(itemsToAdd)
            populateDataList()
        } else {
            Toast.makeText(this, R.string.left_out, Toast.LENGTH_LONG).show()
        }
    }

    private fun getClipboardText(): String? {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        // Check if the clipboard has data and if it is text
        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
            val item = clipboard.primaryClip?.getItemAt(0)
            return item?.text?.toString()
        }

        return ""
    }

    // Usage in an Activity or Fragment


}