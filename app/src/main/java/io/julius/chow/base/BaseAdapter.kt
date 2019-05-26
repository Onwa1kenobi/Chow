package io.julius.chow.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<Data> : RecyclerView.Adapter<BaseAdapter<Data>.ViewHolder>() {

    lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        this.binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            viewType,
            parent,
            false
        )

        val viewHolder = ViewHolder(binding)
        viewHolder.bind(this)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemForPosition(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    abstract fun setData(data: List<Data>)

    protected abstract fun getItemForPosition(position: Int): Data

    @LayoutRes
    protected abstract fun getLayoutIdForPosition(position: Int): Int

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Data) {
            binding.setVariable(BR.`object`, item)
            binding.executePendingBindings()
        }

        fun bind(adapter: BaseAdapter<Data>) {
            binding.setVariable(BR.adapter, adapter)
            binding.executePendingBindings()
        }
    }
}
