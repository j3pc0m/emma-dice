package org.emmadice.app.model

/**
 * Fuente visual mostrada por una tarjeta de comunicación.
 *
 * DrawableResource se utiliza para los recursos provisionales del MVP.
 * LocalPhoto permite mostrar posteriormente las fotografías capturadas
 * por los padres sin modificar el contrato de CommunicationCard.
 */
sealed interface CommunicationVisual {

    data class DrawableResource(
        val resourceId: Int
    ) : CommunicationVisual

    data class LocalPhoto(
        val absolutePath: String
    ) : CommunicationVisual
}