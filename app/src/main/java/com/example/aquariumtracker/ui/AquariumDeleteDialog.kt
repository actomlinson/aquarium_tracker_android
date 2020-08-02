package com.example.aquariumtracker.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import com.example.aquariumtracker.R

class AquariumDeleteDialog(context: Context, fragment: AquariumFragment) : Dialog(context) {
    private var numClicked = 5
    private val view: View = layoutInflater.inflate(R.layout.dialog_delete, null)
    private val delButton: Button = view.findViewById(R.id.del_button)
    private val cancelButton: Button = view.findViewById(R.id.cancel_button)

    interface DeleteDialogListener {
        fun onDeleteConfirmation(dialog: Dialog)
    }

    init {
        setContentView(view)
        cancelButton.setOnClickListener {
            cancel()
        }
        val listener = fragment as DeleteDialogListener
        delButton.text = context.getString(R.string.delete_button_message, numClicked)
        delButton.setOnClickListener {
            numClicked -= 1
            if (numClicked == 0) {
                listener.onDeleteConfirmation(this)
                dismiss()
            }
            delButton.text = context.getString(R.string.delete_button_message, numClicked)
        }
    }
}