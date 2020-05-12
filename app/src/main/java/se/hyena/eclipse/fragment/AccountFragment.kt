package se.hyena.eclipse.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.editText_name
import se.hyena.eclipse.*
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.recyclerview.item.WatchlistItem
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil
import java.io.ByteArrayOutputStream


class AccountFragment : Fragment() {

    private val selectImage = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    private lateinit var viewFlipper: ViewFlipper

    private lateinit var movieListenerRegistration: ListenerRegistration
    private lateinit var movieSection: Section
    private var shouldInitRecyclerView = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        viewFlipper = view.findViewById(R.id.view_flipper_watchlist)

        movieListenerRegistration = FirestoreUtil.addWatchlistListener(this.requireContext(), this::updateRecyclerView)


        view.apply {
            image_view_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/jpg", "image/png", "image/gif"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), selectImage)
            }
            button_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(), imagePath)
                    }
                else
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(), null)

                val toast = Toast.makeText(this.context, "Saving...", Toast.LENGTH_SHORT)
                toast.show()
            }

            button_sign_out.setOnClickListener { AuthUI.getInstance()
                .signOut(this@AccountFragment.requireContext())
                .addOnCompleteListener {
                    val signOutIntent = Intent(this.context, SignInActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    startActivity(signOutIntent)
                }}


        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(movieListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        if (items.isEmpty()) {
            viewFlipper.displayedChild = 1
        } else {
            viewFlipper.displayedChild = 0
            fun init() {
                recyclerview_watchlist.apply {
                    layoutManager = LinearLayoutManager(this@AccountFragment.context)
                    adapter = GroupAdapter<GroupieViewHolder>().apply {
                        movieSection = Section(items)
                        add(movieSection)
                        setOnItemClickListener(onItemClick)
                    }
                }
                shouldInitRecyclerView = false
            }

            fun updateItems() = movieSection.update(items)

            if (shouldInitRecyclerView)
                init()
            else
                updateItems()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == selectImage && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            val selectedImageBitmap = when {
                Build.VERSION.SDK_INT >= 28 -> {
                    val source = ImageDecoder.createSource(activity?.contentResolver!!, selectedImageUri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                else -> {
                    MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        selectedImageUri)
                }
            }
            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(image_view_profile_picture)

            pictureJustChanged = true
        }
    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is WatchlistItem) {
            val chatIntent = Intent(this.context, MovieDetailsActivity::class.java)
                .apply {
                    putExtra(MOVIE_ID, item.movie.id)
                    putExtra(MOVIE_TITLE, item.movie.title)
                    putExtra(MOVIE_BACKDROP, item.movie.backdropPath)
                    putExtra(MOVIE_POSTER, item.movie.posterPath)
                    putExtra(MOVIE_OVERVIEW, item.movie.overview)
                    putExtra(MOVIE_RATING, item.movie.rating)
                    putExtra(MOVIE_RELEASE_DATE, item.movie.releaseDate)
                }
            startActivity(chatIntent)
        }

    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this@AccountFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.profilePath != null)
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePath))
                        .placeholder(R.drawable.ic_menu_profile)
                        .into(image_view_profile_picture)
            }
        }
    }
}
