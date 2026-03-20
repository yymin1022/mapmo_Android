package com.a6w.memo.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.a6w.memo.domain.repository.LabelRepository
import com.a6w.memo.domain.repository.MapmoListRepository
import com.a6w.memo.domain.service.MapmoNotificationService

/**
 * Mapmo Notification Worker
 * - Show notification of mapmo with Coroutine Worker
 */
class MapmoNotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val labelRepository: LabelRepository,
    private val mapmoListRepository: MapmoListRepository,
    private val notificationService: MapmoNotificationService
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Get Label ID / User ID Data
        val labelID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_LABEL_ID) ?: return Result.failure()
        val userID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_USER_ID) ?: return Result.failure()

        // Get Mapmo / Label Data from repository
        val targetLabel = labelRepository.getLabel(labelID, userID) ?: return Result.failure()
        val targetMapmoList = mapmoListRepository.getMapmoList(userID)
            ?.list?.firstOrNull { it.labelItem?.id == labelID }
            ?.mapmoList?.filter { it.isNotifyEnabled } ?: return Result.failure()

        val notificationTitle = "${targetLabel.name}에 도착하셨나요?"
        val notificationContent = targetMapmoList.joinToString("\n") { "- ${it.title}" }

        // Show notification
        notificationService.showNotification(notificationTitle, notificationContent)

        return Result.success()
    }
}