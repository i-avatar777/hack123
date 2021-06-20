package com.gravitygroup.avangard.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.*
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.doOnApplyWindowInsets
import com.gravitygroup.avangard.core.ext.dpToPx
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentMapOrdersBinding
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.Companion.toPointMapState
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.map.MapOrdersVm.MapOrdersViewState
import com.gravitygroup.avangard.presentation.map.adapter.AddressOrderAdapterDelegates
import com.gravitygroup.avangard.presentation.map.data.PointActiveState
import com.gravitygroup.avangard.presentation.map.data.PointActiveState.*
import com.gravitygroup.avangard.presentation.map.data.PointMap
import com.gravitygroup.avangard.presentation.map.data.StartMapType.MasterOrders
import com.gravitygroup.avangard.presentation.map.data.StartMapType.OpenOrders
import com.gravitygroup.avangard.presentation.orders.OrdersFragment
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.presentation.utils.SystemBackPressed
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider
import org.koin.android.viewmodel.ext.android.viewModel


class MapOrdersFragment : BaseFragment<MapOrdersVm>(R.layout.fragment_map_orders),
    MapObjectTapListener, SystemBackPressed {

    override val viewModel: MapOrdersVm by viewModel()

    override val stateBinding by lazy { MapOrdersStateBinding() }

    private val vb by viewBinding(FragmentMapOrdersBinding::bind)

    private val iconStyle by lazy {
        IconStyle().setAnchor(PointF(POINT_SIZE_ICON_STYLE_X, POINT_SIZE_ICON_STYLE_Y))
    }

    private val args: MapOrdersFragmentArgs by navArgs()

    private val startMapType by lazy { args.specs }

    private val addressOrdersAdapter by lazy { createAddressOrdersAdapter() }
    private var sheetBehavior: BottomSheetBehavior<View>? = null
    private val listHeightMax by lazy { resources.displayMetrics.heightPixels / 2 }

    private var systemWindowInsetTop = 0
    private var systemWindowInsetBottom = 0

    private val pinWithStatusList: MutableList<Pair<PlacemarkMapObject, PointActiveState>> = mutableListOf()

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            tvTitle.text = when (startMapType) {
                is OpenOrders -> getString(R.string.main_title)
                is MasterOrders -> getString(R.string.order_master_title)
                else -> ""
            }
            ivFilter.setSafeOnClickListener {
                viewModel.navigateToFilterScreen()
            }

            mapView.map.isRotateGesturesEnabled = true
            mapView.map.mapObjects.addTapListener(this@MapOrdersFragment)
            moveCamera(
                Point(LATITUDE_START_CAMERA_POS, LONGITUDE_START_CAMERA_POS),
                ZOOM_START_CAMERA_POS
            )

            bottomSheet.rvOrders.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = addressOrdersAdapter
            }
            bottomSheet.ivClose.setSafeOnClickListener {
                viewModel.onAddress(ADDRESS_NONE)
            }
            sheetBehavior = BottomSheetBehavior.from(bottomSheet.orderInfoContainer)
            sheetBehavior!!.isDraggable = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mapOrdersLiveData.observe(viewLifecycleOwner, { list ->
            viewModel.updateState {
                it.copy(orders = list)
            }
        })
        viewModel.requestOrders(startMapType)
    }

    override fun setupInsets() {
        vb?.root?.doOnApplyWindowInsets { view, insets ->
            systemWindowInsetTop = insets.systemWindowInsetTop
            systemWindowInsetBottom = insets.systemWindowInsetBottom
            vb?.appbarLayout?.updatePadding(
                top = systemWindowInsetTop
            )
            vb?.mapView?.updatePadding(
                bottom = systemWindowInsetBottom,
                top = systemWindowInsetTop
            )
            vb?.bottomSheet?.orderInfoContainer?.updatePadding(
                bottom = systemWindowInsetBottom
            )
            insets
        }
    }

    override fun onStop() {
        super.onStop()
        vb?.mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        sheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
    }

    override fun onStart() {
        super.onStart()
        vb?.mapView?.onStart()
        MapKitFactory.getInstance().onStart()
        sheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onDestroyView() {
        pinWithStatusList.clear()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        viewModel.onAddress(ADDRESS_NONE)
        return true
    }

    override fun onMapObjectTap(mapObject: MapObject, pointVal: Point): Boolean {
        return (mapObject as? PlacemarkMapObject)?.let { placeMark ->
            viewModel.onAddress(placeMark.userData as String)
            true
        } ?: false
    }

    private fun renderPoints(points: List<PointMap>) {
        val mapObjects = vb?.mapView?.map?.mapObjects ?: return
        if (pinWithStatusList.isEmpty()) {
            addPoints(mapObjects, points)
        } else {
            val actualAddresses = points.map { it.address }
            val pinStatusIter = pinWithStatusList.iterator()
            while (pinStatusIter.hasNext()) {
                val pinWithStatus = pinStatusIter.next()
                val pin = pinWithStatus.first
                val status = pinWithStatus.second
                if (!actualAddresses.contains(pin.userData)) {
                    mapObjects.remove(pin)
                    pinStatusIter.remove()
                } else {
                    val point = points.firstOrNull { it.address == pin.userData }
                    point?.let {
                        if (status != it.activationStatus) {
                            mapObjects.remove(pin)
                            pinStatusIter.remove()
                        }
                    }
                }
            }
            if (pinWithStatusList.size != actualAddresses.size) {
                val renderedAddresses = pinWithStatusList.map { it.first.userData }
                val newPoints = points.filter { !renderedAddresses.contains(it.address) }
                addPoints(mapObjects, newPoints)
            }
        }
    }

    private fun addPoints(mapObjects: MapObjectCollection, points: List<PointMap>) {
        points.forEach { pointMap ->
            val pin = createPinForOrder(requireContext(), pointMap.activationStatus)
            pin?.let { defaultImage ->
                val pm = mapObjects.addPlacemark(pointMap.geoPoint, defaultImage, iconStyle).apply {
                    userData = pointMap.address
                }
                pinWithStatusList.add(Pair(pm, pointMap.activationStatus))
            }
        }
    }

    private fun createPinForOrder(
        context: Context,
        activationStatus: PointActiveState
    ): ImageProvider? {
        return try {
            val pinDrawable = when (activationStatus) {
                Selected -> R.drawable.ic_map_active_marker
                else -> R.drawable.ic_map_marker
            }
            val bitmap = getBitmapFromVectorDrawable(context, pinDrawable, activationStatus)
            ImageProvider.fromBitmap(bitmap)
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getBitmapFromVectorDrawable(
        context: Context, @DrawableRes drawableId: Int, activationStatus: PointActiveState
    ): Bitmap? {
        return ContextCompat.getDrawable(context, drawableId)?.let { drawable ->
            val pinColor = when (activationStatus) {
                Base -> R.color.basePin
                Active -> R.color.activePin
                Inactive -> R.color.inactivePin
                Selected -> R.color.mainButton
            }
            drawable.setTint(context.getColor(pinColor))

            if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
                DrawableCompat.wrap(drawable).mutate()
            }
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(
                START_POSITION_BOUND,
                START_POSITION_BOUND,
                canvas.width,
                canvas.height
            )
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun renderOrderAddresses(data: List<OrderShortUIModel>, selectedAddress: String) {
        val points = data
            .groupBy(OrderShortUIModel::addressName)
            .map {
                val address = it.key
                val orders = it.value
                val isSelected = address == selectedAddress
                val firstOrder = orders[0]
                PointMap(
                    address = address,
                    geoPoint = Point(firstOrder.latitude.toDouble(), firstOrder.longitude.toDouble()),
                    activationStatus = if (isSelected) Selected else getOrdersActiveState(orders)
                )
            }
        if (points.isNotEmpty()) {
            renderPoints(points)
        }
    }

    private fun getOrdersActiveState(orders: List<OrderShortUIModel>): PointActiveState {
        val states = orders.map { it.status.toPointMapState() }
        return when {
            states.contains(Active) -> Active
            else -> states[0]
        }
    }

    private fun renderSelectedAddressInfo(orders: List<OrderShortUIModel>, selectedAddress: String) {
        if (selectedAddress == ADDRESS_NONE) {
            sheetBehavior?.apply {
                if (state != BottomSheetBehavior.STATE_HIDDEN) {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
            return

        }

        val addressOrders = orders.filter { it.addressName == selectedAddress }
        val listHeight =
            if (addressOrders.size <= 1) ViewGroup.LayoutParams.WRAP_CONTENT
            else listHeightMax

        vb?.bottomSheet?.apply {
            addressOrdersAdapter.items = orders.filter { it.addressName == selectedAddress }
            rvOrders.updateLayoutParams {
                height = listHeight
            }
            rvOrders.requestLayout()
            rvOrders.invalidate()
            rvOrders.postDelayed(300L) {
                sheetBehavior?.apply {
                    if (state != BottomSheetBehavior.STATE_EXPANDED) {
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                ensureSelectedPinVisible(selectedAddress, orderInfoContainer.measuredHeight)
            }
        }
    }

    private fun ensureSelectedPinVisible(selectedAddress: String, sheetHeight: Int) {
        val selectedPin = pinWithStatusList.firstOrNull { it.first.userData == selectedAddress }?.first
        selectedPin ?: return

        // Двигать, только если под плашкой
        val point = selectedPin.geometry
        vb?.mapView?.let { mapView ->
            val screenPt = mapView.worldToScreen(point) ?: return
            val screenPointY = screenPt.y
            val screenHeight = resources.displayMetrics.heightPixels
            val visiblePad = 16.dpToPx
            if (screenPointY > screenHeight - sheetHeight - systemWindowInsetTop - visiblePad) {
                val newScreenPt = ScreenPoint(
                    screenPt.x,
                    screenPointY - systemWindowInsetTop + sheetHeight / 2f
                )
                val moveTo = mapView.screenToWorld(newScreenPt)
                moveCamera(
                    moveTo,
                    mapView.map?.cameraPosition?.zoom ?: ZOOM_START_CAMERA_POS
                )
            }
        }
    }

    private fun moveCamera(point: Point, zoom: Float) {
        vb?.mapView?.map?.move(
            CameraPosition(
                point,
                zoom,
                AZIMUTH_START_CAMERA_POS,
                TILT_START_CAMERA_POS
            ),
            Animation(SMOOTH, ANIMATION_DURATION),
            null
        )
    }

    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    viewModel.onAddress(ADDRESS_NONE)
                }
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun createAddressOrdersAdapter() = BaseDelegationAdapter(
        AddressOrderAdapterDelegates.ordersDelegate {
            viewModel.navigateToOrderInfoScreen(it)
        }
    )

    inner class MapOrdersStateBinding : StateBinding() {

        private var selectedAddress = ADDRESS_NONE

        private var orders = listOf<OrderShortUIModel>()

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
                                    flexGrow = OrdersFragment.DEFAULT_FLEX_GROW

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

        private var isLoading by RenderProp(false) {
            vb?.progressBar?.isVisible = it
        }

        override fun bind(data: IViewModelState) {
            data as MapOrdersViewState
            selectedAddress = data.selectedAddress
            orders = data.orders.filter { it.latitude.isNotEmpty() && it.longitude.isNotEmpty() }
            filters = data.filters
            isLoading = data.isLoading
            renderOrderAddresses(orders, selectedAddress)
            renderSelectedAddressInfo(orders, selectedAddress)
        }
    }

    companion object {

        const val ADDRESS_NONE = ""

        private const val ANIMATION_DURATION = 0.4f // android middle

        private const val LATITUDE_START_CAMERA_POS = 58.010455
        private const val LONGITUDE_START_CAMERA_POS = 56.229443
        private const val ZOOM_START_CAMERA_POS = 11.0f
        private const val AZIMUTH_START_CAMERA_POS = 0.0f
        private const val TILT_START_CAMERA_POS = 0.0f
        private const val POINT_SIZE_ICON_STYLE_Y = 1f
        private const val POINT_SIZE_ICON_STYLE_X = 0.5f

        private const val START_POSITION_BOUND = 0
    }
}