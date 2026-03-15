package com.a6w.memo.data.repository

import com.a6w.memo.domain.repository.BleRepository
import java.util.UUID

class BleRepositoryImpl: BleRepository {
    override fun sendGattCommand(
        btAddress: String,
        serviceUuid: UUID,
        characteristicUuid: UUID,
        payload: ByteArray
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}