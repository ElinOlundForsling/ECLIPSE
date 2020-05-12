package se.hyena.eclipse.recyclerview.item

import android.content.Context
import android.view.View
import android.widget.Toast
import android.widget.ViewFlipper
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_person.*
import se.hyena.eclipse.R
import se.hyena.eclipse.model.Friend
import se.hyena.eclipse.model.User
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil


class PersonItem (
    val person: User,
    val userId: String,
    private val context: Context
)
    : Item() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var doesFriendExistListenerRegistration: ListenerRegistration
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewFlipper = viewHolder.view_flipper_friend_button
        viewHolder.text_view_name.text = person.name
        viewHolder.text_view_friend_profile_bio.text = person.bio
        viewHolder.button_add_friend.setOnClickListener(befriend)
        viewHolder.button_remove_friend.setOnClickListener(unfriend)
        if (person.profilePath != null)
            Glide.with(context)
                .load(StorageUtil.pathToReference(person.profilePath))
                .placeholder(R.drawable.ic_menu_profile)
                .into(viewHolder.image_view_friend_picture)

        doesFriendExistListenerRegistration = FirestoreUtil.doesFriendExistListener(userId, this::changeButton)

    }

    override fun getLayout() = R.layout.item_person

    private fun changeButton(doesExist: Boolean) {
        if (doesExist) {
            viewFlipper.displayedChild = 1
        } else {
            viewFlipper.displayedChild = 0
        }
    }

    private val befriend = View.OnClickListener {
        val newFriend = Friend(person.name, person.bio, person.profilePath)
        FirestoreUtil.addFriend(userId, newFriend) { friendName ->
            val toast = Toast.makeText(this.context, "$friendName added to watchlist!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private val unfriend = View.OnClickListener {
        FirestoreUtil.removeFriend(userId)
    }
}