package com.aki.core.domain.usecase

import com.aki.core.base.BaseUseCase
import javax.inject.Inject

/**
 * Use case for getting a greeting.
 */
class GetGreetingUseCase @Inject constructor(
    private val mainRepository: MainRepository
) : BaseUseCase<Unit, String> {
    override suspend fun execute(input: Unit): String {
        return mainRepository.getGreeting()
    }
}
