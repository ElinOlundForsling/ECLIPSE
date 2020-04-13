package se.hyena.eclipse.model

data class ChatChannel (val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}