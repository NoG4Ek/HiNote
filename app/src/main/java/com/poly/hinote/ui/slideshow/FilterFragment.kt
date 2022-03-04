package com.poly.hinote.ui.slideshow

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.poly.hinote.R
import com.poly.hinote.SharedViewModel
import com.poly.hinote.databinding.FragmentFilterBinding
import com.poly.hinote.ui.home.NoteListAdapter

class FilterFragment : Fragment() {

    private lateinit var filterViewModel: FilterViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFilterBinding? = null

    private val binding get() = _binding!!

    lateinit var filterMenuTagSearch: SearchView
    lateinit var filterMenuTagSearchChipGroup: ChipGroup
    lateinit var filterMenuSelectedTagChipGroup: ChipGroup
    lateinit var filterMenuAddButton: Button
    lateinit var filterMenuApplyButton: Button

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
        filterMenuTagSearchChipGroup = binding.filterMenuTagSearchChipGroup
        filterMenuSelectedTagChipGroup = binding.filterMenuSelectedTagChipGroup
        filterMenuAddButton = binding.filterMenuAddButton
        filterMenuApplyButton = binding.filterMenuApplyButton

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
                        addChipSuggestion(filterMenuTagSearchChipGroup, tag.text)
                    }
                }
            }
        })

        filterMenuAddButton.setOnClickListener {
            addChipSelected(filterMenuSelectedTagChipGroup, filterMenuTagSearch.query.toString())
        }

        filterMenuApplyButton.setOnClickListener {
            sharedViewModel.applyFilters(
                filterMenuSelectedTagChipGroup.children.toList()
                    .map { view -> (view as Chip).text.toString() }, true)
            findNavController().navigateUp()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun restoreStartSuggestions() {
        filterMenuTagSearchChipGroup.removeAllViews()
        NoteListAdapter.Tag.values().copyOfRange(1, 3).forEach { tag ->
            addChipSuggestion(filterMenuTagSearchChipGroup, tag.text)
        }
    }

    private fun addChipSuggestion(suggestionChipGroup: ChipGroup, tagText: String) {
        val chipSuggestion =
            layoutInflater.inflate(
                R.layout.chip_suggestion,
                suggestionChipGroup,
                false
            ) as Chip
        chipSuggestion.isCheckable = false
        chipSuggestion.text = tagText
        chipSuggestion.setOnClickListener { chipSug ->
            addChipSelected(filterMenuSelectedTagChipGroup, tagText)
        }
        suggestionChipGroup.addView(chipSuggestion)
    }

    private fun addChipSelected(selectedChipGroup: ChipGroup, tagText: String) {
        val chipSelected =
            layoutInflater.inflate(
                R.layout.chip_suggestion,
                selectedChipGroup,
                false
            ) as Chip
        chipSelected.setOnClickListener { chipSel ->
            chipSel.isClickable = false
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 100
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    selectedChipGroup.removeView(chipSel)
                }

                override fun onAnimationStart(animation: Animation?) {}
            })

            chipSel.startAnimation(anim)
        }
        chipSelected.isCheckable = true
        chipSelected.isChecked = true
        chipSelected.text = tagText
        selectedChipGroup.addView(chipSelected)
    }
}