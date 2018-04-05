package no.aegisdynamics.habitat.data.location

/**
 * Immutable model class for a Z-Wave location
 */
class Location(val id: Int,
               val title: String?,
               /**
                * @return Image name that can be retrieved from server:
                * http://hostname/ZAutomation/api/v1/load/image/imagename
                */
               val imageName: String?,
               val deviceCount: Int) {

    val isEmpty: Boolean
        get() = title == null || "" == title

    fun hasImage(): Boolean {
        return imageName != null
    }
}

