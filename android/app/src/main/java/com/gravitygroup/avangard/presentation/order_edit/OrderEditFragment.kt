package com.gravitygroup.avangard.presentation.order_edit

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.getDefaultCalendar
import com.gravitygroup.avangard.core.ext.showDateDialog
import com.gravitygroup.avangard.core.ext.showDoubleTimeDialog
import com.gravitygroup.avangard.core.ext.showTimeDialog
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.DateUtils.fullDateToLongTime
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDate
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.core.utils.DateUtils.toTimeString
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentOrderEditBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType
import com.gravitygroup.avangard.presentation.catalog.data.CatalogSourceOpen
import com.gravitygroup.avangard.presentation.catalog.data.CatalogSpecs
import com.gravitygroup.avangard.presentation.order_edit.adapter.OrderEditDateAdapterDelegates
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.Correct
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.DateGrowError
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.TimeGrowError
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.TimeStartMoreEnd
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.WorkTimesError
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateTimeType
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateTimeType.Time
import com.gravitygroup.avangard.presentation.order_edit.data.TechFieldsOrderUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel.Companion.LONG_DEFAULT
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findName
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findType
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.gravitygroup.avangard.utils.popup
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class OrderEditFragment : BaseFragment<OrderEditViewModel>(R.layout.fragment_order_edit) {

    private val args: OrderEditFragmentArgs by navArgs()

    override val viewModel: OrderEditViewModel by sharedViewModel()

    private val vb by viewBinding(FragmentOrderEditBinding::bind)

    override val stateBinding by lazy { OrderEditStateBinding() }

    private val adapter by lazy { createAdapter() }

    private val order by lazy { args.specs }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fillState(order.dateTimeExec)
        super.onViewCreated(view, savedInstanceState)
        viewModel.showTimeSetupError.observe(viewLifecycleOwner, { typeError ->
            when (typeError) {
                is WorkTimesError -> R.string.edit_time_verification_text to R.string.edit_time_verification_title
                is TimeGrowError -> R.string.edit_time_grow_wrong to R.string.edit_time_verification_title
                is TimeStartMoreEnd -> R.string.edit_time_start_more_end_wrong to R.string.edit_time_verification_title
                is DateGrowError -> R.string.edit_date_grow_wrong to R.string.edit_date_verification_title
                is Correct -> CORRECT_RES_ID to CORRECT_RES_ID
            }
                .takeIf { (bodyResId, titleResId) -> bodyResId != CORRECT_RES_ID && titleResId != CORRECT_RES_ID }
                ?.also { (bodyResId, titleResId) ->
                    popup(getString(bodyResId))
                        .title(getString(titleResId))
                        .positive()
                        .alert()
                }
        })
    }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            icCheck.setSafeOnClickListener {
                val editOrder = OrderFullUIModel(
                    id = order.id,
                    nameTech = order.nameTech,
                    typeTech = order.typeTech,
                    dateTimeCreate = "${etCreateDate.text.toString()} ${etCreateTime.text.toString()}".fullDateToLongTime(),
                    num = etNumber.text.toString(),
                    latitude = order.latitude,
                    longitude = order.longitude,
                    addressName = etAddress.text.toString(),
                    equipment = etDescription.text.toString(),
                    defect = etMalfunction.text.toString(),
                    stage = etStep.text.toString(),
                    tel = etPhone.text.toString(),
                    dateTimeExec = order.dateTimeExec,
                    status = order.status,
                )
                viewModel.editOrderInfo(editOrder)
            }
            buttonAdd.setSafeOnClickListener {
                viewModel.addDatesToOrder()
            }

            rvDate.apply {
                val manager = LinearLayoutManager(requireContext())
                layoutManager = manager
                adapter = this@OrderEditFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainWhiteBackgroundColor))
            }

            etName.setSafeOnClickListener {
                viewModel.navigateToCatalog(
                    CatalogSpecs(
                        CatalogSourceOpen.Edit,
                        CatalogItemType.NAME
                    )
                )
            }
            etType.setSafeOnClickListener {
                viewModel.navigateToCatalog(
                    CatalogSpecs(
                        CatalogSourceOpen.Edit,
                        CatalogItemType.TYPE
                    )
                )
            }

            viewModel.initialOrder(order)
        }
    }

    override fun onPause() {
        super.onPause()
        vb?.apply {
            val savedInstanceOrder = CachedViewOrderState(
                num = etNumber.text.toString(),
                addressName = etAddress.text.toString(),
                equipment = etDescription.text.toString(),
                defect = etMalfunction.text.toString(),
                stage = etStep.text.toString(),
                tel = etPhone.text.toString(),
            )
            viewModel.cacheViewState(savedInstanceOrder)
        }
    }

    data class CachedViewOrderState(
        val num: String,
        val addressName: String,
        val equipment: String,
        val defect: String,
        val stage: String,
        val tel: String
    )

    private fun renderDateList(state: OrderEditState) {
        adapter.items = state.orderDates.toList()
        adapter.notifyDataSetChanged()
    }

    private fun renderTechFields(state: OrderEditState) {
    }

    private fun removeDate(order: DateTimeExecUIModel, position: Int) {
        viewModel.removeWorkDateTime(order, position)
    }

    private fun setDate(order: DateTimeExecUIModel, position: Int) {
        val orderDate = getDefaultCalendar()
        if (order.start != LONG_DEFAULT) {
            orderDate.time = Date(order.start)
        }

        requireContext().showDateDialog(orderDate) { date ->
            val start = date.time
            val end = LONG_DEFAULT
            viewModel.updateWorkDateTime(OrderDateTimeType.Date, DateTimeExecUIModel(position, start, end), position)
        }
    }

    private fun setTime(order: DateTimeExecUIModel, position: Int) {
        val startOrderDate = order.start
            .takeIf { it != LONG_DEFAULT }
            ?.let { Date(order.start) }
            ?: Date(System.currentTimeMillis())
        val endOrderDate = order.end
            .takeIf { it != LONG_DEFAULT }
            ?.let { Date(order.end) }
            ?: startOrderDate
        requireContext().showDoubleTimeDialog(startOrderDate, endOrderDate) { (start, end) ->
            viewModel.updateWorkDateTime(Time, DateTimeExecUIModel(position, start.time, end.time), position)
        }
    }

    private fun createAdapter() = BaseDelegationAdapter(
        OrderEditDateAdapterDelegates.orderEditDateDelegate(
            ::removeDate,
            ::setDate,
            ::setTime
        )
    )

    companion object {

        const val EMPTY_STRING = ""

        private const val CORRECT_RES_ID = 0
    }

    inner class OrderEditStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        private var orderDates by RenderProp(mutableListOf<DateTimeExecUIModel>()) {

        }

        private var techFieldsProps by RenderProp(TechFieldsOrderUIModel.empty()) { techFields ->
            val typeTech = techFields.directories.typeTech.findType(techFields.typeTechId)
            val nameTech = techFields.directories.nameTech.findName(techFields.nameTechId)
            vb?.apply {
                etType.setText(typeTech)
                etName.setText(nameTech)
            }
        }

        private var orderFullProp by RenderProp(OrderFullUIModel.empty()) { order ->
            vb?.apply {
                order.dateTimeCreate.also { dateTime ->
                    etCreateDate.setText(dateTime.toSimpleDateString())
                    etCreateTime.setText(dateTime.toTimeString())
                }
                etCreateDate.setSafeOnClickListener {
                    val orderDate = getDefaultCalendar()
                    orderDate.time = Date(order.dateTimeCreate)
                    requireContext().showDateDialog(orderDate) {
                        etCreateDate.setText(it.toSimpleDate())
                    }
                }
                etCreateTime.setSafeOnClickListener {
                    requireContext().showTimeDialog(Date(order.dateTimeCreate)) {
                        etCreateTime.setText(it.toTimeString())
                    }
                }

                etNumber.setText(order.num)
                etAddress.setText(order.addressName)
                etPhone.setText(order.tel)
                etDescription.setText(order.equipment)
                etStep.setText(order.stage)
                etMalfunction.setText(order.defect)
            }
        }

        override fun bind(data: IViewModelState) {
            data as OrderEditState
            isLoading = data.isLoading
            orderDates = data.orderDates
            techFieldsProps = TechFieldsOrderUIModel(
                data.directories,
                data.orderFullUIModel.nameTech,
                data.orderFullUIModel.typeTech
            )
            orderFullProp = data.orderFullUIModel
            renderTechFields(data)
            renderDateList(data)
        }
    }
}
