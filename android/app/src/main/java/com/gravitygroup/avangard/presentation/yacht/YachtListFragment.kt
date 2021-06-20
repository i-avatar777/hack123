package com.gravitygroup.avangard.presentation.yacht

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.ext.visible
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.databinding.FragmentYachtListBinding
import com.gravitygroup.avangard.interactors.yacht.data.YachtUIModel
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.base.Notify.ErrorHide
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Captain
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Client
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import com.gravitygroup.avangard.presentation.yacht.adapter.YachtAdapterDelegates
import com.gravitygroup.avangard.utils.popup
import org.koin.android.viewmodel.ext.android.sharedViewModel

class YachtListFragment : BaseFragment<YachtFilterVm>(R.layout.fragment_yacht_list) {

    override val viewModel: YachtFilterVm by sharedViewModel()

    private val vb by viewBinding(FragmentYachtListBinding::bind)

    override val stateBinding by lazy { YachtListStateBinding() }

    private val args: YachtListFragmentArgs by navArgs()

    private val params by lazy { args.specs }

    private val adapter by lazy { createAdapter() }

    private var sheetBehavior: BottomSheetBehavior<View>? = null

    private val listHeightMax by lazy { resources.displayMetrics.heightPixels / 2 }

    private var systemWindowInsetTop = 0
    private var systemWindowInsetBottom = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.socketMessageObserve.observe(viewLifecycleOwner, {
            when (it) {
                Captain -> {
//                    (vb?.bottomSheet as? FrameLayout)?.isVisible = true
//                    renderOrderIncoming()
                    popup(getString(R.string.yacht_captain_incoming_order_title))
                        .title(getString(R.string.yacht_captain_incoming_order_hint))
                        .positive()
                        .alert()
//                    requireContext().bottomSheet()
//                        .header(BottomSheetHeaderView(context, null).also {
//                        it.title = getString(R.string.goal_create_goal_screen_choose_account_title)
//                    })
//                        .items(
//                            items.map {
//                                it
//                                    .toGoalSourceAccountModel(iconProvider, this@GoalParametersActivity)
//                                    .toAccountCellView(context)
//                            }.toTypedArray()
//                        )
//                        .closeByClick()
//                        .closeButtonId(R.id.closeImageButton)
//                        .show { onUserCheckAccountForTransfer(items[it]) }
                }

                Client -> viewModel.notify(ErrorHide(getString(R.string.yacht_client_allow_order)))
                is Error -> viewModel.notify(Notify.Error(it.message ?: getString(R.string.yacht_socket_error)))
                else -> {
                }
            }
        })

        viewModel.requestYachtList(params.placeCount, params.typeRent, params.priceFrom, params.priceTo)
        viewModel.initSocket()
    }

    override fun setupViews() {
        vb?.apply {
            layoutRecyclerView.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@YachtListFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackgroundColor))
            }

            sheetBehavior = BottomSheetBehavior.from(bottomSheet.orderInfoContainer)
            sheetBehavior!!.isDraggable = false
        }
    }

    override fun onStop() {
        super.onStop()
        sheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
    }

    override fun onStart() {
        super.onStart()
        sheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {

                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun renderOrders(yachtList: List<YachtUIModel>) {
        vb?.apply {
            layoutRecyclerView.recyclerView.visible(yachtList.isNotEmpty())
            adapter.items = yachtList
        }
    }

    private fun renderOrderIncoming() {
        val listHeight = listHeightMax

        vb?.bottomSheet?.apply {
            vb?.layoutRecyclerView?.recyclerView?.apply {
                updateLayoutParams {
                    height = listHeight
                }
                requestLayout()
                invalidate()
                postDelayed(300L) {
                    sheetBehavior?.apply {
                        if (state != BottomSheetBehavior.STATE_EXPANDED) {
                            state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                }
            }
        }
    }

    private fun requestYachtOrder(yachtModel: YachtUIModel) {
//        val intent = Intent(activity, ChatRoomActivity::class.java)
//        intent.putExtra("userName", "new-user")
//        intent.putExtra("roomName", "'room1'")
//        startActivity(intent)
        showToastMessage("Send Request")
        viewModel.sendYachtRequest()
    }

    private fun createAdapter() = BaseDelegationAdapter(
        YachtAdapterDelegates.yachtListDelegate(this::requestYachtOrder)
    )

    companion object {

        private const val TAG = "YachtListFragment"
    }

    inner class YachtListStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            vb?.layoutRecyclerView?.progressBar?.isVisible = it
        }

        override fun bind(data: IViewModelState) {
            data as YachtListState
            isLoading = data.isLoading
            if (data.yachtData.isNotEmpty()) {
                renderOrders(data.yachtData)
            }
        }
    }
}