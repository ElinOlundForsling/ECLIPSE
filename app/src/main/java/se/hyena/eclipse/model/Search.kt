package se.hyena.eclipse.model

data class SearchResponse(
    val results : ArrayList<Search>
)

data class Search(

    val profile_path : String,
    val poster_path : String,
    val name : String,
    val original_title : String,
    val media_type : String,
    val id : String,
    var original_name: String
)