package com.gravitygroup.avangard.presentation.orders_history

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.ext.visible
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentOrdersHistoryBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.orders_history.adapter.OrdersHistoryAdapterDelegates
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class OrdersHistoryFragment : BaseFragment<OrdersHistoryVm>(R.layout.fragment_orders_history) {

    override val viewModel: OrdersHistoryVm by viewModel()

    private val vb by viewBinding(FragmentOrdersHistoryBinding::bind)

    override val stateBinding by lazy { OrdersHistoryStateBinding() }

    private val adapter by lazy { createAdapter() }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }

            layoutRecyclerView.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@OrdersHistoryFragment.adapter
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
        }

        viewModel.observeState(viewLifecycleOwner) { stateBinding.bind(it) }

        viewModel.getCompleteOrders()
    }

    inner class OrdersHistoryStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        private var orders by RenderProp(listOf<HistoryShortOrder>()) { historyOrders ->
            vb?.apply {
                layoutRecyclerView.recyclerView.visible(historyOrders.isNotEmpty())
                layoutRecyclerView.tvNoData.visible(historyOrders.isEmpty())
                adapter.items = historyOrders
                adapter.notifyDataSetChanged()
            }
        }

        override fun bind(data: IViewModelState) {
            data as OrdersHistoryState
            isLoading = data.isLoading
            orders = data.orders
        }
    }

    private fun showOrderInfo(order: HistoryShortOrder) {
        viewModel.navigateToOrderInfoScreen(order)
    }

    private fun createAdapter() = BaseDelegationAdapter(
        OrdersHistoryAdapterDelegates.ordersDelegate(this::showOrderInfo)
    )
}