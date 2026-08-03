package org.emmadice.app.model

/**
 * Fuente visual mostrada por una tarjeta de comunicación.
 */
sealed interface CommunicationVisual {

    data class DrawableResource(
        val resourceId: Int
    ) : CommunicationVisual

    data class LocalPhoto(
        val absolutePath: String
    ) : CommunicationVisual

    data class Placeholder(
        val initial: String
    ) : CommunicationVisual
}