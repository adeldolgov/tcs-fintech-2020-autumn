package com.adeldolgov.feeder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.LoadState
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_error.view.*

private const val LOADING = 0
private const val ERROR = 1
private const val EMPTY = 2


class LoadStateAdapter(
    val onRetryClickListener: () -> Unit
) : RecyclerView.Adapter<LoadStateAdapter.BaseViewHolder>() {

    private var currentState = listOf<LoadState>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    fun showLoadingState() {
        currentState = listOf(LoadState.Loading())
    }

    fun showErrorState(message: String) {
        currentState = listOf(LoadState.Error(message))
    }

    fun showEmptyState(message: String) {
        currentState = listOf(LoadState.Empty(message))
    }

    fun removeState() {
        currentState = emptyList()
    }


    override fun getItemViewType(position: Int): Int {
        return when (currentState[position]) {
            is LoadState.Loading -> LOADING
            is LoadState.Error -> ERROR
            is LoadState.Empty -> EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADING -> createLoadingViewHolder(inflater, parent)
            EMPTY -> createEmptyViewHolder(inflater, parent)
            else -> createFailureViewHolder(inflater, parent)
        }
    }

    private fun createFailureViewHolder(inflater: LayoutInflater, parent: ViewGroup): FailureViewHolder {
        return FailureViewHolder(
            inflater.inflate(R.layout.item_error, parent, false),
            retryClickListener = { onRetryClickListener() }
        )
    }

    private fun createLoadingViewHolder(inflater: LayoutInflater, parent: ViewGroup): LoadingViewHolder {
        return LoadingViewHolder(
            inflater.inflate(R.layout.item_loading, parent, false)
        )
    }

    private fun createEmptyViewHolder(inflater: LayoutInflater, parent: ViewGroup): EmptyViewHolder {
        return EmptyViewHolder(
            inflater.inflate(R.layout.item_empty, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(currentState[position])
    }

    override fun getItemCount(): Int = currentState.size

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(loadState: LoadState)
    }

    class FailureViewHolder(
        view: View, retryClickListener: () -> Unit) : BaseViewHolder(view) {
        init {
            itemView.retryBtn.setOnClickListener { retryClickListener() }
        }
        override fun bind(loadState: LoadState) {
            with(itemView) {
                errorText.text = (loadState as LoadState.Error).message
            }
        }
    }

    class EmptyViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(loadState: LoadState) {
            with(itemView) {
                emptyListText.text = (loadState as LoadState.Empty).message
            }
        }
    }

    class LoadingViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(loadState: LoadState) {

        }
    }
}