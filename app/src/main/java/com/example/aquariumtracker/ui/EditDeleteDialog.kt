package com.example.aquariumtracker.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import com.example.aquariumtracker.R

class EditDeleteDialog(context: Context, lis: ReminderListAdapter, val position: Int) : Dialog(context) {
    private val view: View = layoutInflater.inflate(R.layout.dialog_edit_delete, null)
    private val delButton: Button = view.findViewById(R.id.delete_button)
    private val cancelButton: Button = view.findViewById(R.id.cancel_button)
    private val editButton: Button = view.findViewById(R.id.edit_button)

    interface DialogListener {
        fun onDeleteConfirmation(pos: Int)
        fun onEditConfirmation(pos: Int)
    }

    init {
        setContentView(view)
        cancelButton.setOnClickListener {
            cancel()
        }
        val listener = lis as DialogListener
        delButton.setOnClickListener {
            listener.onDeleteConfirmation(position)
            dismiss()
        }
        editButton.setOnClickListener {
            listener.onEditConfirmation(position)
            dismiss()
        }
    }

    fun getReminderPosition(): Int {
        return position
    }
}