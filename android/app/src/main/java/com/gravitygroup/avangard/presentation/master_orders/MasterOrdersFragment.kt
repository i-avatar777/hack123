package com.gravitygroup.avangard.presentation.master_orders

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
import com.gravitygroup.avangard.core.ext.getDefaultCalendar
import com.gravitygroup.avangard.core.ext.showDateDialog
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.helper.BlueMidlineItemDecoration
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.DateUtils.toWordsDate
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.DayViewBinding
import com.gravitygroup.avangard.databinding.EventViewBinding
import com.gravitygroup.avangard.databinding.FragmentMasterOrdersBinding
import com.gravitygroup.avangard.domain.orders.EventData
import com.gravitygroup.avangard.domain.orders.EventStatus
import com.gravitygroup.avangard.presentation.RootActivity
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.master_orders.MasterOrdersViewModel.Companion.MINUTES_IN_HOUR
import com.gravitygroup.avangard.presentation.master_orders.apapter.NoTimeMasterOrderAdapterDelegates
import com.gravitygroup.avangard.presentation.orders.OrdersFragment.Companion.DEFAULT_FLEX_GROW
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.linkedin.android.tachyon.DayView
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MasterOrdersFragment : BaseFragment<MasterOrdersViewModel>(R.layout.fragment_master_orders) {

    override val viewModel: MasterOrdersViewModel by viewModel()

    private val vb by viewBinding(FragmentMasterOrdersBinding::bind)

    override val stateBinding by lazy { MasterOrdersStateBinding() }

    private val adapter by lazy { createAdapter() }

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

            ivFilter.setSafeOnClickListener {
                viewModel.navigateToFilterScreen()
            }

            ivLocation.setSafeOnClickListener {
                viewModel.navigateToMapScreen()
            }

            viewModel.requestCalendarView()
            setupCalendarView()

            ivCalendar.setSafeOnClickListener {
                val calendar = getDefaultCalendar()
                calendar.time = Date()
                requireContext().showDateDialog(calendar) { time ->
                    tvDate.text = time.toWordsDate()
                    viewModel.setupChosenDate(time)
                    viewModel.requestCalendarView()
                }
            }

            noTimeOrders.apply {
                val manager = LinearLayoutManager(requireContext())
                layoutManager = manager
                adapter = this@MasterOrdersFragment.adapter
                addItemDecoration(
                    BlueMidlineItemDecoration(
                        requireContext()
                    )
                )
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainWhiteBackgroundColor))
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.resetChosenDate()
    }

    private fun setupCalendarView() {
        vb?.apply {
            val day = getDefaultCalendar()
            day.set(Calendar.HOUR_OF_DAY, 0)
            day.set(Calendar.MINUTE, 0)
            day.set(Calendar.SECOND, 0)
            day.set(Calendar.MILLISECOND, 0)
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            val hour = day.clone() as Calendar
            val hoursViews: MutableList<View> = mutableListOf()
            for (i in dayView.startHour..dayView.endHour step 1) {
                hour.set(Calendar.HOUR_OF_DAY, i)
                val view = DayViewBinding.inflate(layoutInflater, root, false)
                view.tvHour.text = timeFormat.format(hour.time)
                hoursViews.add(view.root)
            }
            dayView.setHourLabelViews(hoursViews)
        }
    }

    private fun renderEvents(state: MasterOrdersState) {
        vb ?: return

        if (state.timingEvents.isNotEmpty()) {
            val eventViews: MutableList<View> = mutableListOf()
            val eventTimes: MutableList<DayView.EventTimeRange> = mutableListOf()
            for (event in state.timingEvents) {
                val view = EventViewBinding.inflate(layoutInflater, vb!!.root, false)
                when (event.status) {
                    EventStatus.TIME -> view.ivClock.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        ), android.graphics.PorterDuff.Mode.SRC_ATOP
                    )
                    EventStatus.NOTSETPUSH -> view.ivClock.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue_grey
                        ), android.graphics.PorterDuff.Mode.SRC_ATOP
                    )
                    EventStatus.SETPUSH -> view.ivClock.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        ), android.graphics.PorterDuff.Mode.SRC_ATOP
                    )
                }
                view.eventRoot.setSafeOnClickListener {
                    viewModel.navigateToOrderInfo(event.id)
                }
                view.tvText.text = event.title
//                eventViews.add(view.root)
                val startMinute = MINUTES_IN_HOUR * event.hour + event.minute
                val endMinute = startMinute + event.duration
                eventTimes.add(DayView.EventTimeRange(startMinute, endMinute))
                eventViews.add(view.root)
            }
            vb!!.dayView.setEventViews(eventViews, eventTimes)
        } else {
            val eventViews: MutableList<View> = mutableListOf()
            val eventTimes: MutableList<DayView.EventTimeRange> = mutableListOf()
            vb!!.dayView.setEventViews(eventViews, eventTimes)
        }
    }

    private fun createAdapter() = BaseDelegationAdapter(
        NoTimeMasterOrderAdapterDelegates.noTimeOrderDelegate(
            ::navigateToOrderNotification,
            ::navigateToEvent
        )
    )

    private fun navigateToEvent(idOrder: Long) {
        viewModel.navigateToOrderInfo(idOrder)
    }

    private fun navigateToOrderNotification(idOrder: Long) {
        viewModel.navigateToNotifyOrder(idOrder)
    }

    inner class MasterOrdersStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            vb?.progressBar?.isVisible = it
        }

        private var titleChoseDate by RenderProp(Date(System.currentTimeMillis())) { time ->
            vb?.tvDate?.text = time.toWordsDate()
        }

        private var events by RenderProp(mutableListOf<EventData>()) {

        }

        private var noTimeEvents by RenderProp(listOf<EventData>()) { events ->
            adapter.items = events
            adapter.notifyDataSetChanged()
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
            data as MasterOrdersState
            isLoading = data.isLoading
            titleChoseDate = data.chosenDate
            filters = data.filters
            events = data.timingEvents
            noTimeEvents = data.noTimeEvents
            renderEvents(data)
        }
    }
}
