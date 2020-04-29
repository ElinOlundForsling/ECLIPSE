package se.hyena.eclipse.model

data class WatchList(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val rating: Float,
    val releaseDate: String,
    val type: WatchListType
)

sealed class WatchListType {
    object MovieType : WatchListType()
    object TvShowType : WatchListType()
}