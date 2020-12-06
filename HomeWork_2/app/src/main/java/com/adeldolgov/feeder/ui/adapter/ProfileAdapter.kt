package com.adeldolgov.feeder.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.decorator.PaddingItemDecoration
import com.adeldolgov.feeder.util.ProfileViewType
import com.adeldolgov.feeder.util.extension.toMinutes
import com.adeldolgov.feeder.util.extension.toRelativeDateStringMinutes
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.item_profile.view.*
import kotlinx.android.synthetic.main.item_profile_field.view.primaryValueText
import kotlinx.android.synthetic.main.item_profile_field.view.secondaryValueText
import kotlinx.android.synthetic.main.item_profile_field_header.view.*
import kotlinx.android.synthetic.main.item_profile_time_field.view.*

private const val PROFILE = 0
private const val FIELD_HEADER = 1
private const val FIELD = 2
private const val TIME_FIELD_WITH_IMAGE = 3



class ProfileAdapter(
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<ProfileAdapter.BaseViewHolder>(), PaddingItemDecoration.PaddingItemInterface {

    var profileFields = listOf<ProfileViewType>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (profileFields[position]) {
            is ProfileViewType.Profile -> PROFILE
            is ProfileViewType.FieldHeader -> FIELD_HEADER
            is ProfileViewType.FieldItem -> FIELD
            is ProfileViewType.TimeFieldWithImageItem -> TIME_FIELD_WITH_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PROFILE -> createProfileViewHolder(inflater, parent)
            FIELD_HEADER -> createFieldHeaderViewHolder(inflater, parent)
            FIELD -> createFieldViewHolder(inflater, parent)
            TIME_FIELD_WITH_IMAGE -> createTimeFieldWithImageViewHolder(inflater, parent)
            else -> createFieldViewHolder(inflater, parent)
        }
    }

    private fun createProfileViewHolder(inflater: LayoutInflater, parent: ViewGroup): ProfileViewHolder {
        return ProfileViewHolder(
            imageLoader, inflater.inflate(R.layout.item_profile, parent, false)
        )
    }

    private fun createFieldHeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup): FieldHeaderViewHolder {
        return FieldHeaderViewHolder(inflater.inflate(R.layout.item_profile_field_header, parent, false))
    }

    private fun createFieldViewHolder(inflater: LayoutInflater, parent: ViewGroup): FieldViewHolder {
        return FieldViewHolder(inflater.inflate(R.layout.item_profile_field, parent, false))
    }

    private fun createTimeFieldWithImageViewHolder(inflater: LayoutInflater, parent: ViewGroup): TimeFieldWithImageViewHolder {
        return TimeFieldWithImageViewHolder(
            imageLoader, inflater.inflate(R.layout.item_profile_time_field, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(profileFields[position])
    }

    override fun getItemCount(): Int = profileFields.size

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(viewType: ProfileViewType)
    }

    class ProfileViewHolder(private val imageLoader: ImageLoader, view: View) : BaseViewHolder(view) {
        companion object {
            private const val ONLINE_TIMEOUT_MINUTES = 5
        }

        override fun bind(viewType: ProfileViewType) {
            with(itemView) {
                (viewType as ProfileViewType.Profile).apply {
                    nameText.text = "${profileItem.firstName} ${profileItem.lastName}"
                    domainText.text = profileItem.domain
                    val onlineTimeDiff = System.currentTimeMillis() - profileItem.lastSeen * DateUtils.SECOND_IN_MILLIS
                    if (onlineTimeDiff.toMinutes() < ONLINE_TIMEOUT_MINUTES) {
                        onlineStatusText.text = context.getText(R.string.online)
                    } else {
                        onlineStatusText.text = context.getString(
                            R.string.was_online,
                            profileItem.lastSeen.toRelativeDateStringMinutes()
                        )
                    }

                    if (profileItem.about.isNotEmpty()) {
                        aboutText.text = profileItem.about
                        aboutText.isVisible = true
                    } else {
                        aboutText.isGone = true
                    }

                    val followers = profileItem.followersCount
                    followersText.text = followers.toString()
                    followersPluralText.text = context.resources.getQuantityString(R.plurals.followers, followers, followers)
                    imageLoader.loadAvatar(profileItem.photo, profileImage)
                }
            }
        }
    }

    class FieldHeaderViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(viewType: ProfileViewType) {
            with(itemView) {
                (viewType as ProfileViewType.FieldHeader).apply {
                    fieldText.text = header
                }
            }
        }
    }

    class FieldViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(viewType: ProfileViewType) {
            with(itemView) {
                (viewType as ProfileViewType.FieldItem).apply {
                    primaryValueText.text = primaryValue
                    secondaryValueText.text = secondaryValue
                }
            }
        }
    }

    class TimeFieldWithImageViewHolder(private val imageLoader: ImageLoader, view: View) : BaseViewHolder(view) {
        override fun bind(viewType: ProfileViewType) {
            with(itemView) {
                (viewType as ProfileViewType.TimeFieldWithImageItem).apply {
                    if (startDate != null) {
                        yearValueText.isVisible = true
                        yearValueText.text = "$startDate — ${endDate?:context.getString(R.string.current_time)}"
                    } else {
                        yearValueText.isGone = true
                    }
                    primaryValueText.text = primaryValue
                    secondaryValueText.text = secondaryValue
                    imageUrl?.let { imageLoader.loadRoundedAvatar(it, fieldImage) } ?: run {
                        fieldImage.setImageDrawable(null)
                    }
                }
            }
        }
    }

    override fun shouldMakePadding(position: Int): Boolean {
        if (position >= itemCount) return false
        return profileFields[position] is ProfileViewType.FieldHeader
    }

}