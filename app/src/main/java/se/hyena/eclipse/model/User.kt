package se.hyena.eclipse.model

data class User(val name: String, 
                val bio: String, 
                val profilePath: String?,
                val registrationTokens: MutableList<String>) {
    constructor(): this("", "", null, mutableListOf())
}