package se.hyena.eclipse.recyclerview.item

import android.content.Context
import android.content.Intent
import android.view.View
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_friend.*
import kotlinx.android.synthetic.main.item_person.image_view_friend_picture
import kotlinx.android.synthetic.main.item_person.text_view_name
import se.hyena.eclipse.*
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.model.User
import se.hyena.eclipse.util.StorageUtil


class FriendItem (
    val person: User,
    val userId: String,
    private val context: Context
)
    : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_name.text = person.name
        viewHolder.text_view_friend_bio.text = person.bio
        viewHolder.button_profile.setOnClickListener(onProfileButtonClick)
        viewHolder.button_chat.setOnClickListener(onChatButtonClick)
        viewHolder.button_match.setOnClickListener(onMatchButtonClick)
        if (person.profilePath != null)
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePath))
                .placeholder(R.drawable.ic_menu_alt_profile)
                .into(viewHolder.image_view_friend_picture)
    }

    private val onChatButtonClick = View.OnClickListener { view ->
        val chatIntent = Intent(this.context, ChatActivity::class.java)
            .apply {
                putExtra(AppConstants.USER_NAME, person.name)
                putExtra(AppConstants.USER_ID, userId)
            }
        context.startActivity(chatIntent)
    }

    private val onProfileButtonClick = View.OnClickListener { view ->
        val profileIntent = Intent(this.context, FriendProfileActivity::class.java)
            .apply {
                putExtra(FRIEND_ID, userId)
                putExtra(FRIEND_NAME, person.name)
                putExtra(FRIEND_BIO, person.bio)
                putExtra(FRIEND_PROFILE_PICTURE_PATH, person.profilePath)
            }
        context.startActivity(profileIntent)
    }

    private val onMatchButtonClick = View.OnClickListener { view ->
        val matchIntent = Intent(this.context, MovieMatchActivity::class.java)
            .apply {
                putExtra(FRIEND_ID, userId)
                putExtra(FRIEND_NAME, person.name)
                putExtra(FRIEND_PROFILE_PICTURE_PATH, person.profilePath)
            }
        context.startActivity(matchIntent)
    }

    override fun getLayout() = R.layout.item_friend


}
