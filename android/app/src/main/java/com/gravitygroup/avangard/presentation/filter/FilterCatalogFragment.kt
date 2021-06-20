package com.gravitygroup.avangard.presentation.filter

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentFilterCatalogBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.filter.adapter.FilterCatalogAdapterDelegate
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.Name
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.None
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.Type
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class FilterCatalogFragment : BaseFragment<FilterCatalogVm>(R.layout.fragment_filter_catalog) {

    companion object {

        const val FILTER_CATALOG_REQUEST_KEY = "CATALOG_REQUEST_KEY"
        private const val FILTER_CATALOG_TYPE = "CATALOG_TYPE"
        private const val FILTER_CATALOG_DATA = "CATALOG_DATA"

        fun getFilterResult(bundle: Bundle): Pair<FilterCatalogItemType, List<FilterCatalogUIModel>> {
            val type = bundle[FILTER_CATALOG_TYPE] as FilterCatalogItemType
            val data = bundle[FILTER_CATALOG_DATA] as List<FilterCatalogUIModel>
            return type to data
        }
    }

    override val viewModel: FilterCatalogVm by viewModel()

    private val args: FilterCatalogFragmentArgs by navArgs()

    private val vb by viewBinding(FragmentFilterCatalogBinding::bind)

    override val stateBinding by lazy { FilterCatalogStateBinding() }

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
                adapter = this@FilterCatalogFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackgroundColor))
            }
            setupHint(false)
            icCheck.setSafeOnClickListener {
                viewModel.state.value?.also { state ->
                    setFragmentResult(
                        FILTER_CATALOG_REQUEST_KEY,
                        bundleOf(
                            FILTER_CATALOG_TYPE to specs.type,
                            FILTER_CATALOG_DATA to state.filterItem.filter { it.isChecked }
                        )
                    )
                }
                viewModel.navigateBack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.requestFilterItem(specs)
    }

    private fun setupHint(state: Boolean) {
        vb?.apply {
            when (specs.type) {
                Type -> tvTitle.text = getString(R.string.catalog_title_name)
                Name -> tvTitle.text = getString(R.string.catalog_title_type)
                None -> {
                }
            }
        }
    }

    private fun renderCatalogList(list: List<FilterCatalogUIModel>) {
        adapter.items = list
    }

    private fun itemClick(filterCatalog: FilterCatalogUIModel) {
        viewModel.setupFilterCatalog(filterCatalog)
        vb?.apply {
            icCheck.isVisible = true
        }
    }

    inner class FilterCatalogStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        override fun bind(data: IViewModelState) {
            data as FilterCatalogState
            renderCatalogList(data.filterItem)
            data.filterItem.filter { it.isChecked && it.type is Name }
            isLoading = data.isLoading
        }
    }

    private fun createAdapter() = BaseDelegationAdapter(
        FilterCatalogAdapterDelegate.filterCatalogDelegate(
            ::itemClick,
        )
    )
}