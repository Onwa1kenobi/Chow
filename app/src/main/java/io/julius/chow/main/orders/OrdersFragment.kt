package io.julius.chow.main.orders

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentOrdersBinding
import io.julius.chow.domain.model.UserType
import io.julius.chow.main.food.FoodDetailsFragment.Companion.FOOD
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
                findNavController().navigate(R.id.action_orders_to_foodDetails, bundle, null, extras)
            }
        }

        // Set recycler view adapter to order adapter
        dataBinding.recyclerView.adapter = orderAdapter
    }

    private fun userTypeActions(userType: UserType?) {
        when (userType) {
            UserType.CUSTOMER -> {
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
                        button_check_out_order.setBackgroundColor(ContextCompat.getColor(context!!, R.color.gray))
                        button_check_out_order.isClickable = false
                    }
                })

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

                orderViewModel.getOrders(userType)
            }
        }
    }

    private fun viewStateResponse(event: Event<OrderViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is OrderViewContract.ProgressDisplay -> {
                    // Toggle progress bar visibility
                    if (data.display && orderAdapter.itemCount <= 0) progress_bar.visibility =
                        View.VISIBLE else progress_bar.visibility =
                        View.INVISIBLE
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
