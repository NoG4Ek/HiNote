package com.poly.hinote.ui.slideshow

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.poly.hinote.R
import com.poly.hinote.databinding.FragmentFilterBinding
import com.poly.hinote.ui.home.NoteListAdapter

class FilterFragment : Fragment() {

    private lateinit var filterViewModel: FilterViewModel
    private var _binding: FragmentFilterBinding? = null

    private val binding get() = _binding!!

    lateinit var filterMenuTagSearch: SearchView
    lateinit var filterMenuTagSearchChipGroup: ChipGroup
    lateinit var filterMenuSelectedTagChipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        filterViewModel =
            ViewModelProvider(this).get(FilterViewModel::class.java)

        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        val root: View = binding.root


        filterMenuTagSearch = binding.filterMenuTagSearch
        //inputTagMenuSearch.isIconified = true
        filterMenuTagSearchChipGroup = binding.filterMenuTagSearchChipGroup
        filterMenuSelectedTagChipGroup = binding.filterMenuSelectedTagChipGroup

        restoreStartSuggestions()

        filterMenuTagSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuTagSearchChipGroup.removeAllViews()
                if (!TextUtils.isEmpty(newText)) {
                    callSearch(newText)
                }
                if (newText == "") {
                    restoreStartSuggestions()
                }
                return true
            }

            fun callSearch(query: String) {
                NoteListAdapter.Tag.values().forEach { tag ->
                    if (tag.text.contains(Regex(query))) {
                        addChipSuggestion(filterMenuTagSearchChipGroup, tag)
                    }
                }
            }
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun restoreStartSuggestions() {
        filterMenuTagSearchChipGroup.removeAllViews()
        NoteListAdapter.Tag.values().copyOfRange(1, 3).forEach { tag ->
            addChipSuggestion(filterMenuTagSearchChipGroup, tag)
        }
    }

    fun addChipSuggestion(suggestionChipGroup: ChipGroup, tag: NoteListAdapter.Tag) {
        val chipSuggestion =
            layoutInflater.inflate(
                R.layout.chip_suggestion,
                filterMenuTagSearchChipGroup,
                false
            ) as Chip
        chipSuggestion.isCheckable = false
        chipSuggestion.text = tag.text
        chipSuggestion.setOnClickListener { chipSug ->
            suggestionChipGroup.removeView(chipSug)
            val chipSelected =
                layoutInflater.inflate(
                    R.layout.chip_suggestion,
                    filterMenuSelectedTagChipGroup,
                    false
                ) as Chip
            chipSelected.isCheckable = true
            chipSelected.isChecked = true
            chipSelected.text = tag.text
            filterMenuSelectedTagChipGroup.addView(chipSelected)
        }
        suggestionChipGroup.addView(chipSuggestion)
    }
}