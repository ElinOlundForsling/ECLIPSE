package se.hyena.eclipse


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import se.hyena.eclipse.model.ImageMessage
import se.hyena.eclipse.model.MessageType
import se.hyena.eclipse.model.TextMessage
import se.hyena.eclipse.model.User
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        FirestoreUtil.getCurrentUser {
            currentUser = it
        }

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)


        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId
            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecycleView)

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(editText_message.text.toString(), Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid, otherUserId, currentUser.name)
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fab_send_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/gif"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            val selectedImageBitmap = when {
                Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    selectedImageUri
                )
                Build.VERSION.SDK_INT >= 28 -> {
                    val source = ImageDecoder.createSource(contentResolver!!, selectedImageUri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                else -> {
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        selectedImageUri)
                }
            }

            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath -> val messageToSend =
                ImageMessage(imagePath, Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid, otherUserId, currentUser.name)
                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
            }
        }


    }


    private fun updateRecycleView(messages : List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount -1)
    }
}
