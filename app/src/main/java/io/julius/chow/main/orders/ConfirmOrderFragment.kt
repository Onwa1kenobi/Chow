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


class ConfirmOrderFragment : RoundedBottomSheetDialogFragment() {

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
    }

}
