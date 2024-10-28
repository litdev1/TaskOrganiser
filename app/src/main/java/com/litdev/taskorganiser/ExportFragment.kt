package com.litdev.taskorganiser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import com.litdev.taskorganiser.actions.Action

/**
 * A simple [Fragment] subclass.
 * Use the [ExportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExportFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var mode:Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_export, container, false)

        val fileActivityResult =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        val data = result.data?.data //URI
                        //File name
                        val cursor = context?.contentResolver?.query(data?: Uri.EMPTY, null, null, null, null)
                        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor?.moveToFirst()
                        val fileName = cursor?.getString(nameIndex?: 0)

                        when (mode)
                        {
                            0 -> {
                                val file = context?.contentResolver?.openOutputStream(data ?: Uri.EMPTY, "w")
                                ApplicationClass.instance.data.reset()
                                val json = Json.encodeToString(ApplicationClass.instance.data)
                                ApplicationClass.instance.data.setParents(null)
                                file?.write(json.toByteArray())
                                file?.close()

                                Toast.makeText(context, "Data exported to $fileName", Toast.LENGTH_LONG).show()
                            }
                            1 -> {
                                val file = context?.contentResolver?.openInputStream(data?: Uri.EMPTY)
                                val json = file?.bufferedReader()?.readText()
                                ApplicationClass.instance.data.deserialise(json?: "")
                                ApplicationClass.instance.data.save(context?.cacheDir.toString(), context)

                                Toast.makeText(context, "Data imported from $fileName", Toast.LENGTH_LONG).show()
                            }
                            2 -> {
                                val file = context?.contentResolver?.openInputStream(data?: Uri.EMPTY)
                                val json = file?.bufferedReader()?.readText()
                                val newData: Action = Json.decodeFromString(json?: "")
                                ApplicationClass.instance.data.children.addAll(newData.children)
                                ApplicationClass.instance.data.save(context?.cacheDir.toString(), context)

                                Toast.makeText(context, "Data appended from $fileName", Toast.LENGTH_LONG).show()
                            }
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

        view.findViewById<Button>(R.id.buttonExport).setOnClickListener {
            val intent = Intent()
                .setType("application/octet-stream")
                .setAction(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, "tasky.json")

            mode = 0
            fileActivityResult.launch(intent)
        }

        view.findViewById<Button>(R.id.buttonImport).setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, "tasky.json")

            mode = 1
            fileActivityResult.launch(intent)
        }

        view.findViewById<Button>(R.id.buttonAppend).setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, "tasky.json")

            mode = 2
            fileActivityResult.launch(intent)
        }

        return view
    }
}
