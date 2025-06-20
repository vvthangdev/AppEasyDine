package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class SignUpResponse(
    @SerializedName("username") val username: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("address") val address: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("googleId") val googleId: String?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class User(
    @SerializedName("_id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameNoAccents") val nameNoAccents: String,
    @SerializedName("role") val role: String,
    @SerializedName("address") val address: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val version: Int,
    @SerializedName("isActive") val isActive: Boolean
)

data class RefreshTokenResponse(
    @SerializedName("accessToken") val accessToken: String
)
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)
data class UpdateUserRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("avatar") val avatar: String?
)

data class DeleteUserRequest(
    @SerializedName("password") val password: String
)

enum class UserRole {
    ADMIN,
    STAFF,
    CUSTOMER;

    companion object {
        fun fromString(role: String?): UserRole? {
            return try {
                role?.uppercase()?.let { valueOf(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}