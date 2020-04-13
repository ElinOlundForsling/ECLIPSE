package se.hyena.eclipse.recyclerview.item

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_image_message.*
import se.hyena.eclipse.R
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.model.ImageMessage
import se.hyena.eclipse.util.StorageUtil

class ImageMessageItem(val message: ImageMessage, val context: Context) : MessageItem(message) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(StorageUtil.pathToReference(message.imagePath))
            .placeholder(R.drawable.ic_picture)
            .into(viewHolder.imageView_message_image)
    }

    override fun getLayout() = R.layout.item_image_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs((other as? ImageMessageItem)!!)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}