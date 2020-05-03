package se.hyena.eclipse.recyclerview.item

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_person.*
import se.hyena.eclipse.R
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.model.User
import se.hyena.eclipse.util.StorageUtil


class PersonItem (
    val person: User,
    val userId: String,
    private val context: Context
)
    : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_name.text = person.name
        viewHolder.text_view_friend_profile_bio.text = person.bio
        if (person.profilePath != null)
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePath))
                .placeholder(R.drawable.ic_menu_alt_profile)
                .into(viewHolder.image_view_friend_picture)
    }

    override fun getLayout() = R.layout.item_person
}