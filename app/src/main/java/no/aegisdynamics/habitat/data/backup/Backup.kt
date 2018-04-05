package no.aegisdynamics.habitat.data.backup

import java.util.*

/**
 * Immutable model class for a backup file
 */

class Backup(val filename: String,
             val fileSize: Long,
             val timestamp: Date)
