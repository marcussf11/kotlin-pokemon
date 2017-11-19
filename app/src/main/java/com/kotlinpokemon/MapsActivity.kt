package com.kotlinpokemon

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat.checkSelfPermission
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kotlinpokemon.R.drawable.charmander

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var ACCESSLOCATION = 123
    private var location:Location? = null
    private var oldLocation:Location? = null
    private var pokemons = ArrayList<Pokemon>()
    private var playerPower = 0.0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(
                    this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            ACCESSLOCATION -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(applicationContext, "Can't use location!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getUserLocation(){
        Toast.makeText(applicationContext, "Using Location!", Toast.LENGTH_SHORT).show()

        var myLocation = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3,
                3f,
                myLocation
        )

        var myThread = myThread()
        myThread.start()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadPokemon()
    }

    inner class MyLocationListener : LocationListener{

        constructor(){
            location = Location("Start")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location = p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    inner class myThread : Thread{
        constructor():super(){
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }



        override fun run() {
            while(true){

                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation = location

                    runOnUiThread(){
                        // Add a marker in Sydney and move the camera
                        val myLoc = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(myLoc)
                                .title("Red")
                                .snippet("Power $playerPower")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 2f))

                        for(j in 0..pokemons.size-1){
                            var pokemon = pokemons[j]
                            if(location!!.distanceTo(pokemon.location) < 2){
                                pokemon.isCatch = true
                                pokemons[j] = pokemon
                                playerPower += pokemon.power

                                Toast.makeText(applicationContext, pokemon.name + " catched!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Thread.sleep(1000)

                }catch (ex: Exception){

                }


            }
        }

    }

    private fun loadPokemon(){

        var pkm = Pokemon("Charmander","Fire", R.drawable.charmander, 7.0)
        pkm.location.latitude = 55.0
        pkm.location.longitude = 60.0
        pokemons.add( pkm )

        pkm = Pokemon("Squirtle", "Wather", R.drawable.squirtle, 7.0)
        pkm.location.latitude = 53.0
        pkm.location.longitude = 55.0
        pokemons.add( pkm )

        pkm = Pokemon("Bulbasaur", "Leaf", R.drawable.bulbasaur, 7.0)
        pkm.location.latitude = 50.0
        pkm.location.longitude = 50.0
        pokemons.add( pkm )

        setPokemonMap()
    }

    private fun setPokemonMap(){
        for(i in 0..pokemons.size-1){
            var pokemon = pokemons[i]
            if(pokemon.isCatch == false){
                val pkmLoc = LatLng(pokemon.location.latitude, pokemon.location.longitude)
                mMap.addMarker(MarkerOptions()
                        .position(pkmLoc)
                        .title(pokemon.name)
                        .snippet(pokemon.power.toString()+" "+pokemon.desc)
                        .icon(BitmapDescriptorFactory.fromResource(pokemon.img)))
            }
        }
    }
}
