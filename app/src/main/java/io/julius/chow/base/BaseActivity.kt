package io.julius.chow.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_layout)
//
//        addFragment(savedInstanceState)
//    }
//
//    override fun onBackPressed() {
//        (supportFragmentManager.findFragmentById(
//            R.id.fragment_container) as BaseFragment<*>).onBackPressed()
//        super.onBackPressed()
//    }
//
//    private fun addFragment(savedInstanceState: Bundle?) =
//        savedInstanceState ?: supportFragmentManager.inTransaction { add(
//            R.id.fragment_container, fragment()) }
//
//    abstract fun fragment(): BaseFragment<*>

}