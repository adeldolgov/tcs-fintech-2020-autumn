package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class UploadedPhoto(

	@SerializedName("server")
	val server: Int,

	@SerializedName("photo")
	val photo: String,

	@field:SerializedName("hash")
	val hash: String
)
