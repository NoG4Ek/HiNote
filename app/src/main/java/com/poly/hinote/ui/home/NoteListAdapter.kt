package com.poly.hinote.ui.home

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.poly.hinote.R
import com.poly.hinote.SharedViewModel
import com.poly.hinote.data.model.Note
import com.poly.hinote.util.Util


//  To add new type of note (card?)
//      add new type
//      getItemViewType() to set type
//      createViewHolder() inflate different cards depends on type and return Holder that you needed
//      onBindViewHolder() bind depend on holder!!.itemViewType
//

class NoteListAdapter(
    context: Context,
    noteList: List<Note>,
    activity: FragmentActivity?,
    homeViewModel: SharedViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext: Context = context
    private val mNoteList: List<Note> = noteList
    private val mActivity = activity
    private val mHomeViewModel = homeViewModel
    override fun getItemCount(): Int {
        return mNoteList.size
    }

    private var mExpandedPosition = -1
    var mMenuPosition = 0

    enum class Tag(val text: String) {
        HOT("HOT"),
        TASK("Task"),
        ANDROID("Android"),
        LIBRARY_DONE("LibDone"),
        LIBRARY_LATER("LibLater")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_card, parent, false)

        return NoteHolder(view)
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note: Note = mNoteList[position]
        (holder as NoteHolder).bind(note)

        val isExpanded = position == mExpandedPosition
        holder.noteExpandedGroup.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.noteCard.isActivated = isExpanded
        holder.noteCard.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            holder.noteArrow.rotation = (if (isExpanded) 180F else 0F)
            notifyItemChanged(position)
        }
        holder.noteCard.setOnLongClickListener {
            mMenuPosition = holder.adapterPosition

            val contextDialog =
                MaterialAlertDialogBuilder(mContext, R.style.Theme_HiNote_CardContextDialog)
                    .setView(R.layout.card_context_menu).show()
            val cContextDelete = contextDialog.findViewById<View>(R.id.card_context_delete)
            cContextDelete?.setOnClickListener {
                mHomeViewModel.deleteNote(note)
                contextDialog.dismiss()
            }

            return@setOnLongClickListener true
        }
    }

    private inner class NoteHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val noteShareData: TextView = itemView.findViewById(R.id.note_share_date)
        val noteHeader: TextView = itemView.findViewById(R.id.note_header)
        val noteTagGroup: ChipGroup = itemView.findViewById(R.id.note_tag_group)
        val noteComplexText: TextView = itemView.findViewById(R.id.note_complex_text)
        val noteLink: TextView = itemView.findViewById(R.id.note_link)
        val noteTime: TextView = itemView.findViewById(R.id.note_time)

        val noteCard: View = itemView.findViewById(R.id.note_card)
        val noteExpandedGroup: View = itemView.findViewById(R.id.note_expanded_view_group)
        val noteArrow: View = itemView.findViewById(R.id.arrow)

        fun bind(note: Note) {
            noteShareData.text = Util.formatDate(note.shareData)
            noteHeader.text = note.header

            noteTagGroup.removeAllViews()
            note.tags.split(" ").dropLast(1).forEach { tagText ->
                val chipCard = mActivity?.layoutInflater?.inflate(
                    R.layout.chip_suggestion,
                    noteTagGroup,
                    false
                ) as Chip
                chipCard.isCheckable = false
                chipCard.text = tagText
                noteTagGroup.addView(chipCard)
            }

            noteComplexText.text = note.complexText
            noteLink.text = note.link
            noteTime.text = note.time // TODO: format date Utils
        }
    }
}