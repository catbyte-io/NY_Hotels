package com.example.nyhotels

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.imports.rememberStyleImportState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.StandardStyleConfigurationState
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.interactions.standard.generated.StandardPlaceLabelsFeature
import com.mapbox.maps.interactions.FeatureState
import com.mapbox.maps.interactions.FeaturesetFeature
import com.mapbox.maps.plugin.attribution.Attribution
import com.mapbox.maps.plugin.scalebar.ScaleBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val selectedPlaces = remember {
                mutableStateListOf<StandardPlaceLabelsFeature>()
            }
            var selectedPriceLabel by remember {
                mutableStateOf<FeaturesetFeature<FeatureState>?>(null)
            }
            MapboxMap(
                Modifier.fillMaxSize().padding(top = 20.dp),
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(11.0)
                        center(Point.fromLngLat(-73.99, 40.72))
                        pitch(45.0)
                        bearing(0.0)
                    }
                },

                scaleBar = {
                    ScaleBar(Modifier.padding(top = 60.dp))
                },
                logo = {
                    Logo(Modifier.padding(bottom = 40.dp))
                },
                attribution = {
                    Attribution(Modifier.padding(bottom = 40.dp))
                },
                // http://docs.mapbox.com/android/maps/guides/styles/set-a-style/
                style = {
                    MapboxStandardStyle(
                        standardStyleState = rememberStandardStyleState {
                            configurationsState.apply {
                                lightPreset = LightPresetValue.DAWN
                            }
                            interactionsState.onPlaceLabelsClicked { placeLabel, _ ->
                                placeLabel.setStandardPlaceLabelsState {
                                    select(select = true)
                                }
                                selectedPlaces.add(placeLabel)
                                return@onPlaceLabelsClicked true
                            }
                            interactionsState.onMapLongClicked { _ ->
                                selectedPlaces.forEach {
                                    it.removeFeatureState()
                                }
                                return@onMapLongClicked true
                            }
                        },
                        styleImportsContent = {
                            StyleImport(
                                importId = "new-york-hotels",
                                style = "asset://new-york-hotels.json",
                                styleImportState = rememberStyleImportState {
                                    interactionsState.onFeaturesetClicked("hotels-price") { priceLabel, _ ->
                                        if (selectedPriceLabel?.id != priceLabel.id) {
                                            selectedPriceLabel = priceLabel
                                            selectedPriceLabel?.setFeatureState(
                                                FeatureState {
                                                    addBooleanState("hidden", true)
                                                }
                                            )
                                        }
                                        return@onFeaturesetClicked true
                                    }
                                }
                            )
                        }
                    )
                }

            )
        }
    }
}