package io.julius.chow.main.orders

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.transition.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentOrdersBinding
import io.julius.chow.domain.model.OrderState
import io.julius.chow.domain.model.UserType
import io.julius.chow.main.food.FoodDetailsFragment.Companion.FOOD
import io.julius.chow.main.food.FoodDetailsFragment.Companion.USER_TYPE
import io.julius.chow.model.Order
import io.julius.chow.util.Event
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {

    override val contentResource = R.layout.fragment_orders

    private lateinit var orderViewModel: OrderViewModel

    // Pass this fragment to the adapter as lifecycle owner so that we can have live data updates for data binding
    private val orderAdapter: OrderAdapter = OrderAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        // Create viewmodel to be shared by this fragment and the order confirmation fragment
        orderViewModel = viewModel(viewModelFactory) {

            // Subscription to LiveData in OrderViewModel
            observe(orderViewContract, ::viewStateResponse)
            observe(orders, ::updateOrders)
            observe(currentAccountType, ::userTypeActions)
        }

        orderViewModel.getCurrentAccountType()

        val transition = TransitionSet().setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeBounds())
            .addTransition(ChangeTransform())
            .addTransition(ChangeImageTransform())

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        enterTransition = Fade(Fade.MODE_IN)
        exitTransition = Fade(Fade.MODE_OUT)
    }

    override fun initialize(state: Bundle?) {
        // Set recycler view adapter to order adapter
        dataBinding.recyclerView.adapter = orderAdapter
    }

    private fun userTypeActions(userType: UserType?) {
        when (userType) {
            UserType.CUSTOMER -> {
                // Hide the category selector group
                category_toggle_group.visibility = View.GONE

                // Make call to viewmodel to fetch orders
                orderViewModel.getOrders(userType)

                // Make call to viewmodel to fetch current user and keep a reference in case we need to check out
                orderViewModel.getCurrentUser()

                // Observe the total order cost from the adapter
                orderAdapter.totalOrderCost.observe(this, Observer {
                    orderViewModel.subTotalOrderCost = it

                    label_total_cost.text = resources.getString(R.string.thousand_format, it)

                    if (it > 0) {
                        button_check_out_order.setBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.colorAccent
                            )
                        )
                        button_check_out_order.isClickable = true
                    } else {
                        button_check_out_order.setBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.gray
                            )
                        )
                        button_check_out_order.isClickable = false
                    }
                })

                // Prepare the restaurants adapter for item click listening
                orderAdapter.listener = { order, image ->
                    if (image == null) {
                        // From our design, if the image is null, then the click operation was to remove the current order
                        orderViewModel.removeOrder(order)
                    } else {
                        // Put order food id in bundle to fetch food in detail view
                        val bundle = bundleOf(FOOD to order.food)
                        // Put the food image in an extra for shared element transition
                        val extras = FragmentNavigatorExtras(
                            image to order.food.id
                        )
                        // Navigate to detail view
                        findNavController().navigate(
                            R.id.action_orders_to_foodDetails,
                            bundle,
                            null,
                            extras
                        )
                    }
                }

                // Click listener for place order button
                button_check_out_order.setOnClickListener {
                    if (orderAdapter.totalOrderCost.value!! > 0) {
                        ConfirmOrderFragment.newInstance(orderViewModel).show(fragmentManager!!, "")
                    }
                }
            }

            UserType.RESTAURANT -> {
                button_check_out_order.visibility = View.GONE
                empty_feed_view_subtitle.visibility = View.GONE

                // Display the category selector group
                category_toggle_group.visibility = View.VISIBLE
                button_active_orders.isChecked = true
                button_active_orders.setOnClickListener {
                    (it as MaterialButton).isChecked = true
                    orderViewModel.filterOrders(OrderState.ACTIVE)
                }
                button_processed_orders.setOnClickListener {
                    (it as MaterialButton).isChecked = true
                    orderViewModel.filterOrders(OrderState.PROCESSED)
                }

                val orderSwipeController =
                    OrderSwipeController(object : OrderSwipeController.Actions {
                        override fun onOrderProcessed(position: Int) {
                            val processedOrder = orderAdapter.getOrder(position)
                            orderAdapter.removeOrder(position)
                            // Display snack bar with Undo option
                            Snackbar.make(view!!, "Order processed.", Snackbar.LENGTH_LONG).apply {
                                this.view.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.colorAccent
                                    )
                                )
                                this.setAction("UNDO") {
                                    orderAdapter.restoreOrder(processedOrder, position)
                                }
                                setActionTextColor(Color.YELLOW)
                                addCallback(object :
                                    BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                                            || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                                        ) {
                                            // Snackbar closed on its own or new Snackbar is shown, we can update the database
                                            processedOrder.status = OrderState.PROCESSED
                                            orderViewModel.updateOrder(processedOrder)
                                        }
                                    }
                                })
                                show()
                            }
                        }

                        override fun onOrderRejected(position: Int) {
                            val rejectedOrder = orderAdapter.getOrder(position)
                            orderAdapter.removeOrder(position)
                            // Display snack bar with Undo option
                            Snackbar.make(view!!, "Order rejected.", Snackbar.LENGTH_LONG).apply {
                                this.view.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.colorAccent
                                    )
                                )
                                this.setAction("UNDO") {
                                    orderAdapter.restoreOrder(rejectedOrder, position)
                                }
                                setActionTextColor(Color.YELLOW)
                                addCallback(object :
                                    BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                                            || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                                        ) {
                                            // Snackbar closed on its own or new Snackbar is shown, we can update the database
                                            rejectedOrder.status = OrderState.REJECTED
                                            orderViewModel.updateOrder(rejectedOrder)
                                        }
                                    }
                                })
                                show()
                            }
                        }
                    })

                val itemTouchDelegate = ItemTouchHelper(orderSwipeController)
                itemTouchDelegate.attachToRecyclerView(recycler_view)

                // Update the userType on the adapter
                orderAdapter.userType = userType

                orderViewModel.orderStatus = OrderState.ACTIVE
                orderViewModel.getOrders(userType)

                orderAdapter.listener = { order, image ->
                    val bundle = bundleOf(
                        FOOD to order.food,
                        USER_TYPE to UserType.RESTAURANT
                    )
                    val extras = FragmentNavigatorExtras(
                        image!! to order.food.id
                    )
                    // Navigate to detail view
                    findNavController().navigate(
                        R.id.action_orders_to_foodDetails,
                        bundle,
                        null,
                        extras
                    )
                }
            }
        }
    }

    private fun viewStateResponse(event: Event<OrderViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is OrderViewContract.ProgressDisplay -> {
                    // Toggle progress bar visibility
                    if (data.display && orderAdapter.itemCount <= 0) progress_bar.visibility =
                        View.VISIBLE
                    else progress_bar.visibility = View.INVISIBLE
                }

                is OrderViewContract.MessageDisplay -> {
                    // Display message feedback to user
                    notify(data.message)
                }
            }
        }
    }

    private fun updateOrders(orders: List<Order>) {
        dataBinding.orders = orders
        orderAdapter.setData(orders)
    }
}
