package no.aegisdynamics.habitat.data.profile

/**
 * Immutable model class for a profile
 */

class Profile(val id: Int,
              val userName: String?,
              val name: String?,
              val email: String?,
              val dashboardDevices: List<String>)
