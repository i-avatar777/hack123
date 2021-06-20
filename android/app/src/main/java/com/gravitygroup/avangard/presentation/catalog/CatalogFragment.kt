package com.gravitygroup.avangard.presentation.catalog

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentCatalogBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.catalog.adapter.CatalogAdapterDelegates
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType
import com.gravitygroup.avangard.presentation.catalog.data.CatalogSourceOpen.Edit
import com.gravitygroup.avangard.presentation.catalog.data.CatalogSourceOpen.Filter
import com.gravitygroup.avangard.presentation.catalog.data.CatalogUIModel
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel.Companion.toFilter
import com.gravitygroup.avangard.presentation.filter.data.FilterUIModel
import com.gravitygroup.avangard.presentation.order_edit.OrderEditViewModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class CatalogFragment : BaseFragment<CatalogViewModel>(R.layout.fragment_catalog) {

    override val viewModel: CatalogViewModel by viewModel()

    private val editOrderViewModel: OrderEditViewModel by sharedViewModel()

    private val args: CatalogFragmentArgs by navArgs()

    private val vb by viewBinding(FragmentCatalogBinding::bind)

    override val stateBinding by lazy { CatalogStateBinding() }

    private val adapter by lazy { createAdapter() }

    private val specs by lazy { args.specs }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }

            layoutRecyclerView.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@CatalogFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackgroundColor))
            }
            setupHint(false)
            etSearchField.setOnFocusChangeListener { _, hasFocus ->
                setupHint(hasFocus)
            }
            icCheck.setSafeOnClickListener {
                viewModel.state.value?.also { state ->
                    val catalogModel = state.filterItem.find { it.title == etSearchField.text.toString() }!!
                    when (specs.sourceOpen) {
                        Edit -> {
                            val filter = FilterUIModel(
                                state.catalogType.toFilter(),
                                catalogModel
                            )
                            editOrderViewModel.updateCategoryOrder(filter)
                        }
                        Filter -> {
                        }
                    }

                }
                viewModel.navigateBack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.requestFilterItem(specs.itemType)
    }

    private fun setupHint(state: Boolean) {
        vb?.apply {
            when (specs.itemType) {
                CatalogItemType.NAME -> {
                    tvTitle.text = getString(R.string.catalog_title_name)
                    tilSearchField.hint =
                        if (state) getString(R.string.catalog_input_name) else getString(R.string.catalog_hint_name)
                }
                CatalogItemType.TYPE -> {
                    tvTitle.text = getString(R.string.catalog_title_type)
                    tilSearchField.hint =
                        if (state) getString(R.string.catalog_input_type) else getString(R.string.catalog_hint_type)
                }
                CatalogItemType.STATUS -> {
                    tvTitle.text = getString(R.string.catalog_title_status)
                    tilSearchField.hint =
                        if (state) getString(R.string.catalog_input_status) else getString(R.string.catalog_hint_status)
                }
            }
        }
    }

    private fun renderCatalogList(list: List<CatalogUIModel>) {
        adapter.items = list
    }

    private fun itemClick(catalog: CatalogUIModel) {
        vb?.apply {
            icCheck.isVisible = true
            etSearchField.setText(catalog.title)
        }
    }

    inner class CatalogStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        override fun bind(data: IViewModelState) {
            data as CatalogState
            renderCatalogList(data.filterItem)
            isLoading = data.isLoading
        }
    }

    private fun createAdapter() = BaseDelegationAdapter(
        CatalogAdapterDelegates.catalogDelegate(
            ::itemClick,
        )
    )
}
