package com.a6w.memo.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.a6w.memo.domain.repository.BleRepository
import com.a6w.memo.domain.repository.LabelRepository
import com.a6w.memo.domain.repository.MapmoListRepository
import java.util.UUID

/**
 * Mapmo BLE Worker
 * - Send GATT command with Coroutine Worker after Notification
 */
class MapmoBluetoothWorker(
    context: Context,
    params: WorkerParameters,
    private val labelRepository: LabelRepository,
    private val mapmoListRepository: MapmoListRepository,
    private val bleRepository: BleRepository
) : CoroutineWorker(context, params) {
    companion object {
        // TODO: BLE Device MAC Address
        private const val BLE_DEVICE_MAC_ADDRESS = "68:FE:71:0C:49:9A"

        // TODO: BLE Service / Characteristic UUID
        val SERVICE_UUID: UUID = UUID.fromString("59462f12-9543-9999-12c8-58b459a2712d")
        val CHAR_UUID: UUID = UUID.fromString("33333333-2222-2222-1111-111100000000")
    }

    override suspend fun doWork(): Result {
        val labelID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_LABEL_ID) ?: return Result.failure()
        val userID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_USER_ID) ?: return Result.failure()

        // Get Mapmo / Label Data from repository
        val targetLabel = labelRepository.getLabel(labelID, userID) ?: return Result.failure()
        val targetMapmoList = mapmoListRepository.getMapmoList(userID)
            ?.list?.firstOrNull { it.labelItem?.id == labelID }
            ?.mapmoList?.filter { it.isNotifyEnabled } ?: return Result.failure()

        // Generate Message and encode it
        val rawMessage = "${targetLabel.name}\n${targetMapmoList.joinToString("\n") { it.title }}"
        val payloadBytes = rawMessage.toByteArray(Charsets.UTF_8)

        // Send data to BLE Repository
        val result = bleRepository.sendGattCommand(
            btAddress = BLE_DEVICE_MAC_ADDRESS,
            serviceUuid = SERVICE_UUID,
            characteristicUuid = CHAR_UUID,
            payload = payloadBytes,
        )

        // Try ble sending for 3 times
        return if(result.isSuccess) {
            Result.success()
        } else {
            if(runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}