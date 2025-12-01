package com.aki.core.base

/**
 * A base interface for UseCases.
 */
interface BaseUseCase<in In, out Out> {
    suspend fun execute(input: In): Out
}
