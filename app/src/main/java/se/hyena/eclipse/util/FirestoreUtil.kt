package se.hyena.eclipse.util

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import se.hyena.eclipse.model.*
import se.hyena.eclipse.recyclerview.item.*
import java.lang.NullPointerException

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser =
                    User(FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", null, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener { onComplete() }
            }
            else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePath != null) userFieldMap["profilePath"] = profilePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                it.toObject(User::class.java)?.let { it1 -> onComplete(it1) }
            }
    }

    fun addSearchResultListener(context: Context, searchText: String, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users").orderBy("name").startAt(searchText).endAt(searchText + "\uf8ff")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun doesMovieExistListener(movieId: Long, onListen: (Boolean) -> Unit) : ListenerRegistration {
        val movieIdToString = movieId.toString()
        return firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}").collection("watchlist").document(movieIdToString)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
            }
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        onListen(true)
                    } else {
                        onListen(false)
                    }
                }
            }
    }

    fun addWatchlistListener(context: Context, onListen: (List<Item>) -> Unit) : ListenerRegistration {
        return firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}").collection("watchlist")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    items.add(WatchlistItem(it.toObject(Movie::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun doesFriendExistListener(friendId: String, onListen: (Boolean) -> Unit) : ListenerRegistration {
        return firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}").collection("friends").document(friendId)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        onListen(true)
                    } else {
                        onListen(false)
                    }
                }
            }
    }

    fun addFriendListener(context: Context, onListen: (List<Item>) -> Unit) : ListenerRegistration {
        return firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}").collection("friends")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    items.add(FriendItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun addFriend(friendId: String, friend: Friend, onComplete: (friendName: String) -> Unit) {
        currentUserDocRef.collection("friends")
            .document(friendId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["friendName"] as String)
                    return@addOnSuccessListener
                }

                val newFriendId = currentUserDocRef.collection("friends").document(friendId)
                newFriendId.set(friend)

                onComplete(friend.name)
            }
    }

    fun removeFriend(friendId: String) {
        currentUserDocRef.collection("friends").document(friendId).delete()
    }

    fun addFriendWatchlistListener(context: Context, friendId: String, onListen: (List<Item>) -> Unit) : ListenerRegistration {
        return firestoreInstance.document("users/${friendId}").collection("watchlist")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    items.add(FriendWatchlistItem(it.toObject(Movie::class.java)!!, context))
                }
                onListen(items)
            }
    }

    fun addMatchlistListener(context: Context, friendId: String, onListen: (List<Item>) -> Unit) : ListenerRegistration {

        val userItems = mutableListOf<Item>()
        val userItemIds = mutableListOf<String>()

        firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}").collection("watchlist")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                querySnapshot!!.documents.forEach {
                    userItems.add(MatchlistItem(it.toObject(Movie::class.java)!!, it.id, context))
                    userItemIds.add(it.id)
                    userItems.forEach { it1 ->
                        Log.i("all items", it1.id.toString())
                    }
                }
            }

        return firestoreInstance.document("users/${friendId}").collection("watchlist")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val friendItems = mutableListOf<Item>()
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    friendItems.add(MatchlistItem(it.toObject(Movie::class.java)!!, it.id, context))

                    userItemIds.forEach { it1 ->
                        Log.i("all items", it1)
                        if (it1 == it.id) {
                            items.add(MatchlistItem(it.toObject(Movie::class.java)!!, it.id, context))
                        }
                    }
                }

                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()



    fun addMovieToWatchlist(movieId: Long, movie: Movie, onComplete: (movieTitle: String) -> Unit) {
        val movieIdToString = movieId.toString()
        currentUserDocRef.collection("watchlist")
            .document(movieIdToString).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["filmId"] as String)
                    return@addOnSuccessListener
                }

                val newMovieId = currentUserDocRef.collection("watchlist").document(movieIdToString)
                newMovieId.set(movie)

                onComplete(movie.title)
            }
    }

    fun removeFromWatchlist(movieId: Long) {
        val movieIdToString = movieId.toString()
        currentUserDocRef.collection("watchlist").document(movieIdToString).delete()
    }

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(channelId: String, context: Context, onListen: (List<Item>) -> Unit) : ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
}