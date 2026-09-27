package com.mindtrainer.data.local.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val mapStringAnyType = TypeToken.getParameterized(Map::class.java, String::class.java, Any::class.java).type
private val listStringType = TypeToken.getParameterized(List::class.java, String::class.java).type
private val listDoubleType = TypeToken.getParameterized(List::class.java, Double::class.java).type
private val listIntType = TypeToken.getParameterized(List::class.java, Int::class.java).type
private val listPointDtoType = TypeToken.getParameterized(List::class.java, PointDto::class.java).type

class Converters {

    @TypeConverter
    fun fromInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun fromLocalDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toLocalDate(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun fromLocalTime(value: Int?): LocalTime? = value?.let { LocalTime.ofSecondOfDay(it.toLong()) }

    @TypeConverter
    fun toLocalTime(value: LocalTime?): Int? = value?.toSecondOfDay()?.toInt()

    @TypeConverter
    fun fromUuid(value: String?): UUID? = value?.let { UUID.fromString(it) }

    @TypeConverter
    fun toUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun fromMapStringAny(value: String?): Map<String, Any>? =
        value?.let { moshi.adapter<Map<String, Any>>(mapStringAnyType).fromJson(it) }

    @TypeConverter
    fun toMapStringAny(value: Map<String, Any>?): String? =
        value?.let { moshi.adapter<Map<String, Any>>(mapStringAnyType).toJson(it) }

    @TypeConverter
    fun fromListString(value: String?): List<String>? =
        value?.let { moshi.adapter<List<String>>(listStringType).fromJson(it) }

    @TypeConverter
    fun toListString(value: List<String>?): String? =
        value?.let { moshi.adapter<List<String>>(listStringType).toJson(it) }

    @TypeConverter
    fun fromListDouble(value: String?): List<Double>? =
        value?.let { moshi.adapter<List<Double>>(listDoubleType).fromJson(it) }

    @TypeConverter
    fun toListDouble(value: List<Double>?): String? =
        value?.let { moshi.adapter<List<Double>>(listDoubleType).toJson(it) }

    @TypeConverter
    fun fromListInt(value: String?): List<Int>? =
        value?.let { moshi.adapter<List<Int>>(listIntType).fromJson(it) }

    @TypeConverter
    fun toListInt(value: List<Int>?): String? =
        value?.let { moshi.adapter<List<Int>>(listIntType).toJson(it) }

    @TypeConverter
    fun fromListPointDto(value: String?): List<PointDto>? =
        value?.let { moshi.adapter<List<PointDto>>(listPointDtoType).fromJson(it) }

    @TypeConverter
    fun toListPointDto(value: List<PointDto>?): String? =
        value?.let { moshi.adapter<List<PointDto>>(listPointDtoType).toJson(it) }
}

// Simple data class for PointDto used in converters
data class PointDto(
    val x: Double,
    val y: Double
)