package com.example.aquariumtracker

import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.ui.EditDeleteDialog

abstract class SelectableListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    EditDeleteDialog.DialogListener