package com.gravitygroup.avangard.presentation.orders

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.ext.visible
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentOrdersBinding
import com.gravitygroup.avangard.presentation.RootActivity
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.orders.adapter.OrderAdapterDelegates
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class OrdersFragment : BaseFragment<OrdersVm>(R.layout.fragment_orders) {

    override val viewModel: OrdersVm by viewModel()

    private val vb by viewBinding(FragmentOrdersBinding::bind)

    override val stateBinding by lazy { OrdersStateBinding() }

    private val adapter by lazy { createAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getOnSeenOrders()
    }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                (requireActivity() as RootActivity).showDrawer(
                    viewLifecycleOwner,
                    containerRoot,
                    requireContext()
                )
            }

            layoutRecyclerView.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@OrdersFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackgroundColor))
            }

            layoutRecyclerView.swipeRefreshLayout.apply {
                setOnRefreshListener {
                    isRefreshing = false
                }
            }
            ivFilter.setSafeOnClickListener {
                viewModel.navigateToFilterScreen()
            }
            ivLocation.setSafeOnClickListener {
                viewModel.navigateToMapOrdersScreen()
            }
        }

        viewModel.openOrdersLiveData.observe(viewLifecycleOwner, { list ->
            viewModel.updateState {
                it.copy(orderDetails = list)
            }
        })
    }

    private fun renderOrders(state: OrdersState) {
        val orders = state.orderDetails
        vb?.apply {
            layoutRecyclerView.recyclerView.visible(orders.isNotEmpty())
            layoutRecyclerView.tvNoData.visible(state.isOrdersUpdated && orders.isEmpty())
            adapter.items = orders
        }
    }

    inner class OrdersStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            vb?.layoutRecyclerView?.progressBar?.isVisible = it
        }

        private var filters by RenderProp(listOf<OrderFilterUIModel>()) { orderFilters ->
            vb?.apply {
                if (orderFilters.isNotEmpty()) {
                    filterRoot.tvFilter.isVisible = true
                    filterRoot.filtersGroup.isVisible = true
                    filterRoot.filtersGroup.removeAllViews()
                    orderFilters.map { orderFilter ->
                        val buttonRoot = View.inflate(
                            requireContext(),
                            R.layout.item_filter_item,
                            null
                        ) as FrameLayout
                        buttonRoot.findViewById<ImageFilterView>(R.id.ivClose)
                            .setSafeOnClickListener {
                                viewModel.removeFilter(orderFilter.type)
                            }
                        buttonRoot.findViewById<TextView>(R.id.tvText).apply {
                            val params = FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT
                            )
                                .apply {
                                    flexGrow = DEFAULT_FLEX_GROW

                                    (this as? MarginLayoutParams)?.updateMargins(
                                        right = context.resources.getDimensionPixelSize(R.dimen.default_margin_10),
                                        bottom = context.resources.getDimensionPixelSize(R.dimen.default_margin_10)
                                    )
                                }

                            buttonRoot.layoutParams = params

                            text = when (orderFilter.type) {
                                is FilterTypeUIModel.Type -> "${getString(orderFilter.type.titleRes)} - ${orderFilter.type.title}"
                                is FilterTypeUIModel.Name -> "${getString(orderFilter.type.titleRes)} - ${orderFilter.type.title}"
                                else -> getString(orderFilter.type.titleRes)
                            }
                        }

                        filterRoot.filtersGroup.addView(buttonRoot)
                    }
                } else {
                    filterRoot.tvFilter.isVisible = false
                    filterRoot.filtersGroup.isVisible = false
                }
            }
        }

        override fun bind(data: IViewModelState) {
            data as OrdersState
            isLoading = data.isLoading
            filters = data.filters
            renderOrders(data)
        }
    }

    private fun showOrderInfo(order: OrderShortUIModel) {
        viewModel.navigateToOrderInfoScreen(order)
    }

    private fun createAdapter() = BaseDelegationAdapter(
        OrderAdapterDelegates.ordersDelegate(this::showOrderInfo)
    )

    companion object {

        const val DEFAULT_FLEX_GROW = 1f
    }
}
