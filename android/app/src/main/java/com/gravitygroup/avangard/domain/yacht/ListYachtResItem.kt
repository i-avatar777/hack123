package com.gravitygroup.avangard.domain.yacht

import com.squareup.moshi.Json

data class ListYachtResItem(

	@Json(name="image")
	val image: String? = null,

	@Json(name="place_count")
	val placeCount: Int? = null,

	@Json(name="price")
	val price: Int? = null,

	@Json(name="id")
	val id: Int? = null,

	@Json(name="type_rent")
	val typeRent: Int? = null,

	@Json(name="rating")
	val rating: Int? = null
)
