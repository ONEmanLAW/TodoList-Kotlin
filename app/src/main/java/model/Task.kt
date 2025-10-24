package model

enum class TaskStatus { A_FAIRE, EN_COURS, TERMINEE }
enum class TaskType { PERSONNEL, TRAVAIL, ETUDE, AUTRE }

data class Task(
    val label: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.A_FAIRE,
    val type: TaskType = TaskType.AUTRE,
    val createdAt: String = nowMillisString(),
    val updatedAt: String = nowMillisString(),
    val dueDate: String? = null // YYYY-MM-DD
)

fun nowMillisString(): String = System.currentTimeMillis().toString()
