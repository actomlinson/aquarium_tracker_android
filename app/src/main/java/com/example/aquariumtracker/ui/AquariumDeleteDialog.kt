package com.example.aquariumtracker.ui

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Button
import com.example.aquariumtracker.R

class AquariumDeleteDialog(context: Context) : AlertDialog(context) {
    private var builder: Builder = context.let {
        AlertDialog.Builder(it)
    }
    private var numClicked = 5
    private lateinit var delButton: Button

    init {

        //builder?.setMessage(R.string.delete_message)
        val view = layoutInflater.inflate(R.layout.dialog_delete, null)
        builder.setView(view)
        delButton = view.findViewById<Button>(R.id.del_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }
        updateDeleteButton()
        delButton.setOnClickListener {
            numClicked -= 1
            if (numClicked == 0) {
                this.dismiss()
                this.cancel()
                dismiss()
                cancel()
                Log.i("AquariumDeleteDialog", "Deleted")
            }
            updateDeleteButton()
        }
        val dialog: AlertDialog? = builder.create()
        builder.create()
    }

    private fun updateDeleteButton() {
        delButton.text = context.getString(R.string.delete_button_message, numClicked)
    }

    override fun show() {
        builder.show()

    }
//
//    fun onCreate(savedInstanceState: Bundle?): Dialog {
//
//        return context?.let {
//            val builder = AlertDialog.Builder(it)
//            var numClicked = 0
//            builder.setMessage(R.string.delete_message)
//                .setNeutralButton("Yes",
//                DialogInterface.OnClickListener{ dialog, id ->
//                    numClicked += 1
//                    if (numClicked == 5) {
//                        Log.i("AquariumDeleteDialog", "Deleted")
//                    }
//                })
//                .setNegativeButton("No",
//                DialogInterface.OnClickListener {dialog, id -> })
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//    }

}