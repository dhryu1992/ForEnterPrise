package com.showorld.data

class Note {
    var _id = 0
    var weather: String? = null
    var address: String? = null
    var locationX: String? = null
    var locationY: String? = null
    var contents: String? = null
    var mood: String? = null
    var picture: String? = null
    var createDateStr: String? = null

    constructor(_int: Int, weather: String, address: String, locationX: String, locationY: String, contents: String, mood: String
    , picture: String, createDateStr: String) {
        this._id = _id
        this.weather = weather
        this.address = address
        this.locationX = locationX
        this.locationY = locationY
        this.contents = contents
        this.mood = mood
        this.picture = picture
        this.createDateStr = createDateStr
    }


    @JvmName("get_id1")
    fun get_id(): Int {
        return _id
    }

    @JvmName("set_id1")
    fun set_id(_id: Int) {
        this._id = _id
    }

    @JvmName("getWeather1")
    fun getWeather(): String? {
        return weather
    }

    @JvmName("setWeather1")
    fun setWeather(weather: String?) {
        this.weather = weather
    }

    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }

    @JvmName("setAddress1")
    fun setAddress(address: String?) {
        this.address = address
    }

    @JvmName("getLocationX1")
    fun getLocationX(): String? {
        return locationX
    }

    @JvmName("setLocationX1")
    fun setLocationX(locationX: String?) {
        this.locationX = locationX
    }

    @JvmName("getLocationY1")
    fun getLocationY(): String? {
        return locationY
    }

    @JvmName("setLocationY1")
    fun setLocationY(locationY: String?) {
        this.locationY = locationY
    }

    @JvmName("getContents1")
    fun getContents(): String? {
        return contents
    }

    @JvmName("setContents1")
    fun setContents(contents: String?) {
        this.contents = contents
    }

    @JvmName("getMood1")
    fun getMood(): String? {
        return mood
    }

    @JvmName("setMood1")
    fun setMood(mood: String?) {
        this.mood = mood
    }

    @JvmName("getPicture1")
    fun getPicture(): String? {
        return picture
    }

    @JvmName("setPicture1")
    fun setPicture(picture: String?) {
        this.picture = picture
    }

    @JvmName("getCreateDateStr1")
    fun getCreateDateStr(): String? {
        return createDateStr
    }

    @JvmName("setCreateDateStr1")
    fun setCreateDateStr(createDateStr: String?) {
        this.createDateStr = createDateStr
    }

}

