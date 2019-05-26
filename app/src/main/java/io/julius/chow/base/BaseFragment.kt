package io.julius.chow.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import io.julius.chow.R
import io.julius.chow.app.App
import io.julius.chow.base.extension.appContext
import io.julius.chow.di.AppComponent
import javax.inject.Inject

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {
    /**
     * Data binding object for an instance fragment that extends this class
     */
    lateinit var dataBinding: DB

    private var rootView: View? = null

    /**
     * Layout resource to be inflated
     *
     * @return layout resource
     */
    @get:LayoutRes
    protected abstract val contentResource: Int

//    /**
//     * @return coordinator layout for the fragment instance
//     */
//    protected abstract val coordinatorContainer: View?

    /**
     * Dependency injection component for injecting fragment instance
     */
    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as App).appComponent
    }

    /**
     * View model factory for every BaseFragment implementation
     */
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            dataBinding = DataBindingUtil.inflate(inflater, contentResource, container, false)
            rootView = dataBinding.root
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize(savedInstanceState)
    }

    /**
     * Initialisations
     */
    protected abstract fun initialize(state: Bundle?)

    internal fun notify(message: String) {
        rootView?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).apply {
                view.setBackgroundColor(ContextCompat.getColor(appContext, R.color.colorAccent))
                show()
            }
        } ?: Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    internal fun notifyWithAction(message: String, actionText: String, action: () -> Any) {
        rootView?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE).apply {
                setAction(actionText) { action.invoke() }
                setActionTextColor(
                    ContextCompat.getColor(
                        appContext,
                        R.color.white
                    )
                )
                view.setBackgroundColor(ContextCompat.getColor(appContext, R.color.colorAccent))
                show()
            }
        }
    }

    open fun onBackPressed() {}
}