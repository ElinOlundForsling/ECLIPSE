package se.hyena.eclipse.fragment

import android.app.Activity
import android.content.Intent
import android.content.ContentResolver
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
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.editText_name
import se.hyena.eclipse.MainActivity

import se.hyena.eclipse.R
import se.hyena.eclipse.SignInActivity
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil
import java.io.ByteArrayOutputStream


class AccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/jpg", "image/png", "image/gif"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }
            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(), imagePath)
                    }
                else
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(), null)

                val toast = Toast.makeText(this.context, "Saving...", Toast.LENGTH_SHORT)
                toast.show()
            }

            btn_sign_out.setOnClickListener { AuthUI.getInstance()
                .signOut(this@AccountFragment.context!!)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            val selectedImageBitmap = when {
                Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    selectedImageUri
                )
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
                .into(imageView_profile_picture)

            pictureJustChanged = true
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
                        .placeholder(R.drawable.ic_menu_alt_profile)
                        .into(imageView_profile_picture)
            }
        }
    }
}
