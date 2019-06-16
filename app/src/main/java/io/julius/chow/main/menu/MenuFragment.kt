package io.julius.chow.main.menu

import android.os.Bundle
import android.view.View
import android.widget.TextView
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.databinding.FragmentMenuBinding
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : BaseFragment<FragmentMenuBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_menu

    private val menuCategories = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)
    }

    override fun initialize(state: Bundle?) {
        dataBinding.clickListener = this

        menuCategories.addAll(listOf(label_breakfast, label_lunch, label_dinner))
    }

    override fun onClick(v: View?) {
        v?.let {
            for (category in menuCategories) {
                category.isSelected = category == v
            }
        }
    }
}
