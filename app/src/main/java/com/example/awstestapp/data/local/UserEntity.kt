package com.example.awstestapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // 우리 RDS의 테이블 이름과 동일하게 지정
data class UserEntity(
    @PrimaryKey val id: Int,
    val firebase_uid: String,
    val phone_number: String?,
    val real_name: String,
    val nickname: String,
    val profile_image_url: String?,
    val created_at: String
)