package io.julius.chow.main.orders


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.julius.chow.R
import io.julius.chow.util.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_confirm_order.*
import java.util.*


class ConfirmOrderFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var orderViewModel: OrderViewModel

    private val editing = MutableLiveData<Boolean>().apply {
        value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_change_address.setOnClickListener {
            editing.value = editing.value!!.not()
        }

        editing.observe(this, Observer {
            // This happens when the current time is after 5:00 PM
            if (it == null) return@Observer

            button_place_order.isEnabled = it.not()
            when (it) {
                true -> {
                    button_change_address.text = getString(R.string.done)
                    field_user_address.isEnabled = true
                }

                false -> {
                    button_change_address.text = getString(R.string.change)
                    field_user_address.isEnabled = false
                }
            }
        })

        updateAvailableTimes(Calendar.getInstance())

        label_user_name.text =
            "${orderViewModel.currentUser.value?.name.toString()} | ${orderViewModel.currentUser.value?.phoneNumber}"

        field_user_address.setText("${orderViewModel.currentUser.value?.address}")

        label_sub_total_amount.text =
            resources.getString(R.string.naira_thousand_format, orderViewModel.subTotalOrderCost)
        label_tax_amount.text = resources.getString(R.string.naira_thousand_format, orderViewModel.tax)
        label_delivery_charge_amount.text =
            resources.getString(R.string.naira_thousand_format, orderViewModel.deliveryCharge)

        button_place_order.text = resources.getString(R.string.place_order) + " for " + resources.getString(
            R.string.naira_thousand_format,
            orderViewModel.subTotalOrderCost
        )
    }

    private fun updateAvailableTimes(currentTime: Calendar) {
        val tenAM = Calendar.getInstance()
        tenAM.set(Calendar.HOUR_OF_DAY, 10)
        tenAM.set(Calendar.MINUTE, 0)
        tenAM.set(Calendar.SECOND, 0)

        val onePM = Calendar.getInstance()
        onePM.set(Calendar.HOUR_OF_DAY, 13)
        onePM.set(Calendar.MINUTE, 0)
        onePM.set(Calendar.SECOND, 0)

        val fivePM = Calendar.getInstance()
        fivePM.set(Calendar.HOUR_OF_DAY, 17)
        fivePM.set(Calendar.MINUTE, 0)
        fivePM.set(Calendar.SECOND, 0)

        when {
            currentTime.after(fivePM) -> {
                checkbox_ten_eleven.isEnabled = false
                checkbox_one_two.isEnabled = false
                checkbox_five_six.isEnabled = false

                radio_group.clearCheck()

                button_change_address.isEnabled = false
                button_place_order.isEnabled = false
                editing.value = null
            }

            currentTime.after(onePM) -> {
                checkbox_ten_eleven.isEnabled = false
                checkbox_one_two.isEnabled = false

                radio_group.check(R.id.checkbox_five_six)
            }

            currentTime.after(tenAM) -> {
                checkbox_ten_eleven.isEnabled = false

                radio_group.check(R.id.checkbox_one_two)
            }

            else -> {
                radio_group.check(R.id.checkbox_ten_eleven)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(viewModel: OrderViewModel) = ConfirmOrderFragment().apply { this.orderViewModel = viewModel }
    }
}
