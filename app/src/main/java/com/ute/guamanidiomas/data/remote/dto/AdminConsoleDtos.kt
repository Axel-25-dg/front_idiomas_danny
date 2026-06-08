package com.ute.guamanidiomas.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RoleRequest(
    val name: String,
    val permissions: List<String>
)

data class AssignRoleRequest(
    val role: String
)

data class AuditLogDto(
    val id: Int,
    val user: String,
    @SerializedName("action_flag") val actionFlag: String,
    @SerializedName("change_message") val changeMessage: String,
    @SerializedName("action_time") val actionTime: String
)
