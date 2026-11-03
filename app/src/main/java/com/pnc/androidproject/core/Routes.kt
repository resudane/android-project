package com.pnc.androidproject.core

import kotlinx.serialization.Serializable

sealed interface EventPlannerRoute

@Serializable data object HomeRoute : EventPlannerRoute

@Serializable
data object ProductListRoute: EventPlannerRoute

@Serializable
data class ProductDetailsRoute(
    val productId: Int
): EventPlannerRoute

@Serializable data object OrdersRoute : EventPlannerRoute