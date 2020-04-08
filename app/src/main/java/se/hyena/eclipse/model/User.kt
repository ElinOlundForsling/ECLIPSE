package se.hyena.eclipse.model

data class User(val name: String,
                val bio: String,
               val profilePath: String?) {
    constructor(): this("", "", null)
}