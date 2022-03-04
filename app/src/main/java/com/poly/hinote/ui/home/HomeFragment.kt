package com.poly.hinote.ui.home


import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.poly.hinote.R
import com.poly.hinote.SharedViewModel
import com.poly.hinote.data.model.Note
import com.poly.hinote.databinding.FragmentHomeBinding
import com.poly.hinote.util.Util
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    lateinit var noteListRecycler: RecyclerView
    lateinit var noteListAdapter: NoteListAdapter

    lateinit var inputHeader: TextInputLayout
    lateinit var inputHeaderEditText: TextInputEditText
    lateinit var inputTagGroup: ChipGroup
    lateinit var inputContent: TextInputLayout
    lateinit var inputContentEditText: TextInputEditText
    lateinit var inputLink: TextInputLayout
    lateinit var inputLinkEditText: TextInputEditText
    lateinit var inputData: TextInputLayout
    lateinit var inputDataEditText: TextInputEditText
    lateinit var inputShareButton: Button
    lateinit var inputTagChipAdd: Chip
    lateinit var inputArrowExp: ImageView
    lateinit var inputBottomSheetLayout: ConstraintLayout
    lateinit var inputBaselineGroup: ConstraintLayout
    lateinit var inputGroup: CoordinatorLayout

    lateinit var homeToolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()

        inputHeader = binding.inputHeader
        inputHeaderEditText = binding.inputHeaderText
        inputTagGroup = binding.inputTagChipGroup
        inputContent = binding.inputContent
        inputContentEditText = binding.inputContentText
        inputLink = binding.inputLink
        inputLinkEditText = binding.inputLinkText
        inputData = binding.inputData
        inputDataEditText = binding.inputDataText
        inputTagChipAdd = binding.inputTagChipAdd
        inputShareButton = binding.inputShareButton
        inputArrowExp = binding.inputExpArrow
        inputBottomSheetLayout = binding.bottomSheet
        inputBaselineGroup = binding.inputBaselineGroup
        inputGroup = binding.inputGroup
        noteListRecycler = binding.noteListRecycler

        initNotes()
        inputShareButton.setOnClickListener {
            shareNote()
        }
        inputTagChipAdd.setOnClickListener {


            val mldInputTagText = MutableLiveData<String>()
            mldInputTagText.observe(viewLifecycleOwner) { tagText ->
                //chipSuggestionToInput(tagText)
            }
            //callTagMenuDialog(mldInputTagText)
        }

        sharedViewModel.applyingFilters.observe(viewLifecycleOwner) { filters ->
            addChipsToFilter(filters)
        }

        //Collapse BS interaction
        val inputBottomSheetBehavior = BottomSheetBehavior.from(inputBottomSheetLayout)
        val noteListRecyclerParams = CoordinatorLayout.LayoutParams(noteListRecycler.layoutParams)
        setInteractBehavior(inputBottomSheetBehavior, noteListRecyclerParams)

        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemSearch = menu.findItem(R.id.action_search)
        val itemSettings = menu.findItem(R.id.action_settings)
        val itemFilter = menu.findItem(R.id.action_filter)
        itemSearch.isVisible = true
        itemSettings.isVisible = false
        itemFilter.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                findNavController().navigate(R.id.action_home_to_filter)
//                val mldFilterTagText = MutableLiveData<String>()
//                mldFilterTagText.observe(viewLifecycleOwner) { tagText ->
//                    chipSuggestionToFilter(tagText)
//                }
//                callTagMenuDialog(mldFilterTagText)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setInteractBehavior(
        inputBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>,
        noteListRecyclerParams: CoordinatorLayout.LayoutParams
    ) {
        inputBaselineGroup.doOnPreDraw { group ->
            inputBottomSheetBehavior.peekHeight = group.height
        }

        inputGroup.doOnPreDraw {
            noteListRecyclerParams.bottomMargin = inputBaselineGroup.height
            noteListRecycler.layoutParams = noteListRecyclerParams
        }

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        inputContentEditText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        inputContentEditText.isSingleLine = false
                        inputContentEditText.setLines(1)
                        inputContentEditText.maxLines = 10

                        noteListRecyclerParams.bottomMargin = bottomSheet.height
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        inputContentEditText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
                        inputContentEditText.isSingleLine = true
                        inputContentEditText.setLines(1)
                        inputContentEditText.maxLines = 1

                        noteListRecyclerParams.bottomMargin = inputBaselineGroup.height
                    }
                    else -> {}
                }
                noteListRecycler.layoutParams = noteListRecyclerParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                inputArrowExp.rotation = slideOffset * 180
            }
        }

        inputBottomSheetBehavior.isHideable = false
        inputBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun initNotes() {
        if (sharedViewModel.applyingFilters.value == null || sharedViewModel.applyingFilters.value!!.isEmpty()) {
            sharedViewModel.readAllNotes()
        }
        sharedViewModel.noteList.observe(viewLifecycleOwner) { notesList ->
            if (notesList != null) {
                noteListRecycler = binding.noteListRecycler
                noteListAdapter =
                    NoteListAdapter(this.requireContext(), notesList, activity, sharedViewModel)
                noteListRecycler.layoutManager = LinearLayoutManager(this.requireContext())
                noteListRecycler.adapter = noteListAdapter
            }
        }
    }

    private fun shareNote() {
        val noteHeaderText: String = inputHeaderEditText.text.toString()
        val noteTagText: String = with(StringBuilder()) {
            inputTagGroup.children.toList().dropLast(1)
                .forEach { append((it as Chip).text); append(" ") }
            toString()
        }
        val noteContentText: String = inputContentEditText.text.toString()
        val noteLinkText: String = inputLinkEditText.text.toString()
        val noteTimeText: String = inputDataEditText.text.toString()

        var checkRequiredFields = true
        if (noteContentText == "") {
            checkRequiredFields = false
            inputContent.error = " "
        }
        if (noteHeaderText == "") {
            checkRequiredFields = false
            inputHeader.error = "*required field"
        }

        if (checkRequiredFields) {
            sharedViewModel.addNote(
                Note(
                    Util.curTime,
                    noteHeaderText,
                    noteTagText,
                    noteContentText,
                    noteLinkText,
                    noteTimeText
                )
            )
        }
    }

    private fun chipSuggestionToInput(tagText: String) {
        val inputTagChipGroup = binding.inputTagChipGroup
        val inputChip =
            layoutInflater.inflate(R.layout.chip_input, inputTagChipGroup, false) as Chip
        inputChip.isCheckable = false
        inputChip.text = tagText
        inputChip.setOnCloseIconClickListener {
            inputTagChipGroup.removeView(inputChip)
        }
        inputTagChipGroup.addView(inputChip, inputTagChipGroup.childCount - 1)
    }

    private fun addChipsToFilter(tags: List<String>) {
        tags.forEach { tagText ->
            val filterTagChipGroup = binding.filterTagChipGroup
            val filterChip =
                layoutInflater.inflate(R.layout.chip_input, filterTagChipGroup, false) as Chip
            filterChip.isCheckable = false
            filterChip.text = tagText
            filterChip.setOnCloseIconClickListener {
                filterTagChipGroup.removeView(filterChip)
                sharedViewModel.applyFilters(filterTagChipGroup.children.toList()
                    .map { view -> (view as Chip).text.toString() }, false)
            }
            filterTagChipGroup.addView(filterChip)
        }
    }
}