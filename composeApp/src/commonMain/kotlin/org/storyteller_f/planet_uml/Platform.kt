package org.storyteller_f.planet_uml

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform