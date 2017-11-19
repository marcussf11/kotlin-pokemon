package com.kotlinpokemon

import android.location.Location

/**
 * Created by marcus on 26/10/17.
 */

data class Pokemon(var name:String,
                   var desc:String,
                   var img:Int,
                   var power:Double,
                   var location:Location = Location(name),
                   var isCatch:Boolean = false){

    //var name:String?? = null
    //var desc:String?? = null
    //var img:Int?? = null
    //var power:Double?? = null
    //var lat:Double?? = null
    //var long:Double?? = null
    //var isCatch:Boolean?? = null

}