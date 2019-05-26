package io.julius.chow.base.extension

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.julius.chow.R


@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    Glide.with(context).load(url).into(this)
    if (null == url) {
        setImageResource(R.color.gray)
    } else {
        Glide.with(context)
            .load(url.trim { it <= ' ' })
            .apply(
                RequestOptions
                    .centerCropTransform()
                    .centerCrop()
                    .placeholder(R.color.gray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter("squareImageUrl")
fun ImageView.setSquareImageUrl(url: String?) {
    if (null == url) {
        setImageResource(R.color.gray)
    } else {
        Glide.with(context)
            .load(url.trim { it <= ' ' })
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .placeholder(R.color.gray)
                    .transforms(CenterCrop(), RoundedCorners(32))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(this)
    }
}

@BindingAdapter("circleImageUrl")
fun ImageView.setCircleImageUrl(url: String?) {
    if (null == url) {
        setImageResource(R.color.gray)
    } else {
        Glide.with(context)
            .load(url.trim { it <= ' ' })
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .transforms(CenterCrop(), CircleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(this)
    }
}

@BindingAdapter("hide")
fun View.hide(hide: Boolean?) {
    visibility = if (hide!!) View.GONE else View.VISIBLE
}

@BindingAdapter("nestScroll")
fun RecyclerView.scrollable(scrollable: Boolean?) {
    scrollable?.let {
        isNestedScrollingEnabled = scrollable

        if (!scrollable) {
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }
}

@BindingAdapter("rating")
fun RatingBar.rating(rating: Double?) {
    rating?.let {
        this.rating = it.toFloat()
    }
}