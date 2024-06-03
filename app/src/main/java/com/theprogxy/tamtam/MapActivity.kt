package com.theprogxy.tamtam

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : Activity() {
    private lateinit var mapView: MapView
    private lateinit var backButton: ImageButton
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var geoPositions: Array<GeoPoint>
    private lateinit var locationsNames: Array<String>
    private var currentLocation : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geoPositions = emptyArray()
        locationsNames = emptyArray()

        val berlin = GeoPoint(52.5200, 13.4050) // Berlin coordinates
        val milan = GeoPoint(45.46416529364403, 9.19191576828787) // Milan coordinates
        val florence = GeoPoint(43.7808062373532, 11.28269620117146) // Milan coordinates
        geoPositions = geoPositions.plus(berlin)
        geoPositions = geoPositions.plus(milan)
        geoPositions = geoPositions.plus(florence)
        locationsNames = locationsNames.plus("Berlin")
        locationsNames = locationsNames.plus("Milan")
        locationsNames = locationsNames.plus("Florence")

        createMap()

        addMarker(berlin, "Berlin")
        addMarker(milan, "Milan")
        addMarker(florence, "Florence")

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Close the map activity and return to the previous screen
        }

        // Set up BottomSheet
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.peekHeight = 50
        bottomSheetBehavior.skipCollapsed = true

        bottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        for (i in 0 until bottomSheet.childCount) {
            val view = bottomSheet.getChildAt(i)
            if (view is TextView) {
                view.text = locationsNames[i]

                view.setOnClickListener {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    else {
                        print("Setting element geoPositions[$i]: ${geoPositions[i]}, `${locationsNames[i]}`\n")
                        currentLocation = i
                        setMapPosition(geoPositions[i])
                    }
                }
            }
        }
    }

    private fun createMap() {
        // Load/initialize the osmdroid configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        // Set the content view
        setContentView(R.layout.activity_map)

        // Find the MapView from the activity_map.xml
        mapView = findViewById(R.id.mapView)

        // Set the tile source
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Set initial map zoom and center
        mapView.controller.setZoom(9.5)
        mapView.controller.setCenter(geoPositions[currentLocation])

    }

    private fun addMarker(point: GeoPoint, title: String) {
        // Add a marker at Berlin
        val marker = Marker(mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_marker, theme)
        marker.title = title
        mapView.overlays.add(marker)
        mapView.invalidate() // Refresh the map
    }

    private fun setMapPosition(point: GeoPoint) {
        mapView.controller.setCenter(point)
        mapView.invalidate()
    }
}