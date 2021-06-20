package com.gravitygroup.avangard.presentation.filter

import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.clearTimeFields
import com.gravitygroup.avangard.core.ext.getDefaultCalendar
import com.gravitygroup.avangard.core.ext.showDateDialog
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentFilterBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.Name
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.Type
import com.gravitygroup.avangard.presentation.filter.data.FilterOpenSpecs.Master
import com.gravitygroup.avangard.presentation.filter.data.FilterOpenSpecs.Other
import com.gravitygroup.avangard.presentation.filter.data.OrdersFilterData
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.redmadrobot.inputmask.MaskedTextChangedListener
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.Date
import java.util.concurrent.TimeUnit

class FilterFragment : BaseFragment<FilterViewModel>(R.layout.fragment_filter) {

    override val viewModel: FilterViewModel by viewModel()

    override val stateBinding by lazy { FilterStateBinding() }

    private val vb by viewBinding(FragmentFilterBinding::bind)

    private val args: FilterFragmentArgs by navArgs()

    private val specs by lazy { args.specs }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }

            icCheck.setSafeOnClickListener {
                viewModel.onDone()
            }

            when (specs) {
                Master -> {
                    dateRoot.isVisible = false
                }
                Other -> {
                    etStartDate.setSafeOnClickListener {
                        val datetime = stateBinding.filterData.dateStart
                        val calendar = getDefaultCalendar().apply {
                            time = if (datetime != null) Date(datetime) else Date()
                            clearTimeFields()
                        }
                        requireContext().showDateDialog(calendar) { startDate ->
                            viewModel.onStartDate(startDate.time)
                        }
                    }

                    etEndDate.setSafeOnClickListener {
                        val datetime = stateBinding.filterData.dateEnd
                        val calendar = getDefaultCalendar().apply {
                            time = if (datetime != null) Date(datetime) else Date()
                            clearTimeFields()
                        }
                        requireContext().showDateDialog(calendar) {
                            val ms = it.time
                                .plus(TimeUnit.DAYS.toMillis(1))
                                .dec()
                            viewModel.onEndDate(ms)
                        }
                    }
                }
            }

            etType.setSafeOnClickListener {
                viewModel.onCatalogField(Type)
            }

            etName.setSafeOnClickListener {
                viewModel.onCatalogField(Name)
            }

            etStartDate.doOnTextChanged { text, _, _, _ -> performTextChanges(etStartDate, !text.isNullOrEmpty()) }
            etEndDate.doOnTextChanged { text, _, _, _ -> performTextChanges(etEndDate, !text.isNullOrEmpty()) }
            etType.doOnTextChanged { text, _, _, _ -> performTextChanges(etType, !text.isNullOrEmpty()) }
            etName.doOnTextChanged { text, _, _, _ -> performTextChanges(etName, !text.isNullOrEmpty()) }

            etNumber.setOnFocusChangeListener { view, hasFocus -> performTextChanges(view, hasFocus) }
            etNumber.doOnTextChanged { text, _, _, _ -> performTextChanges(etNumber, !text.isNullOrEmpty()) }
            etPhone.setOnFocusChangeListener { view, hasFocus -> performTextChanges(view, hasFocus) }
            etPhone.doOnTextChanged { text, _, _, _ -> performTextChanges(etPhone, !text.isNullOrEmpty()) }
            val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", etPhone)
            etPhone.addTextChangedListener(listener)
        }

        setFragmentResultListener(FilterCatalogFragment.FILTER_CATALOG_REQUEST_KEY) { _, bundle ->
            viewModel.onCatalogData(FilterCatalogFragment.getFilterResult(bundle))
        }
    }

    private fun performTextChanges(view: View, state: Boolean) {
        vb?.apply {
            when (view.id) {
                R.id.etStartDate -> tilStartDate.hint = getString(R.string.filter_date_start)
                R.id.etEndDate -> tilEndDate.hint = getString(R.string.filter_date_end)
                R.id.etType -> tilType.hint =
                    if (state || !etType.text.isNullOrEmpty()) getString(R.string.filter_type_input) else getString(R.string.filter_type_hint)
                R.id.etName -> tilName.hint =
                    if (state || !etName.text.isNullOrEmpty()) getString(R.string.filter_name_input) else getString(R.string.filter_name_hint)
                R.id.etNumber -> {
                    val isNotEmptyField = state && !etNumber.text.isNullOrEmpty()
                    tilNumber.hint =
                        if (isNotEmptyField) getString(R.string.filter_number_input) else getString(
                            R.string.filter_number_hint
                        )
                    if (isNotEmptyField) {
                        viewModel.onOrderNumber(etNumber.text.toString())
                    }
                }
                R.id.etPhone -> {
                    val isNotEmptyField = state && !etPhone.text.isNullOrEmpty()
                    tilPhone.hint =
                        if (isNotEmptyField) getString(R.string.filter_phone_input) else getString(
                            R.string.filter_phone_hint
                        )
                    if (isNotEmptyField) {
                        viewModel.onPhone(etPhone.text.toString())
                    }
                }
            }
        }
    }

    inner class FilterStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        var filterData by RenderProp(OrdersFilterData()) {
            vb?.apply {
                //TODO: заюзать по id когда в ордерах будут ids и соот-но в фильтре появится Int
                //etName.setText(viewModel.getTechName(filterData.techName))
                //etType.setText(viewModel.getTechType(filterData.techType))
                etStartDate.setText(it.dateStart?.toSimpleDateString() ?: "")
                etEndDate.setText(it.dateEnd?.toSimpleDateString() ?: "")
                val typeString = it.typesTech
                    ?.takeIf { it.isNotEmpty() }
                    ?.joinToString(TECH_CATALOG_SEPARATOR)
                    ?: requireContext().getString(R.string.filter_type_none)
                etType.setText(typeString)
                val nameString = it.namesTech
                    ?.takeIf { it.isNotEmpty() }
                    ?.joinToString(TECH_CATALOG_SEPARATOR)
                    ?: requireContext().getString(R.string.filter_type_none)
                etName.setText(nameString)
            }
        }

        override fun bind(data: IViewModelState) {
            data as FilterState
            isLoading = data.isLoading
            filterData = data.filterData
        }
    }

    companion object {

        private const val TECH_CATALOG_SEPARATOR = ", "
    }
}
