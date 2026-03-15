package com.a6w.memo.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.a6w.memo.domain.repository.BleRepository
import com.a6w.memo.domain.repository.LabelRepository
import com.a6w.memo.domain.repository.MapmoRepository
import java.util.UUID

/**
 * Mapmo BLE Worker
 * - Send GATT command with Coroutine Worker after Notification
 */
class MapmoBluetoothWorker(
    context: Context,
    params: WorkerParameters,
    private val labelRepository: LabelRepository,
    private val mapmoRepository: MapmoRepository,
    private val bleRepository: BleRepository
) : CoroutineWorker(context, params) {
    companion object {
        // TODO: BLE Device MAC Address
        private const val BLE_DEVICE_MAC_ADDRESS = ""

        // TODO: BLE Service / Characteristic UUID
        val SERVICE_UUID: UUID = UUID.fromString("0000XXXX-0000-1000-8000-00805f9b34fb")
        val CHAR_UUID: UUID = UUID.fromString("0000YYYY-0000-1000-8000-00805f9b34fb")
    }

    override suspend fun doWork(): Result {
        val mapmoID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_MEMO_ID) ?: return Result.failure()
        val userID = inputData.getString(WorkerDefs.KEY_WORKER_INPUT_USER_ID) ?: return Result.failure()

        // Get Mapmo / Label Data from repository
        val targetMapmo = mapmoRepository.getMapmo(mapmoID, userID) ?: return Result.failure()
        val targetLabel = labelRepository.getLabel(targetMapmo.labelID ?: "", userID) ?: return Result.failure()

        // Generate Message and encode it
        val rawMessage = "${targetLabel.name}\n${targetMapmo.title}"
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