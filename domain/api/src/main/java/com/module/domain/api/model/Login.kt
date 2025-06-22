package com.module.domain.api.model

class Login {
    data class Request(
        val email: String,
        val password: String
    )

    data class Response(
        val _id: String,
        val status: String,
        val name: String,
        val message: String? = "",
        val role: String,
        val address: String? = "",
        val avatar: String? = "",
        val email: String,
        val username: String? = "",
        val phone: String?="",
        val accessToken: String?="",
        val refreshToken: String?=""
    )
}
//class Login {
//    data class Request(
//        @SerializedName("user_name")
//        val userName: String,
//        @SerializedName("password")
//        val password: String
//    )
//
//    data class Response(
//        @SerializedName("access_token")
//        val accessToken: String
//    )
//}